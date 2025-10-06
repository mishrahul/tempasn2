package in.taxgenie.viewmodels.oem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response view model for OEM selection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectOemResponseViewModel {
    
    private boolean success;
    private String message;
    private SelectedOemViewModel selectedOEM;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectedOemViewModel {
        private String id;
        private String name;
        private String accessLevel;
        private List<String> permissions;
        private String sessionExpiry;
    }
}
