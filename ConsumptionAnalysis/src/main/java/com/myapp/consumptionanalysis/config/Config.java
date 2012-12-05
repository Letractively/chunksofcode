package com.myapp.consumptionanalysis.config;

import java.io.Serializable;

import com.myapp.consumptionanalysis.chart.barchart.BarChartQueryBuilder;


public final class Config implements Serializable
{

    private static final long serialVersionUID = - 5812757901588874439L;

    private final String name;
    private final String title;
    private final String description;

    private final ConnectionConfig connection;
    private final DataSelectionConfig datasource;
    private final UiSettings uiSettings;

    private transient String debugString = null;

    public Config(String name,
                  String title,
                  String description,
                  ConnectionConfig connection,
                  DataSelectionConfig datasource,
                  UiSettings uiSettings) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.connection = connection;
        this.datasource = datasource;
        this.uiSettings = uiSettings;

        this.connection.setConfig(this);
        this.datasource.setConfig(this);
        this.uiSettings.setConfig(this);
    }

    public UiSettings getUiSettings() {
        return uiSettings;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ConnectionConfig getConnection() {
        return connection;
    }

    public DataSelectionConfig getSelectionConfig() {
        return datasource;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Config [");

        builder.append("name=");
        builder.append(name);
        builder.append(", ");

        builder.append("title=");
        builder.append(title);

        builder.append("]");
        return builder.toString();
    }

    public String getDebugString() {
        return debugString;
    }

    void setDebugString(String debugString) {
        this.debugString = debugString;
    }

    public String createSqlQuery() {
        return new BarChartQueryBuilder(this).generateQuery();
    }
}
