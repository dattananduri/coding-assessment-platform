package com.assessment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.audio-dir:./data/recordings}")
    private String audioStorageDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = Paths.get(audioStorageDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/recordings/**")
                .addResourceLocations(path.endsWith("/") ? path : path + "/");
    }
}
