package in.taxgenie.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Logging configuration for the ASN Vendor Onboarding Portal
 * Provides centralized logger configuration and utilities
 */
@Configuration
public class LoggingConfig {

    /**
     * Creates a logger bean for dependency injection
     * @return Logger instance
     */
    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger("ASN_VENDOR_PORTAL");
    }

    /**
     * Creates a security logger for authentication and authorization events
     * @return Security Logger instance
     */
    @Bean("securityLogger")
    public Logger securityLogger() {
        return LoggerFactory.getLogger("ASN_VENDOR_PORTAL_SECURITY");
    }

    /**
     * Creates an audit logger for tracking user actions
     * @return Audit Logger instance
     */
    @Bean("auditLogger")
    public Logger auditLogger() {
        return LoggerFactory.getLogger("ASN_VENDOR_PORTAL_AUDIT");
    }
}
