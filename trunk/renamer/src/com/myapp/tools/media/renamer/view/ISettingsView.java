package com.myapp.tools.media.renamer.view;

import java.util.Date;

public interface ISettingsView extends IUIComponent {

    /**
     * returns the actual text of the beschreibung text of the controls
     * 
     * @return the actual text of the beschreibung text
     */
    String getBeschreibungText();

    /**
     * returns the actual text of the titel text of the controls
     * 
     * @return the actual text of the titel text
     */
    String getTitelText();

    /**
     * returns the actual text of the thema text of the controls
     * 
     * @return the actual text of the thema text
     */
    String getThemaText();

    /**
     * returns the currently selected date
     * 
     * @return the currently selected date
     */
    Date getSelectedDate();

    /**
     * sets the text of the beschreibung text field to the actual value
     */
    void resetDestinationText();

    /**
     * attempts the gui to display an icon of the selected element.
     * 
     * @param pathToImageFile
     *            the path of the irenamable's icon, if exists, may be null
     */
    void setImageToPreview(String pathToImageFile);
    
    /**
     * if the list is empty, starting the rename process will be prevented.
     * 
     * @param enabled if renaming is enabled now.
     */
    public void setRenamingEnabled(boolean enabled);
}