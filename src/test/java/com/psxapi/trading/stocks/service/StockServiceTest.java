package com.psxapi.trading.stocks.service;

import com.psxapi.trading.stocks.config.PseiConfig;
import com.psxapi.trading.stocks.model.dto.StocksDto;
import org.mockito.InjectMocks;

import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    RestTemplate restTemplate;



    @Mock
    PseiConfig pseiConfig;

    @InjectMocks
    StockServiceImpl stockServiceImpl;


//    @Test
    public void given_memcached_when_cacheStocksHasValue_then_expectAListOfStockSimple() {
        assertEquals(createStocks(), stockServiceImpl.getAllStocks());
    }

//    @Test
    public void given_memcachedAndRestemplate_when_apiCalledAndHasReturn_then_cachedResponseAndExpectAList() {
        when(restTemplate.exchange(pseiConfig.getStocksUrl(), HttpMethod.POST, createRequest(),
                new ParameterizedTypeReference<List<StocksDto>>() {
                })).thenReturn(new ResponseEntity<>(createStocks(), HttpStatus.OK));

        assertEquals(createStocks(), stockServiceImpl.getAllStocks());
    }


//    @Test
    public void given_memcachedAndRestemplate_when_apiCalledAndNotOK_then_expectEmptyList() {
        when(restTemplate.exchange(pseiConfig.getStocksUrl(), HttpMethod.POST, createRequest(),
                new ParameterizedTypeReference<List<StocksDto>>() {
                })).thenReturn(new ResponseEntity<>(createStocks(), HttpStatus.BAD_GATEWAY));

        assertEquals(Collections.EMPTY_LIST, stockServiceImpl.getAllStocks());
    }


    private List<StocksDto> createStocks() {
        StocksDto bdo = new StocksDto();
        bdo.setName("Banco de Oro");
        bdo.setPercentageChange("-2.0");
        bdo.setPrice("127.00");
        bdo.setSymbol("BDO");
        bdo.setVolume("10000");

        StocksDto bpi = new StocksDto();
        bpi.setName("Bank of the Ph");
        bpi.setPercentageChange("-2.0");
        bpi.setPrice("127.00");
        bpi.setSymbol("BPI");
        bpi.setVolume("20000");

        List<StocksDto> listOfStocks = new ArrayList<>();
        listOfStocks.add(bdo);
        listOfStocks.add(bpi);
        return listOfStocks;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private HttpEntity<MultiValueMap<String, String>> createRequest() {
        MultiValueMap<String, String> param = createMultiMap(pseiConfig.getCacheName());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, createHttpHeaders());
        return request;
    }

    private MultiValueMap<String, String> createMultiMap(String method) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("method", method);
        map.add("ajax", "true");
        return map;
    }
}
