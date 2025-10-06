package in.taxgenie.services.interfaces;

import in.taxgenie.viewmodels.dashboard.DashboardStatsViewModel;
import in.taxgenie.viewmodels.dashboard.ProgressViewModel;
import in.taxgenie.viewmodels.dashboard.UserProfileViewModel;

/**
 * Service interface for dashboard operations
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
public interface IDashboardService {

    /**
     * Get dashboard statistics for a vendor
     *
     * @param vendorId Vendor ID
     * @param oemId OEM ID
     * @param companyCode Company code for multi-tenancy
     * @return Dashboard statistics
     */
    DashboardStatsViewModel getDashboardStats(String vendorId, String oemId, String companyCode);

    /**
     * Get onboarding progress for a vendor
     *
     * @param vendorId Vendor ID
     * @param oemId OEM ID
     * @param companyCode Company code for multi-tenancy
     * @return Onboarding progress details
     */
    ProgressViewModel getOnboardingProgress(String vendorId, String oemId, String companyCode);

    /**
     * Get implementation progress for a vendor
     *
     * @param vendorId Vendor ID
     * @param oemId OEM ID
     * @param companyCode Company code for multi-tenancy
     * @return Implementation progress details
     */
    ProgressViewModel getImplementationProgress(String vendorId, String oemId, String companyCode);

    /**
     * Get user profile information
     * 
     * @param vendorId Vendor ID
     * @param companyCode Company code for multi-tenancy
     * @return User profile details
     */
    UserProfileViewModel getUserProfile(String vendorId, String companyCode);

    /**
     * Update user profile information
     * 
     * @param vendorId Vendor ID
     * @param userProfile Updated user profile
     * @param companyCode Company code for multi-tenancy
     * @return Updated user profile
     */
    UserProfileViewModel updateUserProfile(String vendorId, UserProfileViewModel userProfile, String companyCode);

    /**
     * Get quick actions available for the user
     *
     * @param vendorId Vendor ID
     * @param oemId OEM ID
     * @param companyCode Company code for multi-tenancy
     * @return List of available quick actions
     */
    java.util.List<QuickActionViewModel> getQuickActions(String vendorId, String oemId, String companyCode);

    /**
     * Get recent activities for the dashboard
     *
     * @param vendorId Vendor ID
     * @param oemId OEM ID
     * @param companyCode Company code for multi-tenancy
     * @param limit Number of activities to return
     * @return List of recent activities
     */
    java.util.List<ActivityViewModel> getRecentActivities(String vendorId, String oemId, String companyCode, Integer limit);

    /**
     * Nested class for quick actions
     */
    class QuickActionViewModel {
        private String id;
        private String title;
        private String description;
        private String icon;
        private String actionUrl;
        private String actionType; // navigate, modal, external
        private Boolean isEnabled;
        private String category;

        // Constructors, getters, and setters
        public QuickActionViewModel() {}

        public QuickActionViewModel(String id, String title, String description, String icon, 
                                  String actionUrl, String actionType, Boolean isEnabled, String category) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.actionUrl = actionUrl;
            this.actionType = actionType;
            this.isEnabled = isEnabled;
            this.category = category;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        
        public String getActionUrl() { return actionUrl; }
        public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
        
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        
        public Boolean getIsEnabled() { return isEnabled; }
        public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    /**
     * Nested class for activities
     */
    class ActivityViewModel {
        private String id;
        private String title;
        private String description;
        private String type;
        private java.time.LocalDateTime timestamp;
        private String status;
        private String relatedEntity;
        private String relatedEntityId;

        // Constructors, getters, and setters
        public ActivityViewModel() {}

        public ActivityViewModel(String id, String title, String description, String type,
                               java.time.LocalDateTime timestamp, String status, String relatedEntity, String relatedEntityId) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.type = type;
            this.timestamp = timestamp;
            this.status = status;
            this.relatedEntity = relatedEntity;
            this.relatedEntityId = relatedEntityId;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public java.time.LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getRelatedEntity() { return relatedEntity; }
        public void setRelatedEntity(String relatedEntity) { this.relatedEntity = relatedEntity; }
        
        public String getRelatedEntityId() { return relatedEntityId; }
        public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    }
}
