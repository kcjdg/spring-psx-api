package com.psxapi.trading.stocks.config.caller;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


@Configuration
public class RestTemplateConfig {

    private static final int CONNECT_TIMEOUT = 30_000;
    private static final int READ_TIMEOUT = 60_000;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate(requestFactory());
        restTemplate.getMessageConverters().add(0,new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(1,messageConverter());
        return restTemplate;
    }

    private BufferingClientHttpRequestFactory requestFactory(){
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        clientHttpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        clientHttpRequestFactory.setReadTimeout(READ_TIMEOUT);
        return new BufferingClientHttpRequestFactory(clientHttpRequestFactory);
    }

    private MappingJackson2HttpMessageConverter messageConverter(){
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_HTML, MediaType.APPLICATION_JSON));
        return mappingJackson2HttpMessageConverter;
    }

}

