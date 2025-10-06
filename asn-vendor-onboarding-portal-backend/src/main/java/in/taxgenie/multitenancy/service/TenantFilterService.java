package in.taxgenie.multitenancy.service;

import in.taxgenie.multitenancy.context.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for managing Hibernate tenant filters
 * Automatically applies company_code filtering to all queries
 */
@Service
public class TenantFilterService {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilterService.class);
    private static final String COMPANY_FILTER = "companyFilter";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Enables the company filter for the current session
     * This should be called at the beginning of each request
     */
    public void enableCompanyFilter() {
        Long currentTenant = TenantContext.getCurrentTenant();
        
        if (currentTenant != null) {
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter(COMPANY_FILTER);
            filter.setParameter("companyCode", currentTenant);
            
            logger.debug("Enabled company filter for tenant: {}", currentTenant);
        } else {
            logger.warn("No current tenant found, company filter not enabled");
        }
    }

    /**
     * Disables the company filter for the current session
     * This should be called at the end of each request
     */
    public void disableCompanyFilter() {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.disableFilter(COMPANY_FILTER);
            
            logger.debug("Disabled company filter");
        } catch (Exception e) {
            logger.warn("Error disabling company filter: {}", e.getMessage());
        }
    }

    /**
     * Checks if the company filter is currently enabled
     * @return true if filter is enabled, false otherwise
     */
    public boolean isCompanyFilterEnabled() {
        try {
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.getEnabledFilter(COMPANY_FILTER);
            return filter != null;
        } catch (Exception e) {
            logger.warn("Error checking company filter status: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Temporarily disables the company filter for admin operations
     * Use with caution - this allows access to all tenant data
     * @param operation The operation to execute without tenant filtering
     */
    public void executeWithoutTenantFilter(Runnable operation) {
        boolean wasEnabled = isCompanyFilterEnabled();
        
        try {
            if (wasEnabled) {
                disableCompanyFilter();
                logger.debug("Temporarily disabled company filter for admin operation");
            }
            
            operation.run();
            
        } finally {
            if (wasEnabled) {
                enableCompanyFilter();
                logger.debug("Re-enabled company filter after admin operation");
            }
        }
    }
}
