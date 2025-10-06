package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * View model for subscription and billing information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionBillingViewModel {

    private CurrentSubscriptionViewModel currentSubscription;
    private List<SubscriptionHistoryViewModel> subscriptionHistory;
    private BillingInfoViewModel billingInfo;
    private List<PaymentHistoryViewModel> paymentHistory;
    private List<SubscriptionPlanViewModel> subscriptionPlans;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentSubscriptionViewModel {
        private String subscriptionId;
        private String planName;
        private String planCode;
        private String status;
        private String startDate;
        private String endDate;
        private String nextBillingDate;
        private PricingViewModel pricing;
        private BigDecimal annualFee;
        private BigDecimal setupFee;
        private String currency;
        private boolean autoRenewal;
        private List<String> features;
        private ApiLimitsViewModel apiLimits;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiLimitsViewModel {
        private int requestsPerMinute;
        private int requestsPerDay;
        private int requestsPerMonth;
        private int currentUsage;
        private String resetDate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionHistoryViewModel {
        private String subscriptionId;
        private String planName;
        private String status;
        private String startDate;
        private String endDate;
        private String nextBillingDate;
        private BigDecimal amount;
        private String currency;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingInfoViewModel {
        private BigDecimal totalPaid;
        private BigDecimal pendingAmount;
        private String lastPaymentDate;
        private String nextBillingDate;
        private String paymentMethod;
        private String currency;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentHistoryViewModel {
        private String transactionId;
        private String subscriptionId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private String paymentMethod;
        private String transactionDate;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionPlanViewModel {
        private String planId;
        private String planCode;
        private String planName;
        private PricingViewModel pricing;
        private List<String> features;
        private ApiLimitsViewModel apiLimits;
        private boolean isFeatured;
        private boolean isActive;
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
}
