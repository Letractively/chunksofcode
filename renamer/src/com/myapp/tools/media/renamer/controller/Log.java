package com.myapp.tools.media.renamer.controller;

import static com.myapp.tools.media.renamer.controller.Msg.msg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenameProcessListener;
import com.myapp.tools.media.renamer.model.IRenamer;

/**
 * the very own logging facility for the application
 * 
 * @author andre
 * 
 */
public final class Log {

    /**
     * implementors of this may register to the log. all logrecords passed to
     * the log will be handed to the registered listeners
     * 
     * @author andre
     * 
     */
    public static interface IMessageListener {

        /**
         * handles the logrecord just passed to the log class.
         * 
         * @param record
         *            the record that occured
         */
        public void messageOccured(LogRecord record);
    }

    /**
     * helds the last x elements that were added. you may recieve the last x
     * elements that were added.
     * 
     * @author andre
     * 
     * @param <T>
     *            the type of the elements contained in this object
     */
    private static final class LastXElements<T> {

        final List<T> l = new ArrayList<T>();
        final int x;
        final int doubleX;

        LastXElements(int x) {this.x = x; doubleX = x * 2;}

        /**
         * adds the element to the queue
         * 
         * @param t
         *            the element
         */
        void addElement(T t) {
            synchronized (l) {
                if (l.size() >= doubleX) {
                    //rm oldest elements from the beginning of the list
                    for (int i = 0, s = l.size() - x; i <= s; i++) l.remove(0);

                    //the size is now x -1, after adding it will be x
                    assert l.size() == x - 1
                                : "size != " + (x - 1) + ", but: " + l.size();
                }

                l.add(t);
            }
        }

        /**
         * @return a list containing the last elements
         */
        List<T> getLastXElements() {
            synchronized (l) {
                if (l.isEmpty()) return new ArrayList<T>(0);

                int newestElement =  l.size() - 1;
                int xThElement = newestElement - x;
                xThElement = xThElement < 0 ? 0 : xThElement;

                return l.subList(xThElement, newestElement);
            }
        }
    }

    
    private static final Collection<IMessageListener> msgListeners;
    private static final Logger DEFAULT_LOGGER;
    private static final DateFormat FORMAT;
    private static final String lineSeparator;
    private static final LastXElements<LogRecord> historyX;
    private static final Formatter FORMATTER;
    
    private static List<LogRecord> records = new ArrayList<LogRecord>();

    // init static fields ...
    static {
        msgListeners = new HashSet<IMessageListener>();
        DEFAULT_LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
//        DEFAULT_LOGGER = new MutableLogger();
        FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        lineSeparator = System.getProperty("line.separator");
        historyX = new LastXElements<LogRecord>(50);

        FORMATTER = new Formatter() {
            public String format(LogRecord r) {
                for (IMessageListener l : msgListeners) l.messageOccured(r);

                historyX.addElement(r);

                StringBuilder bui = new StringBuilder(getPrefix(r.getLevel()));

                for (int i = bui.length(); i <= 10; i++) bui.append(' ');

                bui.append(FORMAT.format(new Date(r.getMillis())));

                for (int i = bui.length(); i < 35; i++) bui.append(' ');

                bui.append("  #  ");
                bui.append(r.getSourceClassName().replaceFirst(
                                    "com.myapp.tools.media.renamer.", ""));
                bui.append(".");
                bui.append(r.getSourceMethodName());

                for (int i = bui.length(); i < 95; i++) bui.append(' ');

                bui.append("  #  ");
                bui.append(r.getMessage());
                bui.append(lineSeparator);

                return bui.toString();
            }
        };

        Logger l = DEFAULT_LOGGER;
        Handler[] arr = new Handler[0];

        for (int foo = 0; foo < 1000; foo++) {
            arr = l.getHandlers();
            if (arr != null && arr.length > 0)
                break;
            l = l.getParent();
        }

        arr[0].setFormatter(FORMATTER);
    }



    /**
     * no instance needed
     */
    private Log() {}


    /**
     * returns the global logger object instance
     * 
     * @return the global logger object instance
     */
    public static final Logger defaultLogger() {
        return DEFAULT_LOGGER;
    }

    /**
     * adds a message listener to the list. if a record reaches the logger, all
     * listeners will be notified.
     * 
     * @param l
     *            the listener to register
     * @param getLastXMessages
     *            if the new listener wants to be notified about the last saved
     *            records
     */
    public static void addMessageListener(IMessageListener l,
                                          boolean getLastXMessages) {
        msgListeners.add(l);

        if (getLastXMessages)
            for (LogRecord record : historyX.getLastXElements())
                l.messageOccured(record);
    }

