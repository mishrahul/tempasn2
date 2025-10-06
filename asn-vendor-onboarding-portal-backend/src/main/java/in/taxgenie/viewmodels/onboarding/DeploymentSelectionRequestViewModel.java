package in.taxgenie.viewmodels.onboarding;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for deployment method selection request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentSelectionRequestViewModel {

    @NotBlank(message = "OEM code is required")
    private String oemCode;

    @NotBlank(message = "Deployment type is required")
    private String deploymentType; // "SELF_DEPLOYMENT", "ASSISTED_IMPLEMENTATION"

    private String preferredTimeline; // "IMMEDIATE", "1_WEEK", "2_WEEKS", "1_MONTH"

    private String technicalExpertiseLevel; // "BEGINNER", "INTERMEDIATE", "ADVANCED"

    private String existingErpSystem; // "TALLY", "SAP", "ORACLE", "CUSTOM", "NONE"

    private String integrationRequirements;

    private Boolean additionalServicesRequired;

    private String notes;
}
