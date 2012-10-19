package com.myapp.tool.gnomestart.gui.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.myapp.tool.gnomestart.StartItem;

@SuppressWarnings("serial")
class MyTableModel extends AbstractTableModel
{

    static final int INDEX = 0;
    static final int NAME = 1;
    static final int TYPE = 2;
    static final int DESCRIPTION = 3;
    static final int STATUS = 4;

    static final int[] COLUMNS = { INDEX, NAME, TYPE, DESCRIPTION, STATUS };


    private final StatusWindow statusWindow;
    private List<TodoItem> rows;


    public MyTableModel(StatusWindow statusWindow) {
        this.statusWindow = statusWindow;
        rows = new ArrayList<TodoItem>();

        for (StartItem i : this.statusWindow.model.getPreconditionsToWaitFor()) {
            rows.add(new TodoItem(this.statusWindow, i, StatusWindow.PRECONDITION));
        }
        for (StartItem i : this.statusWindow.model.getStartItemsToBeStarted()) {
            rows.add(new TodoItem(this.statusWindow, i, StatusWindow.START));
        }
        for (StartItem i : this.statusWindow.model.getStartItemsToWaitFor()) {
            rows.add(new TodoItem(this.statusWindow, i, StatusWindow.WAITFORSTARTUP));
        }
        for (StartItem i : this.statusWindow.model.getStartItemsToBeLaidOut()) {
            rows.add(new TodoItem(this.statusWindow, i, StatusWindow.LAYOUT));
        }
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TodoItem item = rows.get(rowIndex);
        switch (columnIndex) {
            case INDEX:
                return Integer.valueOf(rowIndex);
            case NAME:
                return item.getItem().getName();
            case TYPE:
                return item.getType();
            case DESCRIPTION:
                return item.getDescription();
            case STATUS:
                return item.getStatus();
        }
        throw new RuntimeException(rowIndex + ", " + columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case INDEX:
                return Integer.class;
            case NAME:
            case TYPE:
            case DESCRIPTION:
                return String.class;
            case STATUS:
                return Boolean.class;
        }
        throw new RuntimeException(columnIndex + "");
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case INDEX:
                return "#";
            case NAME:
                return "Item Name";
            case TYPE:
                return "Item Type";
            case DESCRIPTION:
                return "Details";
            case STATUS:
                return "Status";
        }
        throw new RuntimeException(column + "");
    }

    public int getPendingItemCount() {
        return getRowCount() - getTodoCount();
    }

    public int getTodoCount() {
        int result = 0;
        for (TodoItem i : rows) {
            if (! i.getStatus()) {
                result++;
            }
        }
        return result;
    }
}
