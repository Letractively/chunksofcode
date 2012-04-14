package com.myapp.videotools.cli;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.myapp.util.cli.CommandLineUtil;
import com.myapp.videotools.IVideoThumbnailer;

public final class Parameters extends CommandLineUtil {

    public static final String CMD_CAPTURE_FRAME =             "--capture-frame";
    public static final String CMD_CREATE_THUMB_SERIES =       "--capture-thumb-series";
    public static final String CMD_CREATE_BIG_PICTURE =        "--create-big-picture";
    
    public static final String PARAM_VIDEO_ROOT_DIR =          "--video-root-dir";
    public static final String PARAM_BIG_PIC_ROOT_DIR =        "--big-picture-root-dir";
    public static final String PARAM_BIG_PIC_FILE_NAME_PREFIX ="--big-picture-prefix";
    public static final String PARAM_INPUT_FILE =              "--input-file";
    public static final String PARAM_OUTPUT_FILE =             "--output-file";
    public static final String PARAM_IMAGE_WIDTH =             "--width";
    public static final String PARAM_IMAGE_HEIGTH =            "--height";
    public static final String PARAM_BIG_PICTURE_ROWS =        "--rows";
    public static final String PARAM_BIG_PICTURE_COLS =        "--columns";
    public static final String PARAM_SERIES_COUNT =            "--count";
    public static final String PARAM_THUMB_FILE_NAME_PREFIX =  "--thumbnail-prefix";
    
    public static final String FLAG_HELP =                     "--help";
    public static final String FLAG_RECURSIVE =                "--recursive";
    public static final String FLAG_DEBUG_OUTPUT =             "--debug";
    public static final String FLAG_TRACE_OUTPUT =             "--trace";
    public static final String FLAG_CUSTOM_OUTPUT =            "--custom-loglevel";
    public static final String FLAG_COLORED_OUTPUT =           "--colored";

    private static final Map<String, String> SHORT_TO_LONG;
    private static final Map<String, String> LONG_TO_SHORT;
    
    static {
        Hashtable<String, String> short2long = new Hashtable<String, String>();
        Hashtable<String, String> long2short = new Hashtable<String, String>();
        
        short2long.put("-frame", CMD_CAPTURE_FRAME);
        short2long.put("-thumbs", CMD_CREATE_THUMB_SERIES);
        short2long.put("-bigpic", CMD_CREATE_BIG_PICTURE);
        
        short2long.put("-i", PARAM_INPUT_FILE);
        short2long.put("-o", PARAM_OUTPUT_FILE);
        short2long.put("-w", PARAM_IMAGE_WIDTH);
        short2long.put("-h", PARAM_IMAGE_HEIGTH);
        short2long.put("-n", PARAM_SERIES_COUNT);
        short2long.put("-r", PARAM_BIG_PICTURE_ROWS);
        short2long.put("-c", PARAM_BIG_PICTURE_COLS);
        short2long.put("-tp", PARAM_THUMB_FILE_NAME_PREFIX);
        short2long.put("-bp", PARAM_BIG_PIC_FILE_NAME_PREFIX);
        short2long.put("-bpr", PARAM_BIG_PIC_ROOT_DIR);
        short2long.put("-vr", PARAM_VIDEO_ROOT_DIR);

        short2long.put("-R", FLAG_RECURSIVE);
        
        
        Iterator<Map.Entry<String, String>> itr;
        for (itr = short2long.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String, String> e = itr.next();
            String lonG = e.getValue();
            String shorT = e.getKey();
            long2short.put(lonG, shorT);
        }
        
        SHORT_TO_LONG = Collections.unmodifiableMap(short2long);
        LONG_TO_SHORT = Collections.unmodifiableMap(long2short);
        
        if (SHORT_TO_LONG.size() != LONG_TO_SHORT.size()) {
            throw new IllegalStateException("shorthands not unique!");
        }
    }

    
    public Parameters(String[] argsArray) {
        super(argsArray);
    }
    
    public Parameters(List<String> argsList) {
        super(argsList);
    }
    

    public static String printParam(String parameterToPrint) {
        assert parameterToPrint != null;
        String l, s;
        
        if (LONG_TO_SHORT.containsKey(parameterToPrint)) {
            l = parameterToPrint;
            s = LONG_TO_SHORT.get(l);
        
        } else if (SHORT_TO_LONG.containsKey(parameterToPrint)) {
            s = parameterToPrint;
            l = SHORT_TO_LONG.get(s);
        
        } else {
            return parameterToPrint;
        }

        return s + " '" + l + "'";
    }
    
    @Override
    public String getStringAt(String param) {
        if ( ! list.contains(param) && LONG_TO_SHORT.containsKey(param)) {
            return super.getStringAt(LONG_TO_SHORT.get(param));
        }
        
        return super.getStringAt(param);
    }

    @Override
    public boolean isFlagSet(String flag) {
        if ( ! super.isFlagSet(flag) && LONG_TO_SHORT.containsKey(flag)) {
            return super.isFlagSet(LONG_TO_SHORT.get(flag));
        }

        return super.isFlagSet(flag);
    }

    @Override
    public boolean isFirstArgument(String flag) {
        if ( ! super.isFirstArgument(flag) && LONG_TO_SHORT.containsKey(flag)) {
            return super.isFirstArgument(LONG_TO_SHORT.get(flag));
        }
        
        return super.getFirstArgument().equals(flag);
    }
    

    
    public void applyWidthAndHeight(IVideoThumbnailer nailer) {
        boolean hasWidth = isFlagSet(PARAM_IMAGE_WIDTH);
        boolean hasHeight = isFlagSet(PARAM_IMAGE_HEIGTH);

        if ( ! hasWidth && ! hasHeight) {
            return;
        }
        
        if (hasWidth && hasHeight) {
            nailer.setPreferredHeight(getIntAt(PARAM_IMAGE_HEIGTH));
            nailer.setPreferredWidth(getIntAt(PARAM_IMAGE_WIDTH));
            return;
        }

        throw new IllegalArgumentException(
                   "you must specify either none or both of parameters: "+
                   PARAM_IMAGE_WIDTH + " and " + PARAM_IMAGE_HEIGTH);
    }
    
    public void applyRowsAndCols(IVideoThumbnailer nailer) {
        boolean hasRows = isFlagSet(PARAM_BIG_PICTURE_ROWS);
        boolean hasCols = isFlagSet(PARAM_BIG_PICTURE_COLS);

        if ( ! hasRows && ! hasCols) {
            return;
        }
        
        if (hasRows && hasCols) {
            nailer.setBigPictureRows(getIntAt(PARAM_BIG_PICTURE_ROWS));
            nailer.setBigPictureCols(getIntAt(PARAM_BIG_PICTURE_COLS));
            return;
        }
        
        throw new IllegalArgumentException(
                   "you must specify either none or both of parameters: "+
                   PARAM_BIG_PICTURE_ROWS + ", " + PARAM_BIG_PICTURE_COLS);
    }

    public void applyBigPicPrefix(IVideoThumbnailer nailer) {
        if (isFlagSet(PARAM_BIG_PIC_FILE_NAME_PREFIX)) {
            String bigPicPrefix = getStringAt(PARAM_BIG_PIC_FILE_NAME_PREFIX);
            if (bigPicPrefix.contains("%")) {
                throw new IllegalArgumentException(
                           "invalid value for: " + 
                           PARAM_BIG_PIC_FILE_NAME_PREFIX + ": "+ bigPicPrefix);
            }
            nailer.setBigPicturePrefix(bigPicPrefix);
        
        } else {
            nailer.setBigPicturePrefix("");
        }
    }
}
