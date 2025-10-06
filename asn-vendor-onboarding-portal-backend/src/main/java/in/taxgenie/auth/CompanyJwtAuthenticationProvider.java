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
 * Authentication provider for JWT company tokens
 * Validates and authenticates company JWT tokens
 */
@Component
public class CompanyJwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CompanyJwtAuthenticationProvider.class);
    private final IJwtFacilities jwtFacilities;

    /**
     * Constructor
     * @param jwtFacilities JWT facilities for token validation
     */
    public CompanyJwtAuthenticationProvider(IJwtFacilities jwtFacilities) {
        this.jwtFacilities = jwtFacilities;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String token = String.valueOf(authentication.getCredentials());

        try {
            if (jwtFacilities.isCompanyToken(token)) {
                // Validate and retrieve company-specific details
                if (jwtFacilities.isValidCompanyToken(token)) {
                    CompanyDetails companyDetails = jwtFacilities.getCompanyDetails(token);
                    return new CompanyAuthenticationToken(companyDetails, token);
                }
            }
        } catch (ExpiredJwtException exception) {
            logger.warn("Company JWT token expired: {}", exception.getMessage());
            throw new TokenExpiredException(
                "Company JWT token has expired",
                token,
                exception.getClaims().getExpiration().getTime()
            );
        } catch (MalformedJwtException exception) {
            logger.warn("Malformed company JWT token: {}", exception.getMessage());
            throw new BadCredentialsException("Malformed company JWT token");
        } catch (SignatureException exception) {
            logger.warn("Invalid company JWT signature: {}", exception.getMessage());
            throw new BadCredentialsException("Invalid company JWT token signature");
        } catch (UnsupportedJwtException exception) {
            logger.warn("Unsupported company JWT token: {}", exception.getMessage());
            throw new BadCredentialsException("Unsupported company JWT token");
        } catch (IllegalArgumentException exception) {
            logger.warn("Invalid company JWT token: {}", exception.getMessage());
            throw new BadCredentialsException("Invalid company JWT token");
        }

        // Token is not valid or not a company token
        throw new InvalidTokenException("Company authentication failed");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CompanyAuthenticationToken.class.equals(authentication);
    }
}
