package com.myapp.consumptionanalysis.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.consumptionanalysis.sql.Connect.DBCol;

public final class Table implements Serializable, Comparable<Table>
{
    private static final long serialVersionUID = - 3657198939172996534L;
    
    private final String alias;
    private final String schema;
    private final String tableName;
    private final String dateCol;
    private final String timeCol;

    private Map<String, DBCol> attachedDBCols = null;
    private Config config = null;
    
    // either a column name or a literal (number) expression
    private final SortedMap<Integer, String> valueColumnExpr;

    // a factor may be given to be able to mix different units
    private final SortedMap<Integer, Double> valueFactors;

    // the label of the value columns, as shown in the result set
    private final SortedMap<Integer, String> columnLabels;



    public Table(String alias,
                 String schema,
                 String tableName,
                 Map<Integer, String> valueColumnNames,
                 Map<Integer, Double> valueFactors,
                 String dateCol,
                 String timeCol,
                 Map<Integer, String> columnLabels) throws ConfigException {
        this.schema = schema;
        this.tableName = tableName;
        this.valueColumnExpr = Collections.unmodifiableSortedMap(new TreeMap<>(valueColumnNames));
        this.valueFactors = Collections.unmodifiableSortedMap(new TreeMap<>(valueFactors));
        this.columnLabels = Collections.unmodifiableSortedMap(new TreeMap<>(columnLabels));
        this.dateCol = dateCol;
        this.timeCol = timeCol;
        this.alias = alias;

        if (valueColumnExpr.size() != this.columnLabels.size()) {
            throw new ConfigException("Die Anzahl von Wertespalten "
                    + "und Wertespaltenlabels ist ungleich! " + "spalten:"
                    + this.valueColumnExpr.values() + " - labels:"
                    + this.columnLabels.values());
        }
    }

    public DBCol getDBCol(String colName) {
        String lowerCase = colName.toLowerCase();
        DBCol dbCol = attachedDBCols.get(lowerCase);
        if (dbCol == null) {
            throw new IllegalStateException("Die Spalte " + colName
                    + " scheint nicht in Tabelle " + getQualifiedTableName()
                    + " zu existieren.");
        }
        return dbCol;
    }

    public String getValueColumnLabel(Integer valueColIndex) {
        return columnLabels.get(valueColIndex);
    }

    Map<Integer, String> getColumnLabels() {
        return columnLabels;
    }

    public String getAlias() {
        return alias;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getQualifiedTableName() {
        return schema + "." + tableName;
    }

    /**
     * @return either a column name or a literal (number) expression
     */
    public SortedMap<Integer, String> getValueColumnExpr() {
        return valueColumnExpr;
    }

    public Double getValueFactor(Integer valueColIndex) {
        return valueFactors.get(valueColIndex);
    }

    public String getDateCol() {
        return dateCol;
    }

    public String getTimeCol() {
        return timeCol;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(tableName);
        builder.append("@");
        builder.append(alias);
        return builder.toString();
    }

    public String getInnerQueryLabel(Integer i) {
        String label = getValueColumnLabel(i);
        label = label.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
        return "val_" + i + "_" + label;
    }

    public String getOuterQueryLabel(Integer i) {
        String label = getValueColumnLabel(i);
        label = label.replaceAll("[^A-Za-z0-9]+", "").toUpperCase();
        return "VALUE_" + i + "_" + label;
    }

    public static List<String> tableNames(Collection<Table> tables) {
        List<String> result = new ArrayList<>();
        for (Table t : tables) {
            result.add(t.getQualifiedTableName());
        }
        return result;
    }

    public String getAliasedValueExpr(String internalAlias, int valueIndex) {
        String expr = valueColumnExpr.get(valueIndex);
        if (isLiteralValue(expr)) {
            return expr;
        }

        return getAliasedValueExpr(internalAlias, expr);
    }

    public String getAliasedValueExpr(String internalAlias, String expr) {
        Matcher m = Pattern.compile("(?i)[a-z][a-z0-9_]*").matcher(expr);
        if (! m.find()) {
            throw new RuntimeException("neither a literal nor a colname in: " + expr);
        }

        StringBuilder result = new StringBuilder("");
        int at = 0;

        do {
            result.append(expr.substring(at, m.start()));
            result.append(internalAlias);
            result.append(".");
            result.append(m.group());
            at = m.end();
        } while (m.find());

        if (expr.length() >= at) {
            result.append(expr.substring(at));
        }

        return result.toString();
    }

    /**
     * called from config while parsing, after the database conection was asked for column metadata.
     * @return list of error messages
     */
    List<String> bindDBCol(Map<String, DBCol> m) {
        List<String> errors = new ArrayList<>();
        failIfNotContainingColumn(getDateCol(), m, errors);
        failIfNotContainingColumn(getTimeCol(), m, errors);

        for (String expr : valueColumnExpr.values()) {
            if (isLiteralValue(expr)) {
                continue; // we can use this as it is. e.g.: select 0.0 from table1;
            }

            List<String> exprCols = findColumnNames(expr);

            for (String col : exprCols) {
                DBCol dbCol = m.get(col.toLowerCase());

                if (dbCol == null) {
                    errors.add("In Tabelle " + getAlias() + " ("
                            + getQualifiedTableName() + ") scheint es keine Spalte "
                            + col + " zu geben. " + "Möglicherweise gibt es im Ausdruck "
                            + expr + " einen Fehler.");
                }
            }
        }


        attachedDBCols = m;
        return errors.isEmpty() ? null : errors;
    }

    static List<String> findColumnNames(String expr) {
        Matcher m = Pattern.compile("(?i)[a-z][a-z0-9_]*").matcher(expr);

        List<String> hits = new ArrayList<>();
        while (m.find()) {
            hits.add(m.group());
        }

        return hits;
    }


    private void failIfNotContainingColumn(String column,
                                           Map<String, DBCol> m,
                                           List<String> errors) {
        if (! m.containsKey(column.toLowerCase())) {
            String message = "In Tabelle " + getAlias() + " (" + getQualifiedTableName()
                    + ") scheint es keine Spalte " + column + " zu geben!";
            errors.add(message);
        }
    }

    public static boolean isLiteralValue(String expr) {
        if (expr.equalsIgnoreCase("null")) {
            return true;
        }
        // TODO: this is slow and ugly:
        try {
            Double.valueOf(expr);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @return the config
     */
    final Config getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    final void setConfig(Config config) {
        this.config = config;
    }

    @Override
    public int compareTo(Table o) {
        String alias2 = "zzzzzzzzzzzzzzzzz";
        if (o != null) {
            alias2 = o.getAlias();
        }
        return getAlias().compareTo(alias2);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((alias == null) ? 0 : alias.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof Table)) {
            return false;
        }
        Table other = (Table) obj;
        if (alias == null) {
            if (other.alias != null) {
                return false;
            }
        } else if (! alias.equals(other.alias)) {
            return false;
        }
        return true;
    }

}
