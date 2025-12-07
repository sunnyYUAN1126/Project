package com.book.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /images/** URL path to the local directory
        // "file:/" prefix is required for file system paths
        // The path must end with /
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///D:/Project/picture/");
    }
}
