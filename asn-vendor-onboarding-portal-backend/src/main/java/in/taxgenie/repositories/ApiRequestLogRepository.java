package in.taxgenie.repositories;

import in.taxgenie.entities.ApiCredential;
import in.taxgenie.entities.ApiRequestLog;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for ApiRequestLog entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface ApiRequestLogRepository extends BaseRepository<ApiRequestLog> {

    /**
     * Find API request logs by credential
     * 
     * @param credential the API credential
     * @return List of API request logs
     */
    List<ApiRequestLog> findByCredential(ApiCredential credential);

    /**
     * Find API request logs by credential ordered by timestamp
     * 
     * @param credential the API credential
     * @return List of API request logs ordered by timestamp descending
     */
    List<ApiRequestLog> findByCredentialOrderByRequestTimestampDesc(ApiCredential credential);

    /**
     * Find API request logs by endpoint
     * 
     * @param endpoint the endpoint
     * @return List of API request logs
     */
    List<ApiRequestLog> findByEndpoint(String endpoint);

    /**
     * Find API request logs by HTTP method
     * 
     * @param method the HTTP method
     * @return List of API request logs
     */
    List<ApiRequestLog> findByMethod(String method);

    /**
     * Find API request logs by status code
     * 
     * @param statusCode the HTTP status code
     * @return List of API request logs
     */
    List<ApiRequestLog> findByStatusCode(Short statusCode);

    /**
     * Find API request logs by error code
     * 
     * @param errorCode the error code
     * @return List of API request logs
     */
    List<ApiRequestLog> findByErrorCode(String errorCode);

    /**
     * Find API request logs after given timestamp
     * 
     * @param timestamp the timestamp threshold
     * @return List of API request logs
     */
    List<ApiRequestLog> findByRequestTimestampAfter(LocalDateTime timestamp);

    /**
     * Find API request logs between timestamps
     * 
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return List of API request logs
     */
    List<ApiRequestLog> findByRequestTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find failed API requests (status code >= 400)
     * 
     * @return List of failed API request logs
     */
    @Query("SELECT arl FROM ApiRequestLog arl WHERE arl.statusCode >= 400")
    List<ApiRequestLog> findFailedRequests();

    /**
     * Find successful API requests (status code < 400)
     * 
     * @return List of successful API request logs
     */
    @Query("SELECT arl FROM ApiRequestLog arl WHERE arl.statusCode < 400")
    List<ApiRequestLog> findSuccessfulRequests();

    /**
     * Find slow API requests (response time above threshold)
     * 
     * @param responseTimeThreshold the response time threshold in milliseconds
     * @return List of slow API request logs
     */
    @Query("SELECT arl FROM ApiRequestLog arl WHERE arl.responseTimeMs > :responseTimeThreshold")
    List<ApiRequestLog> findSlowRequests(@Param("responseTimeThreshold") Integer responseTimeThreshold);

    /**
     * Find API request logs by credential and endpoint
     * 
     * @param credential the API credential
     * @param endpoint the endpoint
     * @return List of API request logs
     */
    List<ApiRequestLog> findByCredentialAndEndpoint(ApiCredential credential, String endpoint);

    /**
     * Find API request logs by credential and status code
     * 
     * @param credential the API credential
     * @param statusCode the HTTP status code
     * @return List of API request logs
     */
    List<ApiRequestLog> findByCredentialAndStatusCode(ApiCredential credential, Short statusCode);

    /**
     * Count API requests by credential
     * 
     * @param credential the API credential
     * @return count of API requests
     */
    long countByCredential(ApiCredential credential);

    /**
     * Count API requests by credential and status code
     * 
     * @param credential the API credential
     * @param statusCode the HTTP status code
     * @return count of API requests
     */
    long countByCredentialAndStatusCode(ApiCredential credential, Short statusCode);

    /**
     * Count API requests by credential in time range
     * 
     * @param credential the API credential
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return count of API requests
     */
    long countByCredentialAndRequestTimestampBetween(ApiCredential credential, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Calculate average response time by credential
     * 
     * @param credential the API credential
     * @return average response time in milliseconds
     */
    @Query("SELECT AVG(arl.responseTimeMs) FROM ApiRequestLog arl WHERE arl.credential = :credential AND arl.responseTimeMs IS NOT NULL")
    Double calculateAverageResponseTimeByCredential(@Param("credential") ApiCredential credential);

    /**
     * Find recent API request logs by credential
     *
     * @param credential the API credential
     * @return List of recent API request logs (limited to 50)
     */
    @Query("SELECT arl FROM ApiRequestLog arl WHERE arl.credential = :credential ORDER BY arl.requestTimestamp DESC LIMIT 50")
    List<ApiRequestLog> findRecentByCredential(@Param("credential") ApiCredential credential);

    /**
     * Find API request logs by multiple endpoints
     * 
     * @param endpoints the list of endpoints
     * @return List of API request logs
     */
    @Query("SELECT arl FROM ApiRequestLog arl WHERE arl.endpoint IN :endpoints")
    List<ApiRequestLog> findByEndpointIn(@Param("endpoints") List<String> endpoints);

    /**
     * Find API request logs with errors
     * 
     * @return List of API request logs with errors
     */
    @Query("SELECT arl FROM ApiRequestLog arl WHERE arl.errorCode IS NOT NULL")
    List<ApiRequestLog> findRequestsWithErrors();

    /**
     * Delete old API request logs before given timestamp
     * 
     * @param timestamp the timestamp threshold
     * @return number of deleted records
     */
    @Query("DELETE FROM ApiRequestLog arl WHERE arl.requestTimestamp < :timestamp")
    int deleteOldLogs(@Param("timestamp") LocalDateTime timestamp);
}
