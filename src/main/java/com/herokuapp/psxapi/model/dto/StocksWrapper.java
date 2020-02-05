package com.herokuapp.psxapi.model.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonRootName("stocks")
public class StocksWrapper <T> {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "stock")
    private List<T> stocks;
    @JacksonXmlProperty(isAttribute = true)
    private String asOf;

}
