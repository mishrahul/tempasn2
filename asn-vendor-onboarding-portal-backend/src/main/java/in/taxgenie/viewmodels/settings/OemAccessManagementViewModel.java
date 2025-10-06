package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for OEM access management
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OemAccessManagementViewModel {
    
    private List<OemAccessDetailViewModel> oemAccess;
    private String selectedOemId;
    private String selectedOemName;
    private int totalOemAccess;
    private int activeAccess;
    private int pendingAccess;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OemAccessDetailViewModel {
        private String oemId;
        private String oemName;
        private String shortName;
        private String asnVersion;
        private String accessLevel;
        private String status;
        private boolean hasAccess;
        private boolean isActive;
        private String grantedAt;
        private String expiresAt;
        private String lastAccessedAt;
        private int totalApiCalls;
        private int totalAsnGenerated;
        private List<String> permissions;
        private String vendorCode;
    }
}
