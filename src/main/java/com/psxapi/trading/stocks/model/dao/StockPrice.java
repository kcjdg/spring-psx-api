package com.psxapi.trading.stocks.model.dao;

import lombok.Data;
import org.springframework.stereotype.Repository;


@Data
public class StockPrice {

    private Double totalValue;
    private String symbol;
    private Double percentageClose;


}
