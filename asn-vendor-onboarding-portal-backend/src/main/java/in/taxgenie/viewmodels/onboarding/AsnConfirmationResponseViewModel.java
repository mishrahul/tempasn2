package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View model for ASN 2.1 confirmation response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsnConfirmationResponseViewModel {

    private String confirmationId;

    private String status; // "CONFIRMED", "PENDING", "REJECTED"

    private LocalDateTime confirmedAt;

    private String nextStep;

    private String nextStepDescription;

    private String estimatedCompletionTime;

    private RequirementsViewModel requirements;

    /**
     * Requirements for next steps
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequirementsViewModel {

        private Boolean paymentRequired;

        private Boolean deploymentSelectionRequired;

        private Boolean apiCredentialsRequired;

        private Boolean documentationRequired;
    }
}
