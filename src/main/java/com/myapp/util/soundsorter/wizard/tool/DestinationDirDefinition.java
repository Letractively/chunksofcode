package com.myapp.util.soundsorter.wizard.tool;

import static java.io.File.separator;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.util.soundsorter.wizard.model.DestinationTargets;
import com.myapp.util.soundsorter.wizard.model.IDestinationDefinition;

/**
 * 
 * parses a configfile and constructs a {@link DestinationTargets} instance from
 * it.
 * 
 * @author andre
 * 
 */
//public 
class DestinationDirDefinition implements IDestinationDefinition {

    private static final Pattern LEADING_BLANKS_PATTERN = Pattern.compile("(^\\s*)(.*)$");
    
	private static final String KEYWORD_TARGET_ROOT           = "TARGET_ROOT";
	private static final String KEYWORD_INTERPRET_DIRECTORIES = "INTERPRET_DIRECTORIES";
	private static final String KEYWORD_LONESOME_FILES_DIR    = "LONESOME_FILES_DIR";
	private static final String KEYWORD_UNSORTED              = "UNSORTED";
	private static final String KEYWORD_MAXIMIX_DIR           = "MAXIMIX_DIR";

    static final String PROPKEY_TARGET_ROOT_PATH        =  "TARGET_ROOT_PATH";
	
	private Config config;
	private File destinationRoot;
    private DestinationTargets targets;

	
    public DestinationDirDefinition(Config cfg) {
        this.config = cfg;
        String path = config.getProperty(PROPKEY_TARGET_ROOT_PATH);
        this.destinationRoot = new File(path);

        if (! isExistingWriteableDir(destinationRoot)) {
            throw new NullPointerException("destinationRoot not a writeable dir: "+destinationRoot);
        }
        
        
        parseTargets();
	}

    @Override
    public DestinationTargets getDestinationTargets() {
        return targets;
    }
    

    /**
     * reads the config file and creates a new <code>DestinationTargets</code> from it.
     * */
    private void parseTargets() {
        String lineStr = null, trimStr = null;
        boolean listStartedFlag = false;
        int leadingBlanksCount = -1;
        Integer levelNum = null;
        StringBuilder pathBuilder = new StringBuilder();
        targets = new DestinationTargets();
        Map<Integer, String> parentPathsMap = new HashMap<Integer, String>();
        parentPathsMap.put(Integer.valueOf(0), destinationRoot.getAbsolutePath());

        final String singleFilesDirName = config.getProperty(Config.PROPKEY_LONESOME_FILES_DIR_NAME);
        final String maxiMixDirName = config.getProperty(Config.PROPKEY_MAXIMIX_DIR_NAME);
        final String unsortedDirName = config.getProperty(Config.PROPKEY_UNSORTED_DIR_NAME);
        
        for (Iterator<String> itr = config.getConfigFileLineWise(); itr.hasNext();) {
            lineStr = itr.next();
            trimStr = lineStr.trim();
            
            if (trimStr.length() <= 0 || trimStr.startsWith("#")) {
                continue;
            }
            
            if (trimStr.equals(KEYWORD_TARGET_ROOT)) { 
                // file structure starts with this word
                if ( ! listStartedFlag) {
                    listStartedFlag = true;
                    
                } else {
                    throw new RuntimeException("config not consistent: "+KEYWORD_TARGET_ROOT+" contained twice!");
                }
                
                continue;
            }
            
            if ( ! listStartedFlag) {
                // do nothing until the start keyword occurs
                continue;
            }
            
            // determine level in file hierarchy
            leadingBlanksCount = getLeadingBlankCount(lineStr);
            
            if (leadingBlanksCount % 4 != 0) {
                throw new RuntimeException("intendation ("+leadingBlanksCount+") must be multiple of 4 ! string: '" + lineStr + "'");
            }
            
            // save path to TARGET_ROOT in a map (ugly)
            levelNum = Integer.valueOf(leadingBlanksCount / 4);
            parentPathsMap.put(levelNum, trimStr);
            pathBuilder.setLength(0);
            
            // construct the path of this directory:
            for (int i = 0, lim = levelNum.intValue(); i < lim; i++) {
                // append all nodes between current dir and TARGET_ROOT
                String nodeNumberI = parentPathsMap.get(Integer.valueOf(i));
                pathBuilder.append(nodeNumberI).append(separator);
            }
            
            if (trimStr.equals(KEYWORD_INTERPRET_DIRECTORIES)) {
                // this is a dir where the dirs of an interpret can be contained in:
                targets.mapInterpretTargetDir(pathBuilder.toString());
                continue;
            }
            
            if (trimStr.equals(KEYWORD_LONESOME_FILES_DIR)) {
                pathBuilder.append(singleFilesDirName);
                // this is a container dir where single songs will be stored:
                targets.mapSingleSongsTargetDir(pathBuilder.toString());
                
            } else if (trimStr.equals(KEYWORD_MAXIMIX_DIR)) {
                pathBuilder.append(maxiMixDirName);
                // here, mixes and compilations will be contained:
                targets.mapMaxiMixTargetDir(pathBuilder.toString());
                
            } else if (trimStr.equals(KEYWORD_UNSORTED)) {
                pathBuilder.append(unsortedDirName);
                // this dir will contain unsorted songs and stuff:
                targets.mapUnsortedTargetDir(pathBuilder.toString());
            
            } else {
                // a dir without a special meaning:
                pathBuilder.append(trimStr);
            }
            
            // register dir in the targets to create
            targets.addDirToCreateFirst(pathBuilder.toString());
        }
    } 
	
	private static int getLeadingBlankCount(String s) {
	    Matcher leadingBlanks = LEADING_BLANKS_PATTERN.matcher(s);
	    
	    if (leadingBlanks.find()) {
	        String group1 = leadingBlanks.group(1);
	        int len = group1.length();
	        return len;
	    }
	    
	    throw new RuntimeException("every string starts with zero or more blanks !?");
	}

    private boolean isExistingWriteableDir(File d) {
        return d.exists() && d.isDirectory() && d.canWrite();
    }
    
    public File getDestinationRoot() {
        return destinationRoot;
    }
}
