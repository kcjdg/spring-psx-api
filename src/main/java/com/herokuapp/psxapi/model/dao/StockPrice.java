package com.herokuapp.psxapi.model.dao;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.herokuapp.psxapi.helper.LocalDateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@Table
@NoArgsConstructor
public class StockPrice implements Serializable {

    @Id
    @JsonAlias("securitySymbol")
    private String symbol;
    @JsonAlias("headerSqOpen")
    private Double open;
    @JsonAlias("headerSqLow")
    private Double low;
    @JsonAlias("headerSqHigh")
    private Double high;
    @JsonAlias("headerAvgPrice")
    private Double avePrice;
    @JsonAlias("headerFiftyTwoWeekLow")
    private Double week52Low;
    @JsonAlias("headerFiftyTwoWeekHigh")
    private Double week52High;
    @JsonAlias("headerTotalValue")
    private BigDecimal totalValue;
    @JsonAlias("headerTotalVolume")
    private BigDecimal totalVolume;
    @JsonAlias("headerLastTradePrice")
    private Double lastPrice;
    @JsonAlias("lastTradedDate")
    private LocalDateTime lastTradedDate;
    @JsonAlias("headerPercChangeClose")
    private Double percentageClose;

    public void setTotalValue(String totalValue) {
        this.totalValue = getBd(totalValue);
    }

    public void setOpen(String open) {
        this.open = parseAndReplaceValue(open);
    }

    public void setLow(String low) {
        this.low = parseAndReplaceValue(low);
    }

    public void setHigh(String high) {
        this.high = parseAndReplaceValue(high);
    }

    public void setAvePrice(String avePrice) {
        this.avePrice = parseAndReplaceValue( avePrice);
    }

    public void setWeek52Low(String week52Low) {
        this.week52Low = parseAndReplaceValue(week52Low);
    }

    public void setWeek52High(String week52High) {
        this.week52High = parseAndReplaceValue(week52High);
    }

    public void setTotalVolume(String volume) {
        this.totalVolume = getBd(volume);
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = parseAndReplaceValue(lastPrice);
    }

    public void setLastTradedDate(String lastTradedDate) {
        this.lastTradedDate = LocalDateUtils.convertToLocalDateTimeUsingStandardFormat2(lastTradedDate);
    }

    private Double parseAndReplaceValue(String val){
        return getBd(val).doubleValue();
    }

    private BigDecimal getBd(String val){
        BigDecimal bd = new BigDecimal(val.replaceAll(",", "")).setScale(2, RoundingMode.HALF_UP);
        return bd;
    }
}
