package com.plankenauer.fmcontrol.config;

import static com.plankenauer.fmcontrol.config.Constants.CK_CONNECTION_HOSTNAME;
import static com.plankenauer.fmcontrol.config.Constants.CK_CONNECTION_PASSWORD;
import static com.plankenauer.fmcontrol.config.Constants.CK_CONNECTION_PORTNUMBER;
import static com.plankenauer.fmcontrol.config.Constants.CK_CONNECTION_USER;
import static com.plankenauer.fmcontrol.config.Constants.CK_DATA_FILTER_DATE_BOUNDS;
import static com.plankenauer.fmcontrol.config.Constants.CK_DATA_FILTER_DAYTIME_BOUNDS;
import static com.plankenauer.fmcontrol.config.Constants.CK_DATA_FILTER_SQL_FILTER;
import static com.plankenauer.fmcontrol.config.Constants.CK_DATA_GROUP_TYPE;
import static com.plankenauer.fmcontrol.config.Constants.CK_DATA_TABLES_CHOOSEABLE;
import static com.plankenauer.fmcontrol.config.Constants.CK_DATA_TABLES_FIXED;
import static com.plankenauer.fmcontrol.config.Constants.CK_DESCRIPTION;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_DATE_COLUMN;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_SCHEMA;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_TABLE;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_TIME_COLUMN;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_VALUE_COLUMN_FACTOR;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_VALUE_COLUMN_PATTERN;
import static com.plankenauer.fmcontrol.config.Constants.CK_TABLEDEF_VALUE_LABEL_PATTERN;
import static com.plankenauer.fmcontrol.config.Constants.CK_TITLE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.plankenauer.fmcontrol.jdbc.Connect;
import com.plankenauer.fmcontrol.jdbc.Connect.DBCol;



public class ConfigParser
{

    private static final Logger log = Logger.getLogger(Config.class);

    private final Map<String, String> map;
    private final List<String> errors = new ArrayList<>();

    private ConfigParser(Map<String, String> map) {
        Map<String, String> temp = new TreeMap<>();
        temp.putAll(map);
        this.map = Collections.unmodifiableMap(temp);
    }


    public static Config parseConfig(String configId, String path) throws ConfigException {
        File configFile = new File(path);
        return parseConfig(configId, configFile);
    }

    public static Config parseConfig(String configId, File file) throws ConfigException {
        Properties p = new Properties();

        try {
            p.load(new FileInputStream(file));
            return parseConfig(configId, p);

        } catch (ConfigException e) {
            e.setConfigFilePath(file.getPath());
            throw e;

        } catch (IOException e) {
            String msg = "Probleme beim Lesen der Datei: '" + file + "' - "
                    + e.getMessage();
            log.error(msg, e);
            throw new ConfigException(msg, e);

        }
    }

    // may be needed publicly in the future
    private static Config
            parseConfig(String configId, Properties properties) throws ConfigException {
        Map<String, String> m = new HashMap<>();

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            m.put(key, value);
        }

