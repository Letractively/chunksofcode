package com.plankenauer.fmcontrol.config;

import java.io.Serializable;


public final class Config implements Serializable
{

    private static final long serialVersionUID = - 5812757901588874439L;

    private final String name;
    private final String title;
    private final String description;

    private final ConnectionConfig connection;
    private final DataSelectionConfig datasource;

    private transient String debugString = null;

    public Config(String name,
                  String title,
                  String description,
                  ConnectionConfig connection,
                  DataSelectionConfig datasource) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.connection = connection;
        this.datasource = datasource;
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

    public DataSelectionConfig getDatasource() {
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
}
