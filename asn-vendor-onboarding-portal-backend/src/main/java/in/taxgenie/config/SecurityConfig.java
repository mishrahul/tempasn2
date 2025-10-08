package in.taxgenie.config;

import in.taxgenie.auth.AppJwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration for ASN Vendor Onboarding Portal
 *
 * This configuration class sets up comprehensive security for the application including:
 * - JWT-based stateless authentication
 * - CORS configuration for cross-origin requests
 * - Security headers for enhanced protection
 * - Method-level security with @PreAuthorize annotations
 * - Custom authentication filter integration
 *
 * @author ASN Development Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final AppJwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain with comprehensive security settings
     *
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain with JWT authentication and enhanced headers");

        try {
            http
                    // Configure stateless session management for JWT-based authentication
                    .sessionManagement(session -> {
                        log.debug("Configuring stateless session management");
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    })

                    // Enable CORS with custom configuration
                    .cors(Customizer.withDefaults())

                    // Disable CSRF protection for stateless APIs
                    .csrf(csrf -> {
                        log.debug("Disabling CSRF protection for stateless API");
                        csrf.disable();
                    })

                    // Configure authorization rules
                    .authorizeHttpRequests(auth -> {
                        log.debug("Configuring authorization rules");
                        auth
                                // Public endpoints - no authentication required
                                .requestMatchers("/onboarding/test", "/dashboard/test").permitAll()
                                .requestMatchers("/api-docs", "/api-docs/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()

                                // Actuator endpoints - Production security
                                // Public: Basic health and info (no sensitive data)
                                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                                .requestMatchers("/actuator/info").permitAll()
                                .requestMatchers("/actuator/health/readiness", "/actuator/health/liveness").permitAll()  // K8s probes

                                // Protected: Metrics and monitoring (require authentication)
                                // In production, these should be accessed by monitoring systems with proper credentials
                                .requestMatchers("/actuator/metrics", "/actuator/metrics/**").authenticated()
                                .requestMatchers("/actuator/prometheus").authenticated()

                                // Highly Sensitive: Admin only (env, loggers, threaddump, heapdump)
                                // These should be disabled in production or require ADMIN role
                                .requestMatchers("/actuator/env", "/actuator/env/**").hasRole("ADMIN")
                                .requestMatchers("/actuator/loggers", "/actuator/loggers/**").hasRole("ADMIN")
                                .requestMatchers("/actuator/threaddump").hasRole("ADMIN")
                                .requestMatchers("/actuator/heapdump").hasRole("ADMIN")

                                // Deny all other actuator endpoints by default
                                .requestMatchers("/actuator/**").denyAll()

                                .requestMatchers("/error").permitAll()

                                // All other endpoints require authentication
                                .anyRequest().authenticated();
                    })

                    // Add custom JWT authentication filter
                    .addFilterAt(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

            log.info("Security filter chain configured successfully");
            return http.build();

        } catch (Exception e) {
            log.error("Failed to configure security filter chain", e);
            throw e;
        }
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings
     *
     * This method sets up CORS configuration to allow the frontend application
     * to make requests to the backend API from different origins. It includes
     * specific patterns for development and production environments.
     *
     * @return CorsConfigurationSource configured CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Configuring CORS settings for cross-origin requests");

        try {
            CorsConfiguration configuration = new CorsConfiguration();

            // Configure allowed origins - you can customize this list
            configuration.setAllowedOrigins(Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:4200",
                    "https://supplier-connect-app.web.app"  // Replace with your actual frontend domain
            ));

            // Also set origin patterns for wildcard support
            configuration.setAllowedOriginPatterns(Arrays.asList(
                    "http://localhost:*",           // Development frontend
                    "https://*.taxgenie.in",        // Production domains
                    "https://*.asnportal.com"       // Additional production domains
            ));
            log.debug("Configured allowed origin patterns: {}", configuration.getAllowedOriginPatterns());

            // Configure allowed HTTP methods
            configuration.setAllowedMethods(Arrays.asList(
                    "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
            ));
            log.debug("Configured allowed methods: {}", configuration.getAllowedMethods());

            // Allow all headers for flexibility
            configuration.setAllowedHeaders(Arrays.asList("*"));

            // Allow credentials for authentication
            configuration.setAllowCredentials(true);

            // Set preflight cache duration (1 hour)
            configuration.setMaxAge(3600L);

            // Apply CORS configuration to all endpoints
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            log.info("CORS configuration completed successfully");
            return source;

        } catch (Exception e) {
            log.error("Failed to configure CORS settings", e);
            throw e;
        }
    }
}
