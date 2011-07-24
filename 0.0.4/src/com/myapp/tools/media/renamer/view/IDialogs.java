package com.myapp.tools.media.renamer.view;

import java.util.List;
import java.util.Map;

import com.myapp.tools.media.renamer.model.IRenamable;

public interface IDialogs {

    /**
     * incapsulates the information needed to define the nummerierung behaviour<br>
     * used by {@link IDialogs#showEditNummerierungSettings()}
     * 
     * @author andre
     */
    final class NummerierungsSettings {

        public final int start, increment;
        public final String prefix, suffix;

        /**
         * creates a NummerierungsSettings obj with the given properites.
         * 
         * @param start
         *            the start
         * @param increment
         *            the size of a step
         * @param prefix
         *            the string before the number
         * @param suffix
         *            the string before the number
         */
        public NummerierungsSettings(int start,
                                     int increment,
                                     String prefix,
                                     String suffix) {
            this.start = start;
            this.increment = increment;
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }

    /**
     * shows the quit dialog. invoked when the window is being closed.
     * 
     * @return if the application should be exited
     */
    boolean showQuitDialog();

    /**
     * displays an error occured while saving settings.
     * 
     * @param t
     *            the error occured.
     */
    void showErrorWhileSavingConfigDialog(Throwable t);

    /**
     * informs the user that he chose more than 1000 files and ask him if he
     * wants to continue or abort the process.
     * 
     * @return true if the user clicked yes.
     */
    boolean showHugeSelectionWarning();

    /**
     * shows a message that no file was added
     */
    void showNoFileWasAddedDialog();

    /**
     * shows the config dialog for selecting the destination dir.
     * 
     * @return the changed properties, null if cancelled
     */
    Map<String, String> showChooseDestinationDialog();

    /**
     * displays a editor textarea where the user may change his properties file.
     * 
     * @return the text of the changed textarea if the user clicked ok. null if
     *         not.
     */
    String showEditConfigFileDialog();

    /**
     * shows the log records to the user.
     */
    void showLogHistoryDialog();

    /**
     * shows the dialog to configure the filters used by the file chooser. if
     * the user selects the applyfilter box, the filter will be applied to the
     * already chosen files too.
     * 
     * @return the string for the selection of the files or null, if the user
     *         clicks onto cancel
     */
    String showFilterDefinitionDialog();

    /**
     * shows a successfully done renaming process to the user.
     */
    void showRenameProcessFinished();



    int OVERWRITE_THIS_FILE = 1;
    int OVERWRITE_ALL_FILES = 2;
    int CANCEL = -1;

    /**
     * ask the user if he want to overwrite existing files within the renaming
     * process
     * 
     * @param location
     *            the location that would be overwritten
     * @return if the user wants to overwrite this existing file, all, or cancel
     * 
     * @see IDialogs#OVERWRITE_THIS_FILE
     * @see IDialogs#OVERWRITE_ALL_FILES
     * @see IDialogs#CANCEL
     */
    int showWantToOverwriteDialog(String location);

    /**
     * ask the user where he want to move the selected elements to
     * 
     * @return the index of the insertion point
     */
    int showMoveInsertionDialog();

    /**
     * shows an error msg to the user.
     * 
     * @param msg
     *            the message to describe the error.
     * @param t
     *            the throwable which was the cause of the error.
     */
    void showErrorMessage(String msg, Throwable t);

    /**
     * informs the user that a destination file is not writable.
     * 
     * @param f
     *            the non-writable file
     */
    void showCannotWriteDialog(IRenamable f);

    /**
     * ask user how he want the nummerierung to behave.<br>
     * see also : {@link IDialogs.NummerierungsSettings}
     * 
     * @return an object that contains the nummerierungssettings (start,
     *         increment, format, applytoselection)
     */
    NummerierungsSettings showEditNummerierungSettings();

    /**
     * ask the user for a dateformat which will be used to calculate the file's
     * datum name element
     * 
     * @return a format string that can be used to create a dateformat object,
     *         or null if the user cancels the interaction
     */
    String showEditDateFormat();

    /**
     * ask the user if he wants to create a given directory
     * 
     * @param destination
     *            the destination directory
     * 
     * @return true if the user wants to create that dir
     */
    boolean showWantToCreateDir(String destination);

    /**
     * tells the user that he added files that were already in the list
     * 
     * @param duplicates
     *            the files the user wanted to add a second time during the
     *            insertion.
     */
    void showDuplicatesExcluded(List<IRenamable> duplicates);

    /** tells the user that no file was added (maybe all files were filtered) */
    void showAllFilesFilteredWarning();
}