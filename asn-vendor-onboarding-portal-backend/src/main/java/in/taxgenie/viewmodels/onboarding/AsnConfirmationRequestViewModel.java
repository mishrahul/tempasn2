package in.taxgenie.viewmodels.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for ASN 2.1 confirmation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsnConfirmationRequestViewModel {

    @NotBlank(message = "OEM code is required")
    private String oemCode;

    @NotBlank(message = "Confirmation type is required")
    private String confirmationType; // "ASN_2_1_ACTIVATION"

    @NotNull(message = "Acknowledgment is required")
    private Boolean acknowledgment;

    @NotNull(message = "Terms acceptance is required")
    private Boolean termsAccepted;

    @NotNull(message = "Compliance confirmation is required")
    private Boolean complianceConfirmed;

    private String additionalNotes;
}
