package com.myapp.util.soundsorter.wizard.model;

import java.io.File;
import java.util.List;



/**
 * a source for directories that contain songs. these directories will be 
 * merged into the target sound collection.
 * 
 * @author andre
 *
 */
public interface IUnsortedInterpretDirSrc 
{

    /**
     * returns a list of directories containing songs; this directories will then be sorted into the "sorted collection"
     * 
     * @return a list of dirs contained in this collection of unsorted songs.
     * 
     */
    public abstract List<File> getInterpretDirs();

}