package com.herokuapp.psxapi.controller;

import com.herokuapp.psxapi.helper.LocalDateUtils;
import com.herokuapp.psxapi.model.StocksSimple;
import com.herokuapp.psxapi.model.StocksWrapper;
import com.herokuapp.psxapi.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

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
        List<StocksSimple> allStocks = stockService.getAllStocks();
        if(!ArrayUtils.isEmpty(symbols)){
            return wrapResults(allStocks.stream()
                    .filter(e -> StringUtils.equalsIgnoreCase(e.getPrice(), "date") || ArrayUtils.contains(symbols,e.getSymbol()))
                    .collect(Collectors.toList()));
        }
        return wrapResults(allStocks);
    }

    @GetMapping("/{symbol}")
    public StocksWrapper findStock(@PathVariable String symbol) {
        List<StocksSimple> allStocks = stockService.getAllStocks();
        return wrapResults(allStocks.stream().
                filter(e -> StringUtils.equalsIgnoreCase(e.getPrice(), "date") || StringUtils.equals(e.getSymbol(), symbol)).
                collect(Collectors.toList()));
    }



    private StocksWrapper wrapResults(List<StocksSimple> stocks) {
        String date;
        StocksWrapper stocksWrapper = new StocksWrapper();
        Optional<StocksSimple> index = stocks.stream().findFirst();
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
