package in.taxgenie.auth;

import in.taxgenie.exception.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Authentication provider for JWT user tokens
 * Validates and authenticates user JWT tokens
 */
@Component
public class AppJwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(AppJwtAuthenticationProvider.class);
    private final IJwtFacilities jwtFacilities;

    /**
     * Constructor
     * @param jwtFacilities JWT facilities for token validation
     */
    public AppJwtAuthenticationProvider(IJwtFacilities jwtFacilities) {
        this.jwtFacilities = jwtFacilities;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = String.valueOf(authentication.getCredentials());

        try {
            if (jwtFacilities.isValid(token)) {
                return new AppJwtAuthentication(
                    jwtFacilities.getUserEmail(token),
                    jwtFacilities.getAuthorities(token),
                    token
                );
            }
        } catch (ExpiredJwtException exception) {
            logger.warn("JWT token expired: {}", exception.getMessage());
            throw new TokenExpiredException(
                "JWT token has expired",
                token,
                exception.getClaims().getExpiration().getTime()
            );
        } catch (MalformedJwtException exception) {
            logger.warn("Malformed JWT token: {}", exception.getMessage());
            throw new BadCredentialsException("Malformed JWT token");
        } catch (SignatureException exception) {
            logger.warn("Invalid JWT signature: {}", exception.getMessage());
            throw new BadCredentialsException("Invalid JWT token signature");
        } catch (UnsupportedJwtException exception) {
            logger.warn("Unsupported JWT token: {}", exception.getMessage());
            throw new BadCredentialsException("Unsupported JWT token");
        } catch (IllegalArgumentException exception) {
            logger.warn("Invalid JWT token: {}", exception.getMessage());
            throw new BadCredentialsException("Invalid JWT token");
        }

        // There is an edge case where token is valid, but user might be blocked
        throw new BadCredentialsException("Login failed");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AppJwtAuthentication.class.equals(authentication);
    }
}
