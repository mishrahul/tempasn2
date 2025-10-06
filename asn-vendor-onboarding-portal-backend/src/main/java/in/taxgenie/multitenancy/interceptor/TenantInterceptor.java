package in.taxgenie.multitenancy.interceptor;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.multitenancy.context.TenantContext;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Hibernate interceptor for automatic tenant isolation
 * Automatically sets company_code for all entities on save/update
 */
@Component
public class TenantInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) 
            throws CallbackException {
        
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            Long currentTenant = TenantContext.getCurrentTenant();
            
            if (currentTenant != null && baseEntity.getCompanyCode() == null) {
                baseEntity.setCompanyCode(currentTenant);
                
                // Update the state array to reflect the change
                for (int i = 0; i < propertyNames.length; i++) {
                    if ("companyCode".equals(propertyNames[i])) {
                        state[i] = currentTenant;
                        logger.debug("Set company_code {} for entity {} on save", currentTenant, entity.getClass().getSimpleName());
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, 
                               String[] propertyNames, Type[] types) throws CallbackException {
        
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            Long currentTenant = TenantContext.getCurrentTenant();
            
            if (currentTenant != null && baseEntity.getCompanyCode() == null) {
                baseEntity.setCompanyCode(currentTenant);
                
                // Update the current state array to reflect the change
                for (int i = 0; i < propertyNames.length; i++) {
                    if ("companyCode".equals(propertyNames[i])) {
                        currentState[i] = currentTenant;
                        logger.debug("Set company_code {} for entity {} on update", currentTenant, entity.getClass().getSimpleName());
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) 
            throws CallbackException {
        
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            Long currentTenant = TenantContext.getCurrentTenant();
            
            if (currentTenant != null && !currentTenant.equals(baseEntity.getCompanyCode())) {
                logger.warn("Attempting to delete entity {} with different company_code. Current: {}, Entity: {}", 
                           entity.getClass().getSimpleName(), currentTenant, baseEntity.getCompanyCode());
                throw new CallbackException("Cannot delete entity from different tenant");
            }
        }
    }
}
