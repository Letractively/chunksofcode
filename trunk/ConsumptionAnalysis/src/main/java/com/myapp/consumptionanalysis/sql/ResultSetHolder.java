package com.myapp.consumptionanalysis.sql;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public class ResultSetHolder implements Iterable<DataRow>
{
    private static final Logger log = Logger.getLogger(ResultSetHolder.class);
    private static final String NL = System.getProperty("line.separator");

    public static final int LABEL_COLUMN_INDEX = 0;
    private int valueColumCount = - 1;

    private List<DataRow> rows;

    public ResultSetHolder(List<DataRow> data) {
        checkDataIntegrity(data);
        this.rows = Collections.unmodifiableList(data);
    }

    private void checkDataIntegrity(List<DataRow> data) {
        for (DataRow dataRow : data) {
            int rowsValCount = dataRow.getValueColumnCount();

            if (valueColumCount < 0) {
                valueColumCount = rowsValCount;

            } else if (valueColumCount != rowsValCount) {
                if (log.isDebugEnabled()) {
                    log.debug("row label: " + dataRow.getLabel());

                    for (int i = 0, n = rowsValCount; i < n; i++) {
                        log.debug("row value: " + i + " = " + dataRow.getValue(i));
                    }
                }
                throw new RuntimeException("all rows are expected to have "
                        + "the same number of value-columns! first row had "
                        + valueColumCount + " columns, but this row has " + rowsValCount);
            }
        }
    }

    public int size() {
        return rows.size();
    }

    public Iterator<DataRow> iterator() {
        return rows.iterator();
    }

    public DataRow get(int index) {
        return rows.get(index);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        Iterator<DataRow> iterator = rows.iterator();

        for (int i = 0; iterator.hasNext(); i++) {
            String num = String.valueOf(i);
            DataRow r = iterator.next();
            for (int n = 4 - num.length(); n-- > 0; b.append(" "));
            b.append(i);
            b.append(" ");

            for (int n = 15 - r.getLabel().length(); n-- > 0; b.append(" "));
            b.append(r.getLabel());
            b.append(" - | ");

            for (int j = 0, n = r.getValueColumnCount(); j < n; j++) {
                Object object = r.getValue(j);
                String s = String.valueOf(object);
                for (int m = 20 - s.length(); m-- > 0; b.append(" "));
                b.append(s);
                b.append(" | ");
            }
            b.append(NL);
        }

        return b.toString();
    }
}
