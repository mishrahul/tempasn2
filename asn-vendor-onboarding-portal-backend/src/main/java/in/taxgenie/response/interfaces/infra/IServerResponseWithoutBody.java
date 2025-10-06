package in.taxgenie.response.interfaces.infra;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import in.taxgenie.response.implementations.infra.ServerResponseWithoutBodyImplementation;

/**
 * Interface for server response without body
 */
@JsonDeserialize(as = ServerResponseWithoutBodyImplementation.class)
public interface IServerResponseWithoutBody {
    
    /**
     * Gets the HTTP response code
     * @return Response code
     */
    int getResponseCode();

    /**
     * Gets the response message
     * @return Response message
     */
    String getMessage();

    /**
     * Checks if the response is successful
     * @return true if successful, false otherwise
     */
    boolean isOk();
}
