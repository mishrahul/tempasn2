package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * View model for subscription plans
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlansViewModel {

    private String oemCode;

    private String oemName;

    private List<PlanViewModel> plans;

    private String recommendedPlan;

    /**
     * Individual subscription plan
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanViewModel {

        private String planCode;

        private String planName;

        private String description;

        private PricingViewModel pricing;

        private List<String> features;

        private ApiLimitsViewModel apiLimits;

        private Boolean isFeatured;

        private Boolean isRecommended;

        private String supportLevel;
    }

    /**
     * Pricing details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingViewModel {

        private BigDecimal annualFee;

        private BigDecimal setupFee;

        private BigDecimal gstRate;

        private BigDecimal totalSetupCost;

        private String currency;
    }

    /**
     * API usage limits
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiLimitsViewModel {

        private Integer requestsPerMinute;

        private Integer requestsPerDay;

        private Integer requestsPerMonth;

        private Integer burstLimit;
    }
}
