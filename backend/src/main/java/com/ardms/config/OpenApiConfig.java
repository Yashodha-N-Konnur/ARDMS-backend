package com.ardms.config;

import io.swagger.v3.oas.models.Components;
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

@Configuration
public class OpenApiConfig {

    @Value("${app.version}")
    private String appVersion;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ARDMS - Automated Release & Deployment Management System API")
                .description("Enterprise-grade REST API for managing software releases, deployments, " +
                    "environments, and rollbacks with full CI/CD integration support.")
                .version(appVersion)
                .contact(new Contact()
                    .name("ARDMS Support Team")
                    .email("support@ardms.company.com")
                    .url("https://ardms.company.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server().url("/api").description("Current server"),
                new Server().url("http://localhost:8080/api").description("Local Development"),
                new Server().url("https://staging.ardms.company.com/api").description("Staging")
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Authorization header. Example: 'Bearer {token}'")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
