package com.myapp.tool.gnomestart;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.myapp.tool.gnomestart.programstate.IProcessManager;
import com.myapp.tool.gnomestart.programstate.IWindowManager;


public final class Config {
    
    private static boolean TEST = false;
    private static Config singleton = null;

    
    private int[] globalCoordOffsets = {-10, -44, 0, 0};
    private String winMgrClass = "com.myapp.tool.gnomestart.programstate.impl.linux.WmctrlWinStateMgr";
    private String procMgrClass = "com.myapp.tool.gnomestart.programstate.impl.linux.LinuxProcStateMgr";
    
    
    
    
    private Config() {}
    
    public static Config getInstance() {
        if (singleton == null) {
            synchronized (Config.class) {
                if (singleton == null) {
                    singleton = new Config();
                }
            }
        }
        return singleton;
    }
    
    

    public StartItem[] getStartItems() {
        if (TEST) { // just start a new xclock instance for testing purposes:
            StartItem testItem = new StartItem("xclock");
            testItem.setStartCommand("xclock");
            testItem.setStartRegex("xclock");
            testItem.setVisibleRegex("xclock");
            testItem.setCoords(100, 200, 200, 400);
            testItem.setDesktop(2);
            
            StartItem[] testItems =  { testItem };
            return testItems;
        }

        StartItem firefox      = new StartItem("firefox");
        StartItem skype        = new StartItem("skype");
        StartItem empathy      = new StartItem("empathy");
        StartItem xterm        = new StartItem("xterm");
        StartItem transmission = new StartItem("transmission");
        StartItem lifrea       = new StartItem("lifrea");
        StartItem thunderbird  = new StartItem("thunderbird");
        StartItem gmpc         = new StartItem("gmpc");

        firefox      .setStartCommand("firefox");
        skype        .setStartCommand("skype");
        empathy      .setStartCommand("empathy");
        xterm        .setStartCommand("xterm -bg black -fg white -fn 7x13 -e /bin/zsh");
        transmission .setStartCommand("transmission");
        lifrea       .setStartCommand("liferea");
        thunderbird  .setStartCommand("thunderbird");
        gmpc         .setStartCommand("gmpc");
        
        firefox      .setStartRegex("(?i)firefox");
        skype        .setStartRegex("(?i)skype");
        empathy      .setStartRegex("(?i)empathy");
        xterm        .setStartRegex("(?i)xterm");
        transmission .setStartRegex("(?i)transmission");
        lifrea       .setStartRegex("(?i)liferea");
        thunderbird  .setStartRegex("(?i)thunderbird");
        gmpc         .setStartRegex("(?i)gmpc");

        firefox      .setVisibleRegex("(?i)firefox");
        skype        .setVisibleRegex("(?i)skype");
        empathy      .setVisibleRegex("(?i)(contact list|Kontaktliste)");
        xterm        .setVisibleRegex("(?ix) "+getUsername()+" @ "+getHostname()+" \\s* : \\s* ~");
        transmission .setVisibleRegex("(?i)transmission");
        lifrea       .setVisibleRegex("(?i)liferea");
        thunderbird  .setVisibleRegex("(?i)thunderbird");
        gmpc         .setVisibleRegex(null);
        
//      firefox      .setCoords(  25,  98, 1014, 953);
        firefox      .setCoords(  25,  98, 1286, 953);
        skype        .setCoords(1645,  98,  266, 495);
        empathy      .setCoords(1335,  98,  286, 495);
        xterm        .setCoords(  23,  98,  844, 953);
        transmission .setCoords(1335, 642,  576, 409);
        lifrea       .setCoords(1067,  98,  844, 953);
//      thunderbird  .setCoords(  25,  98, 1286, 953);
        thunderbird  .setCoords(  25,  98, 1014, 953);
        gmpc         .setCoords( 892,  98, 1019, 953);
        
//      firefox      .setDesktop(1);
        firefox      .setDesktop(0);
        skype        .setDesktop(0);
        empathy      .setDesktop(0);
        xterm        .setDesktop(2);
        transmission .setDesktop(0);
        lifrea       .setDesktop(1);
//      thunderbird  .setDesktop(0);
        thunderbird  .setDesktop(1);
        gmpc         .setDesktop(2);
        
        StartItem[] startItems =  { 
            firefox      ,
            skype        ,
            empathy      ,
            xterm        ,
            transmission ,
            lifrea       ,
            thunderbird  ,
            gmpc         ,
        };

        return startItems;
    }


    public StartItem[] getPreconditionItems() {
        StartItem desktop = new StartItem("desktop");
        StartItem upperPanel = new StartItem("upperPanel");
        StartItem lowerPanel = new StartItem("lowerPanel");

        desktop.setStartCommand(null);
        upperPanel.setStartCommand(null);
        lowerPanel.setStartCommand(null);

        desktop.setStartRegex(null);
        upperPanel.setStartRegex(null);
        lowerPanel.setStartRegex(null);

        desktop.setVisibleRegex("(?i)x-nautilus-desktop");
        upperPanel.setVisibleRegex("(?i)(Oberes Kanten-Panel|Top Expanded Edge Panel)");
        lowerPanel.setVisibleRegex("(?i)(Unteres Kanten-Panel|Bottom Expanded Edge Panel)"); 
        
        StartItem[] wait4 = { desktop, upperPanel, lowerPanel };
        return wait4;
    }
    
    public int[] getGlobalCoordOffsets() {
        return globalCoordOffsets;
    }
    
    public String getHostname() {// TODO properties file
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        String name = addr.getHostName();
        return name;
    }
    
    public String getUsername() {// TODO properties file
        return System.getProperty("user.name");
    }
    
    public IWindowManager createWinStateManager() {
        IWindowManager result = (IWindowManager) createInstance(winMgrClass);
        return result;
    }

    public IProcessManager createProcStateManager() {
        IProcessManager result = (IProcessManager) createInstance(procMgrClass);
        return result;
    }

    private static Object createInstance(String cn) {
        try {
            Class<?> clazz = Class.forName(cn);
            Constructor<?> defaultConstructor = clazz.getConstructor();
            Object newInstance = defaultConstructor.newInstance();
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void serialize(XMLStreamWriter w, Config cfg) throws XMLStreamException {
        w.writeStartDocument();
        w.writeStartElement("config");
        serializeGeneralConfig(w, cfg);
        w.writeEndElement();
        w.writeEndDocument();
    }

    private static void serializeGeneralConfig(XMLStreamWriter w, Config cfg) throws XMLStreamException {
        w.writeStartElement("general");
        w.writeStartElement("process-manager");
        w.writeEndElement();
    }
}
