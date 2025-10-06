package in.taxgenie.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when JWT token has expired
 */
public class TokenExpiredException extends AuthenticationException {

    private final String token;
    private final long expiredAt;

    public TokenExpiredException(String message) {
        super(message);
        this.token = null;
        this.expiredAt = 0;
    }

    public TokenExpiredException(String message, String token, long expiredAt) {
        super(message);
        this.token = token;
        this.expiredAt = expiredAt;
    }

    public String getToken() {
        return token;
    }

    public long getExpiredAt() {
        return expiredAt;
    }
}

