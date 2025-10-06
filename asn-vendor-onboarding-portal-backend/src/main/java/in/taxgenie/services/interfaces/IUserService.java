package in.taxgenie.services.interfaces;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.viewmodels.user.UserProfileViewModel;

/**
 * Interface for user service operations
 */
public interface IUserService {

    /**
     * Gets user profile information
     * @param auth Authentication context
     * @return User profile view model
     */
    UserProfileViewModel getUserProfile(IAuthContextViewModel auth);

    /**
     * Updates user profile information
     * @param auth Authentication context
     * @param userProfile Updated user profile data
     * @return Updated user profile view model
     */
    UserProfileViewModel updateUserProfile(IAuthContextViewModel auth, UserProfileViewModel userProfile);

    /**
     * Gets user profile image URL
     * @param auth Authentication context
     * @return Profile image URL
     */
    String getProfileImageUrl(IAuthContextViewModel auth);

    /**
     * Validates user token
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Gets user session context
     * @param auth Authentication context
     * @return User session information
     */
    Object getUserSessionContext(IAuthContextViewModel auth);
}
