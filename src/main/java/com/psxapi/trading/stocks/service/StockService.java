package com.psxapi.trading.stocks.service;


import com.psxapi.trading.stocks.model.dao.Company;
import com.psxapi.trading.stocks.model.dao.StockPrice;
import com.psxapi.trading.stocks.model.dto.StocksDto;

import java.util.List;
import java.util.Optional;

public interface StockService {

    List<StocksDto> getAllStocks();

    List<Company> queryAllCompanyInfo();

    void saveStocksPrice();

    List<StockPrice> getFirebaseData(String date);

    Optional<String> queryLastDate();

    Optional<StockPrice> findStockDetails(String symbol);

    List<StockPrice> filterWatchList(String date, Integer limit, boolean hasBlueChips);

}
