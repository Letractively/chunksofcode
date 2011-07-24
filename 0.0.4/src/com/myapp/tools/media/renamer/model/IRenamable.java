package com.myapp.tools.media.renamer.model;

import java.io.File;
import java.io.IOException;


/**
 * @author andre
 */
public interface IRenamable {   // TODO: DATUM FLEXIBEL MACHEN

    /**
     * commits the renaming transaction for this file. it will be copied to its
     * destination dir, or, if destination dir is equal to the source dir,
     * renamed.
     * 
     * @param overwrite
     *            if the file should be overwritten when already existent
     * @return if the file could be renamed
     * 
     * @throws IOException
     * @throws FileAlreadyExistsException
     */
    boolean renameFile(boolean overwrite) throws IOException, FileAlreadyExistsException;

// *****************************************************************************
// *************** GETTERS AND SETTERS *****************************************
// *****************************************************************************

    /**
     * returns the beschreibung of this file
     * 
     * @return the beschreibung of this file
     */
    String getBeschreibung();

    /**
     * returns the titel of this file
     * 
     * @return the titel of this file
     */
    String getTitel();

    /**
     * returns the thema of this file
     * 
     * @return the thema of this file
     */
    String getThema();

    /**
     * returns a preview of this file's name after the renaming
     * 
     * @return a preview of this file's name after the renaming
     */
    String getNewName();

    /**
     * returns the original name of this file
     * 
     * @return the original name of this file
     */
    String getOldName();

    /**
     * returns the absolute path of this file's parent after renaming
     * 
     * @return the absolute path of this file's parent after renaming
     */
    String getOldParentAbsolutePath();

    /**
     * returns the absolute path of this file's parent dir after renaming
     * 
     * @return the absolute path of this file's parent dir after renaming
     */
    String getNewParentAbsolutePath();

    /**
     * returns the absolute path of this file after renaming
     * 
     * @return the absolute path of this file after renaming
     */
    String getNewAbsolutePath();

    /**
     * sets the beschreibung of this file
     * 
     * @param beschreibung
     *            the new beschreibung of this file
     */
    void setBeschreibung(String beschreibung);

    /**
     * sets the titel of this file
     * 
     * @param titel
     *            the new titel of this file
     */
    void setTitel(String titel);

    /**
     * sets the thema of this file
     * 
     * @param thema
     *            the new thema of this file
     */
    void setThema(String thema);

    /**
     * sets the name of this file. the file will have this name after renaming.
     * 
     * @param newName
     *            the new beschreibung of this file
     */
    void setNewName(String newName);

    /**
     * sets a userdefined nummerierung to this file. if null, the file will use
     * its standard nummerierung string as given by the renamer.
     * 
     * @param alterNummerierung
     *            the nummerierung string to use for this file instead of the
     *            one given from the renamer
     */
    void setAlterNummerierung(String alterNummerierung);

    /**
     * returns a userdefined nummerierung to this file. if null, the file will
     * use its standard nummerierung string as given by the renamer.
     * 
     * @return the alternative nummerierung for this file, null if none is
     *         defined.
     */
    String getAlterNummerierung();

    /**
     * returns the source object for this irenamable
     * 
     * @return the file for this irenamable
     */
    File getSourceObject();

    /**
     * fall back to default values for this irenamable. all user changes will be
     * removed.
     */
    void discardChanges();
}
