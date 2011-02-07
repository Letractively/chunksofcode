package com.myapp.util.cli;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * util for dealing with command line arguments
 * 
 * @author andre
 *
 */
public class CommandLineUtil { 

    protected final List<String> list;
    
    
    public CommandLineUtil(String[] argsArray) {
        this(Arrays.asList(argsArray)); // throws nullpointerexception if null
    }
    
    public CommandLineUtil(List<String> argsList) {
        if (argsList == null) {
            throw new NullPointerException();
        }
        
        list = Collections.unmodifiableList(argsList);
    }

    public boolean isFlagSet(String flag) {
        assert flag != null;
        return list.contains(flag);
    }
    
    public String getStringAt(String param) {
        if ( ! list.contains(param)) {
            throw new IllegalArgumentException("parameter required: "+param);
        }
        
        if (isContainedMoreThanOnce(param)) {
            throw new IllegalArgumentException(
                  "parameter " + param + " is given more than once!");
        } 
        
        String argument = null;
        try {
            argument = list.get(1 + list.indexOf(param));

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "you have to specify an argument after: " + param);
        }

        return argument;
    }
    
    public int getIntAt(String param) {
        String str = getStringAt(param);
        try {
            return Integer.parseInt(str);
            
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "you have to specify an integer after: " + param, e);
        }
    }
    
    public boolean isContainedMoreThanOnce(String param) {
        int firstOccurence = list.indexOf(param);
        int lastOccurence = list.lastIndexOf(param);
        
        if (firstOccurence == lastOccurence) {
            return false;
        }
        
        return true;
    }

    
    public File getFileAt(String fileParam) {
        String path = getStringAt(fileParam);
        return new File(path);
    }
    
    public File getExistingFileAt(String fileParam) {
        return getExistingFileAt(fileParam, false, false, false, true);
    }
    
    public  File getReadableFileAt(String fileParam) {
        return getExistingFileAt(fileParam, true, false, false , true);
    }
    
    public  File writeableFileAtParameter(String fileParam) {
        return getExistingFileAt(fileParam, false, true, false, true);
    }
    
    public  File writableDirAtParameter(String dirParam) {
        return getExistingFileAt(dirParam, false, true, true, false);
    }

    public  File readableDirAtParameter(String dirParam) {
        return getExistingFileAt(dirParam, true, false, true, false);
    }
    
    public File getExistingFileAt(String param,
                                   boolean mustBeReadable,
                                   boolean mustBeWritable,
                                   boolean mustBeDir,
                                   boolean mustBeFile) {
        File file = getFileAt(param);
        String path = file.getAbsolutePath();
        
        if (mustBeFile && mustBeDir) {
            throw new IllegalArgumentException(
                                        "what, file AND directory? ("+path+")");
        }
        
        if ( ! file.exists()) {
            throw new IllegalArgumentException(
                                       "file " + path + " does not exist!");
        }
        if (mustBeDir && ! file.isDirectory()) {
            throw new IllegalArgumentException(
                                   "file " + path + " is not a directory!");
        }
        if (mustBeFile && ! file.isFile()) {
            throw new IllegalArgumentException(
                                "file " + path + " is not a regular file!");
        }
        if (mustBeReadable && ! file.canRead()) {
            throw new IllegalArgumentException(
                                 "file path " + path + " is not readable!");
        }
        if (mustBeWritable && ! file.canWrite()) {
            throw new IllegalArgumentException(
                                 "file path " + path + " is not writable!");
        }

        return file;
    }
    
    public boolean isFirstArgument(String flag) {
        if (flag == null){
            throw new NullPointerException();
        }
        
        if (list.isEmpty()) {
            return false;
        }
        
        return getFirstArgument().equals(flag);
    }
    
    public  void failIfEmpty() {
        if (list.size() == 0) {
            throw new IllegalArgumentException("no arguments were specified.");
        }
    }

    public String getFirstArgument() {
        return list.get(0);
    }

    public boolean containsArgument(Object o) {
        return list.contains(o);
    }

    public String getArgAt(int index) {
        return list.get(index);
    }

    public int indexOf(Object arg) {
        return list.indexOf(arg);
    }

    public Iterator<String> argumentsIterator() {
        return list.iterator();
    }

    public int size() {
        return list.size();
    }
    
}
