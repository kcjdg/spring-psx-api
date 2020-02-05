package com.herokuapp.psxapi.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.herokuapp.psxapi.helper.VolumeParserUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StockDetailsDto {
    private Double percentageClose;
    private String symbol;
    private Double open;
    private Double low;
    private Double high;
    private Double avePrice;
    private Double week52Low;
    private Double week52High;
    private BigDecimal totalValue;
    private BigDecimal totalVolume;
    private LocalDateTime lastTradedDate;
    private Double lastPrice;

    public String getTotalValue() {
        return VolumeParserUtil.convertNumber(totalValue);
    }

    public String getTotalVolume() {
        return VolumeParserUtil.convertNumber(totalVolume);
    }

}
