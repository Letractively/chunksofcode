package com.myapp.tools.media.renamer.model;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.myapp.tools.media.renamer.config.Config;

/**
 * singelton implementation of a IRenamer, designed for a desktop application.
 * 
 * @author andre
 */
public class Renamer extends AbstractRenamer {

    static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static volatile IRenamer instance = null;

    public static IRenamer getInstance () {
        if (instance == null)
            synchronized (Renamer.class) {
                if (instance == null)
                    instance = new Renamer();
            }
        return instance;
    }


    private Date datum = new Date();
    private int nummerierungStart = Config.getInstance().getNummerierungStart();


    /**
     * this private constructor will be called from the getInstance method.
     */
    private Renamer() {}


    @Override
    public void calculateNames() {
        List<INamePart> nameElements = getConfig().getNameElementsList(this);
        StringBuilder hackler = new StringBuilder();

        for (IRenamable file : this) {
            hackler.setLength(0);

            for (INamePart nameElement : nameElements) 
                hackler.append(nameElement.getFormattedString(file));

            file.setNewName(hackler.toString());
        }
    }

    @Override
    public String previewNewAbsolutePath(IRenamable file) {
        return getDestinationDir().getAbsolutePath() +
                FILE_SEPARATOR +
                file.getNewName();
    }

    /* ************************* getter, setter *******************************/    

    public int getNummerierungStart() {
        return nummerierungStart;
    }

    public File getDestinationDir() {
        return new File(getConfig().getDestinationPath());
    }

    public Date getDatum() {
        return datum;
    }

    public void setNummerierungStart(int start) {
        nummerierungStart = start;
    }

    public void setDatum(Date d) {
        datum = d;
    }
}
