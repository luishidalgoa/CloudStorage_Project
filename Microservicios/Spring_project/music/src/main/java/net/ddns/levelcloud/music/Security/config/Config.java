package net.ddns.levelcloud.music.Security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Config implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permitir solicitudes CORS desde localhost:5500
        registry.addMapping("/api/**")
                .allowedOrigins("*")  // Origen permitido
                .allowedMethods("*") // Métodos permitidos
                .allowedHeaders("*"); // Encabezados permitidos

    }
}
