package com.myapp.consumptionanalysis.sql;

import java.io.Serializable;
import java.util.Date;

public class DataRow implements Serializable
{
    private static final long serialVersionUID = 675087742274978724L;

    private String label;
    private Object[] values;
    private Date timestamp;

    public DataRow(Object[] values) {
        this.values = values;
    }

    public int getValueColumnCount() {
        return values.length;
    }

    public Object getValue(int valueColIndex) {
        return values[valueColIndex];
    }

    public String getLabel() {
        return label;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
}
