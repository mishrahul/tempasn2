package in.taxgenie.auth;

import in.taxgenie.audit.SensitiveDataMasker;
import in.taxgenie.utils.StaticDataRegistry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Utility class for logging HTTP requests with sensitive data masking
 * Provides detailed request logging for debugging and audit purposes
 */
@Component
public class RequestLogger {

    private static String activeProfile;

    /**
     * Constructor that initializes the active profile
     * @param environment Spring environment
     */
    @Autowired
    public RequestLogger(Environment environment) {
        String[] profiles = environment.getActiveProfiles();
        // Fall back to default if no profile is set
        activeProfile = profiles.length > 0 ? profiles[0] : "prod";
    }

    /**
     * Logs the HTTP request details with sensitive data masking
     * @param request Cached body HTTP servlet request
     * @param appLogger Logger instance
     */
    public static void logRequest(CachedBodyHttpServletRequest request, Logger appLogger) {
        StringBuilder log = new StringBuilder();
        boolean isProd = activeProfile.contains(StaticDataRegistry.PROFILE_PROD);

        try {
            String requestURI = request.getRequestURI();
            String queryParams = request.getQueryString();
            String method = request.getMethod();

            log.append("\n\n****************START OF REQUEST LOG****************\n");
            log.append("Request Method: ").append(method).append("\n");
            log.append("Request URL: ").append(requestURI).append("\n");
            log.append("Query String: ").append(queryParams != null ? queryParams : "N/A").append("\n");

            // Headers
            log.append("Headers:\n");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                
                // Mask Authorization header in production
                if ("Authorization".equalsIgnoreCase(headerName) && isProd) {
                    headerValue = "Bearer ***";
                }
                
                log.append(headerName).append(": ").append(headerValue).append("\n");
            }

            // Parameters
            log.append("Parameters:\n");
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (!parameterMap.isEmpty()) {
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String key = entry.getKey();
                    String[] values = entry.getValue();
                    String joinedValues = String.join(", ", values);
                    String masked = isProd ? SensitiveDataMasker.mask(key + ": " + joinedValues) : (key + ": " + joinedValues);
                    log.append(masked).append("\n");
                }
            } else {
                log.append("None\n");
            }

            // Body
            if (!isProd) {
                String body = getRequestBody(request, true); // as we are masking in MaskingAppender via logback
                log.append("Body:\n").append(body).append("\n");

                log.append("****************END OF REQUEST LOG****************\n");
            }
            appLogger.info(log.toString());

        } catch (Exception e) {
            appLogger.error("Error logging request: {}", e.getMessage());
        }
    }

    /**
     * Extracts the request body from cached request
     * @param request Cached body HTTP servlet request
     * @param mask Whether to mask sensitive data
     * @return Request body as string
     */
    private static String getRequestBody(CachedBodyHttpServletRequest request, boolean mask) {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            return "Error reading body: " + e.getMessage();
        }

        String bodyStr = body.toString().trim();
        if (bodyStr.isEmpty()) {
            return "None";
        }

        // Apply masking if requested and not in production (as production uses logback masking)
        if (mask && !activeProfile.contains(StaticDataRegistry.PROFILE_PROD)) {
            return SensitiveDataMasker.mask(bodyStr);
        }

        return bodyStr;
    }
}
