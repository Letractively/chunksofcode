package com.plankenauer.fmcontrol.jdbc;

import java.sql.Connection;

public interface SqlWorker
{
    public void run(Connection c) throws Exception;
}