package com.herokuapp.psxapi.controller;

import com.herokuapp.psxapi.helper.LocalDateUtils;
import com.herokuapp.psxapi.helper.StockNotFoundException;
import com.herokuapp.psxapi.model.dao.StockPrice;
import com.herokuapp.psxapi.model.dto.StockDetailsDto;
import com.herokuapp.psxapi.model.dto.StocksDto;
import com.herokuapp.psxapi.model.dto.StocksWrapper;
import com.herokuapp.psxapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
        if(!ArrayUtils.isEmpty(symbols)){
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
        StocksWrapper<StockDetailsDto> stocksWrapper = new StocksWrapper();
        StockPrice stockPrice = stockService.findStockDetails(symbol.toUpperCase()).orElseThrow(StockNotFoundException::new);
        StockDetailsDto stocksDto = new StockDetailsDto();
        BeanUtils.copyProperties(stockPrice, stocksDto);
        stocksWrapper.setStocks(Collections.singletonList(stocksDto));
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping("by/{date}")
    public String findStocksByDate(@PathVariable String date) {
        return stockService.getFirebaseData(date).orElseThrow(StockNotFoundException::new);
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
