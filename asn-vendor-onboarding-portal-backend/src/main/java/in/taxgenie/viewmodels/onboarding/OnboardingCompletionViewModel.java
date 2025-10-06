package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for onboarding completion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingCompletionViewModel {

    private String completionId;

    private String status; // "COMPLETED", "PENDING_VERIFICATION", "FAILED"

    private LocalDateTime completedAt;

    private String vendorCode;

    private String oemCode;

    private String subscriptionId;

    private String apiCredentialsId;

    private LocalDateTime goLiveDate;

    private List<String> nextSteps;

    private List<SupportContactViewModel> supportContacts;

    private List<DocumentationLinkViewModel> documentationLinks;

    private String completionCertificateUrl;

    /**
     * Support contact information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportContactViewModel {

        private String name;

        private String role;

        private String email;

        private String phone;

        private String availability;
    }

    /**
     * Documentation link
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentationLinkViewModel {

        private String title;

        private String url;

        private String type;

        private String description;
    }
}
