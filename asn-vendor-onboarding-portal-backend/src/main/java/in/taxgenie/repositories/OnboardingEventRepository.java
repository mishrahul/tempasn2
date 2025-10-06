package in.taxgenie.repositories;

import in.taxgenie.entities.OnboardingEvent;
import in.taxgenie.entities.OnboardingProcess;
import in.taxgenie.entities.enums.ActorType;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for OnboardingEvent entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface OnboardingEventRepository extends BaseRepository<OnboardingEvent> {

    /**
     * Find events by onboarding process
     * 
     * @param onboardingProcess the onboarding process
     * @return List of onboarding events ordered by timestamp
     */
    List<OnboardingEvent> findByOnboardingProcessOrderByEventTimestampDesc(OnboardingProcess onboardingProcess);

    /**
     * Find events by event type
     * 
     * @param eventType the event type
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByEventType(String eventType);

    /**
     * Find events by actor type
     * 
     * @param actorType the actor type
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByTriggeredByType(ActorType actorType);

    /**
     * Find events by triggered by user
     * 
     * @param triggeredBy the user ID who triggered the event
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByTriggeredBy(UUID triggeredBy);

    /**
     * Find events by onboarding process and event type
     * 
     * @param onboardingProcess the onboarding process
     * @param eventType the event type
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByOnboardingProcessAndEventType(OnboardingProcess onboardingProcess, String eventType);

    /**
     * Find events after given timestamp
     * 
     * @param timestamp the timestamp threshold
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByEventTimestampAfter(LocalDateTime timestamp);

    /**
     * Find events between timestamps
     * 
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByEventTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find event by idempotency key
     * 
     * @param idempotencyKey the idempotency key
     * @return Optional containing the event if found
     */
    Optional<OnboardingEvent> findByIdempotencyKey(String idempotencyKey);

    /**
     * Find recent events for onboarding process
     *
     * @param onboardingProcess the onboarding process
     * @return List of recent onboarding events (limited to 10)
     */
    @Query("SELECT oe FROM OnboardingEvent oe WHERE oe.onboardingProcess = :onboardingProcess ORDER BY oe.eventTimestamp DESC LIMIT 10")
    List<OnboardingEvent> findRecentEventsByOnboardingProcess(@Param("onboardingProcess") OnboardingProcess onboardingProcess);

    /**
     * Find events by onboarding process and actor type
     * 
     * @param onboardingProcess the onboarding process
     * @param actorType the actor type
     * @return List of onboarding events
     */
    List<OnboardingEvent> findByOnboardingProcessAndTriggeredByType(OnboardingProcess onboardingProcess, ActorType actorType);

    /**
     * Count events by onboarding process
     * 
     * @param onboardingProcess the onboarding process
     * @return count of events
     */
    long countByOnboardingProcess(OnboardingProcess onboardingProcess);

    /**
     * Count events by event type
     * 
     * @param eventType the event type
     * @return count of events
     */
    long countByEventType(String eventType);

    /**
     * Check if idempotency key exists
     * 
     * @param idempotencyKey the idempotency key
     * @return true if exists, false otherwise
     */
    boolean existsByIdempotencyKey(String idempotencyKey);

    /**
     * Find events by multiple event types
     * 
     * @param eventTypes the list of event types
     * @return List of onboarding events
     */
    @Query("SELECT oe FROM OnboardingEvent oe WHERE oe.eventType IN :eventTypes ORDER BY oe.eventTimestamp DESC")
    List<OnboardingEvent> findByEventTypeIn(@Param("eventTypes") List<String> eventTypes);

    /**
     * Find latest event by onboarding process and event type
     * 
     * @param onboardingProcess the onboarding process
     * @param eventType the event type
     * @return Optional containing the latest event if found
     */
    @Query("SELECT oe FROM OnboardingEvent oe WHERE oe.onboardingProcess = :onboardingProcess AND oe.eventType = :eventType ORDER BY oe.eventTimestamp DESC")
    Optional<OnboardingEvent> findLatestByOnboardingProcessAndEventType(@Param("onboardingProcess") OnboardingProcess onboardingProcess, 
                                                                        @Param("eventType") String eventType);
}
