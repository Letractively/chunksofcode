package com.myapp.consumptionanalysis.web;

import java.io.File;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.WebApplication;

import com.myapp.consumptionanalysis.config.ConfigRepository;
import com.myapp.consumptionanalysis.web.querypage.DisplayQueryPage;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see com.myapp.consumptionanalysis.web.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
    static final String JNDI_NAME_CONFIG_REPO = "plankenauer/configRepository";
    private static Logger log = Logger.getLogger(WicketApplication.class);
    private static ConfigRepository globalRepo = null;


    public ConfigRepository getGlobalConfigRepo() {
        if (globalRepo == null) {
            synchronized (WicketApplication.class) {
                if (globalRepo == null) {
                    log.debug("creating global config repository...");
                    String path = null;

                    try {
                        InitialContext ic = new InitialContext();
                        Context c = (Context) ic.lookup("java:comp/env");
                        path = (String) c.lookup(JNDI_NAME_CONFIG_REPO);
                        log.debug("looked up path: " + path);

                    } catch (Exception e) {
                        path = null;
                        log.error("error during jndi lookup: " + e);
                    }

                    if (path == null) {
                        String fallback = "C:\\config-repository";
                        if (new File(fallback).isDirectory()) {
                            path = fallback;
                            log.info("using fallback: " + fallback);
                        }
                    }

                    if (path == null) {
                        String fallback = "target/test-classes/testConfigRepository";
                        if (new File(fallback).isDirectory()) {
                            path = fallback;
                            log.error("using fallback2: " + fallback);
                        }
                    }

                    log.info("global repository path: " + path);
                    globalRepo = new ConfigRepository(path);
                }
            }
        }

        return globalRepo;
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        // add your configuration here


        mountPage("/top", HomePage.class);
        mountPage("/query", DisplayQueryPage.class);


//        IJQueryLibrarySettings librarySettings = new JQueryLibrarySettings();
//        librarySettings.setJQueryUIReference(new JQueryPluginResourceReference(WicketApplication.class,
//                                                                               "jquery-ui-1.8.23.js"));
//        librarySettings.setJQueryReference(new PackageResourceReference(WicketApplication.class,
//                                                                        "jquery-1.8.0.js"));
//        this.setJavaScriptLibrarySettings(librarySettings);

        getMarkupSettings().setStripWicketTags(true);
    }
}
