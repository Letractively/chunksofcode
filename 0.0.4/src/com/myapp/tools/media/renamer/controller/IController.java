package com.myapp.tools.media.renamer.controller;

import java.io.File;
import java.util.List;

/**
 * defines the use cases that must be handled by the application.
 * 
 * @author andre
 */
public interface IController {

    /**
     * defines the set of elements that will be handled
     * 
     * @author andre
     */
    enum ElementsToHandle {
        SELECTION, ALL, UNCHANGED
    }

    /**
     * tells the application that the user wants to add elements to the list
     */
    void addElements();

    /**
     * tells the application that the user wants to remove all elements
     */
    void removeAllElements();

    /**
     * tells the application that the user wants edit the copy settings
     */
    void editCopySettings();

    /**
     * tells the application that the user wants edit the extended settings
     */
    void editAllSettings();

    /**
     * tells the application that the user wants to set the beschreibung
     * 
     * @param applyTo
     *            the files that will be changed
     */
    void setBeschreibung(ElementsToHandle applyTo);

    /**
     * tells the application that the user wants to set the titel
     * 
     * @param applyTo
     *            the files that will be changed
     */
    void setTitel(ElementsToHandle applyTo);

    /**
     * tells the application that the user wants to set the thema
     * 
     * @param applyTo
     *            the files that will be changed
     */
    void setThema(ElementsToHandle applyTo);

    /**
     * tells the application that the user wants to discard changes.
     * 
     * @param applyTo
     *            specifies the set of irenamables that will be affected.
     */
    void discardSelectionsChanges(ElementsToHandle applyTo);

    /**
     * tells the application that the user wants to see the log history
     */
    void showLogHistory();

    /**
     * tells the application that the user wants edit the filters
     */
    void editFilter();

    /**
     * tells the application that the user wants to start the rename process
     */
    void renameFiles();

    /**
     * tells the application that the user wants to set the datum
     */
    void setDatum();

    /**
     * tells the application that the user has changed the selection
     */
    void selectionChanged();

    /**
     * tells the application that the user wants to exit the application
     */
    void exitApplication();

    /**
     * tells the application that the user has finished selecting new elements
     */
    void selectionDialogFinished();

    /**
     * tells the application that the user wants to alter the nummerierung for
     * the currently selected items
     */
    void editNummerierungForSelection();

    /**
     * this will add the file list to the list at the given offset. the files in
     * the list will be filtered by the currently active filter of the
     * filechooser component.
     * 
     * @param offset
     *            the position where the files will be added. 0 would be the
     *            start of the list
     * @param excludeDupl
     *            if duplicate files will be excluded (see irenamer interface)
     * @param files
     *            the files to add to the list
     */
    void filesWereSelected(int offset, boolean excludeDupl, List<File> files);

    /**
     * action to remove the currently selected files from the list.
     */
    void removeSelectedElements();

    /**
     * action to move the currently selected files from the list.
     */
    void moveSelectedElements();

    /**
     * refreshes the display after the file count has changed.
     */
    void fileCountChanged();
    
    /**
     * tells the application that the user wants to edit the date format
     */
    void editDateFormat();
}