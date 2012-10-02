package com.plankenauer.fmcontrol.config;

import static com.plankenauer.fmcontrol.config.Constants.CK_CONNECTION_PASSWORD;
import static com.plankenauer.fmcontrol.config.Constants.CK_DESCRIPTION;
import static com.plankenauer.fmcontrol.config.Constants.CK_TITLE;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

final class Str
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

    public static String daytimeToString(Calendar cal) {
        if (cal == null) {
            return null;
        }
        int hh = cal.get(Calendar.HOUR_OF_DAY);
        int mm = cal.get(Calendar.MINUTE);
        int ss = cal.get(Calendar.SECOND);
        return hh + ":" + mm + ":" + ss;
    }

    public static String dayToString(Calendar cal) {
        if (cal == null) {
            return null;
        }
        int yy = cal.get(Calendar.YEAR);
        int mm = cal.get(Calendar.MONTH);
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        return yy + "-" + mm + "-" + dd;
    }

    public static String dumpErrorMsgs(List<ConfigException> ex) {
        StringBuilder bui = new StringBuilder();
        for (ConfigException e : ex) {
            if (null != e.getConfigFilePath()) {
                bui.append(NL);
                bui.append("ConfigFile: ");
                File f = new File(e.getConfigFilePath());
                bui.append(f.getName());
                bui.append(NL);
                bui.append("in directory: ");
                bui.append(f.getParentFile().getAbsolutePath());
                bui.append(NL);
            }
            bui.append(e.getMessage());
            bui.append(NL);
            int errorCount = e.getConfigErrors().size();

            for (int i = 0; i < errorCount; i++) {
                bui.append(i + 1);
                bui.append(".) ");
                String err = e.getConfigErrors().get(i);
                bui.append(err);

                if (i < errorCount - 1) {
                    bui.append(NL);
                }
            }
        }
        return bui.toString();
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
}
