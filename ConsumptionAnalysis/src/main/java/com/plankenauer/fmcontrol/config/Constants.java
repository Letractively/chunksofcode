package com.plankenauer.fmcontrol.config;

public interface Constants
{

    enum GroupByType {
        HOUR, DAY, MONTH, YEAR, TOTAL
    }

    String CK_TITLE = "title";
    String CK_DESCRIPTION = "description";

    String CK_CONNECTION_HOSTNAME = "connection.hostname";
    String CK_DISPLAY_DEBUG_DUMPSQL = "display.debug.dumpsql";
    String CK_DISPLAY_RESULT_TABLE = "display.show.as.table";
    
    String CK_CONNECTION_PASSWORD = "connection.password";
    String CK_CONNECTION_PORTNUMBER = "connection.portnumber";
    String CK_CONNECTION_USER = "connection.user";

    String CK_DATA_TABLES = "data.tables";

    String CK_DATA_FILTER_DATE_BOUNDS = "data.filter.date-bounds";
    String CK_DATA_FILTER_DAYTIME_BOUNDS = "data.filter.daytime-bounds";
//    String CK_DATA_FILTER_SQL_FILTER = "data.filter.sql-filter";

    String CK_USER_CAN_CHANGE_TABLES            = "user.can-change.tables";
    String CK_USER_CAN_CHANGE_COLUMNS           = "user.can-change.columns";
    String CK_USER_CAN_CHANGE_DAYTIME_BOUNDS    = "user.can-change.daytime-bounds";
    String CK_USER_CAN_CHANGE_DATE_BOUNDS       = "user.can-change.date-bounds";
    String CK_USER_CAN_CHANGE_GROUPBY           = "user.can-change.groupby";
    
    String CK_DISPLAY_DATE_FORMAT_PATTERN = "date.format";

    String CK_TABLEDEF_SCHEMA = ".database";
    String CK_TABLEDEF_TABLE = ".table";
    String CK_TABLEDEF_DATE_COLUMN = ".date-column";
    String CK_TABLEDEF_TIME_COLUMN = ".time-column";
    String CK_TABLEDEF_VALUE_COLUMN_PATTERN = ".value-#.column";
    String CK_TABLEDEF_VALUE_COLUMN_FACTOR = ".value-#.factor";

    String CK_TABLEDEF_VALUE_LABEL_PATTERN = "data.value-#.label";
    String CK_DATA_GROUP_TYPE = "data.group.type";


}
