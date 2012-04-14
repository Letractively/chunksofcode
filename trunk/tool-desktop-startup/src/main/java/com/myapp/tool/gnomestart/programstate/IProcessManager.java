package com.myapp.tool.gnomestart.programstate;

import java.util.Map;



public interface IProcessManager {

    Map<Integer, Proc> determineProcessStates();

    Process start(String command);
}