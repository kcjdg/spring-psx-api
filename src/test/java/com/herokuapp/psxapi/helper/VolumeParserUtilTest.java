package com.herokuapp.psxapi.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class VolumeParserUtilTest {

    private VolumeParserUtil volumeParserUtil;
    private LocalDateUtils localDateUtils;

    @BeforeEach
    public void setUp(){
        localDateUtils = new LocalDateUtils();
        volumeParserUtil = new VolumeParserUtil();
    }

    @Test
    public void givenVolumeParserUtil_whenStaticMethodCalled_thenValidateExpectedResponse(){
        Assertions.assertEquals("9.3K",volumeParserUtil.convertNumber(new BigDecimal("9300")));
        Assertions.assertEquals("9.35M",volumeParserUtil.convertNumber(new BigDecimal("9350100")));
        Assertions.assertEquals("9.45B",volumeParserUtil.convertNumber(new BigDecimal("9450100200")));

    }
}
