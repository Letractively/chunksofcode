package com.myapp.tool.gnomestart.programstate;


/** represents a window owned by the current user having a
 * windowId, a title, and a process id
 * 
 * @author andre */
public class Window implements Comparable<Window> {

    private final String winid;
    private final String title;
    private final Integer pid;

    
    public Window(String winid, String title, int pid) {
        if (pid < 0) {
            throw new RuntimeException(pid + " < 0");
        }
        if (winid == null) {
            throw new RuntimeException("winid == null");
        }
        if (title == null) {
            throw new RuntimeException("title == null");
        }
        this.winid = winid;
        this.title = title;
        this.pid = pid;
    }

    @Override
    public int compareTo(Window o) {
        int cmp = getWinId().compareTo(o.getWinId());
        return cmp;
    }

    /**
     * return a key recognized by <br>
     * {@link IWindowManager#applyCoordinates(Window, int[])} <br>
     * and {@link IWindowManager#applyDesktop(Window, int)}
     * @return the key that identifies this window.
     */
    public String getWinId() {
        return winid;
    }

    public String getWinTitle() {
        return title;
    }

    /**
     * @return the process id for this window (cannot be null)
     */
    public Integer getPid() {
        return pid;
    }

    @Override
    public int hashCode() {
        final int prime = 13;
        int result = super.hashCode();
        result = prime * result + getWinId().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ! (obj instanceof Window)) {
            return false;
        }
        return winid.equals(((Window) obj).winid);
    }

    @Override
    public String toString() {
        return "WindowState[winid="+getWinId()+", title="+getWinTitle()+", pid="+pid+"]";
    }
}