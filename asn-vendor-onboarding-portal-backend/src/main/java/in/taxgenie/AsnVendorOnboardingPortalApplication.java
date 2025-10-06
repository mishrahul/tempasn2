package in.taxgenie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application class for ASN Vendor Onboarding Portal
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@EnableAspectJAutoProxy
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
@EntityScan(basePackages = {"in.taxgenie.entities"})
@EnableJpaRepositories(basePackages = {"in.taxgenie.repositories"})
public class AsnVendorOnboardingPortalApplication extends SpringBootServletInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(AsnVendorOnboardingPortalApplication.class, args);
    }
}
