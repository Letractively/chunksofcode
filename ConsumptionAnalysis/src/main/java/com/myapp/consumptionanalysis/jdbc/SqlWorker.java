package com.myapp.consumptionanalysis.jdbc;

import java.sql.Connection;

public interface SqlWorker
{
    public void run(Connection c) throws Exception;
}