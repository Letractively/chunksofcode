package com.myapp.tool.gnomestart.programstate;

import java.util.Map;



public interface IWindowManager {

    int STATUS_DEFAULT   = 0;
    int STATUS_MINIMIZED = 1;
    int STATUS_MAXIMIZED = 2;

    Map<String, Window> determineWindowStates();

    void applyDesktop(String winId, int desktop);

    void applyCoordinates(String winId, int[] coords);

    void setWindowStatus(String winId, int status);
}