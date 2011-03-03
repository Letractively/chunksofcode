package com.myapp.videotools;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVideoFileFilter implements FileFilter {

    private static final Logger log = LoggerFactory.getLogger(DefaultVideoFileFilter.class);
    private static final String EXCLUDED_FILE_SUFFIXES_PROPKEY = "EXCLUDED_FILE_SUFFIXES";
    
    private Set<String> excludedExtensions = new HashSet<String>();
    
    public DefaultVideoFileFilter() {
        log.trace("  loading file filter from config file {}", Configuration.propertiesFileName);
        readConfigFile();
        log.trace("  OK, file filter loaded.");
    }

    public void readConfigFile() {
        String excludies = Configuration.getInstance().getProperty(EXCLUDED_FILE_SUFFIXES_PROPKEY);
        String[] splatter = excludies.split(",");
        
        for (int i = 0; i < splatter.length; i++) {
            String s = splatter[i].toLowerCase();
            excludedExtensions.add(s);
        }
        
        log.trace("    {} files will be filtered: {}" ,excludedExtensions.size(), excludies);
    }

    @Override
    public boolean accept(File f) {
        if ( ! f.exists() || f.isDirectory() || f.isHidden()) {
            return false;
        }

        String name = f.getName().toLowerCase();
        if (name.endsWith("~")) {
            return false; // skip temp files
        }

        int lastDotPos = name.lastIndexOf(".");
        if (lastDotPos <= 0) {
            return false; // skip files with no extension or beginning with a dot
        }

        String extension = name.substring(lastDotPos);
        
        if (extension.length() == 0) {
            return false; // skip files ending with a dot
        }
        if (excludedExtensions.contains(extension)) {
            return false;
        }

        return true;
    }
}
