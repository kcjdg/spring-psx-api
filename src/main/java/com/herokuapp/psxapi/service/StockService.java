package com.herokuapp.psxapi.service;


import com.herokuapp.psxapi.model.dao.StockPrice;
import com.herokuapp.psxapi.model.dto.StocksDto;

import java.util.List;
import java.util.Optional;

public interface StockService {

    List<StocksDto> getAllStocks();

    Optional<StockPrice> findStockDetails(String symbol);

    void saveCompanyInfo();

    void saveStocksPrice();



}
