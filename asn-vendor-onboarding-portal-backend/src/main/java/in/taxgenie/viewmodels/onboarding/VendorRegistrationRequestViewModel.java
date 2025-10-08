package in.taxgenie.viewmodels.onboarding;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for vendor registration/onboarding request
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRegistrationRequestViewModel {

    // Company Information
    @NotBlank(message = "Company name is required")
    @Size(min = 3, max = 200, message = "Company name must be between 3 and 200 characters")
    private String companyName;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
             message = "PAN number must be in format: ABCDE1234F")
    private String panNumber;

    // Primary Contact Information
    @NotBlank(message = "Contact person name is required")
    @Size(min = 2, max = 100, message = "Contact person name must be between 2 and 100 characters")
    private String contactPersonName;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be a valid email address")
    @Size(max = 100, message = "Contact email must not exceed 100 characters")
    private String contactEmail;

    @NotBlank(message = "Contact mobile is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", 
             message = "Contact mobile must be a valid 10-digit Indian mobile number")
    private String contactMobile;

    @Size(max = 100, message = "Contact designation must not exceed 100 characters")
    private String contactDesignation;

    // Terms and Conditions
    @NotNull(message = "Terms acceptance is required")
    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean termsAccepted;

    @NotNull(message = "Privacy policy acceptance is required")
    @AssertTrue(message = "You must accept the privacy policy")
    private Boolean privacyPolicyAccepted;
}

