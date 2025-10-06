package in.taxgenie.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for ASN Vendor Onboarding Portal
 * Provides Swagger documentation and API specification
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + contextPath)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.asnvendorportal.com/api/v1")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("ASN Vendor Onboarding Portal API")
                .description("REST API for ASN Vendor Onboarding Portal - A comprehensive platform for vendor onboarding and OEM integration")
                .version("1.0.0")
                .contact(new Contact()
                        .name("TaxGenie Support")
                        .email("support@taxgenie.in")
                        .url("https://taxgenie.in"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://taxgenie.in/license"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("JWT token for authentication. Format: Bearer {token}");
    }
}
