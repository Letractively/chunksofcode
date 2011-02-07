package com.myapp.tools.media.renamer.model;

import java.io.File;

/**
 * used for testing the renamers list methods without using a physical file.
 * just a dumb placeholder.
 * 
 * @author andre
 * 
 */
class DummyRenamable implements IRenamable, Cloneable {

    private static final File file = new File(System.getProperty("user.dir"));

    private static int counter = 0;
    public final String name;
    public final int number;

    public DummyRenamable() {
        name = null;
        number = counter++;
    }

    public DummyRenamable(String name) {
        number = name.hashCode();
        this.name = name;
    }

    private DummyRenamable(DummyRenamable f) {
        number = f.number;
        name = f.name;
    }

    public String getBeschreibung() {
        return null;
    }

    public String getNewAbsolutePath() {
        return null;
    }

    public String getNewName() {
        return null;
    }

    public String getNewParentAbsolutePath() {
        return null;
    }

    public String getOldName() {
        return null;
    }

    public String getOldParentAbsolutePath() {
        return null;
    }

    public String getThema() {
        return null;
    }

    public String getTitel() {
        return null;
    }

    public String getAlterNummerierung() {
        return null;
    }

    public File getSourceObject() {
        return file;
    }

    public boolean renameFile(boolean b) {
        return false;
    }

    public void setBeschreibung(String beschreibung) {
    }

    public void setNewName(String newName) {
    }

    public void setThema(String thema) {
    }

    public void setTitel(String titel) {
    }

    public void setAlterNummerierung(String alterNummerierung) {
    }

    public int hashCode() {
        return number;
    }

    public DummyRenamable clone() {
        return new DummyRenamable(this);
    }

    public String toString() {
        return "DUMMY-" + (name == null ? number : name);
    }

    public boolean equals(Object o) {
        return (o instanceof DummyRenamable) 
                ? ((DummyRenamable) o).number == number
                : false;
    }

    @Override
    public void discardChanges() {
    }
}
