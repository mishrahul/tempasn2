package in.taxgenie.viewmodels.oem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * View model for OEM configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OemConfigurationViewModel {
    
    private String asnVersion;
    private Map<String, Object> brandingConfig;
    private Map<String, Object> featureConfig;
    private Map<String, Object> integrationConfig;
    private Map<String, Object> validationRules;
    private boolean requiresApproval;
    private int maxApiCallsPerDay;
    private String timeZone;
    private String currency;
}
