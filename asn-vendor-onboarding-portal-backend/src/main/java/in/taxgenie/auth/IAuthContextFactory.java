package in.taxgenie.auth;

import org.springframework.security.core.context.SecurityContext;

import java.util.List;

/**
 * Factory interface for creating authentication context view models
 * Provides methods to extract authentication information from security context
 */
public interface IAuthContextFactory {

    /**
     * Creates authentication context from individual parameters
     * @param userId User ID
     * @param companyCode Company code for multi-tenancy
     * @param email User email
     * @param userClientCode User client code
     * @param token JWT token
     * @param authorities List of user authorities
     * @return Authentication context view model
     */
    IAuthContextViewModel getAuthContext(long userId, long companyCode, String email, 
                                       String userClientCode, String token, List<String> authorities);

    /**
     * Creates authentication context from Spring Security context
     * @param securityContext Spring Security context
     * @return Authentication context view model
     */
    IAuthContextViewModel getAuthContext(SecurityContext securityContext);

    /**
     * Creates company authentication context for company tokens
     * @param securityContext Spring Security context
     * @return Company authentication context view model
     */
    IAuthContextViewModel getCompanyAuthContext(SecurityContext securityContext);
}
