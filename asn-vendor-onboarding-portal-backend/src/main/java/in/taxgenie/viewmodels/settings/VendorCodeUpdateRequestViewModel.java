package in.taxgenie.viewmodels.settings;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for updating vendor code
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCodeUpdateRequestViewModel {
    
    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;
    
    private String status; // ACTIVE or INACTIVE
}

