package in.taxgenie.multitenancy.context;

/**
 * Thread-local context for storing tenant information
 * Provides tenant isolation for multi-tenant applications
 */
public class TenantContext {

    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();
    private static final ThreadLocal<String> currentSchema = new ThreadLocal<>();

    /**
     * Gets the current tenant ID
     * @return Current tenant ID
     */
    public static Long getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Sets the current tenant ID
     * @param tenant Tenant ID to set
     */
    public static void setCurrentTenant(Long tenant) {
        currentTenant.set(tenant);
    }

    /**
     * Sets the current database schema
     * @param schema Schema name to set
     */
    public static void setCurrentSchema(String schema) {
        currentSchema.set(schema);
    }

    /**
     * Gets the current database schema
     * @return Current schema name
     */
    public static String getCurrentSchema() {
        return currentSchema.get();
    }

    /**
     * Clears the tenant context
     * Should be called at the end of request processing
     */
    public static void clear() {
        currentTenant.remove();
        currentSchema.remove();
    }
}
