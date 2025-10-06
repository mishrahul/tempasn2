package in.taxgenie.viewmodels.onboarding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for API credentials creation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCredentialsRequestViewModel {

    @NotBlank(message = "OEM code is required")
    private String oemCode;

    @NotBlank(message = "e-Sakha User ID is required")
    private String eSakhaUserId;

    @NotBlank(message = "e-Sakha Password is required")
    private String eSakhaPassword;

    @NotBlank(message = "Environment is required")
    @Pattern(regexp = "^(sandbox|production)$", message = "Environment must be 'sandbox' or 'production'")
    private String environment;

    @Pattern(regexp = "^https?://.*", message = "Webhook URL must be a valid HTTP/HTTPS URL")
    private String webhookUrl;

    private String ipWhitelist;

    private String rateLimitTier; // "BASIC", "STANDARD", "PREMIUM"

    private String additionalConfig;
}
