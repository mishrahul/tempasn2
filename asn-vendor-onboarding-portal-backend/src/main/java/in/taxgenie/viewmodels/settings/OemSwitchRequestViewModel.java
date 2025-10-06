package in.taxgenie.viewmodels.settings;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for switching OEM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OemSwitchRequestViewModel {

    @NotBlank(message = "OEM ID is required")
    private String oemId;

    @Builder.Default
    private boolean rememberSelection = true;
    private String sessionPreferences;
}
