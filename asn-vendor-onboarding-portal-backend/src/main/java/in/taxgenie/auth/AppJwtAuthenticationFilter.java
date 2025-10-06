package in.taxgenie.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import in.taxgenie.exception.ErrorResponse;
import in.taxgenie.exception.TokenExpiredException;
import in.taxgenie.multitenancy.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for processing JWT tokens
 * Handles both user and company tokens with tenant context setup
 */
@Component
public class AppJwtAuthenticationFilter extends OncePerRequestFilter {

    private final AppJwtAuthenticationManager jwtAuthenticationManager;
    private final IJwtFacilities jwtFacilities;
    private final Logger appLogger;
    private final ObjectMapper objectMapper;

    /**
     * Constructor
     * @param jwtAuthenticationManager JWT authentication manager
     * @param jwtFacilities JWT facilities for token processing
     */
    public AppJwtAuthenticationFilter(AppJwtAuthenticationManager jwtAuthenticationManager,
                                    IJwtFacilities jwtFacilities) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.jwtFacilities = jwtFacilities;
        this.appLogger = LoggerFactory.getLogger(AppJwtAuthenticationFilter.class);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, 
                                  HttpServletResponse httpServletResponse,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        
        // wrap request in cache
        boolean isMultipart = httpServletRequest.getContentType() != null &&
                httpServletRequest.getContentType().toLowerCase().startsWith("multipart/");

        HttpServletRequest effectiveRequest = httpServletRequest;
        if (!isMultipart) {
            effectiveRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            RequestLogger.logRequest((CachedBodyHttpServletRequest) effectiveRequest, appLogger);
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);

            try {
                Authentication authentication;
                setTenantContext(jwt);

                if (jwtFacilities.isCompanyToken(jwt)) {
                    // Create the CompanyAuthenticationToken for company tokens
                    CompanyDetails companyDetails = jwtFacilities.getCompanyDetails(jwt);
                    authentication = new CompanyAuthenticationToken(companyDetails, jwt);
                } else {
                    // Create the AppJwtAuthentication for user tokens
                    authentication = new AppJwtAuthentication(jwt);
                }

                // Authenticate the token (company or user) using the authentication manager
                authentication = jwtAuthenticationManager.authenticate(authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(effectiveRequest, httpServletResponse);

            } catch (io.jsonwebtoken.ExpiredJwtException eje) {
                appLogger.warn("JWT token expired: {}", eje.getMessage());
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "TOKEN_EXPIRED", "JWT token has expired",
                    "Your session has expired. Please login again to continue.",
                    httpServletRequest.getRequestURI());
            } catch (TokenExpiredException te) {
                appLogger.warn("JWT token expired: {}", te.getMessage());
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "TOKEN_EXPIRED", "JWT token has expired",
                    "Your session has expired. Please login again to continue.",
                    httpServletRequest.getRequestURI());
            } catch (io.jsonwebtoken.MalformedJwtException mje) {
                appLogger.warn("Malformed JWT token: {}", mje.getMessage());
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_TOKEN", "Invalid JWT token format",
                    "The provided token is malformed. Please login again.",
                    httpServletRequest.getRequestURI());
            } catch (io.jsonwebtoken.security.SignatureException se) {
                appLogger.warn("Invalid JWT signature: {}", se.getMessage());
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_TOKEN_SIGNATURE", "Invalid JWT token signature",
                    "The token signature is invalid. Please login again.",
                    httpServletRequest.getRequestURI());
            } catch (io.jsonwebtoken.UnsupportedJwtException uje) {
                appLogger.warn("Unsupported JWT token: {}", uje.getMessage());
                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    "UNSUPPORTED_TOKEN", "Unsupported JWT token",
                    "The token format is not supported. Please login again.",
                    httpServletRequest.getRequestURI());
            } catch (AuthenticationException ae) {
                appLogger.warn("Authentication failed: {}", ae.getMessage());
                String errorCode = "AUTHENTICATION_FAILED";
                String message = "Authentication failed";
                String details = ae.getMessage() != null ? ae.getMessage() : "Invalid credentials. Please check and try again.";

                sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                    errorCode, message, details, httpServletRequest.getRequestURI());
            } finally {
                TenantContext.clear();
            }
        } else {
            appLogger.debug("No Authorization header found or invalid format");
            sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED,
                "MISSING_TOKEN", "Missing authentication token",
                "Authorization header is missing or invalid. Please provide a valid Bearer token.",
                httpServletRequest.getRequestURI());
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        appLogger.info("Traffic from: {} - Path: {}", request.getRemoteAddr(), path);
        return path.startsWith("/swagger")              //  for keeping swagger away from spring security
                || path.startsWith("/v3")
                || path.startsWith("/api-docs")         //  OpenAPI documentation
                || path.startsWith("/webjars")          //  Swagger UI resources
                || path.contains("/handshake")
                || path.startsWith("/health")           //  Health check endpoint
                || path.startsWith("/actuator/health")  //  Public health endpoints
                || path.startsWith("/actuator/info");   //  Public info endpoint
                // Note: Protected actuator endpoints (/actuator/metrics, /actuator/prometheus)
                // will go through JWT authentication
    }

    /**
     * Sets the tenant context from JWT token
     * @param authToken JWT token
     */
    private void setTenantContext(String authToken) {
        try {
            long companyCode = jwtFacilities.getCompanyCode(authToken);
            // Store tenant identifier in a ThreadLocal variable
            TenantContext.setCurrentTenant(companyCode);
            appLogger.debug("Set tenant context for company: {}", companyCode);
        } catch (Exception e) {
            appLogger.warn("Error setting tenant context: {}", e.getMessage());
        }
    }

    /**
     * Sends a JSON error response
     * @param response HTTP servlet response
     * @param status HTTP status code
     * @param error Error code
     * @param message Error message
     * @param details Error details
     * @param path Request path
     */
    private void sendErrorResponse(HttpServletResponse response, int status,
                                   String error, String message, String details, String path) {
        try {
            ErrorResponse errorResponse = ErrorResponse.of(status, error, message, details, path);

            response.setStatus(status);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

        } catch (IOException e) {
            appLogger.error("Error sending error response: {}", e.getMessage());
        }
    }
}
