package com.api.reserva.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins(
            "https://techhub-cze9exb3bmh7axhx.brazilsouth-01.azurewebsites.net",
            "http://127.0.0.1:5500"
        )
        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Permite servir CSS/JS diretamente de /templates via /templates/**
    registry.addResourceHandler("/templates/**")
        .addResourceLocations("classpath:/templates/");
    // E os recursos padrão em /static/**
    registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/");
    }
}

