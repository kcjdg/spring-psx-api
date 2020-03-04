package com.herokuapp.psxapi.service;


import com.herokuapp.psxapi.model.dao.Company;
import com.herokuapp.psxapi.model.dao.StockPrice;
import com.herokuapp.psxapi.model.dto.StocksDto;

import java.util.List;
import java.util.Optional;

public interface StockService {

    List<StocksDto> getAllStocks();

    List<Company> queryAllCompanyInfo();

    void saveStocksPrice();

    List<StockPrice> getFirebaseData(String date);

    Optional<String> queryLastDate();

    Optional<StockPrice> findStockDetails(String symbol);

    List<StockPrice> filterWatchList(String date, Integer limit);

}
