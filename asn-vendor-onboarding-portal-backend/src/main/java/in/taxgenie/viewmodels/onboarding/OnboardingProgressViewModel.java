package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for onboarding progress tracking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingProgressViewModel {

    private Integer percentage;

    private Integer completedSteps;

    private Integer totalSteps;

    private List<StepProgressViewModel> steps;

    private String currentStep;

    private String nextStep;

    private Integer estimatedCompletionDays;

    /**
     * Individual step progress view model
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepProgressViewModel {

        private Integer id;

        private String title;

        private String description;

        private StepStatus status;

        private String completedAt;

        private Integer estimatedDurationHours;

        private List<String> dependencies;
    }

    /**
     * Step status enumeration
     */
    public enum StepStatus {
        COMPLETED,
        CURRENT,
        PENDING,
        BLOCKED,
        SKIPPED
    }
}
