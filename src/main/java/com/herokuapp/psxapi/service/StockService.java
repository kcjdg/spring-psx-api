package com.herokuapp.psxapi.service;

import com.herokuapp.psxapi.model.dto.StocksDto;
import java.util.List;

public interface StockService {

    public List<StocksDto> getAllStocks();

    public void saveCompanyInfo();

    public void saveStocksPrice();

}
