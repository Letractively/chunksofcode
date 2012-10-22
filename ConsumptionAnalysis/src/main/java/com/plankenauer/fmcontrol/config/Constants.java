package com.plankenauer.fmcontrol.config;

public interface Constants
{

    enum GroupByType {
        HOUR, DAY, MONTH, YEAR
    }

    String CK_TITLE = "title";
    String CK_DESCRIPTION = "description";

    String CK_CONNECTION_HOSTNAME = "connection.hostname";
    String CK_CONNECTION_PASSWORD = "connection.password";
    String CK_CONNECTION_PORTNUMBER = "connection.portnumber";
    String CK_CONNECTION_USER = "connection.user";

    String CK_DATA_TABLES_CHOOSEABLE = "data.tables.chooseable";
    String CK_DATA_TABLES_FIXED = "data.tables.fixed";

    String CK_DATA_FILTER_DATE_BOUNDS = "data.filter.date-bounds";
    String CK_DATA_FILTER_DAYTIME_BOUNDS = "data.filter.daytime-bounds";
    String CK_DATA_FILTER_SQL_FILTER = "data.filter.sql-filter";

    String CK_TABLEDEF_SCHEMA = ".database";
    String CK_TABLEDEF_TABLE = ".table";
    String CK_TABLEDEF_DATE_COLUMN = ".date-column";
    String CK_TABLEDEF_TIME_COLUMN = ".time-column";
    String CK_TABLEDEF_VALUE_COLUMN_PATTERN = ".value-#.column";
    String CK_TABLEDEF_VALUE_COLUMN_FACTOR = ".value-#.factor";

    String CK_TABLEDEF_VALUE_LABEL_PATTERN = "data.value-#.label";
    String CK_DATA_GROUP_TYPE = "data.group.type";


}
