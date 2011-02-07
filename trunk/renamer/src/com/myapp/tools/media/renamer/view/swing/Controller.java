package com.myapp.tools.media.renamer.view.swing;

import static com.myapp.tools.media.renamer.config.IConstants.INameConstants.DATUM_FORMAT;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.myapp.tools.media.renamer.config.Config;
import com.myapp.tools.media.renamer.config.IRenamerConfiguration;
import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.IApplication;
import com.myapp.tools.media.renamer.controller.IController;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Msg;
import com.myapp.tools.media.renamer.controller.Util;
import com.myapp.tools.media.renamer.model.IRenamable;
import com.myapp.tools.media.renamer.model.IRenamer;
import com.myapp.tools.media.renamer.model.RenamableFile;
import com.myapp.tools.media.renamer.view.IDialogs;
import com.myapp.tools.media.renamer.view.ISettingsView;

/**
 * the global listener implementation of the renamer application.
 * 
 * @author andre
 */
class Controller implements ActionListener,
                            WindowListener,
                            PropertyChangeListener,
                            ListSelectionListener,
                            DropTargetListener,
                            ISysConstants,
                            IActionCommands,
                            IController {

    private static final Logger L = Log.defaultLogger();
    private static final String LINE_SEPARATOR = 
                                           System.getProperty("line.separator");
    
    private final IApplication app;

    /**
     * creates a controller for the given app
     * 
     * @param iapp
     *            the app to serve to
     */
    Controller(IApplication iapp) {
        app = iapp;
    }

    @Override
    public void addElements() {
        app.getElementChooser().dialogShow();
        ((SwingApplication)app).getMenuBar().refreshDisplay();
    }

    @Override
    public void removeAllElements() {
        app.getRenamer().clear();
        ((SwingApplication)app).getMenuBar().refreshDisplay();
    }

    @Override
    public void editCopySettings() {
        Map<String, String> changes; 
        changes = app.getDialogs().showChooseDestinationDialog();

        if (changes == null || changes.isEmpty()) return;

        for (Map.Entry<String, String> en : changes.entrySet()) {
            assert cfg().getString(en.getKey()) != null : en.getKey();
            cfg().setCustomProperty(en.getKey(), en.getValue());
        }
        app.getSettingsView().resetDestinationText();
        app.getRenamer().calculateNames();
    }

    @Override
    public void editAllSettings() {
        String newProps = app.getDialogs().showEditConfigFileDialog();
        if (newProps == null) return;

        try {
            cfg().saveCustomSettings();
            ((Config) cfg()).overWriteConfig(newProps);
            cfg().clearCustomProperties();
            cfg().loadCustomSettings();
            app.getRenamer().calculateNames();
            
        } catch (IOException e) {
            e.printStackTrace();
            app.getDialogs().showErrorMessage(
               Msg.msg("Controller.EDIT_ALL_SETTINGS.errorWhileSaving"), e);
        }
    }

    @Override
    public void setBeschreibung(ElementsToHandle applyTo) {
        assert applyTo != null;
        IRenamer renamer = app.getRenamer();
        String beschreibung = app.getSettingsView().getBeschreibungText();

        switch (applyTo) {
            case ALL: 
                for (IRenamable r : renamer) r.setBeschreibung(beschreibung);
                break;
                
            case SELECTION : 
                for (int i : app.getListView().getSelection())
                    renamer.getElementAt(i).setBeschreibung(beschreibung);
                break;
                
            case UNCHANGED : 
                cfg().setCustomProperty(DEFAULT_NAME_BESCHREIBUNG, beschreibung);
                break;
        }

        renamer.calculateNames();
    }

    @Override
    public void setTitel(ElementsToHandle applyTo) {
        assert applyTo != null;
        IRenamer renamer = app.getRenamer();
        String titel = app.getSettingsView().getTitelText();

        switch (applyTo) {
            case ALL: 
                for (IRenamable r : renamer) r.setTitel(titel);
                break;
                
            case SELECTION : 
                for (int i : app.getListView().getSelection())
                    app.getRenamer().getElementAt(i).setTitel(titel);
                break;
                
            case UNCHANGED : 
                cfg().setCustomProperty(DEFAULT_NAME_TITEL, titel);
                break;
        }

        renamer.calculateNames();
    }

    @Override
    public void setThema(ElementsToHandle applyTo) {
        assert applyTo != null;
        IRenamer renamer = app.getRenamer();
        String thema = app.getSettingsView().getThemaText();

        switch (applyTo) {
            case ALL: 
                for (IRenamable r : renamer) r.setThema(thema);
                break;
                
            case SELECTION : 
                for (int i : app.getListView().getSelection())
                    app.getRenamer().getElementAt(i).setThema(thema);
                break;
                
            case UNCHANGED : 
                cfg().setCustomProperty(DEFAULT_NAME_THEMA, thema);
                break;
        }

        renamer.calculateNames();
    }
    
    @Override
    public void discardSelectionsChanges(ElementsToHandle applyTo) {
        assert applyTo != null;
        IRenamer renamer = app.getRenamer();
        
        switch (applyTo) {
            case UNCHANGED :
                return; // nothing to do
                
            case SELECTION : 
                for (int i : app.getListView().getSelection())
                    renamer.getElementAt(i).discardChanges();
                break;
                
            case ALL :
                for (IRenamable r : renamer) r.discardChanges();
        }

        renamer.calculateNames();
    }

    @Override
    public void showLogHistory() {
        app.getDialogs().showLogHistoryDialog();
    }

    @Override
    public void editFilter() {
        String f = app.getDialogs().showFilterDefinitionDialog();
        if (f == null) return; 
            
        cfg().setCustomProperty(FILE_FILTERS, f);        
        app.getElementChooser().setFileFilter(
                                      Util.createFileFilterFromCommaSepList(f));
    }
    
    @Override
    public void renameFiles() {
        IRenamer renamer = app.getRenamer();
        if (renamer.isEmpty()) return; // nothing to do
        
        synchronized (renamer) {
            boolean overwrite = false;
            
            if (cfg().getBoolean(REPLACE_ORIGINAL_FILES)) {
                overwrite = true;
            }
            
            if ( ! overwrite) { 
                // check if destination dir existing:
                String destination = cfg().getDestinationPath();
                File dst = new File(destination);

                if ( ! dst.exists()) {
                    if (app.getDialogs().showWantToCreateDir(destination)) {
                        dst.mkdirs();
                        
                    } else {
                        return;
                    }
                }
                
                //check for existing files that would be overwritten:
                for (IRenamable f : renamer) {
                    File file = new File(f.getNewAbsolutePath());
                    
                    if (file.exists()) {
                        int i = app.getDialogs().showWantToOverwriteDialog(
                                                        file.getAbsolutePath());
                        if (i == IDialogs.OVERWRITE_THIS_FILE) {
                            continue;
                        
                        } else if (i == IDialogs.OVERWRITE_ALL_FILES){
                            break;
                        
                        } else if (i == IDialogs.CANCEL) {
                            return;
                        }
                    
                    } else if ( ! file.getParentFile().canWrite()) {
                        app.getDialogs().showCannotWriteDialog(f);
                        return;
                    }
                }
            }
            
            try {
                renamer.renameFiles();
                
            } catch (Exception e) {
                // errors will be reported @see: IRenameProcessListener
                assert false : e;
            }
        }

        ((SwingApplication)app).getMenuBar().refreshDisplay();
    }

    @Override
    public void setDatum() {
        Date selectedDate = app.getSettingsView().getSelectedDate();
        app.getRenamer().setDatum(selectedDate);
        app.getRenamer().calculateNames();
    }

    @Override
    public void filesWereSelected(int offset,  
                                  boolean excludeDupl, 
                                  List<File> files) {
        List<IRenamable> rfList = new ArrayList<IRenamable>();
        IRenamer renamer = app.getRenamer();

        if (files == null || files.size() <= 0) {
            app.getDialogs().showNoFileWasAddedDialog();
            return;
        }

        FileFilter filter = app.getElementChooser().getFileFilter();
        
        Log.defaultLogger().info(Msg.msg("Controller.applyFileFilter")
                                 .replace("#filter#", filter.getDescription()));
        boolean asked = false;
        int fileCounter = 0;
        
        for (File f : files) {
            if ( ! filter.accept(f)) continue;
            
            rfList.add(new RenamableFile(f, renamer));
            Log.defaultLogger().info(Msg.msg("Controller.addFileToList")
                                       .replace("#file#", f.getAbsolutePath()));

            if (fileCounter++ > 1000 && ( ! asked)) {
                if ( ! app.getDialogs().showHugeSelectionWarning()) return;

                asked = true;
            }
        }
        
        if (rfList.isEmpty()) {
            app.getDialogs().showAllFilesFilteredWarning();
        }

        List<IRenamable> duplicates = renamer.add(offset, excludeDupl, rfList);
        ((SwingApplication)app).getMenuBar().refreshDisplay();
        
        if ( ! excludeDupl || duplicates == null || duplicates.isEmpty()) {
            return;
        }
        
        app.getDialogs().showDuplicatesExcluded(duplicates);
    }

    @Override
    public void removeSelectedElements() {
        int[] selectedElements = app.getListView().getSelection();
        app.getListView().clearSelection();

        if (selectedElements.length <= 0) return; 
        
        app.getRenamer().remove(selectedElements[0], selectedElements.length);
    } 

    @Override
    public void moveSelectedElements() {
        int insertionPosition = app.getDialogs().showMoveInsertionDialog();
        if (insertionPosition < 0) return;
        
        int[] selectedElements = app.getListView().getSelection();
        assert selectedElements.length > 0 : Arrays.toString(selectedElements);
        
        app.getRenamer().move(selectedElements[0],
                              selectedElements.length,
                              insertionPosition);
    }

    @Override
    public void fileCountChanged() {
        boolean moreThan0 = app.getRenamer().getSize() > 0;
        app.getSettingsView().setRenamingEnabled(moreThan0);
        app.getListView().clearSelection();
        ((SwingApplication)app).getMenuBar().refreshDisplay();
    }
    
    @Override
    public void selectionChanged() {
        int[] selected = app.getListView().getSelection();
        ISettingsView cp = app.getSettingsView();
                
        if (selected.length == 0) { // nothing selected!
            L.info(Msg.msg("Controller.selectionChanged.selected.none"));
            cp.setImageToPreview(null);
            ((SwingApplication)app).getMenuBar().refreshDisplay();
            return;
        }

        IRenamable firstSelected = app.getRenamer().getElementAt(selected[0]);

        if (selected.length == 1) {
            L.info(Msg.msg("Controller.selectionChanged.selected.one")
                    .replace("#sel#", firstSelected.getOldName()));

        } else {
            L.info(Msg.msg("Controller.selectionChanged.selected.many")
                    .replace("#num#", Integer.toString(selected.length)));
        }

        ((SwingApplication)app).getMenuBar().refreshDisplay();
        cp.setImageToPreview(firstSelected.getSourceObject().getAbsolutePath());
    }

    @Override
    public void exitApplication() {
        if ( ! app.getDialogs().showQuitDialog()) 
            return;
        
        int exitValue = 0;

        try {
            app.persistSettings(cfg());
            cfg().saveCustomSettings();
            
        } catch (Exception t) {
            app.getDialogs().showErrorWhileSavingConfigDialog(t);
            exitValue = 1;
        }

        Log.defaultLogger().info(Msg.msg("Log.goodBye"));
        System.exit(exitValue);
    }


    @Override
    public void editNummerierungForSelection() {
        int[] selection = app.getListView().getSelection();
        if (selection == null || selection.length <= 0) return;

        IDialogs.NummerierungsSettings ns;
        ns = app.getDialogs().showEditNummerierungSettings();
        if (ns == null) return;

        int increment = ns.increment;
        IRenamer renamer = app.getRenamer();
        String pfx = ns.prefix, sfx = ns.suffix;
        int nummer = ns.start;
        
        for (int i : selection) {
            renamer.getElementAt(i).setAlterNummerierung(pfx + nummer + sfx);
            nummer += increment;
        }
        
        renamer.calculateNames();
    }
    
    
    @Override
    public void selectionDialogFinished() {
        app.getElementChooser().dialogEnd(false);
    }


    @Override
    public void editDateFormat() {
        String fmt = app.getDialogs().showEditDateFormat();
        if (fmt == null) return;
        
        cfg().setCustomProperty(DATUM_FORMAT, fmt);
        app.getRenamer().calculateNames();
    }
    

    // propertychange listener impl ###########################################
    
   @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("date")) setDatum();
    }
    
    

    // listselection listener impl ###########################################
    
   @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        
        if (e.getSource() != app.getListView().getUIComponent()) return;
 
        selectionChanged();
    }
    
    

    // action listener impl #################################################
    
    @Override
    public void actionPerformed(ActionEvent event) {
        final String cmd = event.getActionCommand();
        
        Log.defaultLogger().info(
                   Msg.msg("Controller.actionPerformed").replace("#cmd#", cmd));
        
        if (ADD_FILES.equals(cmd)) 
            addElements();
            
        else if (REMOVE_SELECTED_FILES.equals(cmd)) 
            removeSelectedElements();
        
        else if (MOVE_SELECTED_FILES.equals(cmd)) 
            moveSelectedElements();
        
        else if (REMOVE_ALL_FILES.equals(cmd)) 
            removeAllElements();
        
        else if (EXIT_APP.equals(cmd)) 
            exitApplication();
        
        else if (EDIT_COPY_SETTINGS.equals(cmd)) 
            editCopySettings();
        
        else if (EDIT_ALL_SETTINGS.equals(cmd)) 
            editAllSettings();
        
        else if (SET_BESCHREIBUNG_TO_ALL.equals(cmd)) 
            setBeschreibung(ElementsToHandle.ALL);
        
        else if (SET_THEMA_TO_ALL.equals(cmd))
            setThema(ElementsToHandle.ALL);
        
        else if (SET_TITEL_TO_ALL.equals(cmd))
            setTitel(ElementsToHandle.ALL);
        
        else if (SET_BESCHREIBUNG_TO_SELECTION.equals(cmd)) 
            setBeschreibung(ElementsToHandle.SELECTION);
        
        else if (SET_THEMA_TO_SELECTION.equals(cmd)) 
            setThema(ElementsToHandle.SELECTION);
        
        else if (SET_TITEL_TO_SELECTION.equals(cmd)) 
            setTitel(ElementsToHandle.SELECTION);
        
        else if (SET_BESCHREIBUNG_TO_UNCHANGED.equals(cmd)) 
            setBeschreibung(ElementsToHandle.UNCHANGED);
        
        else if (SET_THEMA_TO_UNCHANGED.equals(cmd)) 
            setThema(ElementsToHandle.UNCHANGED);
        
        else if (SET_TITEL_TO_UNCHANGED.equals(cmd)) 
            setTitel(ElementsToHandle.UNCHANGED);
        
        else if (EDIT_FILTER.equals(cmd)) 
            editFilter();
        
        else if (START_RENAME_PROCESS.equals(cmd)) 
            renameFiles();
            
        else if (SHOW_LOG_HISTORY.equals(cmd)) 
            showLogHistory();
        
        else if (SET_NUMMERIERUNG_TO_SELECTION.equals(cmd))
            editNummerierungForSelection();
        
        else if (DISCARD_SELECTIONS_CHANGES.equals(cmd))
            discardSelectionsChanges(ElementsToHandle.SELECTION);
        
        else if (DISCARD_ALL_CHANGES.equals(cmd))
            discardSelectionsChanges(ElementsToHandle.ALL);
        
        else if (SHOW_COPYRIGHT.equals(cmd))
            ((Dialogs) app.getDialogs()).showCopyRight();
        
        else if (EDIT_DATE_FORMAT.equals(cmd))
            editDateFormat();
        
        else if (SET_CROSS_PLATFORM_LOOK_AND_FEEL.equals(cmd))
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        
        else if (SET_SYSTEM_LOOK_AND_FEEL.equals(cmd))
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        else assert false : cmd;
    }

    /**
     * sets the look and feel to the application ui component and their children
     * 
     * @param lookAndFeelClassName
     *            the class name of the look and feel to apply
     */
    private void setLookAndFeel(String lookAndFeelClassName) {
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
            SwingUtilities.updateComponentTreeUI(
                                          (Component) app.getUIComponent());
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
    
    private IRenamerConfiguration cfg() {
        return app.getRenamer().getConfig();
    }
    
    // drag listener impl #####################################################
    


    
    @SuppressWarnings("unchecked")
    @Override
    public void drop(DropTargetDropEvent dtde) {
        System.out.println("Drop");
        
        try {
            Transferable t = dtde.getTransferable();
            DataFlavor[] flvrs = t.getTransferDataFlavors();
            List<File> droppedFiles = new ArrayList<File>();
            List<File> filesInsertToApp = null;
        
            for (int i = 0; i < flvrs.length; i++) {
                DataFlavor d = flvrs[i];
                
                
                    //on linux:
                if (d.getRepresentationClass() == String.class) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    String data = (String) t.getTransferData(d);
                    System.out.println(data);
                    
                    for (String s : data.split(LINE_SEPARATOR)) {
                        try {
                            File file = new File(new URL(s).toURI());
                            
                            if (file.exists())
                                droppedFiles.add(file);
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    filesInsertToApp = Util.getFileList(
                           droppedFiles.toArray(new File[droppedFiles.size()]),
                           cfg().getBoolean(RECURSE_INTO_DIRECTORIES),
                           cfg().getBoolean(SHOW_HIDDEN_FILES));
                    
                    break; // bugfix: cannot accept drop more than once !
                
                    
                    //for the redmonders:
                } else if (d.getRepresentationClass() == List.class) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    List<File> data = (List<File>) t.getTransferData(d);
                    System.out.println(data);
                    
                    filesInsertToApp = Util.getFileList(
                            data.toArray(new File[data.size()]),
                            cfg().getBoolean(RECURSE_INTO_DIRECTORIES),
                            cfg().getBoolean(SHOW_HIDDEN_FILES));
                            
                    break; // bugfix: cannot accept drop more than once !
                }
            }
            
            if (filesInsertToApp == null)
                return;
            
            boolean recursive = cfg().getBoolean(RECURSE_INTO_DIRECTORIES);
            app.getController().filesWereSelected(Integer.MAX_VALUE,
                                                  recursive,
                                                  filesInsertToApp);            
        } catch (Exception e) {
            e.printStackTrace(System.out);
            dtde.rejectDrop();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {}

    @Override
    public void dragExit(DropTargetEvent dte) {}

    @Override
    public void dragOver(DropTargetDragEvent dtde) {}

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}

    
    
    // window listener impl #################################################
    
    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getSource() == app.getUIComponent()) {
            exitApplication();

        } else if (e.getSource() == app.getElementChooser().getUIComponent()) {
            selectionDialogFinished();

        } else {
            assert false : e.getSource();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}
}