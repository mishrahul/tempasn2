package in.taxgenie.context;

import in.taxgenie.multitenancy.context.TenantContext;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

/**
 * Task decorator that preserves tenant context across async operations
 * Ensures multi-tenancy is maintained in background tasks
 */
@Component
public class TenantAwareTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Capture the current tenant context
        Long tenantId = TenantContext.getCurrentTenant();

        return () -> {
            try {
                // Set the tenant context before running the task
                TenantContext.setCurrentTenant(tenantId);
                runnable.run();
            } finally {
                // Clear the tenant context after execution
                TenantContext.clear();
            }
        };
    }
}
