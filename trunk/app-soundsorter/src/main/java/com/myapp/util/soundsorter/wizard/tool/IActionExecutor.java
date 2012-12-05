package com.myapp.util.soundsorter.wizard.tool;

import java.io.File;



/**
 * handles a user decision while sorting songs
 * @author andre
 *
 */
public interface IActionExecutor {

    /**
     * perform an action to handle a transfer request
     * @param interpretDir the directory to move
     * @param destinationDir the target directory where to copy to
     * @return
     */
    public abstract int handleFiles(File interpretDir, 
                                    File destinationDir) throws Exception;

}