    /**
     * calculates the prefix for a given loglevel, debug, info or fail
     * 
     * @param l
     *            the level we want the prefix for
     * @return the prefix for this level
     */
    private static String getPrefix(Level l) {
        // 1000 is highest, return 5 * for 1000 in steps of 200
        int i = l.intValue();
        i  %= 5000;
        i /= 200;

        switch (i) {
            case 0 :
            case 1 :
            case 2 :
                return "[debug] *";
            case 3 :
            case 4 :  
                return "[info]";
        }

        return "[fail] !!! ";
    }
    
    /**
     * @return the records
     */
    public static List<LogRecord> getLogRecords() {
        return records;
    }


    public static class LogProcessListener implements IRenameProcessListener {

        @Override
        public void processFailed(Throwable t, IRenamable f) {
            synchronized (DEFAULT_LOGGER) {
                DEFAULT_LOGGER.info(msg("Log.processListener.errorOccured")
                            .replace("#oldFile#", f.getSourceObject().getAbsolutePath())
                            .replace("#newFile#", f.getNewAbsolutePath()));
                DEFAULT_LOGGER.info(Util.stackTraceToString(t));
            }
        }
    
        @Override
        public void processFileSuccess() {
            synchronized (DEFAULT_LOGGER) {
                DEFAULT_LOGGER.info(msg("Log.processListener.fileWasRenamed"));
            }
        }
    
        @Override
        public void processFileStart(IRenamable f) {      
            synchronized (DEFAULT_LOGGER) {  
                DEFAULT_LOGGER.info(msg("Log.processListener.fileWillNowBeRenamed")
                            .replace("#oldFile#", f.getSourceObject().getAbsolutePath())
                            .replace("#newFile#", f.getNewAbsolutePath()));
            }
        }
    
        @Override
        public void processFinished() {
            synchronized (DEFAULT_LOGGER) {
                DEFAULT_LOGGER.info(msg("Log.processListener.processFinished"));
            }
        }
    
