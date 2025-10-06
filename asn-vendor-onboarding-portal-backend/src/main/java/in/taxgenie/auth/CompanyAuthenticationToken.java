package in.taxgenie.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

/**
 * Authentication token for company-based authentication
 * Used when authenticating with company tokens
 */
public class CompanyAuthenticationToken extends AbstractAuthenticationToken {

    private final CompanyDetails companyDetails;
    private String token;

    /**
     * Constructor for company authentication token
     * @param companyDetails Company details from JWT
     * @param jwt JWT token
     */
    public CompanyAuthenticationToken(CompanyDetails companyDetails, String jwt) {
        super(Arrays.asList(new SimpleGrantedAuthority("COMPANY")));  // Default authority
        this.companyDetails = companyDetails;
        this.token = jwt;
        setAuthenticated(true);  // Mark as authenticated
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return companyDetails;
    }

    /**
     * Gets the company details
     * @return Company details
     */
    public CompanyDetails getCompanyDetails() {
        return companyDetails;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.token = null;
    }
}
