package com.myapp.test;

import static java.lang.System.currentTimeMillis;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ConnectionTimeoutTest {

    /** @param args
     * @throws MalformedURLException */
    public static void main(String[] args) throws MalformedURLException {
        URL u1 = new URL("http://vbox0");
        log("testing connectivity to " + u1 + " ...");
        boolean c1 = isConnectable(u1, 3000);
        log("connect to " + u1 + (c1 ? " was successful." : " failed."));
        log("");

        URL u2 = new URL("http://bytesare.us");
        log("testing connectivity to " + u2 + " ...");
        boolean c2 = isConnectable(u2, 1000);
        log("connect to " + u2 + (c2 ? " was successful." : " failed."));
    }

    
    
    private static final int DONE = 0; // do we have determined a result?
    private static final int SUCCESS = 1; // was the connection established?
    private static final long POLL_INTERVAL_MILLIS = 900;

    public static boolean isConnectable(final String url, final long timeoutMillis) {
        try {
            return isConnectable(new URL(url), timeoutMillis);
        } catch (MalformedURLException e) {
            throw new RuntimeException("invalid url='"+url+"'", e);
        }
    }
    
    public static boolean isConnectable(final URL u, final long timeoutMillis) {
        final long waitBeforeStartTimeout = 300;
        final boolean[] flags = new boolean[2];
        flags[DONE] = false;
        flags[SUCCESS] = false;
        
        Runnable connectCode = new Runnable() {
            @Override
            public void run() {
                URLConnection connection = null;
                long startTime = currentTimeMillis();

                try {
                    log("opening connection ...");
                    connection = u.openConnection();

                    log("connection opened. try to connect ...");
                    connection.connect();

                    synchronized (flags) {
                        if (!flags[DONE]) {
                            flags[DONE] = true;
                            flags[SUCCESS] = true;

                            long duration = currentTimeMillis() - startTime;
                            log("SUCCESS! connected after " + duration
                                + " ms to " + u + "!");
                        }
                    }
                } catch (IOException e) {
                    synchronized (flags) {
                        if (!flags[DONE]) {
                            flags[DONE] = true;
                            log("ERROR! while connecting to: " + u + " - " + e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        
        Runnable timeoutCode = new Runnable() {
            @Override
            public void run() {
                log("timeout started.");
                try {
                    Thread.sleep(timeoutMillis-waitBeforeStartTimeout);
                    synchronized (flags) {
                        if (!flags[DONE]) {
                            flags[DONE] = true;
                            log("TIMEOUT! after " + timeoutMillis + " ms. " + u);
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };


        new Thread(connectCode).start();
        
        
        // wait a bit for fast connections.
        // if connection was established quickly enough, timeout is not needed.
        sleepWell(waitBeforeStartTimeout);
        synchronized (flags) {
            if (!flags[DONE]) {
                new Thread(timeoutCode).start();
            }
        }

        // wait for either connection or timeout:
        for (long waitMillis = 0L;;) {
            synchronized (flags) {
                if (flags[DONE]) {
                    break;
                }
            }

            sleepWell(POLL_INTERVAL_MILLIS);

            synchronized (flags) {
                if (!flags[DONE]) {
                    waitMillis += POLL_INTERVAL_MILLIS;
                    log("waited for connection: " + waitMillis + " (timeout: "
                        + timeoutMillis + ")");
                }
            }
        }

        return flags[SUCCESS];
    }

    private static void sleepWell(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void log(String msg) {
        System.out.println(msg);
    }
}
