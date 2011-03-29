package com.myapp.videotools.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.security.krb5.Config;

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
    public static final String MONTAGE_COMMAND_PROPKEY = "com.myapp.videotools.MONTAGE_CMD";
    
    public static final String FFMPEG_PARSER_IMPL_PROPKEY = "com.myapp.videotools.FFMPEG_PARSER_IMPL";
    public static final String THUMBNAILER_IMPL_PROPKEY = "com.myapp.videotools.THUMBNAILER_IMPL";
    public static final String IMAGE_MERGER_IMPL_PROPKEY = "com.myapp.videotools.IMAGE_MERGER_IMPL";
    
    private static final Logger LOG = LoggerFactory.getLogger(FFMPEG.class);

    
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
        ffmpegProperties = Configuration.getInstance().getProperties();
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
    
    private FFMPEGData parseFfmpegMetaData() {
        data = new FFMPEGData();
        
        Configuration cfg = Configuration.getInstance();

        // determine how to execute ffmpeg:
        //-----
        String ffmpegCmd = cfg.getProperty(FFMPEG_COMMAND_PROPKEY);
        String montageCmd = cfg.getProperty(MONTAGE_COMMAND_PROPKEY);
        
        data.setFfmpegCommand(ffmpegCmd);
        data.setMontageCommand(montageCmd);

        LOG.debug("  ffmpeg  command is: {}", data.getFfmpegCommand());
        LOG.debug("  montage command is: {}", data.getMontageCommand());
        
        parseMetadataOsSpecific();
        traceSupportedCodecsAndFileTypes();
        
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
            throw new RuntimeException("no default constructor for :"+classname+" !");
        
        Object o = null;

        try {
            o = defaultConstructor.newInstance();
            
        } catch (Exception e) {
            throw new RuntimeException("could not create "+classname+" !", e);
        }
        
        return o;
    }
    
    private void parseMetadataOsSpecific() {        
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("linux")) {
            parseLinux();
        } else if (osName.contains("windows")) {
            parseWindows();
        } else {
            throw new RuntimeException("unknown operating system: " + osName);
        }
    }
    
    private void parseLinux() {
        Map<String, String> types, codecs;
        ProcessBuilder pb = new ProcessBuilder().redirectErrorStream(true).command(
            data.getFfmpegCommand(), 
            "-formats"
        );
        Process process = null;
        BufferedReader reader = null;
        StringBuilder temp = new StringBuilder();
        
        try {
            process = pb.start();
            reader = new BufferedReader(new InputStreamReader(
                                                 process.getInputStream()));
            
            for (String l = null; (l = reader.readLine()) != null;) {
                temp.append(l);
                temp.append("\n");
            }
            
            String out = temp.toString();
            types = parseFileTypes(new BufferedReader(new StringReader(out)));
            codecs = parseCodecs(new BufferedReader(new StringReader(out)));
            
        } catch (IOException e) {
            throw ffmpegNotAvailableException(e);
        }
        
        data.setSupportedCodecs(codecs);
        data.setSupportedFileTypes(types);
    }
    
    private RuntimeException ffmpegNotAvailableException(IOException e) {
        URL url = FFMPEG.class.getClassLoader().getResource(Configuration.propertiesFileName);
        File configFile = new File(url.getFile());
        
        return new RuntimeException(
            "Are you sure program '"+data.getFfmpegCommand()+"' is available?" +
            "Make sure that FFMPEG is properly installed. (for more details see:"+
            configFile.getAbsolutePath()+")", 
            e
        );
    }
    
    private void parseWindows() {
        Map<String, String> types, codecs;
        ProcessBuilder pb = new ProcessBuilder().redirectErrorStream(true).command(
            data.getFfmpegCommand(), 
            "-formats"
        );

        try {
            Process p = pb.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(
                                                           p.getInputStream()));
            types = parseFileTypes(r);
        
            // need to call ffmpeg twice with different arguments:
            pb = new ProcessBuilder().redirectErrorStream(true).command(
                data.getFfmpegCommand(),
                "-codecs"
            );
        
            p = pb.start();
            r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            codecs = parseCodecs(r);
            
        } catch (IOException e) {
            throw ffmpegNotAvailableException(e);
        }
        
        data.setSupportedCodecs(codecs);
        data.setSupportedFileTypes(types);
    }

    private static Map<String, String> parseFileTypes(BufferedReader input) throws IOException  {
        Map<String, String> fileFormats = new HashMap<String, String>();
        Matcher formatMatcher = Pattern.compile(
            // ' DE alaw            PCM A-law format'
            // '  E avm2            Flash 9 (AVM2) format'
            // ' D  mov,mp4,m4a,3gp,3g2,mj2 QuickTime/MPEG-4/Motion JPEG 2000 format'
             "^ .{4} \\s* ([_,a-zA-Z0-9]{2,}) \\s+ (.+?) \\s* $",
             Pattern.COMMENTS
        ).matcher("foo");
        boolean parse = false;
        
        for(String l = null; (l = input.readLine()) != null;) {
            if (l.trim().startsWith("File formats:")) {
                parse = true;
                continue;
            }
            if (l.trim().startsWith("Codecs:")) {
                parse = false;
                continue;
            }
            if ( ! parse)
                continue;
            
            if ( ! formatMatcher.reset(l).matches()) {
                LOG.trace("  SKIP line : '{}'", l);
                continue;
            }

            String fmtKey = formatMatcher.group(1);
            String description = formatMatcher.group(2);
            // System.out.println("fileFormats parsed: "+fmtKey+" = " + description);
            fileFormats.put(fmtKey, description);
        }
        
        return fileFormats;
    }
    
    private static Map<String, String> parseCodecs(BufferedReader input) throws IOException {
        Map<String, String> codecs = new HashMap<String, String>();
        Matcher codecMatcher = Pattern.compile(
            // 'D V D  vmdvideo        Sierra VMD video'
            // 'D V    vmnc            VMware Screen Codec / VMware Video'
            // 'D VSDT mpegvideo_xvmc  MPEG-1/2 video XvMC (X-Video Motion Compensation)'
            // 'D A    adpcm_ea_maxis_xa ADPCM Electronic Arts Maxis CDROM XA'
            "^ .{7} \\s* ([_,a-zA-Z0-9]{2,}) \\s+ (.+?) \\s* $",
            Pattern.COMMENTS
        ).matcher("foo");
        boolean parse = false;
        
        for (String l = null; (l = input.readLine()) != null;) {
            if (l.trim().startsWith("File formats:")) {
                parse = false;
                continue;
            }
            if (l.trim().startsWith("Codecs:")) {
                parse = true;
                continue;
            }
            if ( ! parse)
                continue;
            
            if ( ! codecMatcher.reset(l).matches()) {
                LOG.trace("  SKIP line : '{}'", l);
                continue;
            }
            
            String cdcKey = codecMatcher.group(1);
            String description = codecMatcher.group(2);
            // System.out.println("codec parsed: "+cdcKey+" = " + description);
            codecs.put(cdcKey, description);
        }
        
        return codecs;
    }
    
    
    public void printFfmpegDebugInfo() {
        LOG.trace("******************* FFMPEG INFO: **************************");
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
    
                LOG.trace("******* STD-OUT *********");
                while ((line = or.readLine()) != null)
                    LOG.trace("*    {}", line);
    
                LOG.trace("******* STD-ERR *********");
                while ((line = er.readLine()) != null)
                    LOG.trace("*    {}", line);
            }
            
        } catch (IOException e) {
            LOG.error("error while reading from process "+Arrays.toString(infoCmd), e);
        }
        
        LOG.trace("******************* FFMPEG INFO ***************************");
    }

    

    public void traceSupportedCodecsAndFileTypes() {
        if ( ! LOG.isTraceEnabled()) {
            return;
        }
        
        LOG.trace(data.toString());
    }
}
