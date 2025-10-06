package in.taxgenie.response.implementations.infra;

import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;

/**
 * Implementation of server response with body
 * @param <T> Type of response body
 */
public class ServerResponseWithBodyImplementation<T> extends ServerResponseWithoutBodyImplementation 
        implements IServerResponseWithBody<T> {
    
    private final T body;

    public ServerResponseWithBodyImplementation() {
        super();
        this.body = null;
    }

    public ServerResponseWithBodyImplementation(int responseCode, String message, boolean isOk, T body) {
        super(responseCode, message, isOk);
        this.body = body;
    }

    @Override
    public T getBody() {
        return body;
    }
}
