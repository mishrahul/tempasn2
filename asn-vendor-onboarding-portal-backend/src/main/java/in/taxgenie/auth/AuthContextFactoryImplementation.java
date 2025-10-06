package in.taxgenie.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of authentication context factory
 * Creates authentication context view models from various sources
 */
@Component
public class AuthContextFactoryImplementation implements IAuthContextFactory {

    private final IJwtFacilities jwtFacilities;

    public AuthContextFactoryImplementation(IJwtFacilities jwtFacilities) {
        this.jwtFacilities = jwtFacilities;
    }

    @Override
    public IAuthContextViewModel getAuthContext(long userId, long companyCode, String email,
                                              String userClientCode, String token, List<String> authorities) {
        return new AuthContextViewModelImplementation(userId, email, userClientCode, 
                                                    companyCode, token, authorities, false);
    }

    @Override
    public IAuthContextViewModel getAuthContext(SecurityContext securityContext) {
        String jwt = (String) securityContext.getAuthentication().getCredentials();
        List<String> authorities = jwtFacilities.getAuthorities(jwt)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        return new AuthContextViewModelImplementation(
                jwtFacilities.getUserId(jwt),
                jwtFacilities.getUserEmail(jwt),
                null,
                jwtFacilities.getCompanyCode(jwt),
                jwt,
                authorities,
                false
        );
    }

    @Override
    public IAuthContextViewModel getCompanyAuthContext(SecurityContext securityContext) {
        String jwt = (String) securityContext.getAuthentication().getCredentials();
        
        if (!jwtFacilities.isCompanyToken(jwt)) {
            throw new InvalidTokenException("Invalid company auth token");
        }
        
        String pan = jwtFacilities.getCompanyPan(jwt);
        Long companyId = jwtFacilities.getCompanyCode(jwt);
        Long senderProductId = jwtFacilities.getSenderProductId(jwt);
        Long receiverProductId = jwtFacilities.getReceiverProductId(jwt);

        return new AuthContextViewModelImplementation(pan, companyId, 
                                                    senderProductId, receiverProductId, true);
    }
}
