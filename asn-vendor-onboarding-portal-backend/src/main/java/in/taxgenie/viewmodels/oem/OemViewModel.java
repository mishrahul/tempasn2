package in.taxgenie.viewmodels.oem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for OEM information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OemViewModel {

    private String id;
    private String oemCode;
    private String shortName;
    private String fullName;
    private String logoBackground;
    private List<String> features;
    private String status;
    private boolean hasAccess;
    private boolean isComingSoon;
    private boolean noAccess;
    private String noAccessReason;
    private String description;
    private String website;
    private String supportEmail;
    private String supportPhone;
    private OemConfigurationViewModel configuration;
}
