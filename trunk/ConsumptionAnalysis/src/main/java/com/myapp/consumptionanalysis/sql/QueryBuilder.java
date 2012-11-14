package com.myapp.consumptionanalysis.sql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.config.DataSelectionConfig;
import com.myapp.consumptionanalysis.config.Table;
import com.myapp.consumptionanalysis.config.Constants.GroupByType;
import com.myapp.consumptionanalysis.jdbc.Connect.DBCol;

public class QueryBuilder
{

    private static final String RESULT_YEARCOL_NAME = "YEAR_COL";
    private static final String RESULT_MONTHCOL_NAME = "MONTH_COL";
    private static final String RESULT_DAYCOL_NAME = "DAY_COL";
    private static final String RESULT_HOURCOL_NAME = "HOUR_COL";

    private static final String INNER_YEARCOL_NAME = "yearcol";
    private static final String INNER_MONTHCOL_NAME = "monthcol";
    private static final String INNER_DAYCOL_NAME = "daycol";
    private static final String INNER_HOURCOL_NAME = "hourcol";

    static final String NL = System.getProperty("line.separator");
    private static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");


    private int indent = 0;
    private String indentString = "  ";

    private final Config config;
    private final DataSelectionConfig dsc;
    private StringBuilder bui = null;

    public QueryBuilder(Config c) {
        this.config = c;
        this.dsc = config.getDatasource();
    }

    public String generateQuery() {
        String sql;

        synchronized (this) {
            bui = new StringBuilder();
            indent = 0;
            impl();
            sql = bui.toString();
            bui = null;
        }

        return sql;
    }

    private void impl() {
        select();
        from();
        groupBy();
        orderBy();
    }

    private void groupBy() {
        GroupByType groupBy = dsc.getGroupBy();
        if (groupBy == GroupByType.TOTAL) {
            return;
        }
        bui.append(newLine());
        bui.append("group by ");
        indent++;

        final int len = bui.length();

        switch (groupBy) { // fall through
            case HOUR:
                bui.insert(len, newLine() + INNER_HOURCOL_NAME + ", ");
            case DAY:
                bui.insert(len, newLine() + INNER_DAYCOL_NAME + ", ");
            case MONTH:
                bui.insert(len, newLine() + INNER_MONTHCOL_NAME + ", ");
            case YEAR:
                bui.insert(len, newLine() + INNER_YEARCOL_NAME + ", ");
        }

        // remove trailing ", "
        bui.deleteCharAt(bui.length() - 1);
        bui.deleteCharAt(bui.length() - 1);

        indent--;
    }

    private void orderBy() {
        GroupByType groupBy = dsc.getGroupBy();
        if (groupBy == GroupByType.TOTAL) {
            return;
        }
        bui.append(newLine());
        bui.append("order by ");
        indent++;

        final int len = bui.length();

        switch (groupBy) { // fall through
            case HOUR:
                bui.insert(len, newLine() + INNER_HOURCOL_NAME + ", ");
            case DAY:
                bui.insert(len, newLine() + INNER_DAYCOL_NAME + ", ");
            case MONTH:
                bui.insert(len, newLine() + INNER_MONTHCOL_NAME + ", ");
            case YEAR:
                bui.insert(len, newLine() + INNER_YEARCOL_NAME + ", ");
        }

        // remove trailing ", "
        bui.deleteCharAt(bui.length() - 1);
        bui.deleteCharAt(bui.length() - 1);

        indent--;
    }

