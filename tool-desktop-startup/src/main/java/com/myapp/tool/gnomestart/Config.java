package com.myapp.tool.gnomestart;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import com.myapp.tool.gnomestart.programstate.IProcessManager;
import com.myapp.tool.gnomestart.programstate.IWindowManager;

public final class Config
{

    @SuppressWarnings("serial")
    static class ConfigException extends RuntimeException
    {
        private File configFile = null;

        public ConfigException(String message, Throwable cause) {
            super(message, cause);
        }

        public ConfigException(String message) {
            super(message);
        }

        public File getConfigFile() {
            return configFile;
        }

        private void setConfigFile(File configFile) {
            this.configFile = configFile;
        }

        @Override
        public String getMessage() {
            String message = super.getMessage();
            if (configFile != null) {
                message += " ConfigFile: " + configFile;
            }
            return message;
        }
    }

    @SuppressWarnings("serial")
    static class ItemConfigException extends ConfigException
    {

        private final StartItem item;

        public ItemConfigException(StartItem item, String message, Throwable cause) {
            super(message, cause);
            this.item = item;
        }

        public ItemConfigException(StartItem item, String message) {
            super(message);
            this.item = item;
        }

        @Override
        public String getMessage() {
            return "Invalid item: '" + item.getName() + "' " + super.getMessage();
        }
    }

    public static final class GuiSettings
    {

        private boolean showGui = false;
        private boolean guiWaitForPrecondition = false;

        private String windowTitle;
        private int windowPreferredWidth = 400;
        private int windowPreferredHeight = 400;
        private long waitbeforequit = 0L;
        private String logPattern = "%-5level [%thread]: %message%n";
        private Level logLevel = Level.toLevel("INFO");

        private GuiSettings() {
        }

        public long getWaitbeforequit() {
            return waitbeforequit;
        }

        public boolean isShowGui() {
            return showGui;
        }

        public boolean isGuiWaitForPrecondition() {
            return guiWaitForPrecondition;
        }

        public String getWindowTitle() {
            return windowTitle;
        }

        public int getWindowPreferredHeight() {
            return windowPreferredHeight;
        }

        public int getWindowPreferredWidth() {
            return windowPreferredWidth;
        }

        public String getLogPattern() {
            return logPattern;
        }

        public Level getLogLevel() {
            return logLevel;
        }
    }


    private static final String configFileName = ".tool-desktop-startup.properties";
    private static final String defaultConfigFilePath = "default-config.properties";

    private static final String PROPKEY_gui_show = "global.gui.show";
    private static final String PROPKEY_gui_waitforprecondition = "global.gui.waitforpreconditions";
    private static final String PROPKEY_gui_windowtitle = "global.gui.title";
    private static final String PROPKEY_gui_windowpreferredheight = "global.gui.height";
    private static final String PROPKEY_gui_windowpreferredwidth = "global.gui.width";
    private static final String PROPKEY_gui_waitbeforequit = "global.gui.waitbeforequit";
    private static final String PROPKEY_gui_log_pattern = "global.gui.log.pattern";
    private static final String PROPKEY_gui_log_level = "global.gui.log.level";

    private static final String PROPKEY_statepollinterval = "global.statuscheck.interval.millis";
    private static final String PROPKEY_coordinates_offset = "global.coordinates.offset";
    private static final String PROPKEY_windowmanager_implementation = "global.windowmanager.implementation";
    private static final String PROPKEY_processmanager_implementation = "global.processmanager.implementation";
    private static final String PROPKEY_stdout_log_level = "global.stdout.log.level";
    private static final String PROPKEY_stdout_log_pattern = "global.stdout.log.pattern";
    private static final String PROPKEY_switch_to_desktop = "global.switch.to.desktop";

    private static final String PROPKEY_preconditionitems = "preconditionitems";
    private static final String PROPKEY_startitems = "startitems";

