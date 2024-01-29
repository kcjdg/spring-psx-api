package com.psxapi.trading.stocks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.herokuapp.psxapi"})
public class SpringPsxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPsxApiApplication.class, args);
    }

}
