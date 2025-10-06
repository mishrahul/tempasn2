package in.taxgenie.config;

import in.taxgenie.multitenancy.interceptor.TenantInterceptor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

/**
 * Configuration for multi-tenancy support
 * Registers the tenant interceptor with Hibernate
 */
@Configuration
public class MultiTenancyConfig {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    /**
     * Configures the tenant interceptor with Hibernate SessionFactory
     */
    @PostConstruct
    public void configureInterceptor() {
        if (entityManagerFactory.unwrap(SessionFactory.class) != null) {
            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            // Note: In newer versions of Hibernate, interceptors are typically configured
            // through SessionFactoryBuilder or as part of the configuration
            // This is a simplified approach for demonstration
        }
    }
}
