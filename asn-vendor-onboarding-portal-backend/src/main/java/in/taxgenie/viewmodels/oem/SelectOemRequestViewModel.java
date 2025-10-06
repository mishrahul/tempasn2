package in.taxgenie.viewmodels.oem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Request view model for selecting an OEM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectOemRequestViewModel {
    
    @NotBlank(message = "OEM ID is required")
    private String oemId;
    
    @NotBlank(message = "OEM name is required")
    private String oemName;
}
