package com.herokuapp.psxapi.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.herokuapp.psxapi.helper.VolumeParserUtil;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StocksSimple implements Serializable {
    @JsonAlias("totalVolume")
    private String volume;
    @JsonAlias("percChangeClose")
    private String percentageChange;
    @JsonAlias("lastTradedPrice")
    private String price;
    @JsonAlias("securityAlias")
    private String name;
    @JsonAlias("securitySymbol")
    @JacksonXmlProperty(isAttribute = true)
    private String symbol;

    public void setVolume(String totalVolume) {
        String volAsStr = totalVolume.replaceAll(",", "");
        if (NumberUtils.isCreatable(volAsStr)) {
            BigDecimal volAsBigInt = new BigDecimal(volAsStr);
            this.volume = VolumeParserUtil.convertNumber(volAsBigInt);
        } else {
            this.volume = totalVolume;
        }
    }

}
