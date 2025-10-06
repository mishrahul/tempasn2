package in.taxgenie.viewmodels.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for updating company information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoUpdateRequestViewModel {
    
    @NotBlank(message = "Company name is required")
    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;
    
    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", 
             message = "PAN number must be in format: ABCDE1234F")
    private String panNumber;
    
    @NotBlank(message = "Contact person is required")
    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    private String contactPerson;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", 
             message = "Phone number must be valid")
    private String phone;
}
