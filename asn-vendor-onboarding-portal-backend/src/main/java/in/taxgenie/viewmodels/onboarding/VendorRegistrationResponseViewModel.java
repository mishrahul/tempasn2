package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for vendor registration/onboarding response
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRegistrationResponseViewModel {

    private String vendorId;
    
    private String vendorCode;
    
    private String companyName;
    
    private String panNumber;
    
    private String status; // "ACTIVE", "PENDING_VERIFICATION", "INACTIVE"
    
    private LocalDateTime registeredAt;

    private ContactInfoViewModel primaryContact;
    
    private List<String> nextSteps;
    
    private OnboardingStatusViewModel onboardingStatus;
    
    private String welcomeMessage;
    
    private List<ResourceLinkViewModel> resourceLinks;

    /**
     * Contact information view model
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfoViewModel {
        private String name;
        private String email;
        private String mobile;
        private String designation;
    }

    /**
     * Onboarding status view model
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnboardingStatusViewModel {
        private String currentStep;
        private Integer progressPercentage;
        private Integer completedSteps;
        private Integer totalSteps;
        private String nextStepTitle;
        private String nextStepDescription;
    }

    /**
     * Resource link view model
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResourceLinkViewModel {
        private String title;
        private String description;
        private String url;
        private String type; // "DOCUMENTATION", "VIDEO", "GUIDE", "SUPPORT"
    }
}

