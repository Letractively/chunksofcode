package com.myapp.videotools.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.videotools.Configuration;
import com.myapp.videotools.IImageMerger;
import com.myapp.videotools.IVideoFileParser;
import com.myapp.videotools.IVideoThumbnailer;



/**
 * @author andre
 *
 */
public final class FFMPEG {


    public static final String FFMPEG_COMMAND_PROPKEY = "com.myapp.videotools.FFMPEG_CMD";
    public static final String FFMPEG_PARSER_IMPL_PROPKEY = "com.myapp.videotools.FFMPEG_PARSER_IMPL";
    public static final String THUMBNAILER_IMPL_PROPKEY = "com.myapp.videotools.THUMBNAILER_IMPL";
    public static final String IMAGE_MERGER_IMPL_PROPKEY = "com.myapp.videotools.IMAGE_MERGER_IMPL_PROPKEY";
    
    private static final Logger LOG = LoggerFactory.getLogger(FFMPEG.class);
    private static final String FFMPEG_CONFIG_FILE_NAME = "ffmpeg.properties";

    
    private static FFMPEG instance = null;
    private Properties ffmpegProperties = null;
    
    
    
    public static FFMPEG getInstance() {
        if (instance == null)
            synchronized (FFMPEG.class) {
                if (instance == null)
                    instance = new FFMPEG();
            }

        return instance;
    }

    

    private FFMPEGData data = null;
    
    
    private FFMPEG() {
        LOG.debug("will now create FFMPEG wrapper instance...");
        initFfmpegProperties();
        parseFfmpegMetaData();
        
        if (data.isNoCodecsLoaded()) 
            LOG.warn("  no codecs loaded, is ffmpeg program available?");
        else
            LOG.debug("  {} codecs available", data.getSupportedCodecs().size());
        
        if (data.isNoSupportedFileTypesLoaded()) 
            LOG.warn("  no formats loaded, is ffmpeg program available?");
        else
            LOG.debug("  {} filetypes available", data.getSupportedFileTypes().size());
        
        LOG.debug("OK, FFMPEG wrapper instance created!");
    }
    
    private void initFfmpegProperties() {                
        ClassLoader cl = getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(FFMPEG_CONFIG_FILE_NAME);
        ffmpegProperties = new Properties();
        
        try { 
            ffmpegProperties.load(is);
            
        } catch (Exception e) {
            LOG.warn("  could not read config file " + FFMPEG_CONFIG_FILE_NAME, e);
            throw new RuntimeException("can't live without ffmpeg command!", e);
        }
    
    }

    private FFMPEGData parseFfmpegMetaData() {
        data = new FFMPEGData();
        
        // determine how to execute ffmpeg:
        //-----
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("windows")) {
            data.setFfmpegCommand(ffmpegProperties.getProperty(FFMPEG_COMMAND_PROPKEY));
            
        } else if (osName.contains("linux")) {
            data.setFfmpegCommand("ffmpeg");
        
        } else {
            throw new RuntimeException("unknown os name: " + osName);
        }
    
        LOG.debug("  ffmpeg command is: {}", data.getFfmpegCommand());
        
        // call ffmpeg info and determine which fileformats and codecs are supported:
        //-----
        Map<String, String> fileFormats = new HashMap<String, String>();
        Map<String, String> codecs = new HashMap<String, String>();
        Matcher formatMatcher = 
             Pattern.compile("\\s*([_,a-zA-Z0-9]{2,})\\s+(.+)").matcher("");
        ProcessBuilder b = new ProcessBuilder()
                                .redirectErrorStream(true)
                                .command(data.getFfmpegCommand(), "-formats");
        try {
            Process proc = b.start();
            BufferedReader input = new BufferedReader(
                                   new InputStreamReader(proc.getInputStream()));
            boolean listingFileFormats = false, listingCodecs = false;
            
            for (String line = null; (line = input.readLine()) != null;) {
                String trimmed = line.trim();

                if (trimmed.length() == 0) {
                    listingCodecs = false;
                    listingFileFormats = false;
                    
                } else if (trimmed.equals("File formats:")) {
                    listingCodecs = false;
                    listingFileFormats = true;
                    
                } else if (trimmed.equals("Codecs:")) {
                    listingCodecs = true;
                    listingFileFormats = false;

                } else if (listingCodecs) {
                    String codec = line.substring(8).trim();
                    String code = codec.substring(0, codec.indexOf(' ')).trim();
                    String description = codec.substring(codec.indexOf(' ')).trim();
                    codecs.put(code, description);
                    // System.out.println(
                    // "FFMPEG.parseFfmpegMetaData()  CODEC:  " +
                    // "code='"+code+"',               " +
                    // "description='"+description+"'");

                } else if (listingFileFormats) {
                    String fmt = line.substring(4);
                    formatMatcher.reset(fmt);
                    
                    if ( ! formatMatcher.matches()) {
                        LOG.error("  pattern '{}' did not match for format '{}'",formatMatcher, fmt);
                        continue;
                    }
                    
                    String fmtKey = formatMatcher.group(1);
                    String description = formatMatcher.group(2);
                    fileFormats.put(fmtKey, description);
                    // System.out.println(
                    // "FFMPEG.parseFfmpegMetaData()  FILE FORMAT:  " +
                    // "fmtKey='"+fmtKey+"',               " +
                    // "description='"+description+"'");
                }
            }
            
        } catch (IOException e) {
            LOG.error(  "while determining ffmpeg supported types. this could mean that ffmpeg cannot be started by this program.", e);
        }
        
