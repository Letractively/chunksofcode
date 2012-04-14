package com.myapp.util.soundsorter.wizard.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * represents a structured collection of target directories. <br>
 * these dirs are the targets where the unsorted dirs will be copied to. <br>
 * <br>
 * there are four types of target dirs:
 * <ul>
 * <li>unsorted directory for putting unsorted songs into a targetdir that is
 * supposed to collect unsorted songs for e.g. a genre</li>
 * <li>interpret dirs, supposed to collect songs grouped by interpret</li>
 * <li>maximix dirs, supposed to collect maxi-cds, mixes, compilations</li>
 * <li>singlesong dirs, supposed to collect single songs grouped by e.g. genre</li>
 * </ul>
 * 
 * @author andre
 * 
 */
public class DestinationTargets 
{

    private static final Comparator<String> CMP = String.CASE_INSENSITIVE_ORDER;

    private SortedMap<String, File> interpretTargetDirs = new TreeMap<String, File>(CMP);
    private SortedMap<String, File> unsortedTargetDirs = new TreeMap<String, File>(CMP);
    private SortedMap<String, File> singleSongTargetDirs = new TreeMap<String, File>(CMP);
    private SortedMap<String, File> maxiMixTargetDirs = new TreeMap<String, File>(CMP);
    private List<File> dirsToCreateFirst = new ArrayList<File>();


    public DestinationTargets() {
    }


    public void addDirToCreateFirst(String dir) {
        dirsToCreateFirst.add(new File(dir));
    }

    /**
     * traverse all dirs according to the state of this targets, and create all
     * directories needed to continue.
     * */
    public List<File> getMissingDirs() {
        List<File> reallyCreated = new ArrayList<File>();

        for (ListIterator<File> itr = dirsToCreateFirst.listIterator(); itr.hasNext();) {
            File toMake = itr.next();

            if (toMake.isDirectory()) {
                continue;
            }

            reallyCreated.add(toMake);
        }

        return reallyCreated;
    }

    public void mapInterpretTargetDir(String i) {
        interpretTargetDirs.put(i, new File(i));
    }

    public void mapUnsortedTargetDir(String i) {
        unsortedTargetDirs.put(i, new File(i));
    }

    public void mapSingleSongsTargetDir(String i) {
        singleSongTargetDirs.put(i, new File(i));
    }

    public void mapMaxiMixTargetDir(String i) {
        maxiMixTargetDirs.put(i, new File(i));
    }

    public File getInterpretTargetDir(String dirPath) {
        return interpretTargetDirs.get(dirPath);
    }

    public File removeInterpretTargetDir(String dirPath) {
        return interpretTargetDirs.remove(dirPath);
    }

    public File getUnsortedTargetDir(String dirPath) {
        return interpretTargetDirs.get(dirPath);
    }

    public File removeUnsortedTargetDir(String dirPath) {
        return interpretTargetDirs.remove(dirPath);
    }

    public File getSingleSongsTargetDir(String dirPath) {
        return interpretTargetDirs.get(dirPath);
    }

    public File removeSingleSongsTargetDir(String dirPath) {
        return interpretTargetDirs.remove(dirPath);
    }

    public File getMaxiMixTargetDir(String dirPath) {
        return interpretTargetDirs.get(dirPath);
    }

    public File removeMaxiMixTargetDir(String dirPath) {
        return interpretTargetDirs.remove(dirPath);
    }

    public SortedMap<String, File> getInterpretTargetDirs() {
        return interpretTargetDirs;
    }

    public SortedMap<String, File> getUnsortedTargetDirs() {
        return unsortedTargetDirs;
    }

    public SortedMap<String, File> getSingleSongTargetDirs() {
        return singleSongTargetDirs;
    }

    public SortedMap<String, File> getMaxiMixTargetDirs() {
        return maxiMixTargetDirs;
    }
}
