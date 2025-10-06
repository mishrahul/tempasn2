package in.taxgenie.services;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.multitenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Service for managing user session data including selected OEM
 */
@Service
public class UserSessionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    // In-memory session storage (in production, use Redis or database)
    private final Map<String, UserSessionData> userSessions = new ConcurrentHashMap<>();

    /**
     * Sets the selected OEM for a user session
     * @param auth Authentication context
     * @param oemId Selected OEM ID
     * @param oemName Selected OEM name
     */
    public void setSelectedOem(IAuthContextViewModel auth, String oemId, String oemName) {
        String sessionKey = generateSessionKey(auth);
        
        UserSessionData sessionData = userSessions.computeIfAbsent(sessionKey, k -> new UserSessionData());
        sessionData.setSelectedOemId(oemId);
        sessionData.setSelectedOemName(oemName);
        sessionData.setLastActivity(System.currentTimeMillis());
        
        logger.info("Set selected OEM {} for user {}", oemId, auth.getUserId());
    }

    /**
     * Gets the selected OEM for a user session
     * @param auth Authentication context
     * @return Selected OEM data or null if not set
     */
    public SelectedOemData getSelectedOem(IAuthContextViewModel auth) {
        String sessionKey = generateSessionKey(auth);
        UserSessionData sessionData = userSessions.get(sessionKey);
        
        if (sessionData != null && sessionData.getSelectedOemId() != null) {
            return new SelectedOemData(sessionData.getSelectedOemId(), sessionData.getSelectedOemName());
        }
        
        return null;
    }

    /**
     * Clears the selected OEM for a user session
     * @param auth Authentication context
     */
    public void clearSelectedOem(IAuthContextViewModel auth) {
        String sessionKey = generateSessionKey(auth);
        UserSessionData sessionData = userSessions.get(sessionKey);
        
        if (sessionData != null) {
            sessionData.setSelectedOemId(null);
            sessionData.setSelectedOemName(null);
            logger.info("Cleared selected OEM for user {}", auth.getUserId());
        }
    }

    /**
     * Clears all session data for a user
     * @param auth Authentication context
     */
    public void clearUserSession(IAuthContextViewModel auth) {
        String sessionKey = generateSessionKey(auth);
        userSessions.remove(sessionKey);
        logger.info("Cleared session data for user {}", auth.getUserId());
    }

    /**
     * Checks if user has selected an OEM
     * @param auth Authentication context
     * @return true if OEM is selected, false otherwise
     */
    public boolean hasSelectedOem(IAuthContextViewModel auth) {
        return getSelectedOem(auth) != null;
    }

    /**
     * Updates last activity timestamp for user session
     * @param auth Authentication context
     */
    public void updateLastActivity(IAuthContextViewModel auth) {
        String sessionKey = generateSessionKey(auth);
        UserSessionData sessionData = userSessions.get(sessionKey);
        
        if (sessionData != null) {
            sessionData.setLastActivity(System.currentTimeMillis());
        }
    }

    /**
     * Cleans up expired sessions (should be called periodically)
     * @param maxInactiveTime Maximum inactive time in milliseconds
     */
    public void cleanupExpiredSessions(long maxInactiveTime) {
        long currentTime = System.currentTimeMillis();
        
        userSessions.entrySet().removeIf(entry -> {
            UserSessionData sessionData = entry.getValue();
            boolean isExpired = (currentTime - sessionData.getLastActivity()) > maxInactiveTime;
            
            if (isExpired) {
                logger.info("Removing expired session: {}", entry.getKey());
            }
            
            return isExpired;
        });
    }

    private String generateSessionKey(IAuthContextViewModel auth) {
        // Use tenant context for multi-tenancy
        Long tenantId = TenantContext.getCurrentTenant();
        return String.format("%d:%d", tenantId != null ? tenantId : 0L, auth.getUserId());
    }

    /**
     * Internal class for storing user session data
     */
    private static class UserSessionData {
        private String selectedOemId;
        private String selectedOemName;
        private long lastActivity;

        public UserSessionData() {
            this.lastActivity = System.currentTimeMillis();
        }

        public String getSelectedOemId() {
            return selectedOemId;
        }

        public void setSelectedOemId(String selectedOemId) {
            this.selectedOemId = selectedOemId;
        }

        public String getSelectedOemName() {
            return selectedOemName;
        }

        public void setSelectedOemName(String selectedOemName) {
            this.selectedOemName = selectedOemName;
        }

        public long getLastActivity() {
            return lastActivity;
        }

        public void setLastActivity(long lastActivity) {
            this.lastActivity = lastActivity;
        }
    }

    /**
     * Data class for selected OEM information
     */
    public static class SelectedOemData {
        private final String oemId;
        private final String oemName;

        public SelectedOemData(String oemId, String oemName) {
            this.oemId = oemId;
            this.oemName = oemName;
        }

        public String getOemId() {
            return oemId;
        }

        public String getOemName() {
            return oemName;
        }
    }
}
