package com.myapp.tools.media.renamer.model;


/**
 * instances of this are able to notify the status of a running rename
 * process of the irenamer where it will be added. this may be useful to
 * implement a status bar or sth. else.<br/>
 * irenameprocesslistener instances must be registered at the irenamer
 * instance.
 * 
 * @author andre
 * 
 */
public interface IRenameProcessListener {

    /**
     * this will be called on start of the renaming process.
     * 
     * @param renamer
     *            the irenamer which will rename at this moment.
     */
    void processStarting(IRenamer renamer);

    /**
     * indicates that a file will be renamed now.
     * 
     * @param file
     *            the file that will be renamed now.
     */
    void processFileStart(IRenamable file);

    /**
     * indicates that the previously declared file was renamed successfully.
     */
    void processFileSuccess();

    /**
     * indicates that the whole process of renaming was successful.
     */
    void processFinished();

    /**
     * shows an error during the renaming process.
     * 
     * @param t
     *            the error that occured.
     * @param f
     *            the file which was involved currently.
     */
    void processFailed(Throwable t, IRenamable f);
}