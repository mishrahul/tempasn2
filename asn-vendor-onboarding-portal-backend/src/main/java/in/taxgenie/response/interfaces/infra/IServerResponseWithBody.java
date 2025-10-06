package in.taxgenie.response.interfaces.infra;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import in.taxgenie.response.implementations.infra.ServerResponseWithBodyImplementation;

/**
 * Interface for server response with body
 * @param <T> Type of response body
 */
@JsonDeserialize(as = ServerResponseWithBodyImplementation.class)
public interface IServerResponseWithBody<T> extends IServerResponseWithoutBody {
    
    /**
     * Gets the response body
     * @return Response body
     */
    T getBody();
}
