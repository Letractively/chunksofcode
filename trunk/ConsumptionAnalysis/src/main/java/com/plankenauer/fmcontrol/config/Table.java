package com.plankenauer.fmcontrol.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.plankenauer.fmcontrol.jdbc.Connect.DBCol;

public final class Table
{
    private final String alias;
    private final String schema;
    private final String tableName;
    private final String dateCol;
    private final String timeCol;

    private Map<String, DBCol> attachedDBCols = null;

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
    public Map<Integer, String> getValueColumnExpr() {
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

    /**
     * @return list of error messages
     */
    List<String> bindDBCol(Map<String, DBCol> m) {
        List<String> errors = new ArrayList<>();

        for (String col : valueColumnExpr.values()) {
            failIfNotContainingColumn(col, m, errors);
        }

        failIfNotContainingColumn(getDateCol(), m, errors);
        failIfNotContainingColumn(getTimeCol(), m, errors);

        attachedDBCols = m;
        return errors.isEmpty() ? null : errors;
    }

    private void failIfNotContainingColumn(String column,
                                           Map<String, DBCol> m,
                                           List<String> errors) {
        if (! m.containsKey(column)) {
            String message = "In Tabelle " + getQualifiedTableName()
                    + " scheint es keine Spalte " + column + " zu geben!";
            errors.add(message);
        }
    }
}
