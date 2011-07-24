package com.myapp.tools.media.renamer.view;

import javax.swing.filechooser.FileFilter;

public interface IElementChooserView extends IUIComponent {

    /**
     * shows the filechooser dialog
     */
    void dialogShow();

    /**
     * stops the wait-for-end-of-dialog. this will end the dialog. calling this
     * method without starting the dialog with dialogShow() has no effect.
     * 
     * @param userClickedOk
     *            if the user clicked ok.
     */
    void dialogEnd(boolean userClickedOk);

    /**
     * sets a commaseparated filename extension filter string as the new filter
     * of the renamer.
     * 
     * @param filter the new filter
     */
    void setFileFilter(FileFilter filter);

    /**
     * Returns the currently selected file filter.
     * 
     * @return the currently selected file filter.
     */
    FileFilter getFileFilter();
}