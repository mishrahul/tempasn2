package in.taxgenie.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Custom authentication manager for JWT tokens
 * Routes authentication to appropriate provider based on token type
 */
@Component
public class AppJwtAuthenticationManager implements AuthenticationManager {

    private final AppJwtAuthenticationProvider jwtAuthenticationProvider;
    private final CompanyJwtAuthenticationProvider companyJwtAuthenticationProvider;

    /**
     * Constructor
     * @param jwtAuthenticationProvider User JWT authentication provider
     * @param companyJwtAuthenticationProvider Company JWT authentication provider
     */
    public AppJwtAuthenticationManager(AppJwtAuthenticationProvider jwtAuthenticationProvider, 
                                     CompanyJwtAuthenticationProvider companyJwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.companyJwtAuthenticationProvider = companyJwtAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication instanceof CompanyAuthenticationToken) {
            // If it's a company token, use CompanyJwtAuthenticationProvider
            return companyJwtAuthenticationProvider.authenticate(authentication);
        } else if (authentication instanceof AppJwtAuthentication) {
            // If it's a user token, use AppJwtAuthenticationProvider
            return jwtAuthenticationProvider.authenticate(authentication);
        } else {
            throw new BadCredentialsException("Unsupported authentication type");
        }
    }
}
