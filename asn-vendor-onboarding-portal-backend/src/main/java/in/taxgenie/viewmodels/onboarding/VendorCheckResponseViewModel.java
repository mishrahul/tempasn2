package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View model for vendor registration check response
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCheckResponseViewModel {

    private Boolean isRegistered;
    
    private String vendorId;
    
    private String vendorCode;
    
    private String companyName;
    
    private String panNumber;
    
    private String status; // "ACTIVE", "INACTIVE"
    
    private LocalDateTime registeredAt;
    
    private ContactInfoViewModel primaryContact;
    
    private String message;

    /**
     * Contact information view model
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInfoViewModel {
        private String name;
        private String email;
        private String mobile;
        private String designation;
    }
}

