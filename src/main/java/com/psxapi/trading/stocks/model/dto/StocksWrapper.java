package com.psxapi.trading.stocks.model.dto;


import lombok.Data;

import java.util.List;

@Data
public class StocksWrapper<T> {
    private List<T> stocks;
    private String asOf;
}
