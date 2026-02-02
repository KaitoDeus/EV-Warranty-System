package com.oem.evwarranty.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI evWarrantyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EV Warranty Management System API")
                        .description(
                                "Comprehensive API documentation for the OEM Electric Vehicle Warranty Management System. "
                                        +
                                        "This system manages warranties, claims, vehicle inventory, and customer relations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("OEM EV Warranty Support")
                                .email("support@oem-ev.com")
                                .url("https://oem-ev.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