        data.setSupportedCodecs(codecs);
        data.setSupportedFileTypes(fileFormats);
        traceSupportedCodecsAndFileTypes();
        
        if (data.isNoCodecsLoaded()) 
            LOG.warn("  no codecs loaded, is ffmpeg program available?");
        else
            LOG.debug("  {} codecs available", data.getSupportedCodecs().size());
        
        if (data.isNoSupportedFileTypesLoaded()) 
            LOG.warn("  no formats loaded, is ffmpeg program available?");
        else
            LOG.debug("  {} filetypes available", data.getSupportedFileTypes().size());
        
        return data;
    }
    
    public FFMPEGData getApplication() {
        return data;
    }
    
    public String getFfmpegProperty(String key) {
        return ffmpegProperties.getProperty(key);
    }
    
    public IVideoFileParser createVideoFileParser() {
        String className = Configuration.getInstance().getProperty(FFMPEG_PARSER_IMPL_PROPKEY);
        Object o = getInstance(className);
        LOG.debug("  IVideoFileParser impl is:     {}", o.getClass());
        return (IVideoFileParser) o;
    }
    
    public IVideoThumbnailer createVideoThumbnailer() {
        String className = Configuration.getInstance().getProperty(THUMBNAILER_IMPL_PROPKEY);
        Object o = getInstance(className);
        LOG.debug("  IVideoThumbnailer impl is:    {}", o.getClass());
        return (IVideoThumbnailer) o;
    }
    
    public IImageMerger createImageMerger() {
        String className = Configuration.getInstance().getProperty(IMAGE_MERGER_IMPL_PROPKEY);
        Object o = getInstance(className);
        LOG.debug("  IImageMerger impl is:         {}", o.getClass());
        return (IImageMerger) o;
    }
    
    private static Object getInstance(String classname) {
        Class<?> implClass;
        
        try {
            implClass = Class.forName(classname);
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("no such class :"+classname+" !", e);
        }
        
        Constructor<?>[] constructors = implClass.getConstructors();
        Constructor<?> defaultConstructor = null;
        
        for (int i = 0; i < constructors.length; i++)
            if (constructors[i].getParameterTypes().length == 0) {
                defaultConstructor = constructors[i];
                break;
            }
        
        if (defaultConstructor == null)
            throw new RuntimeException("no no-args constructor for class :"+classname+" !");
        
        Object o = null;

        try {
            o = defaultConstructor.newInstance();
            
        } catch (Exception e) {
            throw new RuntimeException("could not create an instance of "+classname+" !", e);
        }
        
        return o;
    }
    
    
    public void printFfmpegDebugInfo() {
        LOG.info("******************* FFMPEG INFO: **************************");
        String[] infoCmd = new String[]{data.getFfmpegCommand(), "-version"};
        
        Process p = null;
        
        try {
            ProcessBuilder pb = new ProcessBuilder(infoCmd);
            pb.redirectErrorStream(true);
            p = pb.start();
            
        } catch (IOException e) {
            LOG.error("error while executing " + Arrays.toString(infoCmd), e);
        }

        try {
            if (p != null) {
                BufferedReader
                or = new BufferedReader(new InputStreamReader(p.getInputStream())),
                er = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line = null;
    
                LOG.debug("******* STD-OUT *********");
                while ((line = or.readLine()) != null)
                    LOG.debug("*    {}", line);
    
                LOG.debug("******* STD-ERR *********");
                while ((line = er.readLine()) != null)
                    LOG.debug("*    {}", line);
            }
            
        } catch (IOException e) {
            LOG.error("error while reading from process "+Arrays.toString(infoCmd), e);
        }
        
        LOG.debug("******************* FFMPEG INFO ***************************");
    }

    

    public void traceSupportedCodecsAndFileTypes() {
        if ( ! LOG.isTraceEnabled()) {
            return;
        }
        
        LOG.trace(data.toString());
    }
}
