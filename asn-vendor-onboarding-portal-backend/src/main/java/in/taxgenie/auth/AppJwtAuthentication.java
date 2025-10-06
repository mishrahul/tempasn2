package in.taxgenie.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom Authentication implementation for JWT tokens
 * Represents an authenticated user with JWT token
 */
public class AppJwtAuthentication implements Authentication {

    private String jwt;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean authenticated;

    /**
     * Constructor for unauthenticated token
     * @param jwt JWT token
     */
    public AppJwtAuthentication(String jwt) {
        this.jwt = jwt;
    }

    /**
     * Constructor for authenticated token
     * @param username User email/username
     * @param authorities User authorities
     * @param jwt JWT token
     */
    public AppJwtAuthentication(String username, Collection<? extends GrantedAuthority> authorities, String jwt) {
        this.username = username;
        this.authorities = authorities;
        this.jwt = jwt;
        this.setAuthenticated(true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return this.username;
    }

    /**
     * Gets the JWT token
     * @return JWT token
     */
    public String getJwt() {
        return jwt;
    }

    /**
     * Sets the JWT token
     * @param jwt JWT token
     */
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
