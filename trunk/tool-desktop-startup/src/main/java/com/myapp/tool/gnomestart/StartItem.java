package com.myapp.tool.gnomestart;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.tool.gnomestart.programstate.Proc;
import com.myapp.tool.gnomestart.programstate.Window;

public class StartItem
{

    
    private final String name;

    private Matcher startMatcher = null;
    private String startCommand = null;
    private Proc process = null;

    private int[] coordinates = null;
    private Integer desktop = null;

    private Matcher visibleMatcher = null;
    private Window window = null;

    
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
        this.coordinates = coords;
        this.desktop = desk;
        setStartRegex(procCmdMatcher);
        setVisibleRegex(winTitleMatcher);
    }

    
    public boolean needsToWait() {
        boolean wait4vis = needsToWaitForVisibility();
        boolean wait4run = needsToWaitForRunning();
        return wait4vis || wait4run;
    }

    @Override
    public String toString() {
        return name;
    }



    // visible


    public boolean isVisible() {
        return window != null;
    }

    public boolean isVisibilityRequired() {
        return visibleMatcher != null;
    }

    public boolean needsToWaitForVisibility() {
        boolean isvis = isVisible();
        boolean needvis = isVisibilityRequired();
        return needvis && ! isvis;
    }

    void setWindow(Window value) {
        this.window = value;
    }

    public boolean isWindowedBy(Window value) {
        return visibleMatcher.reset(value.getWinTitle()).find();
    }



    // startup and running status


    public boolean isRunning() {
        return process != null;
    }

    public boolean isRunningRequired() {
        return startMatcher != null;
    }

    public boolean needsToWaitForRunning() {
        boolean needrun = isRunningRequired();
        boolean isrun = isRunning();
        return needrun && ! isrun;
    }

    void setProcess(Proc value) {
        this.process = value;
    }

    public Proc getProcess() {
        return process;
    }

    boolean isStartedBy(Proc value) {
        return startMatcher.reset(value.getCommand()).find();
    }

    public boolean isStartupCandidate() {
        return startCommand != null;
    }



    // layout


    public boolean isLayoutAdjustmentRequired() {
        return desktop != null || coordinates != null;
    }



    // getter, setter


    public String getStartCommand() {
        return startCommand;
    }

    void setStartCommand(String c) {
        startCommand = c;
    }

    public String getVisibleRegex() {
        if (visibleMatcher == null) {
            return null;
        }
        return visibleMatcher.pattern().pattern();
    }

    void setVisibleRegex(String regex) {
        if (regex == null) {
            visibleMatcher = null;
            return;
        }
        visibleMatcher = Pattern.compile(regex).matcher("foo");
    }

    public String getStartRegex() {
        if (startMatcher == null) {
            return null;
        }
        return startMatcher.pattern().pattern();
    }

    void setStartRegex(String regex) {
        if (regex == null) {
            startMatcher = null;
            return;
        }
        startMatcher = Pattern.compile(regex).matcher("foo");
    }

    /** @return an int[] {x,y,w,h} if these are set, null otherwise */
    public int[] getCoordinates() {
        return coordinates;
    }

    void setCoordinates(int x, int y, int w, int h) {
        setCoordinates(new int[] { x, y, w, h });
    }

    void setCoordinates(int[] xywh) {
        if (xywh != null && xywh.length != 4) {
            throw new RuntimeException(Arrays.toString(xywh));
        }
        this.coordinates = xywh;
    }

    public Integer getTargetDesktop() {
        return desktop;
    }

    void setTargetDesktop(Integer desk) {
        this.desktop = desk;
    }

    public Window getWindow() {
        return window;
    }

    public String getName() {
        return name;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StartItem other = (StartItem) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (! name.equals(other.name))
            return false;
        return true;
    }
}
