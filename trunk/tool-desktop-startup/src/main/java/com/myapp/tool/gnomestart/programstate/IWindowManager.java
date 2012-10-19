package com.myapp.tool.gnomestart.programstate;

import java.util.List;

import com.myapp.tool.gnomestart.DesktopStarter;



public interface IWindowManager {

    int STATUS_DEFAULT = 0;
    int STATUS_MINIMIZED = 1;
    int STATUS_MAXIMIZED = 2;
    
    void setDesktopStarter(DesktopStarter ds);

    List<Window> determineWindowStates();

    void applyDesktop(String winId, int desktop);

    void applyCoordinates(String winId, int[] coords);

    void hideWindow(String winId);

    void setFocusOnWindow(String winId);

    void setActiveDesktop(int desktop);

}