package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for deployment method selection response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentSelectionResponseViewModel {

    private String selectionId;

    private String deploymentType;

    private String status; // "SELECTED", "CONFIRMED", "IN_PROGRESS"

    private String estimatedTimeline;

    private LocalDateTime estimatedCompletionDate;

    private BigDecimal additionalCost;

    private List<NextStepViewModel> nextSteps;

    private AssignedResourcesViewModel assignedResources;

    private List<DocumentationLinkViewModel> documentationLinks;

    /**
     * Next step in the deployment process
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextStepViewModel {

        private String stepId;

        private String title;

        private String description;

        private String estimatedDuration;

        private List<String> dependencies;
    }

    /**
     * Assigned resources for deployment
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignedResourcesViewModel {

        private ContactViewModel accountManager;

        private ContactViewModel technicalLead;

        private ContactViewModel supportTeam;
    }

    /**
     * Contact information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactViewModel {

        private String name;

        private String email;

        private String phone;

        private String role;
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

        private String type; // "GUIDE", "VIDEO", "API_DOC", "SAMPLE"

        private String description;
    }
}
