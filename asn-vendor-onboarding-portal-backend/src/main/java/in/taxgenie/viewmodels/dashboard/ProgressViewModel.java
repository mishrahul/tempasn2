package in.taxgenie.viewmodels.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * View model for progress tracking (onboarding and implementation)
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressViewModel {

    /**
     * Overall progress percentage
     */
    private Integer percentage;

    /**
     * Number of completed steps
     */
    private Integer completedSteps;

    /**
     * Total number of steps
     */
    private Integer totalSteps;

    /**
     * List of progress steps
     */
    private List<StepProgressViewModel> steps;

    /**
     * Type of progress (onboarding, implementation)
     */
    private String type;

    /**
     * Current step ID
     */
    private Integer currentStepId;

    /**
     * Estimated completion date
     */
    private LocalDateTime estimatedCompletion;

    /**
     * Last updated timestamp
     */
    private LocalDateTime lastUpdated;

    /**
     * Nested class for individual step progress
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepProgressViewModel {
        
        /**
         * Step ID
         */
        private Integer id;
        
        /**
         * Step title
         */
        private String title;
        
        /**
         * Step description
         */
        private String description;
        
        /**
         * Step status: completed, current, pending, blocked, skipped
         */
        private String status;
        
        /**
         * Step completion date
         */
        private LocalDateTime completedAt;
        
        /**
         * Estimated duration in hours
         */
        private Integer estimatedDuration;
        
        /**
         * Dependencies for this step
         */
        private List<String> dependencies;
        
        /**
         * Action URL for this step
         */
        private String actionUrl;
        
        /**
         * Whether this step is actionable
         */
        private Boolean isActionable;
    }
}
