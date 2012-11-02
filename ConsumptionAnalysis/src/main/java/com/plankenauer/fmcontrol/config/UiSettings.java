package com.plankenauer.fmcontrol.config;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class UiSettings implements Serializable
{

    private static final long serialVersionUID = - 5041250440271460760L;
    private static Logger log = Logger.getLogger(UiSettings.class);

    private static final boolean DEFAULT_dateBoundsAreChangeable = false;
    private static final boolean DEFAULT_daytimeBoundsAreChangeable = false;
    private static final boolean DEFAULT_tablesAreChangeable = false;
    private static final boolean DEFAULT_valueColumnsAreChangeable = false;
    private static final boolean DEFAULT_groupbyIsChangeable = false;


    private final boolean showSqlDebug;
    private final boolean showResultTable;
    private final Boolean dateBoundsAreChangeable;
    private final Boolean daytimeBoundsAreChangeable;
    private final Boolean tablesAreChangeable;       // if multiple tables are defined
    private final Boolean valueColumnsAreChangeable; // if multiple columns are defined
    private final Boolean groupbyIsChangeable;


    public boolean isGroupbyChangeable() {
        if (groupbyIsChangeable == null) {
            return DEFAULT_groupbyIsChangeable;
        }
        return groupbyIsChangeable.booleanValue();
    }

    private Config config = null;

    private final Set<Table> chosenTables = new TreeSet<>();
    private final Set<Integer> chosenColumns = new TreeSet<>();



    public UiSettings(boolean showSqlDebug,
                      boolean showResultTable,
                      Boolean dateBoundsAreChangeable,
                      Boolean daytimeBoundsAreChangeable,
                      Boolean tablesAreChangeable,
                      Boolean valueColumnsAreChangeable,
                      Boolean groupbyIsChangeable) {
        this.showSqlDebug = showSqlDebug;
        this.showResultTable = showResultTable;
        this.dateBoundsAreChangeable = dateBoundsAreChangeable;
        this.daytimeBoundsAreChangeable = daytimeBoundsAreChangeable;
        this.tablesAreChangeable = tablesAreChangeable;
        this.valueColumnsAreChangeable = valueColumnsAreChangeable;
        this.groupbyIsChangeable = groupbyIsChangeable;
    }

    /**
     * @return the showSqlDebug
     */
    public boolean isShowSqlDebug() {
        return showSqlDebug;
    }

    public boolean isShowResultTable() {
        return showResultTable;
    }

    /**
     * @return the dateBoundsAreChangeable
     */
    public Boolean isDateBoundsChangeable() {
        if (dateBoundsAreChangeable == null) {
            return DEFAULT_dateBoundsAreChangeable;
        }
        return dateBoundsAreChangeable;
    }

    /**
     * @return the daytimeBoundsAreChangeable
     */
    public Boolean isDaytimeBoundsChangeable() {
        if (daytimeBoundsAreChangeable == null) {
            return DEFAULT_daytimeBoundsAreChangeable;
        }
        return daytimeBoundsAreChangeable;
    }

    /**
     * @return the tablesAreChangeable
     */
    public Boolean isTableSelectionChangeable() {
        if (tablesAreChangeable == null) {
            return DEFAULT_tablesAreChangeable;
        }
        return tablesAreChangeable;
    }

    /**
     * @return the valueColumnsAreChangeable
     */
    public Boolean isColumnsSelectionChangeable() {
        if (valueColumnsAreChangeable == null) {
            return DEFAULT_valueColumnsAreChangeable;
        }
        return valueColumnsAreChangeable;
    }


    final void setConfig(Config config) {
        synchronized (this) {
            if (this.config != null) {
                throw new RuntimeException("cannot set multiple times");
            }
            this.config = config;

            List<Table> tables = config.getDatasource().getTables();
            chosenTables.clear();
            chosenTables.addAll(tables);

            Table table = tables.get(0);
            Map<Integer, String> columnLabels = table.getColumnLabels();
            chosenColumns.clear();
            chosenColumns.addAll(columnLabels.keySet());
        }
    }

    final Config getConfig() {
        return config;
    }



    /// chosen tables and columns: /////



    public Set<Integer> getChosenColumns() {
        return Collections.unmodifiableSet(chosenColumns);
    }

    public boolean addChosenColumn(Integer e) {
        log.debug("Column was chosen: " + e);
        return chosenColumns.add(e);
    }

    public boolean removeChosenColumn(Integer o) {
        return chosenColumns.remove(o);
    }

    public boolean isColumnChosen(Integer o) {
        return chosenColumns.contains(o);
    }

    public void removeAllChosenColumns() {
        log.debug("Column selection cleared.");
        chosenColumns.clear();
    }



    public Set<Table> getChosenTables() {
        return Collections.unmodifiableSet(chosenTables);
    }

    public boolean addChosenTable(Table e) {
        log.debug("Table was chosen: " + e.getAlias());
        return chosenTables.add(e);
    }

    public boolean removeChosenTable(Table o) {
        return chosenTables.remove(o);
    }

    public void removeAllChosenTables() {
        log.debug("Table selection cleared.");
        chosenTables.clear();
    }

    public boolean isTableChosen(Table o) {
        return chosenTables.contains(o);
    }

}
