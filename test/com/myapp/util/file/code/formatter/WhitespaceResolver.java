package com.myapp.util.file.code.formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class WhitespaceResolver {

    private static final int INDENTATION_SIZE = 4;

    private File rootDirectory;
    private int blanksPerTab;
    private Collection<String> validExtenstions = new ArrayList<String>();
    private Collection<String> excludeDirs = new ArrayList<String>();
    private Collection<File> changedFiles = new HashSet<File>();

    {
        validExtenstions.add(".java");
        validExtenstions.add(".sh");
        validExtenstions.add(".xml");
        validExtenstions.add(".jsp");
        validExtenstions.add(".js");
        excludeDirs.add(".svn");
        excludeDirs.add(".metadata");
    }


    public WhitespaceResolver(String rootDir, int blanksPerTab) {
        setBlanksPerTab(blanksPerTab);
        setRootDirectory(new File(rootDir));
    }


    public void start() throws IOException {
        changedFiles.clear();
        recursively(rootDirectory);

        List<String> chList = new ArrayList<String>();


        String rootPath = rootDirectory.getAbsolutePath().trim();

        for (File f : changedFiles) {
            chList.add(f.getAbsolutePath().replace(rootPath, ""));
        }

        Collections.sort(chList);

//        int i = 0;
//        for (String s : chList) {
//            System.out.println(i++ + ".) changed: " +s);
//        } 
    }

    protected void recursively(File f) throws IOException {
        if (f.isDirectory()) {
            if (excludeDirs.contains(f.getName())) return;

            for (File kid : f.listFiles()) recursively(kid);

        } else {
            boolean skip = true;
            String name = f.getName();

            for (String ext : validExtenstions)
                if (name.endsWith(ext)) {
                    skip = false;
                    break;
                }

            if (skip) return;

            System.out.println("handling file: " + f.getAbsolutePath());

            File newFile = handleFile(f.getAbsolutePath());
            copyFile(newFile, f);
            System.out.println("file " + f.getAbsolutePath() + " " +
                               "was replaced by " + newFile.getAbsolutePath());
        }
    }


    public static void copyFile(File src, File dst) throws IOException {
        FileChannel from = new FileInputStream(src).getChannel();
        FileChannel to = new FileOutputStream(dst).getChannel();
        from.transferTo(0, src.length(), to);

        from.close();
        to.close();
    }


    public Collection<String> getValidExtenstions() {
        return validExtenstions;
    }

    public void setValidExtenstions(Collection<String> validExtenstions) {
        this.validExtenstions = validExtenstions;
    }

    public Collection<String> getExcludeDirs() {
        return excludeDirs;
    }

    public void setExcludeDirs(Collection<String> excludeDirs) {
        this.excludeDirs = excludeDirs;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public int getBlanksPerTab() {
        return blanksPerTab;
    }

    public void setRootDirectory(File rootDirectory) {
        if (!rootDirectory.exists())      
            throw new RuntimeException(rootDirectory.toString());
        if (!rootDirectory.isDirectory()) 
            throw new RuntimeException(rootDirectory.toString());

        this.rootDirectory = rootDirectory;
    }

    public void setBlanksPerTab(int blanksPerTab) {
        this.blanksPerTab = blanksPerTab <= 0 
                                ? INDENTATION_SIZE 
                                : blanksPerTab;
    }

    public File handleFile(String path) throws IOException {
        if (path == null) throw new RuntimeException(path);

        File file = new File(path);
        File tempFile = File.createTempFile(file.getName(), null);
        BufferedReader r = new BufferedReader(
                           new InputStreamReader(
                           new FileInputStream(file)));
        PrintWriter w = new PrintWriter(new FileOutputStream(tempFile));
        String l = null, foo = null;

        while ((l = r.readLine()) != null) {
            foo = handleLine(l, blanksPerTab);
            if (! foo.equals(l)) changedFiles.add(file);

            w.println(foo);
        }

        r.close();
        w.flush();
        w.close();

        return tempFile;
    }

    public static void main(String[] args) throws IOException {
        WhitespaceResolver whtspcRslvr = 
            new WhitespaceResolver(
                    "/home/andre/workspace/playground",
//                    "/home/andre/Desktop/untitledfolder/app-renamer",
                    4);
        whtspcRslvr.start();

    }

    public static String handleLine(String line, final int blanksPerDir) {
        if (line.contains("\n") || line.contains("\r"))
            throw new RuntimeException("not a single line string");

        if (line.trim().isEmpty())  return ""; //no lines with only whitespace !
        if ( ! line.contains("\t")) return line;  //nothing to do!


        // line is not empty and contains \t chars !

        StringBuilder bui = new StringBuilder();
        int tabPos;
        int blanksToAdd;
        
        String l = line;

        while ((tabPos = l.indexOf('\t')) >= 0) {
            blanksToAdd = blanksPerDir - tabPos % blanksPerDir;
            bui.setLength(0);
            for (int i = 0; i < blanksToAdd; i++) bui.append(' ');

            l = l.replaceFirst("\t", bui.toString());
        }

        return l;
    }
}
