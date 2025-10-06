package in.taxgenie.controllers;

import in.taxgenie.auth.IAuthContextFactory;
import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.response.interfaces.factory.IServerResponseFactory;
import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.services.interfaces.IDashboardService;
import in.taxgenie.viewmodels.dashboard.DashboardStatsViewModel;
import in.taxgenie.viewmodels.dashboard.ProgressViewModel;
import in.taxgenie.viewmodels.dashboard.UserProfileViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for dashboard operations
 *
 * @author ASN Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @Autowired
    private IAuthContextFactory authContextFactory;

    @Autowired
    private IServerResponseFactory serverResponseFactory;

    /**
     * Test endpoint to verify dashboard APIs are working
     */
    @GetMapping("/test")
    public ResponseEntity<String> testDashboardApi() {
        log.info("Testing dashboard API - method called");
        try {
            String response = "Dashboard API is working! All endpoints are available.";
            log.info("Returning response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in test endpoint", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats/{id}")
    public ResponseEntity<IServerResponseWithBody<DashboardStatsViewModel>> getDashboardStats(@PathVariable String id) {
        log.info("Getting dashboard stats for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            DashboardStatsViewModel stats = dashboardService.getDashboardStats(
                String.valueOf(auth.getUserId()),
                id,
                String.valueOf(auth.getCompanyCode())
            );

            log.info("Successfully retrieved dashboard stats for OEM ID: {}", id);
            IServerResponseWithBody<DashboardStatsViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Dashboard stats retrieved successfully", true, stats);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting dashboard stats for OEM ID: {}", id, e);
            IServerResponseWithBody<DashboardStatsViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get dashboard stats: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get onboarding progress
     */
    @GetMapping("/onboarding-progress/{id}")
    public ResponseEntity<IServerResponseWithBody<ProgressViewModel>> getOnboardingProgress(@PathVariable String id) {
        log.info("Getting onboarding progress for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            ProgressViewModel progress = dashboardService.getOnboardingProgress(
                String.valueOf(auth.getUserId()),
                id,
                String.valueOf(auth.getCompanyCode())
            );

            log.info("Successfully retrieved onboarding progress for OEM ID: {}", id);
            IServerResponseWithBody<ProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Onboarding progress retrieved successfully", true, progress);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting onboarding progress for OEM ID: {}", id, e);
            IServerResponseWithBody<ProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get onboarding progress: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get implementation progress
     */
    @GetMapping("/implementation-progress/{id}")
    public ResponseEntity<IServerResponseWithBody<ProgressViewModel>> getImplementationProgress(@PathVariable String id) {
        log.info("Getting implementation progress for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            ProgressViewModel progress = dashboardService.getImplementationProgress(
                String.valueOf(auth.getUserId()),
                id,
                String.valueOf(auth.getCompanyCode())
            );

            log.info("Successfully retrieved implementation progress for OEM ID: {}", id);
            IServerResponseWithBody<ProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Implementation progress retrieved successfully", true, progress);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting implementation progress for OEM ID: {}", id, e);
            IServerResponseWithBody<ProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get implementation progress: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<IServerResponseWithBody<UserProfileViewModel>> getUserProfile() {
        log.info("Getting user profile");

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            UserProfileViewModel profile = dashboardService.getUserProfile(
                String.valueOf(auth.getUserId()),
                String.valueOf(auth.getCompanyCode())
            );

            log.info("Successfully retrieved user profile");
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "User profile retrieved successfully", true, profile);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting user profile", e);
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get user profile: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<IServerResponseWithBody<UserProfileViewModel>> updateUserProfile(@Valid @RequestBody UserProfileViewModel userProfile) {
        log.info("Updating user profile");

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            UserProfileViewModel updatedProfile = dashboardService.updateUserProfile(
                String.valueOf(auth.getUserId()),
                userProfile,
                String.valueOf(auth.getCompanyCode())
            );

            log.info("Successfully updated user profile");
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "User profile updated successfully", true, updatedProfile);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating user profile", e);
            IServerResponseWithBody<UserProfileViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to update user profile: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get quick actions
     */
    @GetMapping("/quick-actions/{id}")
    public ResponseEntity<IServerResponseWithBody<List<IDashboardService.QuickActionViewModel>>> getQuickActions(@PathVariable String id) {
        log.info("Getting quick actions for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            List<IDashboardService.QuickActionViewModel> actions = dashboardService.getQuickActions(
                String.valueOf(auth.getUserId()),
                id,
                String.valueOf(auth.getCompanyCode())
            );

            log.info("Successfully retrieved quick actions for OEM ID: {}", id);
            IServerResponseWithBody<List<IDashboardService.QuickActionViewModel>> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Quick actions retrieved successfully", true, actions);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting quick actions for OEM ID: {}", id, e);
            IServerResponseWithBody<List<IDashboardService.QuickActionViewModel>> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get quick actions: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get recent activities
     */
    @GetMapping("/activities/{id}")
    public ResponseEntity<IServerResponseWithBody<List<IDashboardService.ActivityViewModel>>> getRecentActivities(
            @PathVariable String id,
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("Getting recent activities for OEM ID: {}, limit: {}", id, limit);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());

            List<IDashboardService.ActivityViewModel> activities = dashboardService.getRecentActivities(
                String.valueOf(auth.getUserId()),
                id,
                String.valueOf(auth.getCompanyCode()),
                limit
            );

            log.info("Successfully retrieved recent activities for OEM ID: {}", id);
            IServerResponseWithBody<List<IDashboardService.ActivityViewModel>> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Recent activities retrieved successfully", true, activities);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting recent activities for OEM ID: {}", id, e);
            IServerResponseWithBody<List<IDashboardService.ActivityViewModel>> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get recent activities: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
