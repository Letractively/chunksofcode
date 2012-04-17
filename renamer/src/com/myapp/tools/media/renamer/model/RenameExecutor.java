package com.myapp.tools.media.renamer.model;

import java.io.*;
import java.util.logging.Logger;

import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Util;

/** encapsulates the logic of the execution and the validation of the
 * renaming process.
 * 
 * @author andre */
public class RenameExecutor {
    
    private static Logger L = Log.defaultLogger();
    
    private final Object sync = new Object();
    private File src;
    private File dst;
    private long srcSize;
    
    
    public RenameExecutor() {
    }
    
    public final boolean moveFile(File srcFile, File dstFile, boolean overwriteArg) throws IOException {
        try {
            synchronized (sync) {
                L.entering("RenameExecutor", "moveFile");
                preCheck(srcFile, dstFile, overwriteArg);
                this.src = srcFile;
                this.srcSize = src.length();
                this.dst = dstFile;
                L.info("source file:      '"+src+"' (size:"+src.length()+")");
                L.info("destination file: '"+dst+"'");
                moveImpl();
                return true;
            }
        } finally {
            L.exiting("RenameExecutor", "moveFile");
            this.src = null;
            this.dst = null;
        }
    }
    
    public final boolean copyFile(File srcFile, File dstFile, boolean overwriteArg) throws IOException {
        try {
            synchronized (sync) {
                L.entering("RenameExecutor", "copyFile");
                preCheck(srcFile, dstFile, overwriteArg);
                this.src = srcFile;
                this.srcSize = src.length();
                this.dst = dstFile;
                L.info("source file:      '"+src+"' (size:"+src.length()+")");
                L.info("destination file: '"+dst+"'");
                copyImpl();
                return true;
            }
        } finally {
            L.exiting("RenameExecutor", "moveFile");
            this.src = null;
            this.dst = null;
        }
    }

    private void copyImpl()  throws IOException {
        copyTryHard();
        if (! dst.isFile()) {
            throw new IOException("could not copy '"+src+"' to '"+dst+"'");
        }
    }

    private void moveImpl() throws IOException {
        // try renameTo() n times until call succeeds:
        
        renameToTryHard();
        
        // check result of renameTo():
        
        if ( ! src.isFile()) {
            handleSourceNotPresent();
            return;
        }

        L.warning("WARNING! source file is still existing, renaming was NOT SUCCESSFUL!");
        
        if (dst.isFile()) {     
            handleDstIsPresent();
            return;
        }
        
        L.warning("ERROR: destination file is not existing, renaming was NOT SUCCESSFUL!");
        
        if (src.length() != srcSize) {
            L.warning("FATAL: source has incorrect size (maybe data damaged!)");
            throw new IOException("source file size has changed during operation: "+src.length());
        }
        
        L.info("source has still correct size");
        
        // XXX HACK START -----------------------------------------------
        L.info("will now try a workaround: copy and then delete source...");
        if (copyTryHard() && dst.length() == srcSize) {
            deleteSourceTryHard();
            L.info("worked around :-)");
            return; // maybe incorrect, src may still exist
        }
        // XXX HACK END -----------------------------------------------

        L.severe("source has still correct size");
        throw new IOException("unable to rename file: "+src);
    }


    /**
     * called when src and dst are existing files after trying to rename.
     * when dst has the correct size, src will be deleted.<br>
     * else when src is still consistent (size did not change), i'll try to
     * copy the file to the destination and delete the source afterwards.<br>
     * 
     * @throws Exception if src and dst both have incorrect sizes
     */
    private void handleDstIsPresent() throws IOException {
        L.info("destination file is existing. size="+dst.length());
        
        if (dst.length() == srcSize) {     
            L.info("destination has correct size.");
            deleteSourceTryHard();
            return; // maybe incorrect, src may still exist
        } 

        L.warning("WARNING destination has incorrect size");
        
        if (src.length() == srcSize) { // try to copy
            L.info("source has still correct size");
            
            if (copyTryHard()) {
                deleteSourceTryHard();
                return; // maybe incorrect, src may still exist
            }
            
            throw new IOException("could not copy source to destination!");
        }
        
        L.warning("FATAL: source also has incorrect size (maybe data damaged!)");
        throw new IOException("source file size has changed during operation: "+src.length());
    }


    /**
     * called when the src file was not present after trying to rename the file.<br>
     * when the destination file is existing and its length differs from the original
     * length, a warning will be raised.<br>
     * when the length of the destination file is equal to the original length,
     * this method will return normally, because everything is fine in this case.
     * 
     * @throws IOException if the destination file does not exist either
     */
    private void handleSourceNotPresent() throws IOException {
        L.info("source file does not exist");
        
        if (dst.isFile()) {
            L.info("destination file does exist.");
            if (dst.length() != srcSize) { // FAIL, but there is nothing we can do...
                L.warning("WARNING: destination file not same size as source file! dst="+dst);
            }
            
        } else { // hope this will never happen !
            L.info("destination file also not exist either.");
            L.warning("FATAL: both source and destination are not existing!");
            throw new IOException("no such files: '"+src+"', '"+dst+"'");
        }
    }
    
    /**
     * copies src to dst
     * @return if the destination file exists after the operation and has the same size as the source file
     * @throws IOException when an I/O error occured.
     */
    private boolean copyTryHard() throws IOException {
        L.info("will now copy source to destination...");
        for (int n = 3, i = 1; i <= n; i++) {
            Util.copyFile(src, dst);
            
            if (dst.isFile() && dst.length() == srcSize) {
                L.info("file copied.");
                return true;
            }
        }
        return false;
    }


    /**
     * @return
     */
    private boolean renameToTryHard() {
        for (int n = 3, i = 1; i <= n; i++) {
            L.info("will now call renameTo() (try number:"+i+")...");
            boolean ok = src.renameTo(dst);
            
            if (ok) {
                L.info("renameTo() routine returned true, seems to be ok.");
                return true;
            }
            L.warning("renameTo() routine returned false!");
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean deleteSourceTryHard() {
        for (int i = 1; i <= 3; i++) {
            L.info("deleting source (try number:"+i+") ...");
            src.delete();
            
            if (! src.isFile()) {
                L.info("ok, source was deleted!");
                return true;
            }
            
            L.warning("WARNING: could not delete source file: "+src);
        }
        
        if (! src.isFile()) {
            return true;
        }
        
        L.warning("ERROR: could not delete source file: "+src);
        return false;
    }
    

    private static void preCheck(File srcFile, File dstFile, boolean overwriteArg) throws IOException {
        if (srcFile == null || ! srcFile.isFile()) {
            throw new FileNotFoundException(String.valueOf(srcFile));
        }
        if (dstFile == null) {
            throw new NullPointerException("destination file is null!");
        }
        if (! overwriteArg && dstFile.exists()) {
            throw new IOException("overwrite is disabled and destination exists: "+dstFile);
        }
        if (dstFile.isDirectory()) {
            throw new IOException("cannot overwrite directories: "+dstFile);
        }
    }
}
