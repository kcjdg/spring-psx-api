package com.herokuapp.psxapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final Contact DEFAULT_CONTACT = new Contact("Ken de Guzman", "https://github.com/kencrisjohn/spring-psx-api", "kcjdeguzman@gmail.co,");
    public static final ApiInfo DEFAULT = new ApiInfo("PSX API", "An API for Philippine Stock Exchange (PSE)", "1.0", "",DEFAULT_CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<>());

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.herokuapp.psxapi.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
