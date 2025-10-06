package in.taxgenie.response.implementations.factory;

import in.taxgenie.response.implementations.infra.ServerResponseWithBodyImplementation;
import in.taxgenie.response.implementations.infra.ServerResponseWithoutBodyImplementation;
import in.taxgenie.response.interfaces.factory.IServerResponseFactory;
import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.response.interfaces.infra.IServerResponseWithoutBody;
import org.springframework.stereotype.Component;

/**
 * Implementation of server response factory
 */
@Component
public class ServerResponseFactoryImplementation implements IServerResponseFactory {

    @Override
    public IServerResponseWithoutBody getServerResponseWithoutBody(int responseCode, String message, boolean isOk) {
        return new ServerResponseWithoutBodyImplementation(responseCode, message, isOk);
    }

    @Override
    public <T> IServerResponseWithBody<T> getServerResponseWithBody(int responseCode, String message, boolean isOk, T body) {
        return new ServerResponseWithBodyImplementation<>(responseCode, message, isOk, body);
    }
}
