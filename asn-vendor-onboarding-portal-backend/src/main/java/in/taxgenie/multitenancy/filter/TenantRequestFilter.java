package in.taxgenie.multitenancy.filter;

import in.taxgenie.auth.IJwtFacilities;
import in.taxgenie.multitenancy.context.TenantContext;
import in.taxgenie.multitenancy.service.TenantFilterService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet filter for setting up tenant context on each request
 * Extracts company code from JWT token and sets up tenant filtering
 */
@Component
@Order(1)
public class TenantRequestFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TenantRequestFilter.class);

    @Autowired
    private IJwtFacilities jwtFacilities;

    @Autowired
    private TenantFilterService tenantFilterService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Extract tenant information from JWT token
            String authHeader = httpRequest.getHeader("Authorization");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                try {
                    // Extract company code from JWT token
                    Long companyCode = jwtFacilities.getCompanyCodeFromToken(token);
                    
                    if (companyCode != null) {
                        // Set tenant context
                        TenantContext.setCurrentTenant(companyCode);
                        
                        // Enable Hibernate filter for this tenant
                        tenantFilterService.enableCompanyFilter();
                        
                        logger.debug("Set tenant context for company: {}", companyCode);
                    } else {
                        logger.debug("No company code found in token");
                    }
                    
                } catch (Exception e) {
                    logger.warn("Error extracting tenant from token: {}", e.getMessage());
                }
            } else {
                logger.debug("No Authorization header found or invalid format");
            }

            // Continue with the request
            chain.doFilter(request, response);

        } finally {
            // Clean up tenant context after request processing
            try {
                tenantFilterService.disableCompanyFilter();
                TenantContext.clear();
                logger.debug("Cleared tenant context");
            } catch (Exception e) {
                logger.warn("Error clearing tenant context: {}", e.getMessage());
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("TenantRequestFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("TenantRequestFilter destroyed");
    }
}
