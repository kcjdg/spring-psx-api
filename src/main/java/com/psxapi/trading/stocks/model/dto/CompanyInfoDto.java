package com.psxapi.trading.stocks.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CompanyInfoDto<T> {
    private List<T> records;
}
