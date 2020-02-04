package com.herokuapp.psxapi.service;


import com.herokuapp.psxapi.config.PseiConfig;
import com.herokuapp.psxapi.model.dao.Company;
import com.herokuapp.psxapi.model.dao.CompanyRepository;
import com.herokuapp.psxapi.model.dto.CompanyInfoDto;
import com.herokuapp.psxapi.model.dto.StocksSimple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final RestTemplate restTemplate;
    private final PseiConfig pseiConfig;
    private final MemcachedClient memcachedClient;
    private final CompanyRepository companyRepository;


    @Override
    public List<StocksSimple> getAllStocks() {
        List<StocksSimple> stocks = (List<StocksSimple>) memcachedClient.get(pseiConfig.getCacheName());
        if (stocks == null) {
            MultiValueMap<String, String> param = createMultiMap(pseiConfig.getStocksTickerApiName());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
            ResponseEntity<List<StocksSimple>> response = restTemplate.exchange(pseiConfig.getStocksUrl(), HttpMethod.POST, request,
                    new ParameterizedTypeReference<List<StocksSimple>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                memcachedClient.set(pseiConfig.getCacheName(), 60, response.getBody());
                return response.getBody();
            } else {
                return Collections.EMPTY_LIST;
            }
        }
        return stocks;
    }


    @Override
    public void saveCompanyInfo() {
        if (companyRepository.findAll().isEmpty()) {
            log.info("Unable to find stocks list");
            getAllStocks().stream().forEach(stocks -> {
                MultiValueMap<String, String> param = createMultiMap(pseiConfig.getStocksCompanyApiName());
                param.add("start", "0");
                param.add("limit", "1");
                param.add("Referer", pseiConfig.getStocksUrl());
                param.add("query", stocks.getSymbol());
                try{
                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
                    ResponseEntity<CompanyInfoDto<Company>> response = restTemplate.exchange(pseiConfig.getStocksUrl(), HttpMethod.POST, request,
                            new ParameterizedTypeReference<CompanyInfoDto<Company>>() {
                            });
                    if (response.getStatusCode() == HttpStatus.OK) {
                        companyRepository.saveAll(response.getBody().getRecords());
                    }
                }catch (Exception e){
                    log.info("Unable to continue {}",e);
                }
            });
        }
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }


    private MultiValueMap<String, String> createMultiMap(String method) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("method", method);
        map.add("ajax", "true");
        return map;
    }
}
