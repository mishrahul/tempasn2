package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for company information response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoResponseViewModel {

    private String companyName;
    private String panNumber;
    private String contactPerson;
    private String email;
    private String phone;
    private String vendorCode;
    private String status;
    private String lastUpdatedAt;
    private PrimaryGstinViewModel primaryGstin;

    /**
     * Nested class for Primary GSTIN details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimaryGstinViewModel {
        private String gstinId;
        private String gstin;
        private String state;
        private String stateCode;
        private String vendorCode;
        private boolean isVerified;
        private String verifiedAt;
        private String status;
    }
}
