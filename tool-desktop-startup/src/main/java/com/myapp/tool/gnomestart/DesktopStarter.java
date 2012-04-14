package com.myapp.tool.gnomestart;

import java.util.Arrays;
import java.util.Map;

import com.myapp.tool.gnomestart.programstate.IProcessManager;
import com.myapp.tool.gnomestart.programstate.IWindowManager;
import com.myapp.tool.gnomestart.programstate.Proc;
import com.myapp.tool.gnomestart.programstate.Window;
import com.myapp.util.collections.MultiIterator;


public class DesktopStarter {
    
    
    private StartItem[] startItms;
    private StartItem[] preItms;
    
    private IProcessManager procStateManager = Config.getInstance().createProcStateManager();
    private IWindowManager winStateManager = Config.getInstance().createWinStateManager();
    
    private Map<String, Window> winStates = null;
    private Map<Integer, Proc> procStates = null;

    public DesktopStarter() { }

    
    public static void main(String[] args) throws Exception {
        DesktopStarter starter = new DesktopStarter();
        starter.setPreconditionItems(Config.getInstance().getPreconditionItems());
        starter.setStartItems(Config.getInstance().getStartItems());
        starter.pimp();
    }
    

    public void setPreconditionItems(StartItem[] wait4Items) {
        preItms = wait4Items;
    }
    public void setStartItems(StartItem[] startItems) {
        this.startItms = startItems;
    }
    
    public void pimp() {
        waitForPrecondition();
        startProcesses();
        waitForAppsReady();
        layoutWindows();
    }
    

    private void waitForPrecondition() {
        waitForItemsReady(preItms, false);
    }
    private void waitForAppsReady() {
        waitForItemsReady(startItms, true);
    }
    
    private void startProcesses() {
        for (int i = 0; i < startItms.length; i++) {
            StartItem startItem = startItms[i];
            String startCommand = startItem.getStartCommand();
            
            if (startCommand == null) { // this item will not be started
                continue;
            }
            
            procStateManager.start(startCommand);
        }
    }
    
    private void layoutWindows() {
        for (StartItem si : new MultiIterator<StartItem>(preItms, startItms)) {
            layoutWindow(si);
        }
    }

    private void layoutWindow(StartItem item) {
        if (! item.isLayoutCandidate() || item.isLayouted()) {
            return;
        }
        
        String winid = item.getWinid();
        if (winid == null) {
            throw new IllegalStateException();
        }
        
        Window w = winStates.get(winid);
        
        int[] coordinates = item.getCoordinates();
        if (coordinates != null) {
            coordinates = sumIntArrays(coordinates, 
                                       Config.getInstance().getGlobalCoordOffsets());
            winStateManager.applyCoordinates(w.getWinId(), coordinates);
        }
        
        Integer desktop = item.getDesktop();
        if (desktop != null) {
            winStateManager.applyDesktop(w.getWinId(), desktop);
        }
    }

    private void waitForItemsReady(StartItem[] items, 
                                   boolean layoutWhenVisible) {
        if (items == null) {
            return;
        }
        
        for (boolean firstRun = true;;) {
            if (firstRun) {
                firstRun = false;
            } else {
                sleep(1000); // TODO: config
            }

            procStates = procStateManager.determineProcessStates();
            winStates = winStateManager.determineWindowStates();
            boolean visible = areWindowsVisible(items, layoutWhenVisible);
            
            if ( ! visible) {
                continue;
            }
            
            return;
        }
    }

    private boolean areWindowsVisible(StartItem[] items, 
                                      boolean performLayout) {
        for (int i = 0; i < items.length; i++) {
            StartItem item = items[i];
            String name = item.getName();
            
            if (! item.isStartedAndVisible(winStates, procStates)) {
                System.err.println("window of '"+name+"' is not yet visible! wait another cycle...");
                return false;
            }
            if (performLayout && item.isLayoutCandidate() && ! item.isLayouted()) {
                System.err.println("performing layout on item '"+name+"'...");
                layoutWindow(item);
                item.setLayouted(true);
            }
        }
        return true;
    }
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] sumIntArrays(int[] coordinates, int[] offsets) { 
        if (coordinates == null || offsets == null) {
            return coordinates;
        }
        if (coordinates.length != 4 || offsets.length != 4) {
            throw new IllegalArgumentException(
                   "coordinates:"+Arrays.toString(coordinates) +", " +
                   "offsets:"+ Arrays.toString(offsets));
        }
        int[] result = new int[4];
        for (int i = 0; i < result.length; i++) {
            result[i] = coordinates[i] + offsets[i];
        }
        return result;
    }
}
