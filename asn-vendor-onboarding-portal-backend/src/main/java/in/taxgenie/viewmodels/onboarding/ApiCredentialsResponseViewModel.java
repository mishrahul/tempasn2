package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for API credentials creation response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCredentialsResponseViewModel {

    private String credentialId;

    private String developerId;

    private String apiKey;

    private String clientSecret;

    private String environment;

    private String endpointUrl;

    private String status; // "ACTIVE", "PENDING", "SUSPENDED"

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private RateLimitsViewModel rateLimits;

    private List<EndpointViewModel> endpoints;

    private String documentationUrl;

    private String supportContact;

    /**
     * Rate limits for API usage
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateLimitsViewModel {

        private Integer requestsPerMinute;

        private Integer requestsPerDay;

        private Integer requestsPerMonth;

        private Integer burstLimit;
    }

    /**
     * Available API endpoints
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointViewModel {

        private String name;

        private String url;

        private String method;

        private String description;

        private Boolean authenticationRequired;
    }
}
