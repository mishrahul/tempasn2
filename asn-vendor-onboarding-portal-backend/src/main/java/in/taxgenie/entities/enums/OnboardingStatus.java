package in.taxgenie.entities.enums;

/**
 * Enumeration for onboarding process status
 * Must match the database constraint values exactly
 *
 * @author ASN Development Team
 * @version 1.0.0
 */
public enum OnboardingStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}
