package in.taxgenie.viewmodels.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for user profile information
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileViewModel {

    /**
     * User ID
     */
    private String id;

    /**
     * Company name
     */
    private String companyName;

    /**
     * PAN number
     */
    private String panNumber;

    /**
     * Contact person name
     */
    private String contactPerson;

    /**
     * Email address
     */
    private String email;

    /**
     * Phone number
     */
    private String phone;

    /**
     * Vendor code
     */
    private String vendorCode;

    /**
     * Current subscription plan
     */
    private String currentPlan;

    /**
     * GSTIN details
     */
    private List<GSTINDetailViewModel> gstinDetails;

    /**
     * Account creation date
     */
    private LocalDateTime createdAt;

    /**
     * Last login date
     */
    private LocalDateTime lastLogin;

    /**
     * Account status
     */
    private String status;

    /**
     * OEM associations
     */
    private List<String> oemAssociations;

    /**
     * Subscription details
     */
    private SubscriptionDetailViewModel subscription;

    /**
     * Nested class for GSTIN details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GSTINDetailViewModel {
        
        private String gstin;
        
        private String state;
        
        private String vendorCode;
        
        private Boolean isActive;
    }

    /**
     * Nested class for subscription details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionDetailViewModel {
        
        private String planName;
        
        private String planType; // Basic, Professional, Enterprise
        
        private LocalDateTime startDate;
        
        private LocalDateTime endDate;
        
        private Boolean isActive;
        
        private List<String> features;
        
        private PlanLimitsViewModel limits;
    }

    /**
     * Nested class for plan limits
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanLimitsViewModel {
        
        private Integer maxTransactions;
        
        private Integer maxUsers;
        
        private String supportLevel;
        
        private Boolean hasAISpecialist;
    }
}
