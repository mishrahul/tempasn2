package in.taxgenie.viewmodels.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for GSTIN details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GstinDetailViewModel {
    
    private String id;
    private String gstinNumber;
    private String legalName;
    private String tradeName;
    private String state;
    private String stateCode;
    private String address;
    private String pincode;
    private String status;
    private boolean isPrimary;
    private String registrationDate;
    private String lastVerifiedAt;
}
