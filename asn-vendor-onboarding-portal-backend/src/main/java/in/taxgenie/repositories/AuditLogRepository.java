package in.taxgenie.repositories;

import in.taxgenie.entities.AuditLog;
import in.taxgenie.entities.enums.ActorType;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for AuditLog entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface AuditLogRepository extends BaseRepository<AuditLog> {

    /**
     * Find audit logs by actor ID
     * 
     * @param actorId the actor ID
     * @return List of audit logs
     */
    List<AuditLog> findByActorId(UUID actorId);

    /**
     * Find audit logs by actor type
     * 
     * @param actorType the actor type
     * @return List of audit logs
     */
    List<AuditLog> findByActorType(ActorType actorType);

    /**
     * Find audit logs by event category
     * 
     * @param eventCategory the event category
     * @return List of audit logs
     */
    List<AuditLog> findByEventCategory(String eventCategory);

    /**
     * Find audit logs by event action
     * 
     * @param eventAction the event action
     * @return List of audit logs
     */
    List<AuditLog> findByEventAction(String eventAction);

    /**
     * Find audit logs by resource type
     * 
     * @param resourceType the resource type
     * @return List of audit logs
     */
    List<AuditLog> findByResourceType(String resourceType);

    /**
     * Find audit logs by resource ID
     * 
     * @param resourceId the resource ID
     * @return List of audit logs
     */
    List<AuditLog> findByResourceId(UUID resourceId);

    /**
     * Find audit logs by resource type and resource ID
     * 
     * @param resourceType the resource type
     * @param resourceId the resource ID
     * @return List of audit logs
     */
    List<AuditLog> findByResourceTypeAndResourceId(String resourceType, UUID resourceId);

    /**
     * Find audit logs by IP address
     * 
     * @param ipAddress the IP address
     * @return List of audit logs
     */
    List<AuditLog> findByIpAddress(InetAddress ipAddress);

    /**
     * Find audit logs by session ID
     * 
     * @param sessionId the session ID
     * @return List of audit logs
     */
    List<AuditLog> findBySessionId(String sessionId);

    /**
     * Find audit logs by request ID
     * 
     * @param requestId the request ID
     * @return List of audit logs
     */
    List<AuditLog> findByRequestId(String requestId);

    /**
     * Find audit logs after given timestamp
     * 
     * @param timestamp the timestamp threshold
     * @return List of audit logs
     */
    List<AuditLog> findByEventTimeAfter(LocalDateTime timestamp);

    /**
     * Find audit logs between timestamps
     * 
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return List of audit logs
     */
    List<AuditLog> findByEventTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find audit logs by actor ID and event category
     * 
     * @param actorId the actor ID
     * @param eventCategory the event category
     * @return List of audit logs
     */
    List<AuditLog> findByActorIdAndEventCategory(UUID actorId, String eventCategory);

    /**
     * Find audit logs by actor ID and resource type
     * 
     * @param actorId the actor ID
     * @param resourceType the resource type
     * @return List of audit logs
     */
    List<AuditLog> findByActorIdAndResourceType(UUID actorId, String resourceType);

    /**
     * Find audit logs by event category and action
     * 
     * @param eventCategory the event category
     * @param eventAction the event action
     * @return List of audit logs
     */
    List<AuditLog> findByEventCategoryAndEventAction(String eventCategory, String eventAction);

    /**
     * Find recent audit logs by actor ID
     *
     * @param actorId the actor ID
     * @return List of recent audit logs (limited to 50)
     */
    @Query("SELECT al FROM AuditLog al WHERE al.actorId = :actorId ORDER BY al.eventTime DESC LIMIT 50")
    List<AuditLog> findRecentByActorId(@Param("actorId") UUID actorId);

    /**
     * Find recent audit logs by resource
     *
     * @param resourceType the resource type
     * @param resourceId the resource ID
     * @return List of recent audit logs (limited to 50)
     */
    @Query("SELECT al FROM AuditLog al WHERE al.resourceType = :resourceType AND al.resourceId = :resourceId ORDER BY al.eventTime DESC LIMIT 50")
    List<AuditLog> findRecentByResource(@Param("resourceType") String resourceType,
                                       @Param("resourceId") UUID resourceId);

    /**
     * Find audit logs by multiple event categories
     * 
     * @param eventCategories the list of event categories
     * @return List of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.eventCategory IN :eventCategories ORDER BY al.eventTime DESC")
    List<AuditLog> findByEventCategoryIn(@Param("eventCategories") List<String> eventCategories);

    /**
     * Find audit logs by multiple event actions
     * 
     * @param eventActions the list of event actions
     * @return List of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.eventAction IN :eventActions ORDER BY al.eventTime DESC")
    List<AuditLog> findByEventActionIn(@Param("eventActions") List<String> eventActions);

    /**
     * Count audit logs by actor ID
     * 
     * @param actorId the actor ID
     * @return count of audit logs
     */
    long countByActorId(UUID actorId);

    /**
     * Count audit logs by event category
     * 
     * @param eventCategory the event category
     * @return count of audit logs
     */
    long countByEventCategory(String eventCategory);

    /**
     * Count audit logs by resource type
     * 
     * @param resourceType the resource type
     * @return count of audit logs
     */
    long countByResourceType(String resourceType);

    /**
     * Count audit logs by actor ID in time range
     * 
     * @param actorId the actor ID
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return count of audit logs
     */
    long countByActorIdAndEventTimeBetween(UUID actorId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Delete old audit logs before given timestamp
     * 
     * @param timestamp the timestamp threshold
     * @return number of deleted records
     */
    @Query("DELETE FROM AuditLog al WHERE al.eventTime < :timestamp")
    int deleteOldLogs(@Param("timestamp") LocalDateTime timestamp);

    /**
     * Find suspicious activity (multiple failed attempts from same IP)
     * 
     * @param ipAddress the IP address
     * @param eventCategory the event category (e.g., "AUTHENTICATION")
     * @param eventAction the event action (e.g., "LOGIN_FAILED")
     * @param timeThreshold the time threshold
     * @return List of suspicious audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.ipAddress = :ipAddress AND al.eventCategory = :eventCategory AND al.eventAction = :eventAction AND al.eventTime >= :timeThreshold ORDER BY al.eventTime DESC")
    List<AuditLog> findSuspiciousActivity(@Param("ipAddress") InetAddress ipAddress, 
                                         @Param("eventCategory") String eventCategory, 
                                         @Param("eventAction") String eventAction, 
                                         @Param("timeThreshold") LocalDateTime timeThreshold);
}
