package in.taxgenie.utils;

import java.util.Base64;
import java.util.UUID;

/**
 * Static data registry for application constants
 * Contains commonly used static values and utility methods
 */
public class StaticDataRegistry {

    /**
     * Default tenant identifier for multi-tenancy
     */
    public static Long DEFAULT_TENANT_IDENTIFIER = 0L;

    /**
     * Production profile identifier
     */
    public static String PROFILE_PROD = "prod";

    /**
     * Development profile identifier
     */
    public static String PROFILE_DEV = "dev";

    /**
     * Test profile identifier
     */
    public static String PROFILE_TEST = "test";

    /**
     * Default page size for pagination
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Maximum page size for pagination
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * First index constant
     */
    public static final int FIRST_INDEX = 0;

    /**
     * First level constant
     */
    public static final int FIRST_LEVEL = 1;

    /**
     * Maximum draft reports allowed
     */
    public static final long MAX_DRAFT_REPORTS = 1000;

    /**
     * Generates a short UUID for use as identifiers
     * @return Short UUID string (16 characters)
     */
    public static String generateShortUUID() {
        UUID uuid = UUID.randomUUID();
        // Convert to Base64 and truncate to 16 characters
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(uuid.toString().getBytes())
                .substring(0, 16);
    }

    /**
     * OEM Portal related constants
     */
    public static class OemPortal {
        public static final String DEFAULT_STATUS = "ACTIVE";
        public static final String INACTIVE_STATUS = "INACTIVE";
        public static final String PENDING_STATUS = "PENDING";
    }

    /**
     * Vendor related constants
     */
    public static class Vendor {
        public static final String ACTIVE_STATUS = "ACTIVE";
        public static final String INACTIVE_STATUS = "INACTIVE";
        public static final String PENDING_VERIFICATION = "PENDING_VERIFICATION";
        public static final String VERIFIED = "VERIFIED";
    }

    /**
     * ASN related constants
     */
    public static class Asn {
        public static final String DRAFT_STATUS = "DRAFT";
        public static final String SUBMITTED_STATUS = "SUBMITTED";
        public static final String APPROVED_STATUS = "APPROVED";
        public static final String REJECTED_STATUS = "REJECTED";
    }

    /**
     * API response constants
     */
    public static class ApiResponse {
        public static final String SUCCESS_MESSAGE = "Operation completed successfully";
        public static final String ERROR_MESSAGE = "An error occurred while processing the request";
        public static final String VALIDATION_ERROR = "Validation failed";
        public static final String UNAUTHORIZED_ERROR = "Unauthorized access";
        public static final String FORBIDDEN_ERROR = "Access forbidden";
        public static final String NOT_FOUND_ERROR = "Resource not found";
    }
}
