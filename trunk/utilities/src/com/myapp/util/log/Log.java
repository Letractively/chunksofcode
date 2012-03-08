package com.myapp.util.log;

@Deprecated
public class Log {
    public synchronized static void logln(String msg) {
        System.out.println(getCallerClassName() + " " + msg);
    }
    
    private static String getCallerClassName() {
        Throwable t = new Throwable();
        t.getStackTrace();
        StackTraceElement[] trace = t.getStackTrace();
        
        for (int i = 0; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String clazz = e.getClassName();
            
            if (clazz != Log.class.getName()) {
                String method = e.getMethodName();
                return clazz+"."+method+":"+e.getLineNumber();
            }
        }
        
        return null;
    }
}