    private void select() {
        bui.append(newLine());
        bui.append("select ");
        indent++;

        GroupByType groupBy = dsc.getGroupBy();
        final int len = bui.length();

        switch (groupBy) { // fall through
            case HOUR:
                bui.insert(len, newLine() + INNER_HOURCOL_NAME + " as "
                        + RESULT_HOURCOL_NAME + ", ");
            case DAY:
                bui.insert(len, newLine() + INNER_DAYCOL_NAME + " as "
                        + RESULT_DAYCOL_NAME + ", ");
            case MONTH:
                bui.insert(len, newLine() + INNER_MONTHCOL_NAME + " as "
                        + RESULT_MONTHCOL_NAME + ", ");
            case YEAR:
                bui.insert(len, newLine() + INNER_YEARCOL_NAME + " as "
                        + RESULT_YEARCOL_NAME + ", ");
        }


        // all tables have the same number of value columns,
        // and there must be at least one table, so simply
        // view the first table:

        List<Table> tables = dsc.getTables();
        Table anyTable = tables.get(0);
        Set<Integer> valueColIndexes = anyTable.getValueColumnExpr().keySet();

        for (Iterator<Integer> itr = valueColIndexes.iterator(); itr.hasNext();) {
            Integer i = itr.next();
            if (! config.getUiSettings().isColumnChosen(i)) {
                continue;
            }
            bui.append(newLine());
            bui.append("sum(");
            bui.append(anyTable.getInnerQueryLabel(i));
            bui.append(") as ");
            bui.append(anyTable.getOuterQueryLabel(i));

            if (itr.hasNext()) {
                bui.append(", ");
            }
        }

        indent--;
    }

    private void from() {
        bui.append(newLine());
        bui.append("from ");
        indent++;
        bui.append(newLine());
        bui.append("( ");
        indent++;

        List<Table> tables = dsc.getTables();
        List<Table> chosenTables = new ArrayList<>(); 
        int i = 1;
        
        for (Iterator<Table> itr = tables.iterator(); itr.hasNext();) {
            Table t = itr.next();
            if (config.getUiSettings().isTableChosen(t)) {
                chosenTables.add(t);
            }
        }

        for (Iterator<Table> itr = chosenTables.iterator(); itr.hasNext(); i++) {
            Table t = itr.next();
            final String alias = generateAlias(i, t);

            bui.append(newLine());
            bui.append("(");
            indent++;

            innerSelect(t, alias);
            innerFrom(t, alias);
            innerWhere(t, alias);
            innerGroupBy(t, alias);

            indent--;
            bui.append(newLine());
            bui.append(") ");

            if (itr.hasNext()) {
                bui.append(newLine());
                bui.append("union all ");
            }
        }

        indent--;
        bui.append(newLine());
        bui.append(") as rumpelstielzchen ");
        indent--;
    }


    private void addTimeConstraint(Table t,
                                   final String alias,
                                   final int len,
                                   Calendar calendar,
                                   final String operator) {
        String dateCol = t.getTimeCol();
        DBCol dbCol = t.getDBCol(dateCol);
        String type = dbCol.getType().toUpperCase();

        if (bui.length() > len) { // not the first where clause
            bui.append(newLine());
            bui.append("and ");
        } else {
            bui.append(newLine());
        }

        if (type.startsWith("DATE") || type.startsWith("TIMESTAMP")) {
//          TIMESTAMP(
//              concat('2000-01-01 ', HOUR(tab2.Zeit), ':', MINUTE(tab2.Zeit), ':', SECOND(tab2.Zeit))
//          ) >= TIMESTAMP('2000-01-01 08:00:00') 
//          and TIMESTAMP(
//              concat('2000-01-01 ', HOUR(tab2.Zeit), ':', MINUTE(tab2.Zeit), ':', SECOND(tab2.Zeit))
//          ) <= TIMESTAMP('2000-01-01 20:00:00') 

            bui.append("TIMESTAMP(");

            indent++;
            bui.append(newLine());
            bui.append("concat('2000-01-01 ', ");
            bui.append(createTimeExpression(t, "HOUR", alias));
            bui.append(", ':', ");
            bui.append(createTimeExpression(t, "MINUTE", alias));
            bui.append(", ':', ");
            bui.append(createTimeExpression(t, "SECOND", alias));
            bui.append(")");
            indent--;

            bui.append(newLine());
            bui.append(") ");
            bui.append(operator);
            bui.append(" TIMESTAMP('2000-01-01 ");
            bui.append(TIME_FORMAT.format(calendar.getTime()));
            bui.append("') ");
        } else {
            throw new RuntimeException("Ungültiger Typ für eine Zeit-Spalte: " + type
                    + " für Spalte: " + dbCol.getQualifiedName()
                    + " Erlaubt ist TIMESTAMP und DATE");
        }
    }

