package com.myapp.consumptionanalysis.chart.barchart;

import static com.myapp.consumptionanalysis.chart.barchart.BarChartQueryBuilder.RESULT_DAYCOL_NAME;
import static com.myapp.consumptionanalysis.chart.barchart.BarChartQueryBuilder.RESULT_HOURCOL_NAME;
import static com.myapp.consumptionanalysis.chart.barchart.BarChartQueryBuilder.RESULT_MONTHCOL_NAME;
import static com.myapp.consumptionanalysis.chart.barchart.BarChartQueryBuilder.RESULT_YEARCOL_NAME;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.myapp.consumptionanalysis.config.Config;
import com.myapp.consumptionanalysis.config.Constants.GroupByType;
import com.myapp.consumptionanalysis.config.Table;
import com.myapp.consumptionanalysis.sql.AbstractDataSelector;
import com.myapp.consumptionanalysis.sql.DataRow;

public class BarChartDataSelector extends AbstractDataSelector
{
    public BarChartDataSelector(Config config) {
        super(config);
    }

    @Override
    protected DataRow fetchDataRow(ResultSet rs) throws Exception {
        Set<Integer> chosenColumns = getConfig().getUiSettings().getChosenColumns();
        Table table = getConfig().getSelectionConfig().getTables().get(0);
        int size = table.getValueColumnExpr().size();

        Object[] val2 = fetchValues(rs, chosenColumns, table, size);
        DataRow row = new DataRow(val2);
        calculateTimeStampAndLabel(rs, row);
        return row;
    }

    private Object[] fetchValues(ResultSet rs,
                                 Set<Integer> chosenColumns,
                                 Table table,
                                 int size) throws SQLException {
        Object[] val2 = new Object[size + 1];

        for (Iterator<Integer> itr = chosenColumns.iterator(); itr.hasNext();) {
            Integer i = itr.next();
            String outerQueryLabel = table.getOuterQueryLabel(i);
            Object object = rs.getObject(outerQueryLabel);
            val2[i] = object;
        }
        return val2;
    }

    private void calculateTimeStampAndLabel(ResultSet rs, DataRow row) throws Exception {
        GroupByType groupBy = getConfig().getSelectionConfig().getGroupBy();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0L);

        int yyyy, mon, dd, hh;
        yyyy = mon = dd = hh = - 1;


        // fetch needed data
        switch (groupBy) {
            case HOUR:
                hh = rs.getInt(RESULT_HOURCOL_NAME);
                cal.set(Calendar.HOUR_OF_DAY, hh);
            case DAY:
                dd = rs.getInt(RESULT_DAYCOL_NAME);
                cal.set(Calendar.DAY_OF_MONTH, dd);
            case MONTH:
                mon = rs.getInt(RESULT_MONTHCOL_NAME);
                cal.set(Calendar.MONTH, mon);
            case YEAR:
                yyyy = rs.getInt(RESULT_YEARCOL_NAME);
                cal.set(Calendar.YEAR, yyyy);
            case TOTAL:
                break;
            default:
                throw new RuntimeException("unknown GroupByType: " + groupBy);
        }

        Date time = cal.getTime();
        if (time.getTime() != 0L) {
            row.setTimestamp(time);
        }

        String lbl = null;

        switch (groupBy) {
            case TOTAL:
                lbl = "Gesamt";
                break;
            case YEAR:
                lbl = new SimpleDateFormat("yyyy").format(time);
                break;
            case MONTH:
                lbl = new SimpleDateFormat("yyyy-MM").format(time);
                break;
            case DAY:
                lbl = new SimpleDateFormat("yyyy-MM-dd").format(time);
                break;
            case HOUR:
                lbl = new SimpleDateFormat("yyyy-MM-dd HH:00-59").format(time);
                break;
            default:
                throw new RuntimeException("unknown GroupByType: " + groupBy);
        }

        row.setLabel(lbl);
    }

}
