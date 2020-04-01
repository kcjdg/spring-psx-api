package com.herokuapp.psxapi.service;


import com.herokuapp.psxapi.config.PseiConfig;
import com.herokuapp.psxapi.helper.LocalDateUtils;
import com.herokuapp.psxapi.model.dao.Company;
import com.herokuapp.psxapi.model.dao.StockPrice;
import com.herokuapp.psxapi.model.dto.CompanyInfoDto;
import com.herokuapp.psxapi.model.dto.StocksDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final RestTemplate restTemplate;
    private final PseiConfig pseiConfig;
    private final MemcachedClient memcachedClient;

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
                return Collections.emptyList();
            }
        }
        return stocks;
    }


    @Override
    public List<Company> queryAllCompanyInfo() {
        List<Company> companies = new ArrayList<>();
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
                    companies.addAll(response.getBody().getRecords());
                }
            } catch (Exception e) {
                log.info("Unable to continue saving company info {}", e);
            }
        });
        return companies;
    }


    @Override
    public void saveStocksPrice() {
        List<StockPrice> stockPrices = new ArrayList<>();
        Company[] companies = restTemplate.getForObject(pseiConfig.getFirebaseApi() + "/companies.json?access_token={token}", Company[].class, pseiConfig.getFirebaseToken());
        log.info("Companies count {} ", companies.length);
        if (companies.length > 0) {
            for (Company company : companies) {
                try {
                    List<StockPrice> listStockPrice = fetchStockDetails(company.getSymbol(), company.getSecurityId().toString());
                    stockPrices.addAll(listStockPrice);
                } catch (Exception e) {
                    log.info("Unable to continue saving stocks price {}", e);
                }
            }
            restTemplate.put(pseiConfig.getFirebaseApi() + "/{date}.json?access_token={token}", stockPrices,
                    LocalDateUtils.convertToDateFormatOnly(LocalDateUtils.now()), pseiConfig.getFirebaseToken());
        }
    }


    @Override
    public List<StockPrice> getFirebaseData(String date) {
        ParameterizedTypeReference<List<StockPrice>> responseType = new ParameterizedTypeReference<List<StockPrice>>() {
        };
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, createHttpHeaders());
        final ResponseEntity<List<StockPrice>> exchange = restTemplate.exchange(pseiConfig.getFirebaseApi() + "/{date}.json?access_token={token}", HttpMethod.GET, request, responseType, date, pseiConfig.getFirebaseToken());
        if (exchange.getStatusCode() == HttpStatus.OK) {
            if (exchange.getBody() != null) {
                return exchange.getBody();
            }
        }
        return Collections.emptyList();
    }


    @Override
    public Optional<String> queryLastDate() {
        ParameterizedTypeReference<HashMap<String, String>> responseType =
                new ParameterizedTypeReference<HashMap<String, String>>() {
                };
        final ResponseEntity<HashMap<String, String>> exchange = restTemplate.exchange(pseiConfig.getFirebaseApi() + "/.json?shallow=true&access_token={token}", HttpMethod.GET, null, responseType, pseiConfig.getFirebaseToken());
        if (exchange.getStatusCode() == HttpStatus.OK) {
            Set<Map.Entry<String, String>> entries = exchange.getBody().entrySet();
            Optional<Map.Entry<String, String>> entryOpt = entries.stream().skip((long) entries.size()).findFirst();
            if (entryOpt.isPresent()) {
                return Optional.ofNullable(entryOpt.get().getKey());
            }
        }
        return Optional.empty();
    }


    @Override
    public Optional<StockPrice> findStockDetails(String symbol) {
        Company[] companies = Optional.ofNullable((Company[]) memcachedClient.get("companies_cache")).orElse(restTemplate.getForObject(pseiConfig.getFirebaseApi() + "/companies.json?access_token={token}", Company[].class, pseiConfig.getFirebaseToken()));
        if (companies.length > 0) {
            memcachedClient.set("companies_cache", 60 * 60 * 24, companies);
            final Optional<Company> companyOpt = Arrays.stream(companies).filter(stks -> StringUtils.equalsIgnoreCase(stks.getSymbol(), symbol)).findFirst();
            if (companyOpt.isPresent()) {
                Company company = companyOpt.get();
                return fetchStockDetails(company.getSymbol(), company.getSecurityId().toString()).stream().findFirst();
            }
        }
        return Optional.empty();
    }


    @Override
    public List<StockPrice> filterWatchList(String date, Integer limit, boolean hasBlueChips) {
        List<StockPrice> firebaseData = getFirebaseData(date);
        Predicate<StockPrice> valueFilter = prc->prc.getTotalValue().doubleValue() > 10_000_000;
        Predicate<StockPrice> blueChipsFilter = prc-> !hasBlueChips ? !pseiConfig.getBlueChips().contains(prc.getSymbol()) : true;
        Comparator<StockPrice> comparatorByPercentageClose = Comparator.comparing((StockPrice prc) -> prc.getPercentageClose());

        List<StockPrice> topGainers = firebaseData.stream()
                .sorted(comparatorByPercentageClose.reversed())
                .limit(limit)
                .filter(valueFilter.and(blueChipsFilter))
                .collect(Collectors.toList());

        List<StockPrice> topLosers = firebaseData.stream()
                .sorted(comparatorByPercentageClose)
                .limit(limit)
                .filter(valueFilter.and(blueChipsFilter))
                .collect(Collectors.toList());

        List<StockPrice> mostActive = firebaseData.stream()
                .sorted(Comparator.comparing((StockPrice prc) -> prc.getTotalValue()).reversed())
                .limit(limit)
                .filter(blueChipsFilter)
                .collect(Collectors.toList());

        return Stream.of(topGainers,topLosers,mostActive).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    private List<StockPrice> fetchStockDetails(String symbol, String securityId) {
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
        return Collections.emptyList();
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
