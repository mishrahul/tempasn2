package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for updating GSTIN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GstinUpdateRequestViewModel {

    @Builder.Default
    private boolean isPrimary = false;
    private String vendorCode;
}
