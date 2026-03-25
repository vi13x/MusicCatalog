package com.example.musiccatalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI musicCatalogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Music Catalog API")
                        .description("REST API for managing artists, albums, tracks, genres, playlists and users.")
                        .version("v1")
                        .contact(new Contact().name("Music Catalog"))
                        .license(new License().name("Internal project")));
    }
}