        return parseConfig(configId, m);
    }

    // may be needed publicly in the future
    private static Config
            parseConfig(String configId, Map<String, String> map) throws ConfigException {
        final String debugString = Str.getDebugString(map, configId);
        ConfigParser cp = new ConfigParser(map);

        Exception ex = null;

        try {
            Config config = cp.init(configId);
            if (config != null) {
                config.setDebugString(debugString);
                cp.fetchTableTypes(config);
                return config;
            }

        } catch (Exception e) {
            ex = e;
        }

        ConfigException ce;
        if (ex == null) {
            ce = new ConfigException("Die Konfiguration ist fehlerhaft!");
        } else {
            ce = new ConfigException("Die Konfiguration ist fehlerhaft!", ex);
        }

        ce.setConfigErrors(cp.errors);
        ce.setDebugString(debugString);
        throw ce;
    }


    private void fetchTableTypes(Config config) {
        DataSelectionConfig dsc = config.getDatasource();
        List<Table> tables = dsc.getTables();
        List<String> tableNames = Table.tableNames(tables);
        Connect connect = new Connect(config);
        List<DBCol> dbcols;

        try {
            dbcols = connect.fetchAllColumnsDetailed(tableNames);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Table t : tables) {
            Map<String, DBCol> types = lookupColumnTypes(dbcols,
                                                         t.getQualifiedTableName());
            List<String> errors2 = t.bindDBCol(types);
            if (errors2 != null) {
                errors.addAll(errors2);
            }
        }
    }

    private static Map<String, DBCol> lookupColumnTypes(List<DBCol> dbcols,
                                                        String qualified) {
        Map<String, DBCol> m = new TreeMap<>();
        for (Iterator<DBCol> iterator = dbcols.iterator(); iterator.hasNext();) {
            final DBCol col = iterator.next();

            if (col.getQualifiedName().toLowerCase().startsWith(qualified.toLowerCase())) {
                m.put(col.getColname().toLowerCase(), col);
            }
        }
        return m;
    }

    private Config init(String configId) {
        final int errorCount = errors.size();
        if (configId == null) {
            errors.add("Der Konfiguration wurde keine ID zugewiesen!");
        }

        String title = parseProperty(CK_TITLE, false);
        String description = parseProperty(CK_DESCRIPTION, false);

        ConnectionConfig connection = parseConn();
        DataSelectionConfig datasource = parseDataSrc();

        if (errors.size() == errorCount) {
            Config c = new Config(configId, title, description, connection, datasource);
            return c;
        }

        return null;
    }


    private ConnectionConfig parseConn() {
        final int errorCount = errors.size();

        String hostname = parseProperty(CK_CONNECTION_HOSTNAME, true);
        Integer portnumber = parseNumber(CK_CONNECTION_PORTNUMBER, true);
        String user = parseProperty(CK_CONNECTION_USER, true);
        String password = parseProperty(CK_CONNECTION_PASSWORD, true);

        if (errorCount < errors.size()) {
            return null;
        }

        ConnectionConfig cc = new ConnectionConfig(hostname, portnumber, user, password);
        return cc;
    }

    private DataSelectionConfig parseDataSrc() {
        final int errorCount = errors.size();

        if (errorCount < errors.size()) {
            return null;
        }

        List<String> tablesChooseable = parseList(CK_DATA_TABLES_CHOOSEABLE, false);
        List<String> tablesFixed = parseList(CK_DATA_TABLES_FIXED, false);
        List<String> tables = tablesFixed != null ? tablesFixed : tablesChooseable;

        if (tables == null) {
            errors.add("Es muss entweder " + CK_DATA_TABLES_CHOOSEABLE + " oder "
                    + CK_DATA_TABLES_FIXED + " gesetzt sein!");
        }

        Map<Integer, String> columnLabels = parseColumnLabels();
        List<Table> tableObjects = parseTableDefinitions(tables, columnLabels);

        if (tableObjects.isEmpty()) {
            errors.add("Es wurde keine einzige Tabelle definiert!");
        }

        String groupByStr = parseProperty(CK_DATA_GROUP_TYPE, true);
        Constants.GroupByType groupBy = null;
        try {
            groupBy = Constants.GroupByType.valueOf(groupByStr);
            log.debug(CK_DATA_GROUP_TYPE + "=" + groupByStr);
        } catch (Exception e) {
            log.error(CK_DATA_GROUP_TYPE + "=" + groupByStr);
        }

        if (groupBy == null) {
            errors.add(CK_DATA_GROUP_TYPE + " muss eins von "
                    + Arrays.toString(Constants.GroupByType.values()) + " sein!");
        }

        // every table needs the same number of value columns, so validate this here:
        validateValueColumnIntegrity(tableObjects);

        Calendar dateBoundsStart = parseDateBoundary(CK_DATA_FILTER_DATE_BOUNDS, false, 1);
        Calendar dateBoundsEnd = parseDateBoundary(CK_DATA_FILTER_DATE_BOUNDS, false, 2);

        Calendar dayTimeStart = parseDayTime(CK_DATA_FILTER_DAYTIME_BOUNDS, false, 1);
        Calendar dayTimeEnd = parseDayTime(CK_DATA_FILTER_DAYTIME_BOUNDS, false, 2);
        String filterExpr = parseProperty(CK_DATA_FILTER_SQL_FILTER, false);

        DataSelectionConfig dsCfg = null;

        if (errors.size() == errorCount) {
            dsCfg = new DataSelectionConfig(dateBoundsStart,
                                            dateBoundsEnd,
                                            dayTimeStart,
                                            dayTimeEnd,
                                            filterExpr,
                                            tableObjects,
                                            groupBy);
        }

        return dsCfg;
    }

    private Map<Integer, String> parseColumnLabels() {
        Map<Integer, String> columnLabels = new HashMap<>();

        for (int i = 1;; i++) {
            String labelNameKey = key("", CK_TABLEDEF_VALUE_LABEL_PATTERN, i);
            String labelName = map.get(labelNameKey);

            if (labelName != null) {
                columnLabels.put(i, labelName);
                continue;
            }

            break;
        }

        if (columnLabels.isEmpty()) {
            errors.add("Es ist keine einzige Wertespalte mit einem Label versehen. --> "
                    + CK_TABLEDEF_VALUE_LABEL_PATTERN);
        }

        return columnLabels;
    }

    private static String key(String keyPrefix, String keySuffix, Object hashReplacement) {
        String key = keyPrefix + keySuffix;
        String result = key.replaceFirst("[#]", String.valueOf(hashReplacement));
        return result;
    }

    private List<Table> parseTableDefinitions(List<String> tableAliases,
                                              Map<Integer, String> columnLabels) {
        List<Table> result = new ArrayList<>(2);

        for (String alias : tableAliases) {
            String schema = parseProperty(alias + CK_TABLEDEF_SCHEMA, true);
            String tabname = parseProperty(alias + CK_TABLEDEF_TABLE, true);
            String dateCol = parseProperty(alias + CK_TABLEDEF_DATE_COLUMN, true);
            String timeCol = parseProperty(alias + CK_TABLEDEF_TIME_COLUMN, true);

            Map<Integer, String> valueColumns = new HashMap<>(4);
            Map<Integer, Double> valueFactors = new HashMap<>(4);

            for (int i = 1;; i++) {
                String columnNameKey = key(alias, CK_TABLEDEF_VALUE_COLUMN_PATTERN, i);
                String columnName = map.get(columnNameKey);

                if (columnName != null) {
                    valueColumns.put(Integer.valueOf(i), columnName);
                    String factorKey = key(alias, CK_TABLEDEF_VALUE_COLUMN_FACTOR, i);
                    String factor = map.get(factorKey);

                    if (factor != null) {
                        try {
                            Double f = Double.valueOf(factor.trim().replaceAll(",", "."));
                            valueFactors.put(Integer.valueOf(i), f);
                        } catch (NumberFormatException e) {
                            errors.add("Die Variable "
                                    + factorKey
                                    + " fehlt oder ist keine gültige Gleitkommazahl (z.b. 2.34) - "
                                    + e.getMessage());
                        }
                    }

                    continue;
                }

                break;
            }

            if (valueColumns.isEmpty()) {
                errors.add("Für TabellenDefinition " + alias
                        + " wurde keine einzige Wertespalte definiert!");
                continue;
            }


            try {
                Table table = new Table(alias,
                                        schema,
                                        tabname,
                                        valueColumns,
                                        valueFactors,
                                        dateCol,
                                        timeCol,
                                        columnLabels);
                result.add(table);
            } catch (ConfigException e) {
                errors.add(e.getMessage());
                continue;
            }
        }

        return result;
    }


    private void validateValueColumnIntegrity(List<Table> result) {
        Set<Integer> allValueColumns = new HashSet<>(4);

        for (Table t : result) {
            Set<Integer> valueColumns = t.getValueColumnExpr().keySet();
            allValueColumns.addAll(valueColumns);

            Set<Integer> columnLabels = t.getColumnLabels().keySet();
            if (! columnLabels.containsAll(valueColumns)) {
                errors.add("Es ist nicht für alle Wertespalten ein Label gesetzt. "
                        + "Die Tabelle " + t.getAlias()
                        + " hat eine spalte value-#.column definiert, "
                        + "dazu gibt es aber kein Label. Die Variable "
                        + CK_TABLEDEF_VALUE_LABEL_PATTERN + " fehlt.");
            }
        }

        for (Table t : result) {
            if (! t.getValueColumnExpr().keySet().containsAll(allValueColumns)) {
                errors.add("Eine der Wertespalten " + t.getAlias()
                        + CK_TABLEDEF_VALUE_COLUMN_PATTERN + " ist nicht definiert. "
                        + "In einer der anderen Tabellendefinitionen schon! Die Anzahl "
                        + "und Nummerierung muss in allen Tabellen gleich sein.");
            }
        }
    }


    private Calendar parseDayTime(String key, boolean nullIsInvalid, int index) {
        String asString = parseProperty(key, nullIsInvalid);
        if (asString == null) {
            return null;
        }

        asString = asString.trim();

        String elem = "(\\d{2}:\\d{2}:\\d{2})";
        Matcher m = Pattern.compile(elem + " - " + elem).matcher(asString);

        if (! m.matches()) {
            errors.add("Die Variable " + key + " enspricht nicht dem Muster: '"
                    + m.pattern().pattern() + "'!! Wert = '" + asString + "'");
            return null;
        }

        Calendar cal = newEmptyCalendar();

        String group = m.group(index);
        String[] split = group.split(":");
        int hour;
        int minute;
        int second;

        try {
            hour = Integer.parseInt(Str.getRidOfLeadingZeros(split[0]));
            minute = Integer.parseInt(Str.getRidOfLeadingZeros(split[1]));
            second = Integer.parseInt(Str.getRidOfLeadingZeros(split[2]));
        } catch (Exception e) {
            errors.add("Fehler beim Auswerten des DAYTIME Ausdrucks der Variable '" + key
                    + "': " + group + " - " + e);
            return null;
        }

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);

        return cal;
    }

    private Calendar parseDateBoundary(String key, boolean nullIsInvalid, int index) {
        String asString = parseProperty(key, nullIsInvalid);
        if (asString == null) {
            return null;
        }

        asString = asString.trim();
        String pattern = "(\\d{4}-\\d{2}-\\d{2}|TODAY)";
        Matcher m = Pattern.compile(pattern + " - " + pattern).matcher(asString);

        if (! m.matches()) {
            errors.add("Die Variable " + key + " enspricht nicht dem Muster: '"
                    + m.pattern().pattern() + "'!! Wert = '" + asString + "'");
            return null;
        }

        String group = m.group(index);
        Calendar cal = newEmptyCalendar();

        if (group.equals("TODAY")) {
            cal.setTime(new Date());

        } else {
            String[] split = group.split("-");
            int year;
            int month;
            int day;
            try {
                year = Integer.parseInt(Str.getRidOfLeadingZeros(split[0]));
                month = Integer.parseInt(Str.getRidOfLeadingZeros(split[1]));
                day = Integer.parseInt(Str.getRidOfLeadingZeros(split[2]));
            } catch (Exception e) {
                errors.add("Fehler beim Auswerten des DAYTIME Ausdrucks der Variable '"
                        + key + "': " + group + " - " + e);
                return null;
            }
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
        }

        return cal;
    }


    private static Calendar newEmptyCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0L);
        return cal;
    }


    private List<String> parseList(String key, boolean nullIsInvalid) {
        String asString = parseProperty(key, nullIsInvalid);
        if (asString == null || asString.trim().isEmpty()) {
            return null;
        }

        String[] split = asString.trim().split("(?m)\\s*,\\s*");
        List<String> result = new ArrayList<>(split.length);

        for (int i = 0; i < split.length; i++) {
            String listItem = split[i].trim();
            if (listItem.isEmpty()) {
                errors.add("Die Liste bei '" + key + "' enthält leere Elemente: '"
                        + asString + "'");
                return null;
            }
            result.add(listItem);
        }

        if (result.isEmpty()) {
            if (nullIsInvalid) {
                errors.add("Erforderlicher Konfigurationseintrag ist leer: '" + key + "'");
            }
        }

        return result;
    }


    private Integer parseNumber(String key, boolean nullIsInvalid) {
        String asString = parseProperty(key, nullIsInvalid);
        if (asString == null) {
            return null;
        }

        try {
            return Integer.parseInt(Str.getRidOfLeadingZeros(asString));

        } catch (NumberFormatException e) {
            errors.add(("Wert von '" + key + "' ist keine Ganzzahl! (" + asString + ")"));
            return null;
        }
    }

    private String parseProperty(String key, boolean nullIsInvalid) {
        String value = map.get(key);

        if (value == null) {
            if (nullIsInvalid) {
                errors.add(("Erforderlicher Konfigurationseintrag fehlt: '" + key + "'"));
            }
            return null;
        }

        String trim = value.trim();
        if (trim.isEmpty()) {
            if (nullIsInvalid) {
                errors.add(("Erforderlicher Konfigurationseintrag ist leer: '" + key + "'"));
            }
            return null;
        }
        return trim;
    }
}
