package com.myapp.tools.media.renamer.controller;

import static com.myapp.tools.media.renamer.controller.Msg.msg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.myapp.tools.media.renamer.config.IConstants;
import com.myapp.tools.media.renamer.model.IRenamable;

/**
 * a collection of helpful methods for common needs.
 * 
 * @author andre
 */
public final class Util implements IConstants.ISysConstants {

    public static final Format FORMAT = new SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm:ss.S");
    public static final Pattern FILTER_PATTERN = Pattern.compile(
                                        "([a-z]{1,}\\s*\\,\\s*)*([a-z]{1,})?",
                                        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern HUMAN_READABLE = 
                                 Pattern.compile("[0-9]{0,3}([.,][0-9]{0,2})?");
    private static final String[] sizeSuffixes = 
                                 new String[] {"K","M","G","T","P","E","Z","Y"};

    
    /**
     * no instance needed
     */
    private Util() {}

    /**
     * prints the stack trace of a throwable into a string
     * 
     * @param t
     *            the throwable to print
     * @return a string containing the stacktrace
     */
    public static String stackTraceToString(Throwable t) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(baos));
        return new String(baos.toByteArray());
    }

    /**
     * returns a list of file objects being returned from a jfilechooser.
     * 
     * @param filesFromChooser
     *            the file array from the jfilechooser
     * @param recursive
     *            if true, files from subfolders are being added too
     * @param includeHiddenFiles
     *            if true, hidden folders and files will be added
     * @return the list of the files of the filesFromChooser
     */
    public static List<File> getFileList(File[] filesFromChooser,
                                  boolean recursive,
                                  boolean includeHiddenFiles) {
        if (filesFromChooser == null)
            return null;

        List<File> list = new ArrayList<File>();

        if (recursive)
            for (File f : filesFromChooser)
                recurseDirs(f, list, includeHiddenFiles);
        else {
            for (File f : filesFromChooser) 
                if ((includeHiddenFiles && f.isHidden()) || f.isDirectory())
                    continue;
                else if (f.isFile())
                    list.add(f);
        }
        
        return list;
    }

    /**
     * adds the given file or, if its a dir, its subfiles to the given filelist.
     * 
     * @param file
     *            the file or directory
     * @param list
     *            the list to add files to
     * @param includeHiddenFiles
     *            specifies if hidden files and dirs are included
     */
    private static void recurseDirs(File file,
                                    List<File> list,
                                    boolean includeHiddenFiles) {
        if (includeHiddenFiles && file.isHidden())
            return;

        else if (file.isFile())
            list.add(file);

        else if (file.isDirectory()) {
            File[] farr = file.listFiles();
            
            if (farr == null || farr.length <= 0) return;
                
            Arrays.sort(farr, FILE_COMPARATOR);
            for (File f : farr) recurseDirs(f, list, includeHiddenFiles);
        }
    }
    
    /**
     * returns a case insensitibe comparator for files
     */
    private static final Comparator<File> FILE_COMPARATOR = 
                                                    new Comparator<File>() {
        public int compare(File o1, File o2) {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getAbsolutePath(),
                                                         o2.getAbsolutePath());
        }
    };


    /**
     * formats a logrecord for the renamer application.
     * 
     * @param r
     *            the record
     * @return the string which is the formatted record
     */
    public static String logRecordToString(LogRecord r) {
        String msg = r.getMessage();
        StringBuilder bui = new StringBuilder(25 + msg.length());
        bui.append(date(r));

        for (int i = bui.length(); i < 25; i++)
            bui.append(' ');

        bui.append(' ');
        bui.append(msg);
        return bui.toString();
    }

    /**
     * calculates the formatted date of the logrecord and returns it as an
     * string.
     * 
     * @param r
     *            the record whose date is being fromatted.
     * @return the date as a string.
     */
    public static String date(LogRecord r) {
        return FORMAT.format(new Date(r.getMillis()));
    }

    /**
     * creates a file filter from a comma separated list of file extensions.
     * @param csl the list of file extensions
     * @return the file filter that will filter the extensions 
     */
    public static final FileFilter createFileFilterFromCommaSepList(String csl) {
        List<String> l = new ArrayList<String>();
        
        for (String s : csl.split(","))  {
            if (s == null) continue;
            
            s = s.trim();
            if (s.length() > 0) l.add(s);
        }
        
        String[] filters = l.toArray(new String[]{});
        
        if (filters.length <= 0) return new FileFilter() {
            public boolean accept(File f) {return true;}
            public String getDescription() {
                return msg("Dialogs.createFileFilter.matchAllFilter");
            }
        };
        
        String descr = msg("FileChooser.filter.description")
                                   .replace("#list#", Arrays.toString(filters));
        
        return new FileNameExtensionFilter(descr, filters);
    }



    public static String oldAbsolutePath(IRenamable f) {
        return f.getOldParentAbsolutePath() + File.separator + f.getOldName();
    }

    public static String newAbsolutePath(IRenamable f) {
        return f.getNewParentAbsolutePath() + File.separator + f.getNewName();
    }
    


    /**
     * returns a new list from an given array
     * @param <T> the runtime type
     * 
     * @param arr
     *            the array or varargs
     * @return a list containing the elements of the array
     */
    public static <T> List<T> arrayToList(T... arr) {
        if (arr == null) return null;

        List<T> l = new ArrayList<T>(arr.length);
        for (T t : arr) l.add(t);
        new Object().hashCode();
        return l;
    }

    /**
     * copies the source file to the destination file.
     * @param src the source file
     * @param dst the destination file
     * @throws IOException
     */
    @SuppressWarnings("null")
	public static void copyFile(File src, File dst) throws IOException {
        if ( ! dst.getParentFile().exists()) dst.getParentFile().mkdirs();
        
        dst.createNewFile();
        
        FileChannel srcC = null;
        FileChannel dstC = null;

        try {
            srcC = new FileInputStream(src).getChannel();
            dstC = new FileOutputStream(dst).getChannel();
            dstC.transferFrom(srcC, 0, srcC.size());

        } finally {
            try {
                if (src != null) srcC.close();
                if (dst != null) dstC.close();
            } catch (Exception e) {
                e.printStackTrace();
                assert false;
            }
        }
    }
    
    
    public String getHumanReadableFileSize(File f) {
        return getHumanReadableFileSize(f.length());
    }
    
    public static String getHumanReadableFileSize(long lengthInBytes) {
        double len = new Long(lengthInBytes).doubleValue();
        String suffix = "";
        
        for (int i = 0; i < sizeSuffixes.length; i++) {
            if (len < 1000) break;
            
            len = len / 1000;
            suffix = sizeSuffixes[i];
        }
        
        String size = Double.toString(len);
        Matcher m =  HUMAN_READABLE.matcher(size);
        
        if (m.find()) {
            size = m.group();
            
        } else throw new RuntimeException(size);
        
        size = size.trim();
        size = size.substring(0, size.length() >= 4 ? 4 : size.length());
        
        if (size.contains(".")) {
            while(size.endsWith("0")) {
                size = size.substring(0, size.length() - 1);
            }
        }
        
        if (size.endsWith(".")) size = size.substring(0, size.length() - 1);
        
        return size + " " + suffix + "B";
    }
}