    private static final String ITEMPROP_COORDS = "coords";
    private static final String ITEMPROP_DESKTOP = "desktop";
    private static final String ITEMPROP_STARTCOMMAND = "startcommand";
    private static final String ITEMPROP_VISIBLEREGEX = "visibleregex";
    private static final String ITEMPROP_STARTREGEX = "startregex";

    private static final long DEFAULT_POLL_INTERVAL_MILLIS = 1000L;

    private static boolean TEST = false;
    private static Config singleton = null;


    private Long pollIntervalMillis;
    private IWindowManager winMgr;
    private IProcessManager procMgr;
    private StartItem[] startItems, preconditionItems;
    private int[] globalCoordOffsets;
    private GuiSettings guiSettings;
    private Integer switchToDesktop;

    private Level consoleLogLevel;
    private String consoleLogPattern;


    // ///////// construction, destruction /////////////



    public Config(File config) { // constructor for explicit config parameter
        init(config);
    }

    static Config getInstance() {
        if (singleton == null) {
            synchronized (Config.class) {
                if (singleton == null) {
                    singleton = new Config();
                }
            }
        }
        return singleton;
    }

    private Config() { // constructor for singleton
        String home = System.getProperty("user.home");
        File userConfigFile = new File(home, configFileName);

        if (! userConfigFile.isFile()) {
            // write the default config file and quit:
            writeInitialConfigAndQuit(userConfigFile);
            return;
        }

        try {
            init(userConfigFile);
        } catch (ConfigException ce) {
            ce.setConfigFile(userConfigFile);
            throw ce;
        } catch (Exception e) {
            ConfigException ce = new ConfigException("Something bad happened while initialization!",
                                                     e);
            ce.setConfigFile(userConfigFile);
            throw ce;
        }
    }

