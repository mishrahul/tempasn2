package in.taxgenie.viewmodels.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard server response view model
 * 
 * @param <T> the type of response data
 * @author ASN Development Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerResponseViewModel<T> {
    
    /**
     * Indicates if the operation was successful
     */
    private boolean success;
    
    /**
     * Response message
     */
    private String message;
    
    /**
     * Response data
     */
    private T data;
    
    /**
     * HTTP response code
     */
    private int responseCode;
    
    /**
     * Timestamp of the response
     */
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
}
