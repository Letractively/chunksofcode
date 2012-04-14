package com.myapp.util.soundsorter.wizard.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.myapp.util.file.FileUtils;
import com.myapp.util.soundsorter.wizard.model.IUnsortedInterpretDirSrc;

//public
class UnsortedDirList implements IUnsortedInterpretDirSrc
{
    public static final String PROPKEY_TO_SORT_ROOT_PATH       =  "TO_SORT_ROOT_PATH";
    
    private List<File> dirs;
    
    
    public UnsortedDirList(Config config) {
        String path = config.getProperty(PROPKEY_TO_SORT_ROOT_PATH);
        File root = new File(path);
        
        if (! isExistingWriteableDir(root)) {
            throw new NullPointerException("toSortRoot not a writeable dir: "+root);
        }
        
        dirs = new ArrayList<File>();
        File[] interpretDirs = root.listFiles(new FileUtils.DirectoryFileFilter());
        Arrays.sort(interpretDirs);

        for (int i = 0; i < interpretDirs.length; i++) {
            File d = interpretDirs[i];
            dirs.add(d);
        }

//        System.out.println("UnsortedDirList() initialized: "+dirs.size()+" dirs loaded.");
    }
    
    
    @Override
    public List<File> getInterpretDirs() {
        return dirs;
    }

    private boolean isExistingWriteableDir(File d) {
        return d.exists() && d.isDirectory() && d.canWrite();
    }
}
