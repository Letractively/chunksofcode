package com.myapp.tool.gnomestart;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.read.CyclicBufferAppender;

import com.myapp.tool.gnomestart.Config.GuiSettings;
import com.myapp.tool.gnomestart.programstate.IProcessManager;
import com.myapp.tool.gnomestart.programstate.IWindowManager;
import com.myapp.tool.gnomestart.programstate.Proc;
import com.myapp.tool.gnomestart.programstate.Window;



public final class DesktopStarter
{


    public static interface IStateChangeListener // gui abstraction
    {
        /**
         * notifies the gui that it should refresh to show the changes to the user
         */
        void notifyStateChanged();
    }


    public static interface IGuiFactory // gui abstraction
    {
        /**
         * creates the gui for the desktop starter
         * @return the callback that will be notified when a change to the data happened
         */
        IStateChangeListener createGui();
    }


    private final Logger log;
    private CyclicBufferAppender<ILoggingEvent> logBuffer;


    private final Config config;
    private IStateChangeListener gui = null;
    private IGuiFactory guiFactory;

    private Set<StartItem> preconditionsToWaitFor = new LinkedHashSet<StartItem>();
    private Set<StartItem> preconditionsToWaitForRO = readonly(preconditionsToWaitFor);

    private Set<StartItem> startItemsToBeStarted = new LinkedHashSet<StartItem>();
    private Set<StartItem> startItemsToBeStartedRO = readonly(startItemsToBeStarted);

    private Set<StartItem> startItemsToWaitFor = new LinkedHashSet<StartItem>();
    private Set<StartItem> startItemsToWaitForRO = readonly(startItemsToWaitFor);

    private Set<StartItem> startItemsToBeLaidOut = new LinkedHashSet<StartItem>();
    private Set<StartItem> startItemsToBeLaidOutRO = readonly(startItemsToBeLaidOut);



    public DesktopStarter(Config cfg) {
        config = cfg;
        log = initLogger();
        config.getProcessMgr().setDesktopStarter(this);
        config.getWinMgr().setDesktopStarter(this);
        
        setActiveDesktopIfDefined();

        for (StartItem si : config.getStartItems()) {
            if (si.isLayoutAdjustmentRequired()) {
                startItemsToBeLaidOut.add(si);
            }
            if (si.isStartupCandidate()) {
                startItemsToBeStarted.add(si);
            }
            if (si.isRunningRequired() || si.isVisibilityRequired()) {
                startItemsToWaitFor.add(si);
            }
        }

        for (StartItem si : config.getPreconditionItems()) {
            if (si.isRunningRequired() || si.isVisibilityRequired()) {
                preconditionsToWaitFor.add(si);
            }
            if (si.isLayoutAdjustmentRequired()) {
                startItemsToBeLaidOut.add(si);
            }
        }
    }



    public void startup() {
        GuiSettings gs = config.getGuiSettings();

        if (gs.isShowGui() && ! gs.isGuiWaitForPrecondition()) {
            initGui();
        }

        waitForItems(preconditionsToWaitFor);

        if (gs.isShowGui() && gui == null) {
            initGui();
        }
        
        startProcesses();
        waitForItems(startItemsToWaitFor);
        layoutWindows();

        updateGuiCallback();
    }

    private void setActiveDesktopIfDefined() {
        Integer desktop = config.getSwitchToDesktop();
        
        if (desktop != null) {
            log.info("Will now set the active Desktop to: "+desktop);
            config.getWinMgr().setActiveDesktop(desktop.intValue());
        }
    }



