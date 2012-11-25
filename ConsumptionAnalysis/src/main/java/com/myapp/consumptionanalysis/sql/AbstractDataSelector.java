package com.myapp.consumptionanalysis.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.myapp.consumptionanalysis.chart.barchart.BarChartDataSelector;
import com.myapp.consumptionanalysis.config.Config;



public abstract class AbstractDataSelector
{

    private static final Logger log = Logger.getLogger(BarChartDataSelector.class);


    protected final Config config;


    public AbstractDataSelector(Config config) {
        this.config = config;
    }

    protected abstract DataRow fetchDataRow(ResultSet rs) throws Exception;

    protected final Config getConfig() {
        return config;
    }

    public ResultSetHolder selectData() throws Exception {
        Connect connect = new Connect(getConfig());
        final List<DataRow> rows = new LinkedList<>();

        connect.executeWorkerWithLogging(new SqlWorker() {
            @Override
            public void run(Connection c) throws Exception {
                String sql = getConfig().createSqlQuery();
                try {
                    Statement stm = c.createStatement();
                    ResultSet rs = stm.executeQuery(sql);

                    while (rs.next()) {
                        DataRow dataRow = fetchDataRow(rs);
                        rows.add(dataRow);
                    }

                } catch (Exception e) {
                    e = new Exception("Ein Fehler ist während der "
                            + "SQL-Abfrage aufgetreten. sqlcode: " + sql, e);
                    log.error(e);
                    throw e;
                }
            }
        });

        ResultSetHolder rsh = new ResultSetHolder(rows);
        return rsh;
    }

}
