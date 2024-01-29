package com.psxapi.trading.stocks.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateUtilsTest {



    @Test
    public void given_localDateTime_when_formatToStandardTime_then_expectReturnAsTimeInAsiaManila() {
        LocalDateTime now = LocalDateTime.now();
        String expected = LocalDateUtils.formatToStandardTimeAsString(now);
        ZonedDateTime zonedGMT = now.atZone(ZoneId.of("GMT+8"));
        ZonedDateTime zonedIST = zonedGMT.withZoneSameInstant(ZoneId.of("Asia/Manila"));
        DateTimeFormatter standardFormat = DateTimeFormatter.ofPattern(LocalDateUtils.STANDARD_FORMAT_STR);
        Assertions.assertEquals(expected,zonedIST.toLocalDateTime().format(standardFormat));
    }

    @Test
    public void given_timeAsSimpleFormat_when_convertToStandardFormat_then_validateExpectedResponse() {
        Assertions.assertEquals("2020-02-03 15:20:00",LocalDateUtils.convertToStandardFormat("02/03/2020 03:20 PM"));
    }


}
