package com.myapp.tools.media.renamer.model;

import java.io.File;
import java.io.IOException;

import com.myapp.tools.media.renamer.config.IConstants.ISysConstants;
import com.myapp.tools.media.renamer.controller.Log;
import com.myapp.tools.media.renamer.controller.Util;

/**
 * @author andre
 */
public final class RenamableFile implements IRenamable, ISysConstants {
    // TODO: DATUM FLEXIBEL MACHEN
    
    private static final RenameExecutor executor = new RenameExecutor();
    
    private String beschreibung = null;
    private String thema = null;
    private String titel = null;
    private String alterNummerierung = null;

    private final File file;
    private final int hash; // TODO remove hash too if file not final any more
    
    private String newName = null;

    private final IRenamer renamer;

    public RenamableFile(File absoluteFile, IRenamer renamer) {
        if (absoluteFile == null)
            throw new NullPointerException();

        if ( ! absoluteFile.exists())
            throw new IllegalStateException(
                                   "non-existing source file: " + absoluteFile);

        file = absoluteFile;
        hash = file.getAbsolutePath().hashCode();

        this.renamer = renamer;
    }

    @Override
    public String getThema() {
        return thema;
    }

    @Override
    public void setThema (String thema) {
        this.thema = thema;
    }

    @Override
    public String getTitel() {
        return titel;
    }

    @Override
    public void setTitel(String titel) {
        this.titel = titel;
    }

    @Override
    public String getBeschreibung() {
        return beschreibung;
    }

    @Override
    public String getOldName() {
        return file.getName();
    }

    @Override
    public String getOldParentAbsolutePath() {
        return file.getParentFile().getAbsolutePath();
    }
    
    @Override
    public boolean renameFile(boolean overwrite) throws IOException {
        final File srcFile = new File(file.getAbsolutePath());
        final File dstFile = new File(getNewAbsolutePath());
        
        boolean mv = renamer.getConfig().getBoolean(REPLACE_ORIGINAL_FILES);
        if (mv) {
            boolean moved = executor.moveFile(srcFile, dstFile, overwrite);
            if (! moved) {
                throw new IOException("could not move "+srcFile+" to "+dstFile);
            }
            return true;
        }

        boolean copied = executor.copyFile(srcFile, dstFile, overwrite);
        if (! copied) {
            throw new IOException("could not copy "+srcFile+" to "+dstFile);
        }
        return true;
    }

    public boolean renameFileOld(boolean overwrite) throws IOException, FileAlreadyExistsException {
        final String oldPath = file.getAbsolutePath();
        final String newPath = getNewAbsolutePath();
        final File destination = new File(newPath);
        
        if (renamer.getConfig().getBoolean(REPLACE_ORIGINAL_FILES)) {
            boolean renamed = new File(oldPath).renameTo(destination);
            File oldFile = new File(oldPath);
            
            for (int i = 0; i < 3 && oldFile.isFile() && ! destination.isFile(); i++) {
                Log.defaultLogger().warning(
                          "ERROR! could not remane file '"+oldPath+"' " +
                          "to '"+newPath+"'. trying again...");
                renamed = new File(oldPath).renameTo(new File(newPath));
            }

            ////// XXX hack start (renameTo() did not work several times on wind*ws machines...)
            if (oldFile.isFile() || ! destination.isFile()) {
                Log.defaultLogger().warning("APPLY HACK: copy file '"+oldPath+"' to '"+newPath+"'...");
                Util.copyFile(new File(oldPath), destination);
                
                if (destination.isFile()) {
                    Log.defaultLogger().warning("HACK Successful, delete source file '"+oldPath+"'!");
                    new File(oldPath).delete();
                } else {
                    Log.defaultLogger().warning("HACK Unsuccessful. '"+destination+"' could not be created!!");
                }
            }
            ////// XXX hack end /////////////////
            
            
            if (oldFile.isFile() || ! destination.isFile()) {
                Log.defaultLogger().warning("ERROR! could not remane file '"+oldPath+"' to '"+newPath+"'. skipping :-(.");
                return false;
            }
            
            return renamed;
        }

        if ( ! overwrite && destination.exists()) {
            throw new FileAlreadyExistsException(destination);
        }

        Util.copyFile(file, destination);
        
        return true;
    }

    @Override
    public void setBeschreibung(String beschr) {
        beschreibung = beschr;
    }

    @Override
    public void setNewName(String name) {
        newName = name;
    }


    @Override
    public String getNewName() {
        return newName;
    }

    @Override
    public String getNewAbsolutePath() {
        return new StringBuilder()
                    .append(getNewParentAbsolutePath())
                    .append(Renamer.FILE_SEPARATOR)
                    .append(getNewName())
                    .toString();
    }

    @Override
    public String getNewParentAbsolutePath() {
        return renamer.getConfig().getBoolean(REPLACE_ORIGINAL_FILES)
                ? file.getParentFile().getAbsolutePath()
                : renamer.getDestinationDir().getAbsolutePath();
    }

    public String toString() {
        return getOldName();
    }

    @Override
    public void setAlterNummerierung(String alterNummerierung) {
        this.alterNummerierung = alterNummerierung;
    }

    @Override
    public String getAlterNummerierung() {
        return alterNummerierung;
    }

    @Override
    public File getSourceObject() {
        return file;
    }

    @Override
    public void discardChanges() {
        beschreibung = null;
        thema = null;
        titel = null;
        alterNummerierung = null;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        
        if (other == this)
            return true;
        
        if ( ! (other instanceof RenamableFile))
            return false;
        
        return hashCode() == other.hashCode();
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
}