    private void init(File config) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(config));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        pollIntervalMillis = parsePollInterval(p);
        
        String desk = p.getProperty(PROPKEY_switch_to_desktop);
        if (desk != null) {
            switchToDesktop = Integer.parseInt(desk);
        }
        
        globalCoordOffsets = parseCoords(p, PROPKEY_coordinates_offset);
        winMgr = parseWindowManager(p);
        procMgr = parseProcessManager(p);
        startItems = parseStartItems(p, PROPKEY_startitems);
        preconditionItems = parseStartItems(p, PROPKEY_preconditionitems);
        guiSettings = parseGuiSettings(p);
        initLogStuff(p);

        validate();
    }

    private void initLogStuff(Properties p) {
        String consoleLogLevelString = p.getProperty(PROPKEY_stdout_log_level);
        if (consoleLogLevelString != null) {
            consoleLogLevel = Level.toLevel(consoleLogLevelString);
        }

        String pattern = p.getProperty(PROPKEY_stdout_log_pattern);
        if (pattern != null) {
            this.consoleLogPattern = pattern;
        }
    }

    private void validate() {
        for (StartItem si : startItems) {
            if (! si.isStartupCandidate()) {
                throw new ItemConfigException(si,
                                              "Does not have a startcommand property!");
            }
        }
        for (StartItem si : preconditionItems) {
            if (si.isStartupCandidate()) {
                throw new ItemConfigException(si,
                                              "Precondition items must not have a startcommand!");
            }
            if (! si.isRunningRequired() && ! si.isVisibilityRequired()) {
                throw new ItemConfigException(si, "Every precondition must have"
                        + " at least a one of properties [startregex, visibleregex]!");
            }
        }
    }

    private static void writeInitialConfigAndQuit(File userConfigFile) {
        InputStream in = Config.class.getClassLoader()
                                     .getResourceAsStream(defaultConfigFilePath);
        OutputStream out = null;

        try {
            out = new FileOutputStream(userConfigFile);
            byte[] buf = new byte[256];
            for (int n = 0; (n = in.read(buf)) > 0; out.write(buf, 0, n));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("An error ocured while writing initial config file!");
            System.exit(2);

        } finally {
            close(in, out);
        }

        System.err.println("Default config file was written to " + userConfigFile
                + ". You should edit it and then start the program by "
                + "logout and login to your desktop manager.");
        System.exit(1);
    }



    // ///////// getter, setter /////////////



    public Integer getSwitchToDesktop() {
        return switchToDesktop;
    }

    public Level getConsoleLogLevel() {
        return consoleLogLevel;
    }

    public String getConsoleLogPattern() {
        return consoleLogPattern;
    }

    public int[] getGlobalCoordOffsets() {
        return globalCoordOffsets;
    }

    public IWindowManager getWinMgr() {
        return winMgr;
    }

    public IProcessManager getProcessMgr() {
        return procMgr;
    }

    public StartItem[] getStartItems() {
        if (TEST) { // just start a new xclock instance for testing purposes:
            StartItem[] testItems = { new StartItem("xclock-test", "xclock", new int[] {
                    100, 200, 300, 400 }, 2, "xclock", "(?i)xclock") };
            return testItems;
        }

        return startItems;
    }

    public StartItem[] getPreconditionItems() {
        if (TEST) {
            return new StartItem[0];
        }
        return preconditionItems;
    }

    public long getPollIntervalMillis() {
        Long result = pollIntervalMillis;
        if (result == null) {
            result = DEFAULT_POLL_INTERVAL_MILLIS;
        }
        return result.longValue();
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }



    // ///////////// parse methods ////////////



    private static Long parsePollInterval(Properties p) {
        String pollIntervalString = p.getProperty(PROPKEY_statepollinterval);

        if (pollIntervalString == null) {
            return null;
        }

        try {
            long pollInterval = Long.parseLong(pollIntervalString);
            return Long.valueOf(pollInterval);
        } catch (NumberFormatException e) {
            throw new ConfigException(PROPKEY_statepollinterval + "+ must be a number "
                    + pollIntervalString, e);
        }
    }

    private static int[] parseCoords(Properties config, String key) {
        String coordString = config.getProperty(key);

        if (coordString == null) {
            return null;
        }

        int[] coords = parseCoordsString(coordString, key);
        return coords;
    }

    private static IWindowManager parseWindowManager(Properties config) {
        return (IWindowManager) createInstance(config,
                                               PROPKEY_windowmanager_implementation);
    }

    private static IProcessManager parseProcessManager(Properties config) {
        return (IProcessManager) createInstance(config,
                                                PROPKEY_processmanager_implementation);
    }

    private static Object createInstance(Properties config, String propKey) {
        String className = config.getProperty(propKey);
        if (className == null) {
            throw new ConfigException("Invalid value: " + propKey + "=" + className);
        }
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> defaultConstructor = clazz.getConstructor();
            Object newInstance = defaultConstructor.newInstance();
            return newInstance;

        } catch (Exception e) {
            throw new ConfigException("Unable to create instance of " + propKey + "="
                    + className, e);
        }
    }

    private static int[] parseCoordsString(String coordString, String key4debug) {
        if (coordString == null) {
            return null;
        }
        int[] coords = new int[4]; // x,y,w,h
        String errorMsg = "coordinates must be 4 numbers! x,y,w,h  ";

        String[] split = coordString.split(",");
        if (split.length != 4) {
            throw new ConfigException(errorMsg + key4debug + "='" + coordString + "'");
        }

        for (int i = 0; i < split.length; i++) {
            String s = split[i];

            try {
                int value = Integer.parseInt(s.trim());
                coords[i] = value;
            } catch (NumberFormatException e) {
                throw new ConfigException("Invalid value: " + key4debug + "="
                        + coordString, e);
            }
        }
        return coords;
    }

    private static StartItem[] parseStartItems(Properties config, String key) {
        String itemListString = config.getProperty(key);

        String[] itemList = itemListString.split(",");
        StartItem[] result = new StartItem[itemList.length];

        for (int i = 0; i < itemList.length; i++) {
            String item = itemList[i].trim();
            StartItem startItem = parseStartItem(config, item);
            result[i] = startItem;
        }

        return result;
    }

    private static StartItem parseStartItem(Properties c, String name) {
        String coordString = parseItemProperty(c, name, ITEMPROP_COORDS);
        int[] coords = parseCoordsString(coordString, ITEMPROP_COORDS);
        String desktopString = parseItemProperty(c, name, ITEMPROP_DESKTOP);
        Integer desktop = parseDesktopNumber(desktopString, name + "." + ITEMPROP_DESKTOP);
        String startCmd = parseItemProperty(c, name, ITEMPROP_STARTCOMMAND);
        String startRegex = parseItemProperty(c, name, ITEMPROP_STARTREGEX);
        String visibleRegex = parseItemProperty(c, name, ITEMPROP_VISIBLEREGEX);


        StartItem i = new StartItem(name);
        i.setCoordinates(coords);
        i.setTargetDesktop(desktop);
        i.setStartCommand(startCmd);
        i.setStartRegex(startRegex);
        i.setVisibleRegex(visibleRegex);

        if (i.getVisibleRegex() == null && i.isLayoutAdjustmentRequired()) {
            throw new ItemConfigException(i, "If you want to layout a start-item "
                    + "by setting desktop or coords, a visibility regex is "
                    + "required to determine which window will be affected.");
        }

        return i;
    }

    private static Integer parseDesktopNumber(String desktopString, String key4debug) {
        if (desktopString == null) {
            return null;
        }
        try {
            return Integer.valueOf(desktopString);
        } catch (NumberFormatException e) {
            throw new ConfigException(key4debug + "='" + desktopString + "'", e);
        }
    }

    private static String parseItemProperty(Properties config,
                                            String itemName,
                                            String itemPropKey) {
        return config.getProperty(itemName + "." + itemPropKey);
    }

    private static boolean parseBoolean(Properties p, String key) {
        String s = p.getProperty(key);
        return Boolean.valueOf(s);
    }

    private static int parseInt(Properties p, String key) {
        String s = p.getProperty(key);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ConfigException(key + " = " + s, e);
        }
    }

    private static long parseLong(Properties p, String key) {
        String s = p.getProperty(key);
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new ConfigException(key + " = " + s, e);
        }
    }

    private static GuiSettings parseGuiSettings(Properties p) {
        GuiSettings settings = new GuiSettings();
        settings.showGui = parseBoolean(p, PROPKEY_gui_show);
        settings.guiWaitForPrecondition = parseBoolean(p, PROPKEY_gui_waitforprecondition);
        settings.windowTitle = p.getProperty(PROPKEY_gui_windowtitle);
        settings.windowPreferredHeight = parseInt(p, PROPKEY_gui_windowpreferredheight);
        settings.windowPreferredWidth = parseInt(p, PROPKEY_gui_windowpreferredwidth);
        settings.waitbeforequit = parseLong(p, PROPKEY_gui_waitbeforequit);
        if (settings.waitbeforequit < 0) {
            LoggerFactory.getLogger(Config.class).warn("configuration-property: "
                    + PROPKEY_gui_waitbeforequit
                    + " must not be negative. using 0 instead of.");
            settings.waitbeforequit = 0L;
        }
        String logPattern = p.getProperty(PROPKEY_gui_log_pattern);
        if (logPattern != null) {
            settings.logPattern = logPattern;
        }
        String logLevel = p.getProperty(PROPKEY_gui_log_level);
        if (logLevel != null) {
            settings.logLevel = Level.toLevel(logLevel);
        }
        return settings;
    }



    private static void close(Closeable... c) {
        if (c == null || c.length <= 0) {
            return;
        }
        for (int i = 0; i < c.length; i++) {
            Closeable closeable = c[i];
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }
}
