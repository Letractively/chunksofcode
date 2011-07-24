package com.myapp.tools.media.renamer.view.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.controller.Msg;
import com.myapp.tools.media.renamer.controller.Util;
import com.myapp.tools.media.renamer.model.IProperty;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.model.RenamerWrapper;
import com.myapp.tools.media.renamer.model.naming.AbstractProperty;
import com.myapp.tools.media.renamer.model.naming.impl.BeschreibungNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.DatumNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.ExtensionNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.NummerierungNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.OriginalNameNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.PrefixNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.SuffixNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.ThemaNamePart;
import com.myapp.tools.media.renamer.model.naming.impl.TitelNamePart;
import com.myapp.tools.media.renamer.view.swing.ListView.ColumnWidth;

/**
 * wraps a given IRenamer instance to a listmodel and tablemodel implementation.
 * 
 * @author andre
 * 
 */
@SuppressWarnings("serial")
class RenamerDelegate extends RenamerWrapper implements ListModel,
                                                        TableModel,
                                                        ISysConstants {

//---------------------- inner classes ----------------------------------


    /**
     * property that shows the new name for a file
     * 
     * @author andre
     * 
     */
    private class NewNameProperty extends AbstractProperty {

        /**
         * default constructor
         */
        NewNameProperty() {
            super(COLUMN_NAME_ZIELDATEI);
        }

        @Override
        public Object getRawValue(IRenamable file) {
            return file.getNewName();
        }
    }

    /**
     * property that displays the size of a file
     * 
     * @author andre
     * 
     */
    private class SizeProperty extends AbstractProperty {

        /**
         * default constructor
         */
        SizeProperty() {
            super(COLUMN_NAME_DATEIGROESSE);
        }

        @Override
        public Object getRawValue(IRenamable file) {
            return Util.getHumanReadableFileSize(
                                               file.getSourceObject().length());
        }
    }

    /**
     * private abstractTableModel instance used for listener handling
     * 
     * @author andre
     * 
     */
    private class MyTableModel extends AbstractTableModel {

        @Override
        public int getColumnCount() {
            return RenamerDelegate.this.getColumnCount();
        }

        @Override
        public int getRowCount() {
            return RenamerDelegate.this.getRowCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return RenamerDelegate.this.getValueAt(rowIndex, columnIndex);
        }
    }


    /**
     * a class managing the events using the abstractlistmodel's implementation
     * 
     * @author andre
     */
    private class MyListModel extends AbstractListModel {

        @Override
        public void fireContentsChanged(Object s, int i, int j) {
            super.fireContentsChanged(s, i, j);
        }

        @Override
        public void fireIntervalAdded(Object s, int i, int j) {
            super.fireIntervalAdded(s, i, j);
        }

        @Override
        public void fireIntervalRemoved(Object s, int i, int j) {
            super.fireIntervalRemoved(s, i, j);
        }

        @Override
        public Object getElementAt(int index) {
            return getElementAt(index);
        }

        @Override
        public int getSize() {
            return getSize();
        }
    }




    //---------------------- instance fields ----------------------------------



    private Map<Class<? extends IProperty>, ColumnWidth> colDefs = null;
    private final IRenamerConfiguration config;
    private final MyListModel listModel = new MyListModel();
    private final MyTableModel tableModel = new MyTableModel();
    private final IApplication app;
    private List<IProperty> tableColumns;




    //---------------------- constructor ----------------------------------







    /**
     * creates a wrapper object that wraps the given renamer
     * 
     * @param app
     *            the app instance to serve
     */
    public RenamerDelegate(IApplication app) {
        super(app.getRenamer());
        this.app = app;
        
        IRenamer r = this.app.getRenamer();
        config = r.getConfig();
        createTableColumns(config.getNameElementsList(r));
    }





    //********************** LIST MODEL IMPL ***********************************


    @Override
    public void addListDataListener(ListDataListener l) {
        listModel.addListDataListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listModel.removeListDataListener(l);
    }





    //********************** TABLE MODEL IMPL **********************************



    /**
     * creates the tablecolumns for the application.
     * 
     * @param nameParts the
     *            nameparts which are being used to create the table columns
     */
    private void createTableColumns(List<? extends IProperty> nameParts) {
        List<IProperty> l = new ArrayList<IProperty>();

        for (IProperty namePart : nameParts) {
            if (namePart instanceof OriginalNameNamePart
                ||  namePart instanceof PrefixNamePart
                ||  namePart instanceof SuffixNamePart) {
                continue;
            }
            l.add(namePart);
        }

        l.add(new SizeProperty());
        OriginalNameNamePart onnp = new OriginalNameNamePart();
        onnp.init(this);
        l.add(onnp);
        l.add(new NewNameProperty());

        tableColumns = Collections.unmodifiableList(l);
    }


    /**
     * returns the property objects being used as table columns
     * 
     * @return the property objects being used as table columns
     */
    List<IProperty> getTableColumns() {
        return tableColumns;
    }


    /**
     * @return a mapping of column widths from the renamer configuration.
     */
    synchronized Map<Class<? extends IProperty>, ColumnWidth> getColDefs() {
        if (colDefs != null) return colDefs;
        
        Map<Class<? extends IProperty>, ColumnWidth> m;
        m = new HashMap<Class<? extends IProperty>, ColumnWidth>();
        IRenamerConfiguration c = app.getRenamer().getConfig();
        
        m.put(DatumNamePart.class,
                                 new ColumnWidth(c.getInt(COLUMN_WIDTH_DATUM)));
        m.put(ExtensionNamePart.class, 
                                   new ColumnWidth(c.getInt(COLUMN_WIDTH_TYP)));
        m.put(ThemaNamePart.class,
                                 new ColumnWidth(c.getInt(COLUMN_WIDTH_THEMA)));
        m.put(TitelNamePart.class,
                                 new ColumnWidth(c.getInt(COLUMN_WIDTH_TITEL)));
        m.put(BeschreibungNamePart.class,   
                          new ColumnWidth(c.getInt(COLUMN_WIDTH_BESCHREIBUNG)));
        m.put(NummerierungNamePart.class, 
                                new ColumnWidth(c.getInt(COLUMN_WIDTH_NUMMER)));
        m.put(OriginalNameNamePart.class,   
                            new ColumnWidth(c.getInt(COLUMN_WIDTH_QUELLDATEI)));
        m.put(NewNameProperty.class, 
                             new ColumnWidth(c.getInt(COLUMN_WIDTH_ZIELDATEI)));
        m.put(SizeProperty.class,   
                          new ColumnWidth(c.getInt(COLUMN_WIDTH_DATEIGROESSE)));
        
        colDefs = Collections.unmodifiableMap(m);
        return colDefs;
    }


    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModel.addTableModelListener(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        tableModel.removeTableModelListener(l);
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tableColumns.get(columnIndex).getRawType();
    }

    @Override
    public int getColumnCount() {
        return tableColumns.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return Msg.msg(tableColumns.get(columnIndex).getPropertyName());
    }

    @Override
    public int getRowCount() {
        return getSize();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IRenamable f = getElementAt(rowIndex);
        return tableColumns.get(columnIndex).getRawValue(f); 
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return tableColumns.get(columnIndex).isEditable();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        IRenamable f = getElementAt(rowIndex);
        tableColumns.get(columnIndex).setRawValue(value, f);
        super.calculateNames();
        tableModel.fireTableRowsUpdated(rowIndex, rowIndex);
    }



    //********************** DELEGATE METHODS FOR IRENAMER BEING WRAPPED *******



    @Override
    public void calculateNames() {
        int[] selected = app.getListView().getSelection();
        super.calculateNames();
        
        int from = 0;
        int to = 0;
        
        if (selected != null && selected.length > 0) {
            from = selected[0];
            to = selected[selected.length - 1];
        }
        
        tableModel.fireTableDataChanged();
        
        if (selected.length > 0) {
            app.getListView().setSelection(from, to);
        }
    }


    @Override
    public List<IRenamable> add(int at,
                                boolean excludeDuplicates,
                                IRenamable... newFiles) {
        List<IRenamable> l = super.add(at, excludeDuplicates, newFiles);

        super.calculateNames();
        listModel.fireContentsChanged(unwrap(), at < 0 ? 0 : at, getSize());
        tableModel.fireTableDataChanged();

        app.getController().fileCountChanged();
        
        // thumbnailstore is only used by settings panel:
        ((SettingsView) app.getSettingsView()) 
           .getThumbnailStore()
           .cacheImagesFromRenamables(Util.arrayToList(newFiles));
        return l;
    }

    @Override
    public List<IRenamable> add(int offset,
                                boolean excludeDuplicates,
                                List<IRenamable> newFiles) {
        List<IRenamable> l = super.add(offset, excludeDuplicates, newFiles);
        offset = offset < 0 ? 0 : offset;
        int getSize = getSize();

        super.calculateNames();
        tableModel.fireTableDataChanged();
        listModel.fireContentsChanged(  unwrap(),
                                        offset < 0 ? 0 : offset,
                                        getSize);
        app.getController().fileCountChanged();

        // thumbnailstore is only used by settings panel:
        ((SettingsView) app.getSettingsView()) 
           .getThumbnailStore()
           .cacheImagesFromRenamables(newFiles);
        return l;
    }

    @Override
    public List<IRenamable> append(boolean excludeDuplicates,
                                   IRenamable... filesToAppend) {
        int oldSize = getSize();
        List<IRenamable> l = super.append(excludeDuplicates, filesToAppend);

        super.calculateNames();
        listModel.fireContentsChanged(unwrap(), oldSize, getSize());
        tableModel.fireTableDataChanged();

        app.getController().fileCountChanged();
        
        // thumbnailstore is only used by settings panel:
        ((SettingsView) app.getSettingsView()) 
           .getThumbnailStore()
           .cacheImagesFromRenamables(Util.arrayToList(filesToAppend));
        return l;
    }

    @Override
    public List<IRenamable> append(boolean excludeDuplicates,
                                   List<IRenamable> renamables) {
        int oldSize = getSize();
        List<IRenamable> list = super.append(excludeDuplicates, renamables);

        super.calculateNames();
        listModel.fireContentsChanged(unwrap(), oldSize, getSize());
        tableModel.fireTableDataChanged();

        app.getController().fileCountChanged();
        return list;
    }

    @Override
    public void clear() {
        int index1 = getSize() - 1;
        unwrap().clear();

        if (index1 <= 0)
            return;

        SettingsView sv = (SettingsView) app.getSettingsView();
        sv.getThumbnailStore().dropCache();
        sv.setImageToPreview(null);
        
        listModel.fireIntervalRemoved(this, 0, index1);
        tableModel.fireTableRowsDeleted(0, index1);
        app.getController().fileCountChanged();
    }

    @Override
    public void move(int from, int count, int insertAt) {
        unwrap().move(from, count, insertAt);

        // calc range, two possible cases:   move(1 ,3 ,6)  |   move(5, 3, 2)
        int fireFrom, fireTo;           // 0. a         a   |   a           a
                                        // 1. B -->     e * |   b           b
        if (from < insertAt) {          // 2. C -->     f * |   c <--   --> F *
            fireFrom = from;            // 3. D --> --> B * |   d       --> G *
            fireTo = insertAt - 1;      // 4. e     --> C * |   e       --> H *
                                        // 5. f     --> D * |   F -->       c *
        } else {                        // 6. g <--     g   |   G -->       d *
            fireFrom = insertAt;        // 7. h         h   |   H -->       e *
            fireTo = from + count - 1;  // 8. i         i   |   i           i
        }                               

        calculateNames();
        listModel.fireContentsChanged(unwrap(), fireFrom, fireTo);
        tableModel.fireTableDataChanged();
    }


    @Override
    public IRenamable remove(int index) {
        IRenamable f = super.remove(index);
        
        calculateNames();
        listModel.fireIntervalRemoved(listModel, index, index);
        tableModel.fireTableRowsDeleted(index, index);
        app.getController().fileCountChanged();
        return f;
    }

    @Override
    public boolean removeFirst(Object o) {
        int i = super.indexOf(o);
        boolean b = super.removeFirst(o);
        calculateNames();
        listModel.fireIntervalRemoved(unwrap(), i, i);
        tableModel.fireTableRowsDeleted(i, i);
        app.getController().fileCountChanged();
        return b;
    }

    @Override
    public void remove(int from, int to) {
        super.remove(from, to);
        
        calculateNames();
        tableModel.fireTableRowsDeleted(from, to);
        app.getController().fileCountChanged();
    }

    @Override
    public void applyFilter(FileFilter ft) {
        super.applyFilter(ft);
        tableModel.fireTableDataChanged();
        app.getController().fileCountChanged();
    }
    
    @Override
    public void renameFiles() throws Exception {
        super.renameFiles();
        tableModel.fireTableDataChanged();
    }
}