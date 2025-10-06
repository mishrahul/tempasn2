package in.taxgenie.response.implementations.infra;

import in.taxgenie.response.interfaces.infra.IServerResponseWithoutBody;

/**
 * Implementation of server response without body
 */
public class ServerResponseWithoutBodyImplementation implements IServerResponseWithoutBody {
    
    private final int responseCode;
    private final String message;
    private final boolean isOk;

    public ServerResponseWithoutBodyImplementation() {
        this.responseCode = 200;
        this.message = "";
        this.isOk = true;
    }

    public ServerResponseWithoutBodyImplementation(int responseCode, String message, boolean isOk) {
        this.responseCode = responseCode;
        this.message = message;
        this.isOk = isOk;
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isOk() {
        return isOk;
    }
}
