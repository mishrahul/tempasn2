package in.taxgenie.viewmodels.oem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response view model for available OEMs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableOemsResponseViewModel {
    
    private List<OemViewModel> oems;
    private int totalCount;
    private String message;
}
