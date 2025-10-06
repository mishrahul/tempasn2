package in.taxgenie.controllers;

import in.taxgenie.auth.IAuthContextFactory;
import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.response.interfaces.factory.IServerResponseFactory;
import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.services.interfaces.IUserService;
import in.taxgenie.services.interfaces.IOemPortalService;
import in.taxgenie.services.UserSessionService;
import in.taxgenie.viewmodels.user.UserProfileViewModel;
import in.taxgenie.viewmodels.oem.SelectOemRequestViewModel;
import in.taxgenie.viewmodels.oem.SelectOemResponseViewModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for user management operations
 */
@RestController
@RequestMapping("/user")
@Tag(name = "User Management", description = "APIs for user profile and session management")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final IUserService userService;
    private final IOemPortalService oemPortalService;
    private final UserSessionService userSessionService;
    private final IAuthContextFactory authContextFactory;
    private final IServerResponseFactory serverResponseFactory;

    public UserController(IUserService userService,
                         IOemPortalService oemPortalService,
                         UserSessionService userSessionService,
                         IAuthContextFactory authContextFactory,
                         IServerResponseFactory serverResponseFactory) {
        this.userService = userService;
        this.oemPortalService = oemPortalService;
        this.userSessionService = userSessionService;
        this.authContextFactory = authContextFactory;
        this.serverResponseFactory = serverResponseFactory;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves the current user's profile information")
    public ResponseEntity<IServerResponseWithBody<UserProfileViewModel>> getUserProfile() {
        logger.info("Getting user profile");
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            UserProfileViewModel userProfile = userService.getUserProfile(auth);
            
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "User profile retrieved successfully", true, userProfile);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting user profile", e);
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get user profile: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates the current user's profile information")
    public ResponseEntity<IServerResponseWithBody<UserProfileViewModel>> updateUserProfile(
            @RequestBody UserProfileViewModel userProfile) {
        logger.info("Updating user profile");
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            UserProfileViewModel updatedProfile = userService.updateUserProfile(auth, userProfile);
            
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "User profile updated successfully", true, updatedProfile);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to update user profile: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/profile/image")
    @Operation(summary = "Get profile image", description = "Retrieves the current user's profile image URL")
    public ResponseEntity<IServerResponseWithBody<String>> getProfileImage() {
        logger.info("Getting user profile image");
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            String imageUrl = userService.getProfileImageUrl(auth);
            
            IServerResponseWithBody<String> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Profile image URL retrieved successfully", true, imageUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting profile image", e);
            IServerResponseWithBody<String> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get profile image: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/session-context")
    @Operation(summary = "Get user session context", description = "Retrieves the current user's session information")
    public ResponseEntity<IServerResponseWithBody<Object>> getUserSessionContext() {
        logger.info("Getting user session context");
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            Object sessionContext = userService.getUserSessionContext(auth);
            
            IServerResponseWithBody<Object> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Session context retrieved successfully", true, sessionContext);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting session context", e);
            IServerResponseWithBody<Object> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get session context: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/select-oem")
    @Operation(summary = "Select OEM", description = "Sets the selected OEM for the user session")
    public ResponseEntity<IServerResponseWithBody<SelectOemResponseViewModel>> selectOem(
            @Valid @RequestBody SelectOemRequestViewModel selectOemRequest) {
        logger.info("Selecting OEM for user session: {}", selectOemRequest.getOemId());

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            SelectOemResponseViewModel response = oemPortalService.selectOem(auth, selectOemRequest);

            // Store selected OEM in session
            if (response.isSuccess()) {
                userSessionService.setSelectedOem(auth, selectOemRequest.getOemId(), selectOemRequest.getOemName());
            }

            IServerResponseWithBody<SelectOemResponseViewModel> serverResponse = serverResponseFactory
                    .getServerResponseWithBody(200, response.getMessage(), response.isSuccess(), response);

            return ResponseEntity.ok(serverResponse);

        } catch (Exception e) {
            logger.error("Error selecting OEM", e);
            SelectOemResponseViewModel errorResponse = SelectOemResponseViewModel.builder()
                    .success(false)
                    .message("Failed to select OEM: " + e.getMessage())
                    .build();

            IServerResponseWithBody<SelectOemResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to select OEM", false, errorResponse);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/refresh-oem-access")
    @Operation(summary = "Refresh OEM access", description = "Refreshes the user's OEM access permissions")
    public ResponseEntity<IServerResponseWithBody<Object>> refreshOemAccess() {
        logger.info("Refreshing OEM access");

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            Object refreshResponse = oemPortalService.refreshOemAccess(auth);

            IServerResponseWithBody<Object> serverResponse = serverResponseFactory
                    .getServerResponseWithBody(200, "Access refreshed successfully", true, refreshResponse);

            return ResponseEntity.ok(serverResponse);

        } catch (Exception e) {
            logger.error("Error refreshing OEM access", e);
            IServerResponseWithBody<Object> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to refresh access: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
