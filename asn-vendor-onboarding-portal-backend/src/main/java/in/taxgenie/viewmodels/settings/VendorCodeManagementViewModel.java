package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for vendor code management response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCodeManagementViewModel {
    
    private List<VendorCodeDetailViewModel> vendorCodes;
    private int totalCount;
    private int activeCount;
    private int inactiveCount;
    
    /**
     * Nested view model for vendor code details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorCodeDetailViewModel {
        private String vendorCodeId;
        private String vendorCode;
        private String description;
        private String status;
        private GstinInfoViewModel gstinInfo;
        private String createdAt;
        private String updatedAt;
    }
    
    /**
     * Nested view model for GSTIN information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GstinInfoViewModel {
        private String gstinId;
        private String gstin;
        private String stateCode;
        private String stateName;
        private boolean isPrimary;
        private boolean isVerified;
    }
}

