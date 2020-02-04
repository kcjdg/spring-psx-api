package com.herokuapp.psxapi.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;


@Getter
@Configuration
@Slf4j
public class PseiConfig {

    private String stocksUrl;
    private String companyInfoUrl;
    private String cacheName;
    private String stocksTickerApiName;
    private String stocksCompanyApiName;

    @PostConstruct
    public void setAPIUrls() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(System.getenv("PSEI_CONFIG"), Map.class);
            this.stocksUrl = map.get("default_url");
            this.companyInfoUrl = map.get("company_url");
            this.cacheName = map.get("cache_stocks");
            this.stocksTickerApiName = map.get("get_stocks_api");
            this.stocksCompanyApiName = map.get("get_companyInfo_api");
        } catch (Exception e) {
            log.info("Unable to set the json config");
        }
    }
}
