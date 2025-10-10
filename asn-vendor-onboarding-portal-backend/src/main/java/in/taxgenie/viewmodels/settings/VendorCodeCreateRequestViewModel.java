package in.taxgenie.viewmodels.settings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for creating new vendor code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCodeCreateRequestViewModel {
    
    @NotNull(message = "GSTIN ID is required")
    private String gstinId;
    
    @NotBlank(message = "Vendor code is required")
    @Size(min = 1, max = 50, message = "Vendor code must be between 1 and 50 characters")
    private String vendorCode;
    
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
}

