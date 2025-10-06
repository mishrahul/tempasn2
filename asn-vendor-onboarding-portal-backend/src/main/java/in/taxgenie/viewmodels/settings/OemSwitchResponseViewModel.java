package in.taxgenie.viewmodels.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for OEM switch response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OemSwitchResponseViewModel {
    
    private String selectedOemId;
    private String selectedOemName;
    private String shortName;
    private String asnVersion;
    private String accessLevel;
    private List<String> permissions;
    private String sessionExpiry;
    private boolean switchSuccessful;
    private String message;
}
