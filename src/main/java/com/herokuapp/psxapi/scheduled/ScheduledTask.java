package com.herokuapp.psxapi.scheduled;


import com.herokuapp.psxapi.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {

    private final StockService stockService;

    @Scheduled(cron = "0 45 16 * * MON-FRI", zone = "GMT+8:00")
    public void runSavingStocks(){
        log.info("Running firebase backup..");
        stockService.saveStocksPrice();
    }



}
