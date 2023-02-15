package com.example.pj0701.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

@EnableSwagger2 //swagger 2
@EnableOpenApi //swagger 3
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.OAS_30) // swagger 3
                .useDefaultResponseMessages(true)
//                .groupName("group1")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.pj0701"))
                .paths(PathSelectors.any())
                .build();
    }
//    private OpenAPI openAPI(){}
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("swagger test")
                .description("swagger test")
                .version("1.0")
                .build();
    }

}
