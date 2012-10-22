package com.plankenauer.fmcontrol.config;



import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.plankenauer.fmcontrol.config.Constants.GroupByType;

public class DataSelectionConfig implements Serializable
{

    private static final long serialVersionUID = 1470288040217770328L;

    private final Long dateBoundsStartValue;
    private final Long dateBoundsEndValue;
    private final Long dayTimeBoundsStartValue;
    private final Long dayTimeBoundsEndValue;
    private final GroupByType groupBy;
    private final String filterExpr;
    private final List<Table> tableObjects;



    public DataSelectionConfig(Calendar dateBoundsStart,
                               Calendar dateBoundsEnd,
                               Calendar dayTimeBoundsStart,
                               Calendar dayTimeBoundsEnd,
                               String filterExpr,
                               List<Table> tableObjects,
                               GroupByType groupBy) {
        this.dateBoundsStartValue = cal2val(dateBoundsStart);
        this.dateBoundsEndValue = cal2val(dateBoundsEnd);
        this.dayTimeBoundsStartValue = cal2val(dayTimeBoundsStart);
        this.dayTimeBoundsEndValue = cal2val(dayTimeBoundsEnd);
        this.tableObjects = readonly(tableObjects);
        this.filterExpr = filterExpr;
        this.groupBy = groupBy;
    }

    public GroupByType getGroupBy() {
        return groupBy;
    }

    /**
     * @return the tables that are in fact used for data selection
     */
    public List<Table> getTables() {
        return tableObjects;
    }

    /**
     * defines the bounds of the date (not the daytime!) we are selecting data
     */
    public Calendar getDateBoundsStart() {
        return val2cal(dateBoundsStartValue);
    }

    /**
     * defines the bounds of the date (not the daytime!) we are selecting data
     */
    public Calendar getDateBoundsEnd() {
        return val2cal(dateBoundsEndValue);
    }

    /**
     * defines the bounds of the daytime we are selecting data
     */
    public Calendar getDayTimeBoundsEnd() {
        return val2cal(dayTimeBoundsEndValue);
    }

    /**
     * defines the bounds of the daytime we are selecting data
     */
    public Calendar getDayTimeBoundsStart() {
        return val2cal(dayTimeBoundsStartValue);
    }

    protected static <T> List<T> readonly(List<T> l) {
        if (l == null) {
            return null;
        }
        return Collections.unmodifiableList(l);
    }

    protected static List<String> getItems(List<String> fixed, List<String> chooseable) {
        if (fixed != null) {
            return fixed;
        }
        return chooseable;
    }

    protected Calendar val2cal(Long time) {
        if (time == null) {
            return null;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(time));
        return instance;
    }

    protected static Long cal2val(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return Long.valueOf(cal.getTimeInMillis());
    }

    /**
     * an sql where expression that is used to narrow the data selection.<br>
     * e.g. <pre>Tarif = 'Tag' AND DAYOFWEEK(Datum) != 2</pre> 
     */
    public String getFilterExpr() {
        return filterExpr;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataSelectionConfig [");
        if (dateBoundsStartValue != null) {
            builder.append("dateBounds={");
            builder.append(Str.dayToString(val2cal(dateBoundsStartValue)));
            builder.append(" - ");
            builder.append(Str.dayToString(val2cal(dateBoundsEndValue)));
            builder.append("}, ");
        }
        if (dayTimeBoundsStartValue != null) {
            builder.append("dayTimeBounds={");
            builder.append(Str.daytimeToString(val2cal(dayTimeBoundsStartValue)));
            builder.append(" - ");
            builder.append(Str.daytimeToString(val2cal(dayTimeBoundsEndValue)));
            builder.append("}, ");
        }
        if (groupBy != null) {
            builder.append("groupBy=");
            builder.append(groupBy);
            builder.append(", ");
        }
        if (tableObjects != null) {
            builder.append("tableObjects=");
            builder.append(tableObjects);
            builder.append(", ");
        }
        if (filterExpr != null) {
            builder.append("filterExpr=");
            builder.append(filterExpr);
        }
        builder.append("]");
        return builder.toString();
    }
}
