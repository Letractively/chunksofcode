package com.plankenauer.fmcontrol.config;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class DataSelectionConfig implements Serializable
{
    private static final long serialVersionUID = - 6663324964985252917L;
    
    private final List<String> schemasChooseable;
    private final List<String> schemasFixed;

    private final List<String> tablesChooseable;
    private final List<String> tablesFixed;

    private final List<String> columnsChooseable;
    private final List<String> columnsFixed;

    private final Long dateBoundsStartValue;
    private final Long dateBoundsEndValue;
    private final Long dayTimeBoundsStartValue;
    private final Long dayTimeBoundsEndValue;

    private final String filterExpr;


    public DataSelectionConfig(List<String> schemasChooseable,
                               List<String> schemasFixed,
                               List<String> tablesChooseable,
                               List<String> tablesFixed,
                               List<String> columnsChooseable,
                               List<String> columnsFixed,
                               Calendar dateBoundsStart,
                               Calendar dateBoundsEnd,
                               Calendar dayTimeBoundsStart,
                               Calendar dayTimeBoundsEnd,
                               String filterExpr) {
        this.schemasChooseable = readonly(schemasChooseable);
        this.schemasFixed = readonly(schemasFixed);
        this.tablesChooseable = readonly(tablesChooseable);
        this.tablesFixed = readonly(tablesFixed);
        this.columnsChooseable = readonly(columnsChooseable);
        this.columnsFixed = readonly(columnsFixed);
        this.dateBoundsStartValue = cal2val(dateBoundsStart);
        this.dateBoundsEndValue = cal2val(dateBoundsEnd);
        this.dayTimeBoundsStartValue = cal2val(dayTimeBoundsStart);
        this.dayTimeBoundsEndValue = cal2val(dayTimeBoundsEnd);
        this.filterExpr = filterExpr;
    }

    /**
     * databases that are offered for the data selection
     */
    public List<String> getSchemasChooseable() {
        return schemasChooseable;
    }

    /**
     * databases that are used for the data selection (overrides chooseable)
     */
    public List<String> getSchemasFixed() {
        return schemasFixed;
    }

    /**
     * tables that are offered for the data selection
     */
    public List<String> getTablesChooseable() {
        return tablesChooseable;
    }

    /**
     * tables that are used for the data selection (overrides chooseable)
     */
    public List<String> getTablesFixed() {
        return tablesFixed;
    }
    
    private static List<String> getItems(List<String> fixed, List<String> chooseable) {
        if (fixed != null) {
            return fixed;
        }
        return chooseable;
    }

    /**
     * @return the columns that are in fact used for data selection
     */
    public List<String> getColumns() {
        return getItems(getColumnsFixed(), getColumnsChooseable());
    }

    /**
     * @return the databases that are in fact used for data selection
     */
    public List<String> getSchemas() {
        return getItems(getSchemasFixed(), getSchemasChooseable());
    }

    /**
     * @return the tables that are in fact used for data selection
     */
    public List<String> getTables() {
        return getItems(getTablesFixed(), getTablesChooseable());
    }

    /**
     * columns that are offered for the data selection
     */
    public List<String> getColumnsChooseable() {
        return columnsChooseable;
    }

    /**
     * columns that are used for the data selection (overrides chooseable)
     */
    public List<String> getColumnsFixed() {
        return columnsFixed;
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
    
    private static List<String> readonly(List<String> l) {
        if (l == null) {
            return null;
        }
        return Collections.unmodifiableList(l);
    }
    
    private static Long cal2val(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return Long.valueOf(cal.getTime().getTime());
    }
    private Calendar val2cal(Long time) {
        if (time == null) {
            return null;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(time));
        return instance;
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

        if (schemasFixed != null) {
            builder.append("schemasFixed=");
            builder.append(schemasFixed);
            builder.append(", ");
        } else if (schemasChooseable != null) {
            builder.append("schemas=");
            builder.append(schemasChooseable);
            builder.append(", ");
        }

        if (tablesFixed != null) {
            builder.append("tablesFixed=");
            builder.append(tablesFixed);
            builder.append(", ");
        } else if (tablesChooseable != null) {
            builder.append("tablesChooseable=");
            builder.append(tablesChooseable);
            builder.append(", ");
        }

        if (columnsFixed != null) {
            builder.append("columnsFixed=");
            builder.append(columnsFixed);
            builder.append(", ");
        } else if (columnsChooseable != null) {
            builder.append("columnsChooseable=");
            builder.append(columnsChooseable);
            builder.append(", ");
        }

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

        if (filterExpr != null) {
            builder.append("filterExpr=");
            builder.append(filterExpr);
        }

        builder.append("]");
        return builder.toString();
    }
}
