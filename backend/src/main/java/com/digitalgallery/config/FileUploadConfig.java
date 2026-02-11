package com.digitalgallery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            
            // Create directory if it doesn't exist
            java.nio.file.Files.createDirectories(uploadPath);
            
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + uploadPath.toString() + "/");
        } catch (Exception e) {
            System.err.println("Warning: Failed to configure file upload handler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