    private void waitForItems(Set<StartItem> items) {
        Set<StartItem> allItems = new HashSet<StartItem>(items);
        Set<StartItem> todoItemsFromLastIteration;

        for (;;) {
            List<Proc> procs = config.getProcessMgr().determineProcessStates();
            IWindowManager winMgr = config.getWinMgr();
            List<Window> windows = winMgr.determineWindowStates();

            todoItemsFromLastIteration = new HashSet<StartItem>(items);

            for (Iterator<StartItem> iter = items.iterator(); iter.hasNext();) {
                StartItem i = iter.next();

                checkItemRunning(i, procs);
                checkItemVisibility(i, windows);

                if (! i.needsToWait()) {
                    log.info(i.getName() + " - Done with item!");
                    iter.remove();
                    updateGuiCallback();
                }
            }

            hideAllWindowsBeforeLayout(allItems, winMgr);

            if (items.isEmpty()) {
                break;
            }

            try {
                long pollIntervalMillis = config.getPollIntervalMillis();
                if (! items.containsAll(todoItemsFromLastIteration)) {
                    log.debug("Wait for remaining items: " + items);
                }
                Thread.sleep(pollIntervalMillis);

            } catch (InterruptedException e) {
            }
        }
    }



    private void
            hideAllWindowsBeforeLayout(Set<StartItem> allItems, IWindowManager winMgr) {
        
        if (log.isTraceEnabled()) {
            StringBuilder bui = new StringBuilder();
            for (StartItem i : allItems) {
                if (i.isVisible() && i.getCoordinates() != null) {
                    if (bui.length() == 0) {
                        bui.append("Hiding windows before layout is invoked: [");
                    } else {
                        bui.append(", ");
                    }
                    bui.append(i.getName());
                }
            }
            
            if (bui.length() > 0) {
                bui.append("]");
                log.trace(bui.toString());
            }
        }
        
        for (StartItem i : allItems) {
            if (i.isVisible() && i.getCoordinates() != null) {
                // hide window until we perform the layout afterwards:
                winMgr.hideWindow(i.getWindow().getWinId());
            }
        }
    }



    private void checkItemRunning(StartItem i, List<Proc> procs) {
        if (i.needsToWaitForRunning()) {
            Proc p = searchProcess(i, procs);
            if (p != null) {
                log.info(i.getName() + " - is running! pid=" + p.getPid());
                i.setProcess(p);
            }
        }
    }

    private void checkItemVisibility(StartItem i, List<Window> windows) {
        if (! i.needsToWaitForVisibility()) {
            return;
        }

        Window w = searchWindow(i, windows);
        if (w == null) {
            return;
        }

        i.setWindow(w);
        log.info(i.getName() + " - is visible! winid=" + w.getWinId() + ", pid="
                + w.getPid());
    }

    private static Proc searchProcess(StartItem i, List<Proc> procMap) {
        for (Proc e : procMap) {
            if (i.isStartedBy(e)) {
                return e;
            }
        }
        return null;
    }

    private static Window searchWindow(StartItem i, List<Window> windows) {
        for (Window e : windows) {
            if (i.isWindowedBy(e)) {
                return e;
            }
        }
        return null;
    }

    private void initGui() {
        gui = guiFactory.createGui();
    }

    void updateGuiCallback() {
        if (gui != null) {
            gui.notifyStateChanged();
        }
    }

    private Logger initLogger() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = lc.getLogger(DesktopStarter.class);

        logBuffer = new CyclicBufferAppender<ILoggingEvent>();
        logBuffer.setMaxSize(128);
        logger.addAppender(logBuffer);

        Logger root = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        Layout<ILoggingEvent> layout = root.getAppender("STDOUT").getLayout();

        if (layout instanceof PatternLayout) {
            PatternLayout pl = (PatternLayout) layout;
            pl.setPattern(config.getConsoleLogPattern());
            pl.start();
            root.setLevel(config.getConsoleLogLevel());
        }

        logBuffer.start();
        logger.debug("Logger initialized");

