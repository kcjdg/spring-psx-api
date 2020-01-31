package com.herokuapp.psxapi.service;

import com.herokuapp.psxapi.model.StocksSimple;
import java.util.List;

public interface StockService {

    public List<StocksSimple> getAllStocks();

}
