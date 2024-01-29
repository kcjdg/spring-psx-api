package com.psxapi.trading.stocks.controller;

import com.psxapi.trading.stocks.helper.LocalDateUtils;
import com.psxapi.trading.stocks.helper.StockNotFoundException;
import com.psxapi.trading.stocks.model.dao.StockPrice;
import com.psxapi.trading.stocks.model.dto.StocksDto;
import com.psxapi.trading.stocks.model.dto.StocksWrapper;
import com.psxapi.trading.stocks.service.StockService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@RestController
@RequestMapping("/v1/stocks")
@RequiredArgsConstructor
public class StocksController {

    private final StockService stockService;

    @GetMapping("")
    public StocksWrapper<StocksDto> getStocks(@RequestParam(required = false) String... symbols) {
        List<StocksDto> allStocks = stockService.getAllStocks();
        if (!ArrayUtils.isEmpty(symbols)) {
            return wrapResults(allStocks.stream()
                    .filter(e -> StringUtils.equalsIgnoreCase(e.getPrice(), "date") || Arrays.stream(symbols).anyMatch(e.getSymbol()::equalsIgnoreCase))
                    .collect(Collectors.toList()));
        }
        return wrapResults(allStocks);
    }

    @GetMapping("/{symbol}")
    public StocksWrapper<StocksDto> findStock(@PathVariable String symbol) {
        List<StocksDto> allStocks = stockService.getAllStocks();
        return wrapResults(allStocks.stream().
                filter(e -> StringUtils.equalsIgnoreCase(e.getPrice(), "date") || StringUtils.equalsAnyIgnoreCase(e.getSymbol(), symbol)).
                collect(Collectors.toList()));
    }


    @GetMapping("/{symbol}/details")
    public StocksWrapper<StockPrice> findStockWithDetails(@PathVariable String symbol) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper<>();
        StockPrice stockPrice = stockService.findStockDetails(symbol.toUpperCase()).orElseThrow(StockNotFoundException::new);
        stocksWrapper.setStocks(Collections.singletonList(stockPrice));
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping("by/{date}")
    public StocksWrapper<StockPrice> findStocksByDate(@PathVariable String date) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper<>();
        List<StockPrice> firebaseData = stockService.getFirebaseData(date);
        stocksWrapper.setStocks(firebaseData);
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping("by/{date}/{symbol}")
    public StocksWrapper<StockPrice> findStocksByDateAndSymbol(@PathVariable String date, @PathVariable String symbol) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper<>();
        List<StockPrice> firebaseData = stockService.getFirebaseData(date).stream().filter(stks->StringUtils.equalsIgnoreCase(symbol,stks.getSymbol())).collect(Collectors.toList());
        stocksWrapper.setStocks(firebaseData);
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }


    @GetMapping({"by/{date}/watch", "watch"})
    public StocksWrapper<StockPrice> filterTopStocksByValueOf10M(@PathVariable(required = false) Optional<String> date, @RequestParam(required = false) Optional<Integer> limit, @RequestParam(required = false) Optional<Boolean> blueChips) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper<>();
        Integer currentLimit = limit.isPresent() ? limit.get() : 15;
        String currentDate = LocalDateUtils.convertToDateFormatOnly(LocalDateUtils.now());
        String availableDate = date.isPresent() ? date.get() : stockService.queryLastDate().orElse(currentDate);
        stocksWrapper.setStocks(stockService.filterWatchList(availableDate,currentLimit,blueChips.orElse(false)));
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }



    @GetMapping("watch/weekly")
    public StocksWrapper<StockPrice> filterTopStocksByWeekly(@RequestParam(required = false) Optional<Boolean> blueChips) {
        StocksWrapper<StockPrice> stocksWrapper = new StocksWrapper<>();
        Integer currentLimit = 15;
        List<StockPrice> weeklyWatchList = new ArrayList<>();
        LocalDate startDate = LocalDateUtils.getMondayThisWeek();
        LocalDate today = LocalDateUtils.now().toLocalDate();
        while(startDate.isBefore(today)){
            weeklyWatchList.addAll(stockService.filterWatchList(startDate.toString(),currentLimit,blueChips.orElse(false)));
            startDate = startDate.plusDays(1);
        }
        stocksWrapper.setStocks(
                weeklyWatchList.stream()
                        .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(StockPrice::getSymbol))), ArrayList::new)));
        stocksWrapper.setAsOf(LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now()));
        return stocksWrapper;
    }



    private StocksWrapper<StocksDto> wrapResults(List<StocksDto> stocks) {
        String date;
        StocksWrapper<StocksDto> stocksWrapper = new StocksWrapper<>();
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
