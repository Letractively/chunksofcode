package com.myapp.tool.gnomestart.programstate.impl.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.tool.gnomestart.programstate.IWindowManager;
import com.myapp.tool.gnomestart.programstate.Window;

/** a window implementation based on wmctrl
 * 
 * @author andre */
public class WmctrlWinStateMgr implements IWindowManager {    
    
    public WmctrlWinStateMgr() {}
    
    @Override
    public Map<String, Window> determineWindowStates() {
        Map<String, Window> map = new TreeMap<String, Window>();
        String state = IO.readProcOutput("wmctrl", "-l", "-p", "-G");
        BufferedReader br = new BufferedReader(new StringReader(state));
        String wmctrl_line = null;
        
        try {
            while ((wmctrl_line = br.readLine()) != null) {
                Window ws = parseWindowStateObj(wmctrl_line);
                if (ws.getPid() <= 0) { // this may occur on some strange windows...
                    continue;
                }
                if (map.containsValue(ws)) {
                    throw new RuntimeException("duplicate value: "+String.valueOf(ws));
                }
                map.put(ws.getWinId(), ws);
            }
        } catch (IOException e) {
            throw new RuntimeException(wmctrl_line,e);
        }
        
        return map;
    }

    @Override
    public void applyDesktop(String winId, int desktop) {
        // -r <WIN> -t <DESK>   Move the window to the specified desktop.
        IO.readProcOutput("wmctrl", "-i", "-r", winId, "-t", Integer.toString(desktop));
    }

    @Override
    public void applyCoordinates(String winId, int[] coords) {
        if (coords == null || coords.length != 4) {
            throw new IllegalArgumentException(Arrays.toString(coords));
        }
        
        //        The value of -1 may appear in place of
        //        any of the <X>, <Y>, <W> and <H> properties
        //        to left the property unchanged.
        String coordsStr = "0,"+coords[0]+","+coords[1]+","+coords[2]+","+coords[3];

        //        The -i option may be used to interpret the argument
        //        as a numerical window ID represented as a decimal
        //        number. If it starts with "0x", then
        //        it will be interpreted as a hexadecimal number.
        // -r <WIN> -e <MVARG>  Resize and move the window around the desktop.
        IO.readProcOutput("wmctrl", "-i", "-r", winId, "-e", coordsStr);
    }
    
    
    /** <pre>        
    windowId   desktop                       client machine
    |          |  procId x    y    w    h    |          window title
    |          |  |      |    |    |    |    |           |
    0x00e00022  2 1775   21   86   844  953  buenosaires andre@buenosaires: ~ 
    0x02200003 -1 1788   0    2334 1920 33   buenosaires Bottom Expanded Edge Pa
    0x01e0001e -1 1767   0    0    3840 1200 buenosaires x-nautilus-desktop
    0x05e00022  3 6963   12   97   816  823  buenosaires andre@buenosaires: cat 
    0x06000007  3 0      58   529  752  658          N/A GroovyConsole
    0x05800037  5 4405   2300 271  391  139  buenosaires VLC media player </pre>
    */      
    private static final Pattern WMCTRL_OUTPUT_PATTERN = Pattern.compile(
        "(?ix) " +
        "^ \\s* "+       
      // (1 windowId )      (2 desk)      (3 pid)      
        "(0x[a-f0-9]+) \\s+ (-?\\d+) \\s+ (\\d+ ) \\s+ " +
      // ( 4 x  )      (5 y   )      (6 w )      (7 h )      
        "(-?\\d+) \\s+ (-?\\d+) \\s+ (\\d+) \\s+ (\\d+) \\s+ "+
      // (8client)      (9 title)         
        "( \\S+  ) \\s+ (  .*   ) " +
        "\\s* $" 
    );
    
    private static Window parseWindowStateObj(String wmctrl_line) {
        if (wmctrl_line == null) {
            throw new NullPointerException();
        }
        Matcher m = WMCTRL_OUTPUT_PATTERN.matcher(wmctrl_line);
        if (! m.matches()) {
            throw new RuntimeException("'"+wmctrl_line+"' does not match pattern '"+WMCTRL_OUTPUT_PATTERN.pattern()+"'");
        }
        
        String winid = m.group(1);
        int pid = Integer.parseInt(m.group(3));
        String title = m.group(9);
        
        try {
            Window w = new Window(winid, title, pid);
            return w;
        } catch (Exception e) {
            throw new RuntimeException(wmctrl_line, e);
        }
    }

    @Override
    public void setWindowStatus(String winId, int status) {
        // TODO Auto-generated method stub
    }
}
