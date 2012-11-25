package com.myapp.consumptionanalysis.util;

import static com.myapp.consumptionanalysis.config.Constants.CK_CONNECTION_PASSWORD;
import static com.myapp.consumptionanalysis.config.Constants.CK_DESCRIPTION;
import static com.myapp.consumptionanalysis.config.Constants.CK_TITLE;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class StringUtils
{




    public static final String NL = System.getProperty("line.separator");

    public static String getDebugString(Map<String, String> map, String name) {
        if (map == null) {
            return null;
        }

        // customize order of entries:
        LinkedHashMap<String, String> lhm = new LinkedHashMap<>();
        String value;
        if (null != (value = map.get(CK_TITLE))) {
            lhm.put(CK_TITLE, value);
        }
        if (null != (value = map.get(CK_DESCRIPTION))) {
            lhm.put(CK_DESCRIPTION, value);
        }
        lhm.putAll(new TreeMap<>(map));

        // dump config values:
        StringBuilder sb = new StringBuilder();
        sb.append("ConfigDebug id = '");
        sb.append(name);
        sb.append("' {{{");
        sb.append(NL);

        int longestKeyLength = - 1;
        Set<Entry<String, String>> entrySet = lhm.entrySet();

        for (Entry<String, String> entry : entrySet) {
            int length = entry.getKey().length();
            if (length > longestKeyLength) {
                longestKeyLength = length;
            }
        }

        for (Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            sb.append(key);
            for (int i = longestKeyLength - key.length(); i-- >= 0; sb.append(" "));
            sb.append("= ");
            if (key.equals(CK_CONNECTION_PASSWORD)) {
                sb.append("*************");
            } else {
                sb.append(entry.getValue());
            }
            sb.append(NL);
        }

        sb.append("}}}");
        return sb.toString();
    }

    public static String getRidOfLeadingZeros(final String input) {
        if (input == null) {
            return null;
        }

        StringBuilder bui = new StringBuilder(input.trim());
        while (bui.length() > 1 && bui.charAt(0) == '0') {
            bui.deleteCharAt(0);
        }
        return bui.toString();
    }
    


    public static Date val2date(Long val) {
        if (val == null) {
            return null;
        }
        Date d = new Date(val);
        return d;
    }

    public static String fillWithLeadingZeros(int i, int minLength) {
        StringBuilder b = new StringBuilder();
        b.append(i);
        while (b.length() < minLength) {
            b.insert(0, "0");
        }
        return b.toString();
    }
}
