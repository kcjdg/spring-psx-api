package com.herokuapp.psxapi.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class VolumeParserUtilTest {


    @Test
    public void givenVolumeParserUtil_whenStaticMethodCalled_thenValidateExpectedResponse(){
        Assertions.assertEquals("9.3K",VolumeParserUtil.convertNumber(new BigDecimal("9300")));
        Assertions.assertEquals("9.35M",VolumeParserUtil.convertNumber(new BigDecimal("9350100")));
        Assertions.assertEquals("9.45B",VolumeParserUtil.convertNumber(new BigDecimal("9450100200")));

    }
}
