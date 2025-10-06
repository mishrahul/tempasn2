package in.taxgenie.viewmodels.subscriptionplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * View model for subscription plan detail response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDetailViewModel {

    private String planId;
    private String planCode;
    private String planName;
    private PricingViewModel pricing;
    private List<String> features;
    private ApiLimitsViewModel apiLimits;
    private SupportConfigViewModel supportConfig;
    private boolean isFeatured;
    private boolean isActive;
    private Integer displayOrder;
    private String createdAt;
    private String updatedAt;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportConfigViewModel {
        private String type;
        private int slaHours;
        private String priority;
    }
}

