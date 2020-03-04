package com.herokuapp.psxapi.controller;

import com.herokuapp.psxapi.helper.LocalDateUtils;
import com.herokuapp.psxapi.helper.StockNotFoundException;
import com.herokuapp.psxapi.model.dao.StockPrice;
import com.herokuapp.psxapi.model.dto.StocksDto;
import com.herokuapp.psxapi.model.dto.StocksWrapper;
import com.herokuapp.psxapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {

    private final StockService stockService;

    @GetMapping("")
    public StocksWrapper getStocks(@RequestParam(required = false) String... symbols) {
        List<StocksDto> allStocks = stockService.getAllStocks();
        if (!ArrayUtils.isEmpty(symbols)) {
            return wrapResults(allStocks.stream()
                    .filter(e -> StringUtils.equalsIgnoreCase(e.getPrice(), "date") || Arrays.stream(symbols).anyMatch(e.getSymbol()::equalsIgnoreCase))
                    .collect(Collectors.toList()));
        }
        return wrapResults(allStocks);
    }

    @GetMapping("/{symbol}")
    public StocksWrapper findStock(@PathVariable String symbol) {
        List<StocksDto> allStocks = stockService.getAllStocks();
        return wrapResults(allStocks.stream().
                filter(e -> StringUtils.equalsIgnoreCase(e.getPrice(), "date") || StringUtils.equalsAnyIgnoreCase(e.getSymbol(), symbol)).
                collect(Collectors.toList()));
    }


    @GetMapping("/{symbol}/details")
    public StocksWrapper findStockWithDetails(@PathVariable String symbol) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper();
        StockPrice stockPrice = stockService.findStockDetails(symbol.toUpperCase()).orElseThrow(StockNotFoundException::new);
        stocksWrapper.setStocks(Collections.singletonList(stockPrice));
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping("by/{date}")
    public StocksWrapper findStocksByDate(@PathVariable String date) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper();
        List<StockPrice> firebaseData = stockService.getFirebaseData(date);
        stocksWrapper.setStocks(firebaseData);
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping("by/{date}/{symbol}")
    public StocksWrapper findStocksByDateAndSymbol(@PathVariable String date, @PathVariable String symbol) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper();
        List<StockPrice> firebaseData = stockService.getFirebaseData(date).stream().filter(stks->StringUtils.equalsIgnoreCase(symbol,stks.getSymbol())).collect(Collectors.toList());
        stocksWrapper.setStocks(firebaseData);
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping({"by/{date}/watch", "watch"})
    public StocksWrapper filterTopStocksByValueOf10M(@PathVariable(required = false) Optional<String> date, @RequestParam(required = false) Optional<Integer> limit) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper();
        Integer currentLimit = limit.isPresent() ? limit.get() : 15;
        String currentDate = LocalDateUtils.convertToDateFormatOnly(LocalDateUtils.now());
        String availableDate = date.isPresent() ? date.get() : stockService.queryLastDate().orElse(currentDate);
        stocksWrapper.setStocks(stockService.filterWatchList(availableDate,currentLimit));
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }




    private StocksWrapper wrapResults(List<StocksDto> stocks) {
        String date;
        StocksWrapper<StocksDto> stocksWrapper = new StocksWrapper();
        Optional<StocksDto> index = stocks.stream().findFirst();
        if (index.isPresent()) {
            date = index.get().getName();
            stocks.remove(0);
        } else {
            date = LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now());
        }
        stocksWrapper.setAsOf(LocalDateUtils.convertToStandardFormat(date));
        stocksWrapper.setStocks(stocks);
        return stocksWrapper;
    }


}
