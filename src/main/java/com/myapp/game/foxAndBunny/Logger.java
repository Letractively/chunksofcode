package com.myapp.game.foxAndBunny;

public class Logger {
    
    /**
     * global flag for debug features..
     */
    public static final boolean IS_DEBUG = false;

    @SuppressWarnings("unused") // dead code if ! IS_DEBUG
    public static void debug(String msg) {
        if ( ! IS_DEBUG) {
            return;
        }
        System.err.println(msg);
    }
    
    public static void info(String msg) {
        System.out.println(msg);
    }
    
}
