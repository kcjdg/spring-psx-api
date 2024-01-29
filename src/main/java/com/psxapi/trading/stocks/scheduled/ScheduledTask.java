package com.psxapi.trading.stocks.scheduled;


import com.psxapi.trading.stocks.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTask {

    private final StockService stockService;


    @Scheduled(cron = "0 00 15 * * MON-FRI", zone = "GMT+8:00")
    public void preCloseRun(){
        log.info("Run on market pre close");
        stockService.saveStocksPrice();
    }


    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "GMT+8:00")
    public void marketClosedRun(){
        log.info("Run on market closed");
        stockService.saveStocksPrice();
    }



}
