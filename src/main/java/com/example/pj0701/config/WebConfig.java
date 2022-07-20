package com.example.pj0701.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(basePackages = "com.example.pj0701")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //스태틱 경로
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/static/"};
    //경로 핸들러
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
//                .addResourceLocations(resourcePath);
    };

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/")
//                .allowedOrigins("")
//                .allowedMethods("GET","POST")
//                .maxAge(3000);
//    }->securityConfig
}
