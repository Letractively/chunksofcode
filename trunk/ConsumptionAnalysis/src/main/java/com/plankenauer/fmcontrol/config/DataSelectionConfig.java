package com.plankenauer.fmcontrol.config;



import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.plankenauer.fmcontrol.config.Constants.GroupByType;

public class DataSelectionConfig implements Serializable
{


//    private static final Logger log = Logger.getLogger(DataSelectionConfig.class);
    private static final long serialVersionUID = 1470288040217770328L;

    private Long dateBoundsStartValue;
    private Long dateBoundsEndValue;
    private Long dayTimeBoundsStartValue;
    private Long dayTimeBoundsEndValue;
    private GroupByType groupBy;
    private final List<Table> tableObjects;
    private Config config = null;

    public DataSelectionConfig(Calendar dateBoundsStart,
                               Calendar dateBoundsEnd,
                               Calendar dayTimeBoundsStart,
                               Calendar dayTimeBoundsEnd,
                               List<Table> tableObjects,
                               GroupByType groupBy) {
        this.dateBoundsStartValue = cal2val(dateBoundsStart);
        this.dateBoundsEndValue = cal2val(dateBoundsEnd);
        this.dayTimeBoundsStartValue = cal2val(dayTimeBoundsStart);
        this.dayTimeBoundsEndValue = cal2val(dayTimeBoundsEnd);
        this.tableObjects = readonly(tableObjects);
        this.groupBy = groupBy;
    }

    public GroupByType getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupByType gb) {
        if (gb == null) {
            return;
        }
        groupBy = gb;
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
        builder.append("]");
        return builder.toString();
    }


    public void setDateBoundsEnd(Long time) {
        this.dateBoundsEndValue = time;
    }

    public void setDateBoundsEnd(Date time) {
        Long l = timeAsLongOrNull(time);
        this.dateBoundsEndValue = l;
    }

    public void setDateBoundsStart(Date time) {
        Long l = timeAsLongOrNull(time);
        this.dateBoundsStartValue = l;
    }

    public void setDateBoundsStart(Long time) {
        this.dateBoundsStartValue = time;
    }

    private static Long timeAsLongOrNull(Date time) {
        Long timeAsLong = null;
        if (time != null) {
            timeAsLong = time.getTime();
        }
        return timeAsLong;
    }

    public void setDayTimeBoundsEnd(Date time) {
        Long l = timeAsLongOrNull(time);
        this.dayTimeBoundsEndValue = l;
    }

    public void setDayTimeBoundsStart(Date time) {
        Long l = timeAsLongOrNull(time);
        this.dayTimeBoundsStartValue = l;
    }


    final void setConfig(Config config) {
        synchronized (this) {
            if (this.config != null) {
                throw new RuntimeException("cannot set multiple times");
            }
            this.config = config;
            for (Table t : tableObjects) {
                t.setConfig(this.config);
            }
        }
    }

    final Config getConfig() {
        return config;
    }
}
