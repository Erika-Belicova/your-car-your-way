package com.yourcaryourway.backend.configuration.app;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * Defines API metadata, server details, and JWT Bearer authentication scheme.
 */
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact contact = new Contact();
        contact.setName("Erika Belicova");

        Info information = new Info()
                .title("Your Car Your Way API")
                .version("1.0")
                .description("This API exposes endpoints for the Your Car Your Way support chat PoC.")
                .contact(contact);

        return new OpenAPI().info(information)
                .servers(List.of(server))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }

}