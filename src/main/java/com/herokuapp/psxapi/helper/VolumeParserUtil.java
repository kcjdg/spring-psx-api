package com.herokuapp.psxapi.helper;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class VolumeParserUtil {

    private VolumeParserUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String[] UNITS = new String[]{"K","M","B","T"};
    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private static final NavigableMap<BigDecimal, String> MAP;
    static
    {
        MAP = new TreeMap<>();
        for (int i=0; i<UNITS.length; i++)
        {
            MAP.put(THOUSAND.pow(i+1), UNITS[i]);
        }
    }

    public static String convertNumber(BigDecimal number)
    {
        Entry<BigDecimal, String> entry = MAP.floorEntry(number);
        if (entry == null)
        {
            return number.toString();
        }
        BigDecimal key = entry.getKey();
        BigDecimal d = key.divide(THOUSAND);
        BigDecimal m = number.divide(d);
        float f = m.floatValue() / 1000.0f;
        float rounded = ((int)(f * 100.0))/100.0f;
        if (rounded % 1 == 0)
        {
            return ((int)rounded) + ""+entry.getValue();
        }
        return rounded+entry.getValue();
    }
}
