/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.models.Components
 *  io.swagger.v3.oas.models.OpenAPI
 *  io.swagger.v3.oas.models.info.Contact
 *  io.swagger.v3.oas.models.info.Info
 *  io.swagger.v3.oas.models.info.License
 *  io.swagger.v3.oas.models.security.SecurityRequirement
 *  io.swagger.v3.oas.models.security.SecurityScheme
 *  io.swagger.v3.oas.models.security.SecurityScheme$Type
 *  io.swagger.v3.oas.models.servers.Server
 *  io.swagger.v3.oas.models.tags.Tag
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.blood_donor.swagger.config;

import com.blood_donor.swagger.properties.ApiProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private final ApiProperties apiProperties;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(this.buildApiInfo()).servers(this.buildServers()).tags(this.buildTags()).components(this.buildComponents()).addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info buildApiInfo() {
        ApiProperties.Contact contactProp = this.apiProperties.getContact();
        ApiProperties.License licenseProp = this.apiProperties.getLicense();
        return new Info().title(this.apiProperties.getTitle()).version(this.apiProperties.getVersion()).description(this.apiProperties.getDescription()).contact(new Contact().name(contactProp.getName()).email(contactProp.getEmail()).url(contactProp.getUrl())).license(new License().name(licenseProp.getName()).url(licenseProp.getUrl()));
    }

    private List<Server> buildServers() {
        ApiProperties.Server serverProp = this.apiProperties.getServer();
        Server devServer = new Server().url(serverProp.getDevUrl()).description(serverProp.getDevDescription());
        Server prodServer = new Server().url(serverProp.getProdUrl()).description(serverProp.getProdDescription());
        return List.of(devServer, prodServer);
    }

    private List<Tag> buildTags() {
        return List.of(new Tag().name("Authentication").description("Register, Login, JWT token management"), new Tag().name("Donor").description("Donor registration, search, availability management"), new Tag().name("Blood Request").description("Emergency blood request creation and management"), new Tag().name("Admin").description("Admin operations \u2014 user management, analytics"));
    }

    private Components buildComponents() {
        SecurityScheme jwtScheme = new SecurityScheme().name("bearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").description("JWT Authorization. Enter token without 'Bearer ' prefix. Example: eyJhbGciOiJIUzM4NCJ9...");
        return new Components().addSecuritySchemes("bearerAuth", jwtScheme);
    }

    public SwaggerConfig(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }
}