        @Override
        public void processStarting(IRenamer renamer) {
            synchronized (DEFAULT_LOGGER) {
                DEFAULT_LOGGER.info(msg("Log.processListener.processStart"));
            }
        }
    }

//    public static synchronized void mute() {
//        ((MutableLogger)DEFAULT_LOGGER).setMuted(true);
//    }
//    public static synchronized void unMute() {
//        ((MutableLogger)DEFAULT_LOGGER).setMuted(false);
//    }
//    public static synchronized void setMuted(boolean muted) {
//        ((MutableLogger)DEFAULT_LOGGER).setMuted(muted);
//    }
//    public static synchronized void toggleMuted() {
//        ((MutableLogger)DEFAULT_LOGGER).setMuted( 
//                ! ((MutableLogger)DEFAULT_LOGGER).isMuted());
//    }
//
//
//  /**
//   * delegates log messages to a logger instance.
//   * may be muted.
//   * 
//   * @author andre
//   *
//   */
//  private static final class MutableLogger extends Logger {
//      
//      private boolean muted = false;
//      private final Logger delegate = Logger.getLogger(GLOBAL_LOGGER_NAME);
//      
//      public MutableLogger() {
//          super(null, null);
//      }
//              
//      public synchronized void setMuted(boolean mute) {
//          muted = mute;
//      }
//      
//      public synchronized boolean isMuted() {
//          return muted;
//      }
//
//      public void addHandler(Handler handler) throws SecurityException {
//          delegate.addHandler(handler);
//      }
//
//      public void config(String msg) {
//          if (muted) return;
//          
//          delegate.config(msg);
//      }
//
//      public void entering(String sourceClass,
//                           String sourceMethod,
//                           Object param1) {
//          if (muted) return;
//          
//          delegate.entering(sourceClass, sourceMethod, param1);
//      }
//
//      public void entering(String sourceClass, 
//                           String sourceMethod,
//                           Object[] params) {
//          if (muted) return;
//          
//          delegate.entering(sourceClass, sourceMethod, params);
//      }
//
//      public void entering(String sourceClass, String sourceMethod) {
//          if (muted) return;
//          
//          delegate.entering(sourceClass, sourceMethod);
//      }
//
//      public void exiting(String srcClass, String srcMethod, Object rslt) {
//          if (muted) return;
//          
//          delegate.exiting(srcClass, srcMethod, rslt);
//      }
//
//      public void exiting(String sourceClass, String sourceMethod) {
//          if (muted) return;
//          
//          delegate.exiting(sourceClass, sourceMethod);
//      }
//
//      public void fine(String msg) {
//          if (muted) return;
//          delegate.fine(msg);
//      }
//
//      public void finer(String msg) {
//          if (muted) return;
//          delegate.finer(msg);
//      }
//
//      public void finest(String msg) {
//          if (muted) return;
//          delegate.finest(msg);
//      }
//
//      public Filter getFilter() {
//          return delegate.getFilter();
//      }
//
//      public Handler[] getHandlers() {
//          return delegate.getHandlers();
//      }
//
//      public Level getLevel() {
//          return delegate.getLevel();
//      }
//
//      public String getName() {
//          return delegate.getName();
//      }
//
//      public Logger getParent() {
//          return delegate.getParent();
//      }
//
//      public ResourceBundle getResourceBundle() {
//          return delegate.getResourceBundle();
//      }
//
//      public String getResourceBundleName() {
//          return delegate.getResourceBundleName();
//      }
//
//      public boolean getUseParentHandlers() {
//          return delegate.getUseParentHandlers();
//      }
//
//      public void info(String msg) {
//          if (muted) return;
//          delegate.info(msg);
//      }
//
//      public boolean isLoggable(Level level) {
//          return delegate.isLoggable(level);
//      }
//
//      public void log(Level level, String msg, Object param1) {
//          if (muted) return;
//          delegate.log(level, msg, param1);
//      }
//
//      public void log(Level level, String msg, Object[] params) {
//          if (muted) return;
//          delegate.log(level, msg, params);
//      }
//
//      public void log(Level level, String msg, Throwable thrown) {
//          delegate.log(level, msg, thrown);
//      }
//
//      public void log(Level level, String msg) {
//          if (muted) return;
//          delegate.log(level, msg);
//      }
//
//      public void log(LogRecord record) {
//          if (muted) return;
//          delegate.log(record);
//      }
//
//      public void logp(Level level, String sourceClass, String sourceMethod,
//              String msg, Object param1) {
//          if (muted) return;
//          delegate.logp(level, sourceClass, sourceMethod, msg, param1);
//      }
//
//      public void logp(Level level, String sourceClass, String sourceMethod,
//              String msg, Object[] params) {
//          if (muted) return;
//          delegate.logp(level, sourceClass, sourceMethod, msg, params);
//      }
//
//      public void logp(Level level, String sourceClass, String sourceMethod,
//              String msg, Throwable thrown) {
//          if (muted) return;
//          delegate.logp(level, sourceClass, sourceMethod, msg, thrown);
//      }
//
//      public void logp(Level level, String sourceClass, String sourceMethod,
//              String msg) {
//          if (muted) return;
//          delegate.logp(level, sourceClass, sourceMethod, msg);
//      }
//
//      public void logrb(Level level, String sourceClass, String sourceMethod,
//              String bundleName, String msg, Object param1) {
//          if (muted) return;
//          delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg,
//                  param1);
//      }
//
//      public void logrb(Level level, String sourceClass, String sourceMethod,
//              String bundleName, String msg, Object[] params) {
//          if (muted) return;
//          delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg,
//                  params);
//      }
//
//      public void logrb(Level level, String sourceClass, String sourceMethod,
//              String bundleName, String msg, Throwable thrown) {
//          if (muted) return;
//          delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg,
//                  thrown);
//      }
//
//      public void logrb(Level level, String sourceClass, String sourceMethod,
//              String bundleName, String msg) {
//          if (muted) return;
//          delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg);
//      }
//
//      public void removeHandler(Handler handler) throws SecurityException {
//          delegate.removeHandler(handler);
//      }
//
//      public void setFilter(Filter newFilter) throws SecurityException {
//          delegate.setFilter(newFilter);
//      }
//
//      public void setLevel(Level newLevel) throws SecurityException {
//          delegate.setLevel(newLevel);
//      }
//
//      public void setParent(Logger parent) {
//          delegate.setParent(parent);
//      }
//
//      public void setUseParentHandlers(boolean useParentHandlers) {
//          delegate.setUseParentHandlers(useParentHandlers);
//      }
//
//      public void severe(String msg) {
//          delegate.severe(msg);
//      }
//
//      public void throwing(String sourceClass, String sourceMethod,
//              Throwable thrown) {
//          delegate.throwing(sourceClass, sourceMethod, thrown);
//      }
//
//      public void warning(String msg) {
//          delegate.warning(msg);
//      }
//  }
}
