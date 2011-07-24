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
    public boolean renameFile(boolean overwrite) throws IOException,
                                                    FileAlreadyExistsException {
        final String newFileAbsPath = getNewAbsolutePath();
        File destination = new File(newFileAbsPath);
        final String oldFileAbsPath = file.getAbsolutePath();
        
        if (renamer.getConfig().getBoolean(REPLACE_ORIGINAL_FILES)) {
            
            // System.out.println(
            // "sourcefile path      : " + file.getAbsolutePath());
            // System.out.println("sourcefile can read  : " + file.canRead());
            // System.out.println("sourcefile can write : " + file.canWrite());
            // System.out.println(
            // "targetfile path      : " + destination.getAbsolutePath());
            // System.out.println(
            // "targetfile can read  : " + destination.canRead());
            // System.out.println(
            // "targetfile can write : " + destination.canWrite());
            // System.out.println("will now rename...");
            
            boolean renamed = file.renameTo(destination);
            File oldFile = new File(oldFileAbsPath);
            File newFile = new File(newFileAbsPath);
            
            for (int i = 0; i < 3 && oldFile.isFile() && ! newFile.isFile(); i++) {
                Log.defaultLogger().warning("ERROR! could not remane file '"+oldFileAbsPath+"' to '"+newFileAbsPath+"'. trying again...");
                new File(oldFileAbsPath).renameTo(new File(newFileAbsPath));
            }
            
            if (oldFile.isFile() || ! newFile.isFile()) {
                Log.defaultLogger().warning("ERROR! could not remane file '"+oldFileAbsPath+"' to '"+newFileAbsPath+"'. skipping :-(.");
                return false;
            }
            
            // System.out.println("... done success: " + renamed);
            // System.out.println();
            
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
