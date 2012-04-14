package com.myapp.tool.gnomestart;


import java.util.Map;
import java.util.regex.*;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.myapp.tool.gnomestart.programstate.*;


public class StartItem {

    
    private final String name;
    private String startCommand = null;
    private int[] coords = null;
    private Integer desk = null;
    private Matcher startMatcher = null;
    private Matcher visibleMatcher = null;
    private String winid = null;
    
    private boolean started = false;
    private boolean layouted = false;
    
    
    public StartItem(String name) {
        this.name = name;
    }
    public StartItem(String name,
                     String startCommand,
                     int[] coords,
                     Integer desk,
                     String procCmdMatcher,
                     String winTitleMatcher) {
        this.name = name;
        this.startCommand = startCommand;
        this.coords = coords;
        this.desk = desk;
        setStartRegex(procCmdMatcher);
        setVisibleRegex(winTitleMatcher);
    }
    
    



    /**
     * @return the bash command used to start this item. null if it won't be
     * started.
     */
    public String getStartCommand() {
        return startCommand;
    }
    /**
     * @param c the bash command used to start this item. null if it won't be
     * started.
     */
    public void setStartCommand(String c) {
        startCommand = c;
    }
    
    /**
     * @param regex used to determine if the process of this item is started
     *          (the command of the process will be matched against this regex)
     */
    public void setStartRegex(String regex) {
        if (regex == null) {
            startMatcher = null;
            return;
        } 
        startMatcher = Pattern.compile(regex).matcher("foo");
    }
    /**
     * @param regex used to determine if the window of this item is visible
     *          (the title of the window will be matched against this regex)
     */
    public void setVisibleRegex(String regex) {
        if (regex == null) {
            visibleMatcher = null;
            return;
        }
        visibleMatcher = Pattern.compile(regex).matcher("foo"); 
    }

    public String getVisibleRegex() {
        if (visibleMatcher == null) {
            return null;
        }
        return visibleMatcher.pattern().pattern();
    }

    public String getStartRegex() {
        if (startMatcher == null) {
            return null;
        }
        return startMatcher.pattern().pattern();
    }
    
    /** 
     *  determine if this start item's process is started and its window is visible.
     *  (depends on the configuration of the item)
     *  
     * @param windowStatus
     * @return 
     */
    public boolean isStartedAndVisible(Map<String, Window> winStates, 
                                       Map<Integer, Proc> procStates) {
        if (started) {
            return true;
        }
        
        for (Window w : winStates.values()) {
            if (visibleMatcher != null) {
                String title = w.getWinTitle();
                if (! visibleMatcher.reset(title).find()) {
                    continue;
                }
            }
            
            if (startMatcher != null) {
                Proc ps = procStates.get(w.getPid());
                String command = ps.getCommand();
                if ( ! startMatcher.reset(command).find()) {
                    continue;
                }
            }
            
            winid = w.getWinId();
            break;
        }
        
        if (winid != null) {
            started = true;
        }
        
        return started;
    }
    
    /**
     * @return an int[] {x,y,w,h} if these are set, null otherwise
     */
    public int[] getCoordinates() {
        return coords;
    }
    
    public Integer getDesktop() {
        return desk;
    }
    
    public String getWinid() {
        return winid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setCoords(int x, int y, int w, int h) {
        this.coords = new int[]{x,y,w,h};
    }
    public void unsetCoords() {
        this.coords = null;
    }
    public void setDesktop(Integer desk) {
        this.desk = desk;
    }
    public boolean isStarted() {
        return started;
    }
    public boolean isLayouted() {
        return layouted;
    }
    public boolean isLayoutCandidate() {
        return desk != null || coords != null;
    }
    
    void setLayouted(boolean layoutPerformed) {
        this.layouted = layoutPerformed;
    }
    void setStartPerformed(boolean startPerformed) {
        this.started = startPerformed;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StartItem[");
        if (startCommand != null) {
            builder.append("startCommand='");
            builder.append(startCommand);
            builder.append("', ");
        }
        if (startMatcher != null) {
            builder.append("procCmdMatcher='");
            builder.append(startMatcher.pattern().pattern());
            builder.append("', ");
        }
        if (visibleMatcher != null) {
            builder.append("winTitleMatcher='");
            builder.append(visibleMatcher.pattern().pattern());
            builder.append("', ");
        }
        if (winid != null) {
            builder.append("winid='");
            builder.append(winid);
            builder.append("'");
        }
        builder.append("]");
        return builder.toString();
    }
    
    static StartItem parse(XMLStreamReader reader) throws XMLStreamException {
        // suppose the reader is at START_ELEMENT of the start-item
        String name = reader.getAttributeValue(null, "name");
        StartItem item = new StartItem(name);
        for (;;) {
            int evt = reader.next();
                switch (evt) {
                case XMLStreamConstants.START_ELEMENT : {
                    String elementName = reader.getLocalName();
                    
                    if (elementName.equalsIgnoreCase("start-command")) {
                        String elementText = reader.getElementText();
                        item.setStartCommand(elementText);
                    }
                    if (elementName.equalsIgnoreCase("start-regex")) {
                        String elementText = reader.getElementText();
                        item.setStartRegex(elementText);
                    }
                    if (elementName.equalsIgnoreCase("visible-regex")) {
                        String elementText = reader.getElementText();
                        item.setVisibleRegex(elementText);
                    }
                    if (elementName.equalsIgnoreCase("desktop")) {
                        String elementText = reader.getElementText();
                        item.setDesktop(Integer.valueOf(elementText));
                    }
                    if (elementName.equalsIgnoreCase("coordinates")) {
                        item.setCoords(
                            Integer.valueOf(reader.getAttributeValue(null, "x")),
                            Integer.valueOf(reader.getAttributeValue(null, "y")),
                            Integer.valueOf(reader.getAttributeValue(null, "w")),
                            Integer.valueOf(reader.getAttributeValue(null, "h"))
                        );
                    }
                    break;
                }
                case XMLStreamConstants.END_ELEMENT : {
                    String elementName = reader.getLocalName();
                    if (elementName.equalsIgnoreCase("start-item")) {
                        return item;
                    }
                }
            }
        }
    }
    
    static void serialize(StartItem item, XMLStreamWriter w) throws XMLStreamException {
        w.writeStartElement("start-item");
        w.writeAttribute("name", item.getName());
        
        String s = item.getStartCommand();
        if (s != null) {
            w.writeStartElement("start-command");
            w.writeCharacters(s);
            w.writeEndElement();
        }
        
        s = item.getStartRegex();
        if (s != null) {
            w.writeStartElement("start-regex");
            w.writeCharacters(s);
            w.writeEndElement();
        }
        
        s = item.getVisibleRegex();
        if (s != null) {
            w.writeStartElement("visible-regex");
            w.writeCharacters(s);
            w.writeEndElement();
        }
        
        Integer i = item.getDesktop();
        if (i != null) {
            w.writeStartElement("desktop");
            w.writeCharacters(i.toString());
            w.writeEndElement();
        }
        
        int[] coords = item.getCoordinates();
        if (coords != null) {
            w.writeStartElement("coordinates");
            w.writeAttribute("x", String.valueOf(coords[0]));
            w.writeAttribute("y", String.valueOf(coords[1]));
            w.writeAttribute("w", String.valueOf(coords[2]));
            w.writeAttribute("h", String.valueOf(coords[3]));
            w.writeEndElement();
        }

        w.writeEndElement();
    }
}
