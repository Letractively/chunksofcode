package com.myapp.tools.media.renamer.model;

import java.io.File;

/**
 * indicates that a file that would be overwritten without overwrite-manner.
 * 
 * @author andre
 */
@SuppressWarnings("serial")
public class FileAlreadyExistsException extends Exception {

    private File file;

    /**
     * creates a new FileAlreadyExistsException for a given file.
     * 
     * @param destination
     *            the file that would be overwritten
     */
    public FileAlreadyExistsException(File destination) {
        super("File already exists !" + destination.getAbsolutePath());
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

}
