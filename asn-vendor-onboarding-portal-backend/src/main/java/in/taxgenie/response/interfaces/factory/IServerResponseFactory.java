package in.taxgenie.response.interfaces.factory;

import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.response.interfaces.infra.IServerResponseWithoutBody;

/**
 * Factory interface for creating server responses
 */
public interface IServerResponseFactory {

    /**
     * Creates a server response without body
     * @param responseCode HTTP response code
     * @param message Response message
     * @param isOk Whether the response is successful
     * @return Server response without body
     */
    IServerResponseWithoutBody getServerResponseWithoutBody(int responseCode, String message, boolean isOk);

    /**
     * Creates a server response with body
     * @param responseCode HTTP response code
     * @param message Response message
     * @param isOk Whether the response is successful
     * @param body Response body
     * @param <T> Type of response body
     * @return Server response with body
     */
    <T> IServerResponseWithBody<T> getServerResponseWithBody(int responseCode, String message, boolean isOk, T body);
}
