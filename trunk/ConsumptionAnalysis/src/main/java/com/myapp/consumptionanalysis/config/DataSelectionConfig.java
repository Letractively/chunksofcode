package com.myapp.consumptionanalysis.config;



import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.myapp.consumptionanalysis.config.Constants.GroupByType;
import com.myapp.consumptionanalysis.util.DateUtils;

public class DataSelectionConfig implements Serializable
{


    private static final long serialVersionUID = 1470288040217770328L;

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(DataSelectionConfig.class);

    private Date dateBoundsStart;
    private Date dateBoundsEnd;
    private Date dayTimeBoundsStart;
    private Date dayTimeBoundsEnd;

    private GroupByType groupBy;
    private final List<Table> tableObjects;
    private Config config = null;

    private DataSelectionConfig(List<Table> tables) {
        this.tableObjects = readonly(tables);
    }

    public DataSelectionConfig(List<Table> tables,
                               Date dateBoundsStart,
                               Date dateBoundsEnd,
                               Date dayTimeBoundsStart,
                               Date dayTimeBoundsEnd,
                               GroupByType groupBy) {
        this(tables);

        setDateBoundsStart(dateBoundsStart);
        setDateBoundsEnd(dateBoundsEnd);
        setDayTimeBoundsStart(dayTimeBoundsStart);
        setDayTimeBoundsEnd(dayTimeBoundsEnd);

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
    public Date getDateBoundsStartDate() {
        return dateBoundsStart;
    }

    /**
     * defines the bounds of the date (not the daytime!) we are selecting data
     */
    public Date getDateBoundsEndDate() {
        return dateBoundsEnd;
    }

    /**
     * defines the bounds of the daytime we are selecting data
     */
    public Date getDayTimeBoundsEndDate() {
        return dayTimeBoundsEnd;
    }

    /**
     * defines the bounds of the daytime we are selecting data
     */
    public Date getDayTimeBoundsStartDate() {
        return dayTimeBoundsStart;
    }

    public void setDateBoundsEnd(Date time) {
        this.dateBoundsEnd = DateUtils.normalizeDateBoundDate(time);
    }


    public void setDateBoundsStart(Date time) {
        dateBoundsStart = DateUtils.normalizeDateBoundDate(time);
    }

    public void setDayTimeBoundsEnd(Date time) {
        dayTimeBoundsEnd = DateUtils.normalizeDayTimeDate(time);
    }

    public void setDayTimeBoundsStart(Date time) {
        dayTimeBoundsStart = DateUtils.normalizeDayTimeDate(time);
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



    private static <T> List<T> readonly(List<T> l) {
        if (l == null) {
            return null;
        }
        return Collections.unmodifiableList(l);
    }



    /////////////////// STRING STUFF /////////////////
    //////////////////  AND HELPERS ///////////////



    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("DataSelectionConfig [");


        b.append("dateBounds={");
        if (dateBoundsStart != null) {
            b.append(DateUtils.dayToString(dateBoundsStart));
        }
        b.append(" - ");
        if (dateBoundsEnd != null) {
            b.append(DateUtils.dayToString(dateBoundsEnd));
        }
        b.append("}, ");


        b.append("dayTimeBounds={");
        if (dayTimeBoundsStart != null) {
            b.append(DateUtils.daytimeToString(dayTimeBoundsStart));
        }
        b.append(" - ");
        if (dayTimeBoundsEnd != null) {
            b.append(DateUtils.daytimeToString(dayTimeBoundsEnd));
        }
        b.append("}, ");


        b.append("groupBy=");
        if (groupBy != null) {
            b.append(groupBy);
        }
        b.append(", ");


        b.append("tableObjects=");
        if (tableObjects != null) {
            b.append(tableObjects);
        }


        b.append("]");
        return b.toString();
    }

    public String getDateBoundsString() {
        return getDateBoundsString(this);
    }

    public String getDayTimeBoundsString() {
        return getDayTimeBoundsString(this);
    }

    public String getGroupByString() {
        return getGroupByString(this);
    }

    public String getTableSelectionString() {
        return getTableSelectionString(this);
    }


    static String getDateBoundsString(DataSelectionConfig config) {
        final Date boundsStart = config.getDateBoundsStartDate();
        final Date boundsEnd = config.getDateBoundsEndDate();

        StringBuilder msg = new StringBuilder();
        msg.append("Zeitraum: ");

        if (boundsStart == null || boundsEnd == null) {
            msg.append("alles ");
        }
        if (boundsStart != null) {
            msg.append("von ");
            msg.append(DateUtils.formatDate(boundsStart));
            msg.append(" ");
        }
        if (boundsEnd != null) {
            msg.append("bis ");
            msg.append(DateUtils.formatDate(boundsEnd));
            msg.append(" ");
        }

        return msg.toString().trim();
    }

    static String getDayTimeBoundsString(DataSelectionConfig config) {
        final Date boundsStart = config.getDayTimeBoundsStartDate();
        final Date boundsEnd = config.getDayTimeBoundsEndDate();

        StringBuilder msg = new StringBuilder();
        msg.append("Tageszeit: ");

        if (boundsStart == null && boundsEnd == null) {
            msg.append("ganztägig ");
        } else {
            if (boundsStart == null || boundsEnd == null) {
                msg.append("alles ");
            }
            if (boundsStart != null) {
                msg.append("von ");
                msg.append(DateUtils.formatDayTime(boundsStart));
                msg.append(" ");
            }
            if (boundsEnd != null) {
                msg.append("bis ");
                msg.append(DateUtils.formatDayTime(boundsEnd));
                msg.append(" ");
            }
        }

        return msg.toString().trim();
    }

    static String getGroupByString(DataSelectionConfig c) {
        String text;

        switch (c.groupBy) {
            case HOUR:
                text = "Gruppiert in Stunden";
                break;
            case DAY:
                text = "Gruppiert in Tage";
                break;
            case MONTH:
                text = "Gruppiert in Monate";
                break;
            case YEAR:
                text = "Gruppiert in Jahre";
                break;
            case TOTAL:
                text = "Gesamtsumme über alle verfügbaren Daten";
                break;
            default:
                text = "FEHLER: unbekannte Gruppierung: '" + c.groupBy + "'";
                break;
        }

        return text;
    }

    public static String getTableSelectionString(DataSelectionConfig c) {
        StringBuilder msg = new StringBuilder();
        final List<Table> tables = c.getTables();
        for (Iterator<Table> iterator = tables.iterator(); iterator.hasNext();) {
            Table t = iterator.next();

            msg.append(t.getAlias());

            if (iterator.hasNext()) {
                msg.append(", ");
            }
        }

        return msg.toString();
    }
}
