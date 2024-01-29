package com.psxapi.trading.stocks.model.dto;


import lombok.Data;

@Data
public class StocksDto {
    private String price;
    private String symbol;
    private String name;
    private String percentageChange;
    private String volume;

}
