package in.taxgenie.auth;

import java.util.List;

/**
 * Interface for authentication context view model
 * Provides access to authenticated user information and context
 */
public interface IAuthContextViewModel {

    /**
     * Gets the user ID
     * @return User ID
     */
    long getUserId();

    /**
     * Gets the user email
     * @return User email
     */
    String getUserEmail();

    /**
     * Gets the company code for multi-tenancy
     * @return Company code
     */
    long getCompanyCode();

    /**
     * Gets the JWT token
     * @return JWT token
     */
    String getToken();

    /**
     * Gets the user authorities/roles
     * @return List of authorities
     */
    List<String> getAuthorities();

    /**
     * Gets the user client code
     * @return User client code
     */
    String getUserClientCode();

    /**
     * Checks if this is a company token
     * @return true if company token, false otherwise
     */
    boolean isCompanyToken();

    /**
     * Gets the company PAN (for company tokens)
     * @return Company PAN
     */
    String getCompanyPan();

    /**
     * Gets the sender product ID
     * @return Sender product ID
     */
    Long getSenderProductId();

    /**
     * Gets the receiver product ID
     * @return Receiver product ID
     */
    Long getReceiverProductId();
}
