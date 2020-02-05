package com.herokuapp.psxapi.scheduled;


import com.herokuapp.psxapi.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {

    private final StockService stockService;

    @PostConstruct
    public void startSavingCompanyInfo(){
        stockService.saveCompanyInfo();
    }

    @Scheduled(cron = "0 45 16 * * MON-FRI", zone = "GMT+8:00")
    public void run(){
        stockService.saveStocksPrice();
    }


}
