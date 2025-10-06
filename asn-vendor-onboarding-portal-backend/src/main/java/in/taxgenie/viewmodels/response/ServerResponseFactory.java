package in.taxgenie.viewmodels.response;

/**
 * Utility factory for creating standardized server responses
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
public class ServerResponseFactory {

    /**
     * Creates a successful response with data
     * 
     * @param data the response data
     * @param message the success message
     * @param <T> the type of response data
     * @return ServerResponseViewModel with success status
     */
    public static <T> ServerResponseViewModel<T> success(T data, String message) {
        return ServerResponseViewModel.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .responseCode(200)
                .build();
    }

    /**
     * Creates a successful response without data
     * 
     * @param message the success message
     * @return ServerResponseViewModel with success status
     */
    public static ServerResponseViewModel<Void> success(String message) {
        return ServerResponseViewModel.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .responseCode(200)
                .build();
    }

    /**
     * Creates an error response
     * 
     * @param message the error message
     * @return ServerResponseViewModel with error status
     */
    public static <T> ServerResponseViewModel<T> error(String message) {
        return ServerResponseViewModel.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .responseCode(500)
                .build();
    }

    /**
     * Creates an error response with specific error code
     * 
     * @param message the error message
     * @param responseCode the HTTP response code
     * @return ServerResponseViewModel with error status
     */
    public static <T> ServerResponseViewModel<T> error(String message, int responseCode) {
        return ServerResponseViewModel.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .responseCode(responseCode)
                .build();
    }

    /**
     * Creates a validation error response
     * 
     * @param message the validation error message
     * @return ServerResponseViewModel with validation error status
     */
    public static <T> ServerResponseViewModel<T> validationError(String message) {
        return ServerResponseViewModel.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .responseCode(400)
                .build();
    }

    /**
     * Creates a not found error response
     * 
     * @param message the not found message
     * @return ServerResponseViewModel with not found status
     */
    public static <T> ServerResponseViewModel<T> notFound(String message) {
        return ServerResponseViewModel.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .responseCode(404)
                .build();
    }

    /**
     * Creates an unauthorized error response
     * 
     * @param message the unauthorized message
     * @return ServerResponseViewModel with unauthorized status
     */
    public static <T> ServerResponseViewModel<T> unauthorized(String message) {
        return ServerResponseViewModel.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .responseCode(401)
                .build();
    }

    /**
     * Creates a forbidden error response
     * 
     * @param message the forbidden message
     * @return ServerResponseViewModel with forbidden status
     */
    public static <T> ServerResponseViewModel<T> forbidden(String message) {
        return ServerResponseViewModel.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .responseCode(403)
                .build();
    }
}
