package com.myapp.tools.media.renamer.controller;

import static com.myapp.tools.media.renamer.controller.Msg.msg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.logging.Logger;


/**
 * manages lock files organized by keys. lock files will be stored in the /tmp
 * space of the current system. if a lock is locked, the date of the already
 * existing lock can be displayed.
 * 
 * @author andre
 */
public final class LockManager {

    /**
     * deletes lockfiles when the vm exits.
     * 
     * @author andre
     */
    private static final class OnShutDown implements Runnable {

        final File file;

        /**
         * creates a new OnShutDown for a lock file that will be deleted on
         * exit.
         * 
         * @param file
         */
        OnShutDown(File file) {this.file = file;}
        
        @Override
        public void run() {
            L.info(msg("Log.AppFrame.ShutdownSignalCaught"));
            file.delete();
        }
    }

    private static final Logger L = Log.defaultLogger();
    public static final Object sync = new Object();


    /**
     * all static.
     */
    private LockManager() {}

    /**
     * checks if the key is currently locked.
     * 
     * @param id
     *            the key of the lock
     * @return if there is already a lock for the given key
     */
    public static boolean isLocked(String id) {
        synchronized (sync) {
            return lockFile(id).exists();   
        }
    }

    /**
     * reads the date of the current lock of the given key.
     * 
     * @param id
     *            the key for the lock
     * @return the date when the given lock was created.
     */
    public static Date getLockTime(String id) {
        synchronized (sync) {
            Object obj;

            try {
                ObjectInputStream ois;
                ois = new ObjectInputStream(
                        new FileInputStream(lockFile(id)));
                obj = ois.readObject();
                ois.close();

            } catch (Exception e) {
                e.printStackTrace();
                assert false : e;
                return null;    
            }

            return (Date) obj;
        }
    }

    /**
     * creates a lock for the given key
     * 
     * @param id
     *            the key
     * @return the date of the locking action.
     */
    public static Date lock(String id) {
        synchronized (sync) {
            Date date = new Date(); 
            L.info(msg("Log.AppFrame.createNewLock"));

            try {
                unlock(id);
                final File f = lockFile(id);
                f.createNewFile();
                f.deleteOnExit();

                Runtime.getRuntime().addShutdownHook(
                                            new Thread(new OnShutDown(f)));
                ObjectOutputStream oos; 
                oos = new ObjectOutputStream(new FileOutputStream(f));
                oos.writeObject(date);
                oos.flush();
                oos.close();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return date;
        }
    }

    /**
     * delete the given lock.
     * 
     * @param id
     *            the key of the lock
     */
    private static void unlock(String id) {
        synchronized (sync) {
            if (isLocked(id))
                lockFile(id).delete();
        }
    }

    /**
     * returns the file object for the lock for the given key.
     * 
     * @param id
     *            the key of the lock
     * @return the file that locks the key
     */
    private static File lockFile(String id) {
        return new File(System.getProperty("java.io.tmpdir") + 
                        System.getProperty("file.separator") + 
                        id + ".LOCK");
    }
}