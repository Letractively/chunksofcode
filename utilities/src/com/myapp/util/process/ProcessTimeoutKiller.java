package com.myapp.util.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.util.format.TimeFormatUtil;






/**
 * acts as a registry where processes are registered with a timeout duration.
 * after the process has timed out, it will be killed.
 * 
 * @author andre
 */
public final class ProcessTimeoutKiller {
    
    private static final Logger _LOG = LoggerFactory.getLogger(ProcessTimeoutKiller.class);
    private static final long _HUNT_INTERVAL_MILLIS = 2000;
    private static final ReentrantLock _LOCK = new ReentrantLock();
    
    
    private static final Map<Process, Long> _DEATH_LIST = new HashMap<Process, Long>();
    private static final Map<Process, String> _META_DATA = new HashMap<Process, String>();
    
    static {
        Thread timeOutWatcherThreadInstance = new Thread() {
            @Override
            public void run() {
                _LOG.trace("          process-timeout-killer-registry-thread started. interval: {} ms", _HUNT_INTERVAL_MILLIS);
                
                for (;;) {
                    try {
                        Thread.sleep(_HUNT_INTERVAL_MILLIS);
                        
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    
                    halali();
                }
            }
        };
        
        timeOutWatcherThreadInstance.setName("killer");
        timeOutWatcherThreadInstance.start();
    }

    private ProcessTimeoutKiller() {
    }
    
    private static void halali() {
        try {
            _LOCK.lock();
            long now = System.currentTimeMillis();
            Iterator<Map.Entry<Process, Long>> itr;

            for (itr = _DEATH_LIST.entrySet().iterator(); itr.hasNext();) {
                Map.Entry<Process, Long> e = itr.next();
                long killAt = e.getValue().longValue();
                
                if (now >= killAt) {
                    Process p = e.getKey();
                    _LOG.error("          kill process (timeout) '{}'", getDescriptionFor(p));

                    try {
                        p.destroy();
                        
                    } catch (Exception x) {
                        _LOG.trace("          error while destroying process!", x);
                        
                    } finally {
                        _DEATH_LIST.remove(p);
                    }
                }
            }
        
        } finally {
            _LOCK.unlock();
        }
    }

    public static void registerKillTimeout(Process p, long timeoutMillis, String description, boolean suppressLogging) {
        try {
            _LOCK.lock(); 
            Long killDate = Long.valueOf(System.currentTimeMillis() + timeoutMillis); 
            
            if (_LOG.isTraceEnabled() && ! suppressLogging)
                _LOG.trace("          register timeout ({})  -  at: {}  - id: '{}'", new Object[]{TimeFormatUtil.getTimeLabel(timeoutMillis), TimeFormatUtil.getDateLabel(killDate), description});
            
            _DEATH_LIST.put(p, killDate);
            _META_DATA.put(p, description);
        
        } finally {
            _LOCK.unlock();
        }
    }

    public static void cancelKillTimeout(Process p) {
        try {
            _LOCK.lock();          
            _DEATH_LIST.remove(p);
            _META_DATA.remove(p);

        } finally {
            _LOCK.unlock();
        }
    }
    
    static String getDescriptionFor(Process p) {
        return _META_DATA.get(p);
    }
    
}
