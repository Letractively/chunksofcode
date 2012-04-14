package com.myapp.web.jsf2.getstarted.eyetracker.dbdummy;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DbServerStartup implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        DataBaseDummy.getInstance().persist();
        System.err.println("DbServerStartup.contextDestroyed("
                           + "DataBaseDummy.getInstance().persist();)");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        DataBaseDummy.getInstance();
        System.err.println("DbServerStartup.contextInitialized("
                           + "DataBaseDummy.getInstance();)");
    }
}