    private void addDateConstraint(Table t,
                                   final String alias,
                                   final int len,
                                   Calendar calendar,
                                   final String operator) {
        if (bui.length() > len) { // not the first clause
            bui.append(newLine());
            bui.append("and ");
        } else {
            bui.append(newLine());
        }

        String dateCol = t.getDateCol();
        DBCol dbCol = t.getDBCol(dateCol);
        String type = dbCol.getType().toUpperCase();

        if (type.startsWith("VARCHAR") || type.startsWith("CHAR")) {
            /**
            STR_TO_DATE(tab3.Datum, '%d.%m.%Y') <= STR_TO_DATE('20.09.2012', '%d.%m.%Y')
            and STR_TO_DATE(tab3.Datum, '%d.%m.%Y') >= STR_TO_DATE('1.1.2005', '%d.%m.%Y')
            */
            bui.append("STR_TO_DATE(");
            bui.append(alias);
            bui.append(".");
            bui.append(dateCol);
            bui.append(", '%d.%m.%Y') ");
        } else if (type.startsWith("DATE") || type.startsWith("TIMESTAMP")) {
            bui.append(alias);
            bui.append(".");
            bui.append(dateCol);
            bui.append(" ");
        } else {
            throw new RuntimeException("Unbekannter Typ für eine Datum-Spalte: "
                    + type
                    + " für Spalte: "
                    + dbCol.getQualifiedName()
                    + " Erlaubt sind DATE, TIMESTAMP sowie CHAR, VARCHAR im Format dd.MM.yyyy");
        }

        bui.append(operator);
        bui.append(" STR_TO_DATE('");
        bui.append(calendar.get(Calendar.DAY_OF_MONTH));
        bui.append(".");
        bui.append(calendar.get(Calendar.MONTH));
        bui.append(".");
        bui.append(calendar.get(Calendar.YEAR));
        bui.append("', '%d.%m.%Y') ");
    }


    private void innerSelect(Table t, final String alias) {
        bui.append(newLine());
        bui.append("select ");
        indent++;

        GroupByType groupBy = dsc.getGroupBy();
        final int len = bui.length();

        switch (groupBy) { // fall through
            case HOUR: {
                String expr = createTimeExpression(t, "HOUR", alias);
                bui.insert(len, newLine() + expr + " as " + INNER_HOURCOL_NAME + ", ");
            }
            case DAY: {
                String expr = createDateExpression(t, "DAY", alias);
                bui.insert(len, newLine() + expr + " as " + INNER_DAYCOL_NAME + ", ");
            }
            case MONTH: {
                String expr = createDateExpression(t, "MONTH", alias);
                bui.insert(len, newLine() + expr + " as " + INNER_MONTHCOL_NAME + ", ");
            }
            case YEAR: {
                String expr = createDateExpression(t, "YEAR", alias);
                bui.insert(len, newLine() + expr + " as " + INNER_YEARCOL_NAME + ", ");
            }
        }

        Map<Integer, String> valCols = t.getValueColumnExpr();

        for (Iterator<Map.Entry<Integer, String>> itr = valCols.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<Integer, String> e = itr.next();
            String valueExpr = e.getValue();

            if (! config.getUiSettings().isColumnChosen(e.getKey())) {
                continue;
            }

            bui.append(newLine());
            Integer colIndex = e.getKey();

            if (Table.isLiteralValue(valueExpr)) {
                bui.append(valueExpr);
                bui.append(" ");

            } else {
                bui.append("SUM(");
                bui.append(t.getAliasedValueExpr(alias, valueExpr));
                bui.append(") ");
            }

            bui.append("as ");
            bui.append(t.getInnerQueryLabel(colIndex));
            if (itr.hasNext()) {
                bui.append(",");
            }
            bui.append(" ");
        }

        indent--;
    }

    private void innerFrom(Table t, final String alias) {
        bui.append(newLine());
        bui.append("from ");

        indent++;
        bui.append(newLine());
        bui.append(t.getQualifiedTableName());
        bui.append(" ");
        bui.append(alias);
        indent--;
    }

