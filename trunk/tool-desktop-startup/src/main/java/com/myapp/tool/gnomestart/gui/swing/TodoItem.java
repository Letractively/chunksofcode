package com.myapp.tool.gnomestart.gui.swing;

import com.myapp.tool.gnomestart.StartItem;

class TodoItem
{

    private final StatusWindow gui;
    private String type;
    private StartItem item;


    public TodoItem(StatusWindow statusWindow, StartItem i, String type) {
        this.gui = statusWindow;
        this.item = i;
        this.type = type;
        getDescription(); // validates type
    }

    public String getDescription() {
        if (StatusWindow.PRECONDITION.equals(type)) {
            return waitForItemToString();

        } else if (StatusWindow.START.equals(type)) {
            return "start command " + item.getStartCommand();

        } else if (StatusWindow.WAITFORSTARTUP.equals(type)) {
            return waitForItemToString();

        } else if (StatusWindow.LAYOUT.equals(type)) {
            return layoutItemToString();
        }

        throw new RuntimeException("unknown type: " + type);
    }
    
    public String getType() {
        return type;
    }

    public StartItem getItem() {
        return item;
    }

    /**
     * @return description columns' value for a layout-item
     */
    private String layoutItemToString() {
        StringBuilder bui = new StringBuilder();
        int[] coordinates = item.getCoordinates();
        bui.append("Move win to ");

        if (item.getTargetDesktop() != null) {
            bui.append("desktop ");
            bui.append(item.getTargetDesktop());
            if (coordinates != null) {
                bui.append(" and ");
            }
        }
        if (coordinates != null) {
            bui.append("coords [");
            bui.append(coordinates[0]);
            bui.append(", ");
            bui.append(coordinates[1]);
            bui.append("], resize to WxH:");
            bui.append(coordinates[2]);
            bui.append("x");
            bui.append(coordinates[3]);
        }
        return bui.toString();
    }

    /**
     * @return description columns' value for a wait-for-item
     */
    private String waitForItemToString() {
        StringBuilder bui = new StringBuilder("wait for ");
        if (item.isRunningRequired()) {
            bui.append("process: '");
            bui.append(item.getStartRegex());
            bui.append("' ");
            if (item.isVisibilityRequired()) {
                bui.append("and ");
            }
        }
        if (item.isVisibilityRequired()) {
            bui.append("window titled: '");
            bui.append(item.getVisibleRegex());
            bui.append("' ");
        }
        return bui.toString();
    }

    /**
     * @return the value for the checkbox
     */
    public Boolean getStatus() {
        if (StatusWindow.PRECONDITION.equals(type)) {
            return ! item.needsToWait();

        } else if (StatusWindow.START.equals(type)) {
            return ! gui.model.getStartItemsToBeStarted().contains(item);

        } else if (StatusWindow.WAITFORSTARTUP.equals(type)) {
            return ! item.needsToWait();

        } else if (StatusWindow.LAYOUT.equals(type)) {
            return ! gui.model.getStartItemsToBeLaidOut().contains(item);
        }

        throw new RuntimeException("unknown type: " + type);
    }
}