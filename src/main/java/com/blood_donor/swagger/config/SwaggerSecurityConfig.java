package com.blood_donor.swagger.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name="bearerAuth", type=SecuritySchemeType.HTTP, scheme="bearer", bearerFormat="JWT", description="JWT Authorization. Enter token without 'Bearer ' prefix. Example: eyJhbGciOiJIUzM4NCJ9...")
public class SwaggerSecurityConfig {
}

