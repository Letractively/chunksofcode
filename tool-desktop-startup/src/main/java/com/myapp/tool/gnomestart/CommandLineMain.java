package com.myapp.tool.gnomestart;

import java.io.File;

import ch.qos.logback.classic.Logger;

import com.myapp.tool.gnomestart.DesktopStarter.IStateChangeListener;
import com.myapp.tool.gnomestart.DesktopStarter.IGuiFactory;
import com.myapp.tool.gnomestart.gui.swing.StatusWindow;

public class CommandLineMain
{

    private static final String CLIARG_CONFIGFILE = "-f=";

    public static void main(String[] args) throws Exception {
        Config cfg = null;

        if (args != null && args.length == 1 && args[0].startsWith(CLIARG_CONFIGFILE)) {
            File fileArg = new File(args[0].replaceFirst(CLIARG_CONFIGFILE, ""));

            if (fileArg.isFile()) {
                if (! fileArg.canRead()) {
                    throw new Exception("cannot read file: " + fileArg);
                }
                cfg = new Config(fileArg);
            }
        }

        if (cfg == null) {
            cfg = Config.getInstance(); // load file from userdir by default
        }

        final DesktopStarter starter = new DesktopStarter(cfg);
        final Logger log = starter.getLog();

        starter.setGuiFactory(new IGuiFactory() {
            @Override
            public IStateChangeListener createGui() {
                log.debug("Starting gui ...");
                return StatusWindow.createAndShowGui(starter);
            }
        });

        starter.startup();
        
        long waitbeforequit = cfg.getGuiSettings().getWaitbeforequit();

        
        if (waitbeforequit >= 0L) {
            long waitUntil = System.currentTimeMillis() + waitbeforequit;
            
            // max 1000, else waitbeforequit
            long interval = waitbeforequit >= 1000L ? 1000L : waitbeforequit;
            
            try {
                for (;;) {
                    long now = System.currentTimeMillis();
                    if (now >= waitUntil) {
                        break;
                    }
                    log.debug("Will quit in "+(waitUntil-now)+" ms...");
                    starter.updateGuiCallback();
                    Thread.sleep(interval);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("Bye.");
        System.exit(0);
    }
}
