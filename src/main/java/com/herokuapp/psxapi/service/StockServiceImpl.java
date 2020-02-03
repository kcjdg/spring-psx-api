package com.herokuapp.psxapi.service;


import com.herokuapp.psxapi.config.PseiProps;
import com.herokuapp.psxapi.config.PsxConstants;
import com.herokuapp.psxapi.model.StocksSimple;
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
    private final PseiProps pseiProps;
    private final MemcachedClient memcachedClient;

    @Override
    public List<StocksSimple> getAllStocks() {
        List<StocksSimple> stocks = (List<StocksSimple>) memcachedClient.get(PsxConstants.CACHE_STOCKS);
        if (stocks == null) {
            MultiValueMap<String, String> param = createMultiMap(PsxConstants.PSEI_TICKER_METHOD);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
            ResponseEntity<List<StocksSimple>> response = restTemplate.exchange(pseiProps.getUrl(), HttpMethod.POST, request,
                    new ParameterizedTypeReference<List<StocksSimple>>() {});

            if (response.getStatusCode() == HttpStatus.OK) {
                memcachedClient.set(PsxConstants.CACHE_STOCKS, 60, response.getBody());
                return response.getBody();
            } else {
                return Collections.EMPTY_LIST;
            }
        }
        return stocks;
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
