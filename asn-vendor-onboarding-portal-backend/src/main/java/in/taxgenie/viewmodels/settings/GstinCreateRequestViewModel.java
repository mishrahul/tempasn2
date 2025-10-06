package in.taxgenie.viewmodels.settings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for creating new GSTIN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GstinCreateRequestViewModel {
    
    @NotBlank(message = "GSTIN is required")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{3}$", 
             message = "GSTIN must be in format: 22ABCDE1234F1Z5")
    private String gstin;
    
    @NotBlank(message = "State code is required")
    @Pattern(regexp = "^[0-9]{2}$",
             message = "State code must be 2 digits")
    private String stateCode;

    @Builder.Default
    private boolean isPrimary = false;
}
