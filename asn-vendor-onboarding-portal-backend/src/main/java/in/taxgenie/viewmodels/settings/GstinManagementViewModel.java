package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for GSTIN management response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GstinManagementViewModel {
    
    private List<GstinDetailViewModel> gstinDetails;
    private int totalCount;
    private int verifiedCount;
    private int pendingCount;

    // Fields for pagination metadata
    private int currentPage;
    private int totalPages;
    private long totalItems;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GstinDetailViewModel {
        private String id;
        private String gstin;
        private String state;
        private String stateCode;
        private String vendorCode;
        private boolean isPrimary;
        private boolean isVerified;
        private String verifiedAt;
        private String status;
        private String createdAt;

        //dummy
        private boolean areCredentialsCreated;

    }
}
