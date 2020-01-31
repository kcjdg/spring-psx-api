package com.herokuapp.psxapi.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "psei.config")
public class PseiProps {

    private String url;

}
