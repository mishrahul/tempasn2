package in.taxgenie.viewmodels.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for user profile information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileViewModel {
    
    private String id;
    private String email;
    private String name;
    private String companyName;
    private String panNumber;
    private String contactPerson;
    private String phone;
    private String vendorCode;
    private String currentPlan;
    private List<GstinDetailViewModel> gstinDetails;
    private String profileImageUrl;
    private String status;
    private String createdAt;
    private String lastLoginAt;
}
