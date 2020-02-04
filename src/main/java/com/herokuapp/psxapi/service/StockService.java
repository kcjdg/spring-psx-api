package com.herokuapp.psxapi.service;

import com.herokuapp.psxapi.model.dto.StocksSimple;
import java.util.List;

public interface StockService {

    public List<StocksSimple> getAllStocks();

    public void saveCompanyInfo();

}