    private void innerWhere(Table t, final String alias) {
        bui.append(newLine());
        bui.append("where ");
        indent++;

        final int len = bui.length();

        Calendar calendar;


        if ((calendar = dsc.getDateBoundsStart()) != null) {
            addDateConstraint(t, alias, len, calendar, ">=");
        }
        if ((calendar = dsc.getDateBoundsEnd()) != null) {
            addDateConstraint(t, alias, len, calendar, "<=");
        }

        if ((calendar = dsc.getDayTimeBoundsStart()) != null) {
            addTimeConstraint(t, alias, len, calendar, ">=");
        }
        if ((calendar = dsc.getDayTimeBoundsEnd()) != null) {
            addTimeConstraint(t, alias, len, calendar, "<=");
        }

        if (len == bui.length()) {
            bui.append(newLine());
            bui.append("(1=1)");
        }

        indent--;
    }

    private void innerGroupBy(Table t, final String alias) {
        GroupByType groupBy = dsc.getGroupBy();
        if (groupBy == GroupByType.TOTAL) {
            return;
        }
        bui.append(newLine());
        bui.append("group by ");
        indent++;
        final int len = bui.length();

        switch (groupBy) { // fall-through
            case HOUR: {
                bui.insert(len, newLine() + createTimeExpression(t, "HOUR", alias) + ", ");
            }
            case DAY: {
                bui.insert(len, newLine() + createDateExpression(t, "DAY", alias) + ", ");
            }
            case MONTH: {
                bui.insert(len, newLine() + createDateExpression(t, "MONTH", alias)
                        + ", ");
            }
            case YEAR: {
                bui.insert(len, newLine() + createDateExpression(t, "YEAR", alias) + ", ");
            }
        }

        // remove trailing ", "
        bui.deleteCharAt(bui.length() - 1);
        bui.deleteCharAt(bui.length() - 1);

        indent--;
    }


    private static String
            generateAlias(int index, @SuppressWarnings("unused") Table table) {
        StringBuilder builder = new StringBuilder();
        builder.append("q");
        builder.append(index);
        return builder.toString();
    }

    private String newLine() {
        StringBuilder b = new StringBuilder();
        b.append(NL);
        for (int i = 0; i < indent; i++) {
            b.append(indentString);
        }
        return b.toString();
    }

    private static String createTimeExpression(Table t, String function, String alias) {
        String timeColName = t.getTimeCol();
        DBCol col = t.getDBCol(timeColName);
        String type = col.getType().toUpperCase().trim();
        String aliasedName = alias + "." + timeColName;

        StringBuilder b = new StringBuilder();
        b.append(function);
        b.append("(");

        if (type.startsWith("DATE") || type.startsWith("TIMESTAMP")) {
            b.append(aliasedName);
        } else {
            throw new RuntimeException("Ungültiger Typ für eine Zeit-Spalte: " + type
                    + " für Spalte: " + col.getQualifiedName()
                    + " Erlaubt ist TIMESTAMP und DATE");
        }

        b.append(")");
        return b.toString();
    }

    private static String createDateExpression(Table t, String function, String alias) {
        String dateColName = t.getDateCol();
        DBCol col = t.getDBCol(dateColName);
        String type = col.getType().toUpperCase().trim();
        String aliasedName = alias + "." + dateColName;

        StringBuilder b = new StringBuilder();
        b.append(function);
        b.append("(");


        if (type.startsWith("DATE") || type.startsWith("TIMESTAMP")) {
            b.append(aliasedName);
        } else if (type.startsWith("VARCHAR")) {
            b.append("STR_TO_DATE(");
            b.append(aliasedName);
            b.append(", '%d.%m.%Y')");
        } else {
            throw new RuntimeException("Unbekannter Typ für eine Datum-Spalte: "
                    + type
                    + " für Spalte: "
                    + col.getQualifiedName()
                    + " Erlaubt sind DATE, TIMESTAMP sowie CHAR, VARCHAR im Format dd.MM.yyyy");
        }

        b.append(")");
        return b.toString();
    }
}