        return logger;
    }

    void setGuiFactory(IGuiFactory guiFactory) {
        this.guiFactory = guiFactory;
    }

    private void startProcesses() {
        IProcessManager procStateManager = config.getProcessMgr();
        Iterator<StartItem> iterator = startItemsToBeStarted.iterator();

        for (StartItem item; iterator.hasNext();) {
            item = iterator.next();

            final String startCommand = item.getStartCommand();
            log.info(item.getName() + " - Now starting with '" + startCommand + "'");
            final Process p = procStateManager.start(startCommand);
            waitForProcessInBackground(p, item.getName());

            iterator.remove();
            updateGuiCallback();
        }
    }

    private void waitForProcessInBackground(final Process p, final String itemName) {
        new Thread(new Runnable() {
            public void run() {
                Integer waitFor = null;
                String stdout = null;
                String errout = null;

                try {
                    waitFor = p.waitFor();
                    Thread.sleep(2000);
                    stdout = readStream(p.getInputStream());
                    errout = readStream(p.getErrorStream());

                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    log.debug(itemName + " Command started. Exit value=" + waitFor);
                    throw new RuntimeException(itemName, e);
                }


                debugIfNotEmpty(itemName + "- ERROUT", errout);
                debugIfNotEmpty(itemName + "- STDOUT", stdout);
            }
        }).start();
    }

    private void debugIfNotEmpty(String streamName, String s) {
        if (! log.isDebugEnabled() || s == null) {
            return;
        }
        s = s.trim();
        if (! s.isEmpty()) {
            log.debug("------- " + streamName + " START -------");
            log.debug(s);
            log.debug("------- " + streamName + "  END  -------");
        }
    }

    private static String readStream(InputStream in) throws IOException {
        InputStreamReader r = new InputStreamReader(in);
        StringBuilder bui = new StringBuilder();

        char[] buf = new char[1024];
        for (int charsRead = 0; (charsRead = r.read(buf)) > 0;) {
            bui.append(buf, 0, charsRead);
        }

        return bui.toString();
    }

    private void layoutWindows() {
        Iterator<StartItem> iter = startItemsToBeLaidOut.iterator();

        for (StartItem si = null; iter.hasNext();) {
            si = iter.next();
            layoutWindow(si);
            iter.remove();
            updateGuiCallback();
        }
    }

    private void layoutWindow(StartItem item) {
        if (! item.isLayoutAdjustmentRequired()) {
            return;
        }
        log.info(item.getName() + " - Will now layout item ...");

        Window w = item.getWindow();
        String winid = w.getWinId();
        if (winid == null) {
            throw new IllegalStateException();
        }

        IWindowManager winStateManager = config.getWinMgr();

        // mv to desktop
        Integer desktop = item.getTargetDesktop();
        if (desktop != null) {
            log.info(item.getName() + " - Move to desktop " + desktop + " ...");
            winStateManager.applyDesktop(w.getWinId(), desktop);
        }

        // set coordinates
        int[] coordinates = item.getCoordinates();
        if (coordinates != null) {
            log.debug(item.getName() + " - Coords without offset "
                    + Arrays.toString(coordinates));
            coordinates = sumIntArrays(coordinates, config.getGlobalCoordOffsets());
            log.info(item.getName() + " - Apply coordinates "
                    + Arrays.toString(coordinates) + " ...");
            winStateManager.applyCoordinates(w.getWinId(), coordinates);
        }
    }

    private static int[] sumIntArrays(int[] coordinates, int[] offsets) {
        if (coordinates == null || offsets == null) {
            return coordinates;
        }
        if (coordinates.length != 4 || offsets.length != 4) {
            throw new IllegalArgumentException("coordinates:"
                    + Arrays.toString(coordinates) + ", " + "offsets:"
                    + Arrays.toString(offsets));
        }
        int[] result = new int[4];
        for (int i = 0; i < result.length; i++) {
            result[i] = coordinates[i] + offsets[i];
        }
        return result;
    }

    public Config getConfig() {
        return config;
    }



    public Set<StartItem> getPreconditionsToWaitFor() {
        return preconditionsToWaitForRO;
    }

    public Set<StartItem> getStartItemsToBeStarted() {
        return startItemsToBeStartedRO;
    }

    public Set<StartItem> getStartItemsToWaitFor() {
        return startItemsToWaitForRO;
    }

    public Set<StartItem> getStartItemsToBeLaidOut() {
        return startItemsToBeLaidOutRO;
    }

    public CyclicBufferAppender<ILoggingEvent> getLogBuffer() {
        return logBuffer;
    }

    public Logger getLog() {
        return log;
    }

    private static <T> Set<T> readonly(Set<T> s) {
        return Collections.unmodifiableSet(s);
    }
}
