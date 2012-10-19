package com.myapp.tool.gnomestart.programstate;

import java.util.List;

import com.myapp.tool.gnomestart.DesktopStarter;



public interface IProcessManager {
    
    List<Proc> determineProcessStates();

    Process start(String command);

    void setDesktopStarter(DesktopStarter desktopStarter);

}