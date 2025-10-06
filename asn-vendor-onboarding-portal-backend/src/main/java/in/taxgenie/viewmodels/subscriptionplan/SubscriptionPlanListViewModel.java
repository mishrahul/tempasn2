package in.taxgenie.viewmodels.subscriptionplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * View model for subscription plan list response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanListViewModel {

    private List<SubscriptionPlanItemViewModel> plans;
    private int totalPlans;
    private int activePlans;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionPlanItemViewModel {
        private String planId;
        private String planCode;
        private String planName;
        private PricingViewModel pricing;
        private List<String> features;
        private ApiLimitsViewModel apiLimits;
        private boolean isFeatured;
        private boolean isActive;
        private Integer displayOrder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingViewModel {
        private BigDecimal yearly;
        private BigDecimal monthly;
        private BigDecimal setupFee;
        private BigDecimal gstRate;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiLimitsViewModel {
        private int requestsPerMinute;
        private int requestsPerDay;
        private int requestsPerMonth;
    }
}

