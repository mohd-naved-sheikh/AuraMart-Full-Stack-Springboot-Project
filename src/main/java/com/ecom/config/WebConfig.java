package com.ecom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String imagePath = System.getProperty("user.dir")
                + "/src/main/resources/static/img/";

        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + imagePath);
    }
}