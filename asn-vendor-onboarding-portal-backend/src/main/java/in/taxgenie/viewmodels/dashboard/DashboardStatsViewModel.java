package in.taxgenie.viewmodels.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * View model for dashboard statistics
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsViewModel {

    /**
     * Overall implementation progress percentage
     */
    private Integer progress;

    /**
     * Completed steps in format "x/y"
     */
    private String completedSteps;

    /**
     * Days remaining until deadline (September 30, 2025)
     */
    private Integer daysRemaining;

    /**
     * Current subscription plan
     */
    private String currentPlan;

    /**
     * Deadline date for ASN 2.1 migration
     */
    private LocalDateTime deadline;

    /**
     * Overall status of the implementation
     */
    private String status;

    /**
     * Next action required
     */
    private String nextAction;

    /**
     * Critical alerts or warnings
     */
    private CriticalAlertViewModel criticalAlert;

    /**
     * Nested class for critical alerts
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriticalAlertViewModel {
        
        private String type; // warning, error, info
        
        private String title;
        
        private String message;
        
        private Boolean actionRequired;
        
        private String actionUrl;
    }
}
