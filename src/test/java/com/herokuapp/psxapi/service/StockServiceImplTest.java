package com.herokuapp.psxapi.service;

import com.herokuapp.psxapi.config.PseiProps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
public class StockServiceImplTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PseiProps pseiProps;

//    @BeforeEach
//    public void setUp(){
//        stockService = new StockServiceImpl(restTemplate,pseiProps);
//    }

    @Test
    public void checkPostMethod(){
        stockService.getAllStocks().forEach(System.out::println);
    }
}
