package com.herokuapp.psxapi.model.dto;


import lombok.Data;
import java.util.List;

@Data
public class CompanyInfoDto<T> {
    private Integer count;
    private List<T> records;
}
