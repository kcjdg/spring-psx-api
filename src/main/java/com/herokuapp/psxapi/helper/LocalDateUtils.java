package com.herokuapp.psxapi.helper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateUtils {

    public static final String STANDARD_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String STANDARD_FORMAT_STR2 = "yyyy-MM-dd HH:mm:ss.S";
    public static final String SIMPLE_FORMAT_STR = "MM/dd/yyyy hh:mm a";

    private static final DateTimeFormatter STANDARD_FORMAT = DateTimeFormatter.ofPattern(STANDARD_FORMAT_STR);
    private static final DateTimeFormatter STANDARD_FORMAT2 = DateTimeFormatter.ofPattern(STANDARD_FORMAT_STR2);
    private static final DateTimeFormatter SIMPLE_FORMAT = DateTimeFormatter.ofPattern(SIMPLE_FORMAT_STR);


    public static LocalDateTime now() {
        return asiaManilaTime(LocalDateTime.now());
    }


    public static String formatToStandardTimeAsString(LocalDateTime localDateTime) {
        return asiaManilaTime(localDateTime).format(STANDARD_FORMAT);
    }

    public static String convertToStandardFormat(String date) {
        LocalDateTime parse = LocalDateTime.parse(date, SIMPLE_FORMAT);
        return formatToStandardTimeAsString(parse);
    }


    private static LocalDateTime asiaManilaTime(LocalDateTime localTime){
        ZonedDateTime zonedGMT = localTime.atZone(ZoneId.of("GMT+8"));
        ZonedDateTime zonedIST = zonedGMT.withZoneSameInstant(ZoneId.of("Asia/Manila"));
        return zonedIST.toLocalDateTime();
    }

    public static LocalDateTime convertToLocalDateTimeUsingStandardFormat2(String time){
        return asiaManilaTime(LocalDateTime.parse(time, STANDARD_FORMAT2));
    }



}
