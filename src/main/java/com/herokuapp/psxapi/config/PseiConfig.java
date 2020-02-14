package com.herokuapp.psxapi.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.Arrays;
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
    private String companyPriceApiName;
    private String firebaseToken;
    private String firebaseApi;

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
            this.companyPriceApiName = map.get("get_companyPrice_api");
            this.firebaseApi = map.get("firebase_api");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Unable to set the json config " +e.getStackTrace());
        }
    }


    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void refreshFirebaseAuth(){
        try {
            FileInputStream serviceAccount = new FileInputStream("firebaseauth.json");
            // Authenticate a Google credential with the service account
            GoogleCredential googleCred = GoogleCredential.fromStream(serviceAccount);
            // Add the required scopes to the Google credential
            GoogleCredential scoped = googleCred.createScoped(
                    Arrays.asList(
                            "https://www.googleapis.com/auth/firebase.database",
                            "https://www.googleapis.com/auth/userinfo.email"
                    )
            );
            // Use the Google credential to generate an access token
            scoped.refreshToken();
            firebaseToken = scoped.getAccessToken();
            log.info("generated new token.. {}", firebaseToken);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
