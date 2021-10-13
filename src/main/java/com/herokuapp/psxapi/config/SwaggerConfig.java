package com.herokuapp.psxapi.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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


    private static final String VERSION = "1.0";
    public static final Contact DEFAULT_CONTACT = new Contact("Ken de Guzman", "https://github.com/kencrisjohn/spring-psx-api", "kcjdeguzman@gmail.com");
    public static final ApiInfo DEFAULT = new ApiInfo("PSX API", "An API for Philippine Stock Exchange (PSE)", VERSION, "",DEFAULT_CONTACT, "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<>());

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT)
                .groupName("spring-pxs-api-"+VERSION)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("com.herokuapp.psxapi.controller"))
                    .paths(PathSelectors.regex("/v1.*"))
                .build();
    }
}
