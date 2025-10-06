package in.taxgenie.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Interface for JWT token facilities
 * Provides methods for JWT token validation and claim extraction
 */
public interface IJwtFacilities {

    /**
     * Validates if the JWT token is valid
     * @param token JWT token
     * @return true if valid, false otherwise
     * @throws ExpiredJwtException if token is expired
     * @throws UnsupportedJwtException if token is unsupported
     * @throws MalformedJwtException if token is malformed
     * @throws SignatureException if signature is invalid
     * @throws IllegalArgumentException if token is null or empty
     */
    boolean isValid(String token) throws ExpiredJwtException, UnsupportedJwtException, 
                                       MalformedJwtException, SignatureException, IllegalArgumentException;

    /**
     * Extracts authorities from JWT token
     * @param token JWT token
     * @return List of granted authorities
     */
    List<GrantedAuthority> getAuthorities(String token);

    /**
     * Extracts user email from JWT token
     * @param token JWT token
     * @return User email
     */
    String getUserEmail(String token);

    /**
     * Extracts company PAN from JWT token
     * @param token JWT token
     * @return Company PAN
     */
    String getCompanyPan(String token);

    /**
     * Extracts user ID from JWT token
     * @param token JWT token
     * @return User ID
     */
    long getUserId(String token);

    /**
     * Extracts company code from JWT token
     * @param token JWT token
     * @return Company code
     */
    long getCompanyCode(String token);

    /**
     * Extracts company code from JWT token (nullable version for tenant filtering)
     * @param token JWT token
     * @return Company code or null if not present
     */
    Long getCompanyCodeFromToken(String token);

    /**
     * Extracts sender product ID from JWT token
     * @param token JWT token
     * @return Sender product ID
     */
    Long getSenderProductId(String token);

    /**
     * Extracts receiver product ID from JWT token
     * @param token JWT token
     * @return Receiver product ID
     */
    Long getReceiverProductId(String token);

    /**
     * Checks if the token is a company token
     * @param jwt JWT token
     * @return true if company token, false otherwise
     * @throws ExpiredJwtException if token is expired
     */
    boolean isCompanyToken(String jwt) throws ExpiredJwtException;

    /**
     * Validates if the company token is valid
     * @param token JWT token
     * @return true if valid company token, false otherwise
     */
    boolean isValidCompanyToken(String token);

    /**
     * Gets company details from JWT token
     * @param token JWT token
     * @return Company details
     */
    CompanyDetails getCompanyDetails(String token);
}
