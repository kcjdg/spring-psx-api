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

    @Scheduled(fixedDelay = 1000 * 60 * 1)
    public void saveCompanyInfo(){
        stockService.saveCompanyInfo();
    }
}
