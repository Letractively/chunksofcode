package com.myapp.util.file;

import java.io.File;

public class FileUtil {
    
    /**
     * @deprecated use {@link FileUtils#deleteRecursively(File)} instead
     */
    @Deprecated 
    public static void deleteRecursively(File f) {
        FileUtils.deleteRecursively(f);
    }
}
