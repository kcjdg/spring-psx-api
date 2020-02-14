package com.herokuapp.psxapi.service;


import com.herokuapp.psxapi.config.PseiConfig;
import com.herokuapp.psxapi.helper.LocalDateUtils;
import com.herokuapp.psxapi.model.dao.Company;
import com.herokuapp.psxapi.model.dao.CompanyRepository;
import com.herokuapp.psxapi.model.dao.StockPrice;
import com.herokuapp.psxapi.model.dao.StockPriceRepository;
import com.herokuapp.psxapi.model.dto.CompanyInfoDto;
import com.herokuapp.psxapi.model.dto.StocksDto;
import com.herokuapp.psxapi.model.dto.StocksWrapper;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final RestTemplate restTemplate;
    private final PseiConfig pseiConfig;
    private final MemcachedClient memcachedClient;
    private final CompanyRepository companyRepository;
    private final StockPriceRepository stockPriceRepository;


    @Override
    public List<StocksDto> getAllStocks() {
        List<StocksDto> stocks = (List<StocksDto>) memcachedClient.get(pseiConfig.getCacheName());
        if (stocks == null) {
            MultiValueMap<String, String> param = createMultiMap(pseiConfig.getStocksTickerApiName());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
            ResponseEntity<List<StocksDto>> response = restTemplate.exchange(pseiConfig.getStocksUrl(), HttpMethod.POST, request,
                    new ParameterizedTypeReference<List<StocksDto>>() {
                    });

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
            log.info("Unable to find stocks list. Processing the company saving");
            getAllStocks().stream().forEach(stocks -> {
                MultiValueMap<String, String> param = createMultiMap(pseiConfig.getStocksCompanyApiName());
                param.add("start", "0");
                param.add("limit", "1");
                param.add("Referer", pseiConfig.getStocksUrl());
                param.add("query", stocks.getSymbol());
                try {
                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
                    ResponseEntity<CompanyInfoDto<Company>> response = restTemplate.exchange(pseiConfig.getStocksUrl(), HttpMethod.POST, request,
                            new ParameterizedTypeReference<CompanyInfoDto<Company>>() {
                            });
                    if (response.getStatusCode() == HttpStatus.OK) {
                        companyRepository.saveAll(response.getBody().getRecords());
                    }
                } catch (Exception e) {
                    log.info("Unable to continue saving company info {}", e);
                }
            });
            log.info("Done saving company info");
        }
    }

    @Override
    public void saveStocksPrice() {
        List<Company> companies = companyRepository.findAll();
        if (!companies.isEmpty()) {
            log.info("Start back up data .");
            companies.stream().forEach(company -> {
                try {
                    stockPriceRepository.saveAll(fetchStockDetails(company.getSymbol(), company.getSecurityId().toString()));
                } catch (Exception e) {
                    log.info("Unable to continue saving stocks price {}", e);
                }
            });
            log.info("Done saving stock price");
        }
    }




    @Override
    public Optional<StockPrice> findStockDetails(String symbol) {
        Optional<Company> companyOptional = companyRepository.findById(symbol);
        if(companyOptional.isPresent()){
            Company company = companyOptional.get();
            return fetchStockDetails(company.getSymbol(), company.getSecurityId().toString()).stream().findFirst();
        }
        return Optional.empty();
    }



    @Override
    public Optional<String> getFirebaseData(String date){
        String firebaseResponse = restTemplate.getForObject(pseiConfig.getFirebaseApi() + "/{date}.json?access_token={token}", String.class, date, pseiConfig.getFirebaseToken());
        return Optional.ofNullable(firebaseResponse);
    }



    @Override
    public void saveStockPriceInFireBase(){
        try {
            List<StocksDto> allStocks = getAllStocks();
            restTemplate.put(pseiConfig.getFirebaseApi() + "/{date}.json?access_token={token}", wrapResults(allStocks),
                    LocalDateUtils.convertToDateFormatOnly(LocalDateUtils.now()), pseiConfig.getFirebaseToken());
        }catch (Exception e){
            log.info("Unable to put list due to {}", e);
            saveStocksPrice();
        }

    }


    private List<StockPrice> fetchStockDetails(String symbol, String securityId){
        MultiValueMap<String, String> param = createMultiMap(pseiConfig.getCompanyPriceApiName());
        param.add("company", symbol);
        param.add("security", securityId);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
        ResponseEntity<CompanyInfoDto<StockPrice>> response = restTemplate.exchange(pseiConfig.getCompanyInfoUrl(), HttpMethod.POST, request,
                new ParameterizedTypeReference<CompanyInfoDto<StockPrice>>() {
                });
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().getRecords();
        }
        return Collections.EMPTY_LIST;
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



    private StocksWrapper wrapResults(List<StocksDto> stocks) {
        String date;
        StocksWrapper<StocksDto> stocksWrapper = new StocksWrapper();
        Optional<StocksDto> index = stocks.stream().findFirst();
        if (index.isPresent()) {
            date = index.get().getName();
            stocks.remove(0);
        } else {
            date = LocalDateUtils.formatToStandardTimeAsString(LocalDateUtils.now());
        }
        stocksWrapper.setAsOf(LocalDateUtils.convertToStandardFormat(date));
        stocksWrapper.setStocks(stocks);
        return stocksWrapper;
    }
}
