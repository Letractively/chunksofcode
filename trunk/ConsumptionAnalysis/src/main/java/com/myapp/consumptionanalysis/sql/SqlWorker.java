package com.myapp.consumptionanalysis.sql;

import java.sql.Connection;

public interface SqlWorker
{
    public void run(Connection c) throws Exception;
}