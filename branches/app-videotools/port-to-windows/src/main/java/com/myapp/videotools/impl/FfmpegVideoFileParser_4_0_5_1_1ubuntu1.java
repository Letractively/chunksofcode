package com.myapp.videotools.impl;

import static java.util.regex.Pattern.COMMENTS;
import static java.util.regex.Pattern.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.videotools.Util;
    
/**
videofile parser implementation based on following ffmpeg version:
<br><br>
<pre>
ffmpeg (4:0.5.1-1ubuntu1) lucid

Publishing details
Published on 2010-03-04

Built packages
    * ffmpeg multimedia player, server and encoder
    * ffmpeg-dbg Debug symbols for ffmpeg related packages
    * ffmpeg-doc documentation of the ffmpeg API
    * libavcodec-dev development files for libavcodec
    * libavcodec52 ffmpeg codec library
    * libavdevice-dev development files for libavdevice
    * libavdevice52 ffmpeg device handling library
    * libavfilter-dev development files for libavfilter
    * libavfilter0 ffmpeg video filtering library
    * libavformat-dev development files for libavformat
    * libavformat52 ffmpeg file format library
    * libavutil-dev development files for libavutil
    * libavutil49 ffmpeg utility library
    * libpostproc-dev development files for libpostproc
    * libpostproc51 ffmpeg video postprocessing library
    * libswscale-dev development files for libswscale
    * libswscale0 ffmpeg video scaling library

Package files
    * ffmpeg-dbg_0.5.1-1ubuntu1_amd64.deb (5.0 MiB)
    * ffmpeg-dbg_0.5.1-1ubuntu1_armel.deb (9.1 MiB)
    * ffmpeg-dbg_0.5.1-1ubuntu1_i386.deb (9.3 MiB)
    * ffmpeg-dbg_0.5.1-1ubuntu1_ia64.deb (5.2 MiB)
    * ffmpeg-dbg_0.5.1-1ubuntu1_sparc.deb (9.0 MiB)
    * ffmpeg-doc_0.5.1-1ubuntu1_all.deb (12.7 MiB)
    * ffmpeg_0.5.1-1ubuntu1.diff.gz (59.4 KiB)
    * ffmpeg_0.5.1-1ubuntu1.dsc (2.3 KiB)
    * ffmpeg_0.5.1-1ubuntu1_amd64.deb (236.6 KiB)
    * ffmpeg_0.5.1-1ubuntu1_armel.deb (249.3 KiB)
    * ffmpeg_0.5.1-1ubuntu1_i386.deb (231.1 KiB)
    * ffmpeg_0.5.1-1ubuntu1_ia64.deb (299.3 KiB)
    * ffmpeg_0.5.1-1ubuntu1_sparc.deb (250.9 KiB)
    * ffmpeg_0.5.1.orig.tar.gz (3.2 MiB)
    * libavcodec-dev_0.5.1-1ubuntu1_amd64.deb (2.4 MiB)
    * libavcodec-dev_0.5.1-1ubuntu1_armel.deb (2.8 MiB)
    * libavcodec-dev_0.5.1-1ubuntu1_i386.deb (2.1 MiB)
    * libavcodec-dev_0.5.1-1ubuntu1_ia64.deb (5.0 MiB)
    * libavcodec-dev_0.5.1-1ubuntu1_sparc.deb (2.9 MiB)
    * libavcodec52_0.5.1-1ubuntu1_amd64.deb (2.1 MiB)
    * libavcodec52_0.5.1-1ubuntu1_armel.deb (5.2 MiB)
    * libavcodec52_0.5.1-1ubuntu1_i386.deb (3.8 MiB)
    * libavcodec52_0.5.1-1ubuntu1_ia64.deb (4.7 MiB)
    * libavcodec52_0.5.1-1ubuntu1_sparc.deb (5.3 MiB)
    * libavdevice-dev_0.5.1-1ubuntu1_amd64.deb (58.4 KiB)
    * libavdevice-dev_0.5.1-1ubuntu1_armel.deb (58.6 KiB)
    * libavdevice-dev_0.5.1-1ubuntu1_i386.deb (57.2 KiB)
    * libavdevice-dev_0.5.1-1ubuntu1_ia64.deb (69.4 KiB)
    * libavdevice-dev_0.5.1-1ubuntu1_sparc.deb (57.5 KiB)
    * libavdevice52_0.5.1-1ubuntu1_amd64.deb (57.0 KiB)
    * libavdevice52_0.5.1-1ubuntu1_armel.deb (76.9 KiB)
    * libavdevice52_0.5.1-1ubuntu1_i386.deb (72.5 KiB)
    * libavdevice52_0.5.1-1ubuntu1_ia64.deb (66.7 KiB)
    * libavdevice52_0.5.1-1ubuntu1_sparc.deb (74.7 KiB)
    * libavfilter-dev_0.5.1-1ubuntu1_amd64.deb (54.8 KiB)
    * libavfilter-dev_0.5.1-1ubuntu1_armel.deb (52.7 KiB)
    * libavfilter-dev_0.5.1-1ubuntu1_i386.deb (53.6 KiB)
    * libavfilter-dev_0.5.1-1ubuntu1_ia64.deb (58.9 KiB)
    * libavfilter-dev_0.5.1-1ubuntu1_sparc.deb (53.7 KiB)
    * libavfilter0_0.5.1-1ubuntu1_amd64.deb (46.5 KiB)
    * libavfilter0_0.5.1-1ubuntu1_armel.deb (46.7 KiB)
    * libavfilter0_0.5.1-1ubuntu1_i386.deb (48.3 KiB)
    * libavfilter0_0.5.1-1ubuntu1_ia64.deb (50.5 KiB)
    * libavfilter0_0.5.1-1ubuntu1_sparc.deb (45.8 KiB)
    * libavformat-dev_0.5.1-1ubuntu1_amd64.deb (463.2 KiB)
    * libavformat-dev_0.5.1-1ubuntu1_armel.deb (465.7 KiB)
    * libavformat-dev_0.5.1-1ubuntu1_i386.deb (444.7 KiB)
    * libavformat-dev_0.5.1-1ubuntu1_ia64.deb (736.0 KiB)
    * libavformat-dev_0.5.1-1ubuntu1_sparc.deb (470.9 KiB)
    * libavformat52_0.5.1-1ubuntu1_amd64.deb (362.1 KiB)
    * libavformat52_0.5.1-1ubuntu1_armel.deb (696.6 KiB)
    * libavformat52_0.5.1-1ubuntu1_i386.deb (702.0 KiB)
    * libavformat52_0.5.1-1ubuntu1_ia64.deb (591.3 KiB)
    * libavformat52_0.5.1-1ubuntu1_sparc.deb (729.0 KiB)
    * libavutil-dev_0.5.1-1ubuntu1_amd64.deb (80.0 KiB)
    * libavutil-dev_0.5.1-1ubuntu1_armel.deb (81.0 KiB)
    * libavutil-dev_0.5.1-1ubuntu1_i386.deb (78.6 KiB)
    * libavutil-dev_0.5.1-1ubuntu1_ia64.deb (98.8 KiB)
    * libavutil-dev_0.5.1-1ubuntu1_sparc.deb (84.3 KiB)
    * libavutil49_0.5.1-1ubuntu1_amd64.deb (63.6 KiB)
    * libavutil49_0.5.1-1ubuntu1_armel.deb (97.5 KiB)
    * libavutil49_0.5.1-1ubuntu1_i386.deb (91.1 KiB)
    * libavutil49_0.5.1-1ubuntu1_ia64.deb (79.8 KiB)
    * libavutil49_0.5.1-1ubuntu1_sparc.deb (100.8 KiB)
    * libpostproc-dev_0.5.1-1ubuntu1_amd64.deb (120.8 KiB)
    * libpostproc-dev_0.5.1-1ubuntu1_armel.deb (76.2 KiB)
    * libpostproc-dev_0.5.1-1ubuntu1_i386.deb (112.7 KiB)
    * libpostproc-dev_0.5.1-1ubuntu1_ia64.deb (103.4 KiB)
    * libpostproc-dev_0.5.1-1ubuntu1_sparc.deb (77.7 KiB)
    * libpostproc51_0.5.1-1ubuntu1_amd64.deb (120.0 KiB)
    * libpostproc51_0.5.1-1ubuntu1_armel.deb (111.0 KiB)
    * libpostproc51_0.5.1-1ubuntu1_i386.deb (184.9 KiB)
    * libpostproc51_0.5.1-1ubuntu1_ia64.deb (103.7 KiB)
    * libpostproc51_0.5.1-1ubuntu1_sparc.deb (114.7 KiB)
    * libswscale-dev_0.5.1-1ubuntu1_amd64.deb (178.1 KiB)
    * libswscale-dev_0.5.1-1ubuntu1_armel.deb (97.8 KiB)
    * libswscale-dev_0.5.1-1ubuntu1_i386.deb (138.6 KiB)
    * libswscale-dev_0.5.1-1ubuntu1_ia64.deb (135.2 KiB)
    * libswscale-dev_0.5.1-1ubuntu1_sparc.deb (99.1 KiB)
    * libswscale0_0.5.1-1ubuntu1_amd64.deb (169.5 KiB)
    * libswscale0_0.5.1-1ubuntu1_armel.deb (148.6 KiB)
    * libswscale0_0.5.1-1ubuntu1_i386.deb (224.8 KiB)
    * libswscale0_0.5.1-1ubuntu1_ia64.deb (126.7 KiB)
    * libswscale0_0.5.1-1ubuntu1_sparc.deb (144.1 KiB)
</pre>*/
final class FfmpegVideoFileParser_4_0_5_1_1ubuntu1 extends AbstractVideoFileParser {
    
    private static final String NL;
    public static final Pattern LINE_OF_INTEREST_PATTERN;     
    public static final Pattern VIDEO_STREAM_METADATA_PATTERN;
    public static final Pattern DURATION_AND_BITRATE_PATTERN; 
    public static final Pattern AUDIO_STREAM_METADATA_PATTERN;
    

    private final Matcher lineOfInterestMatcher;

    /**<pre>    GROUP 1: hours     
    GROUP 2: minutes   
    GROUP 3: seconds   
    GROUP 4: hundredths
    GROUP 5: start     
    GROUP 6: bitrate</pre>*/
    private final Matcher durationAndBitrateMatcher;

    /**<pre>    GROUP 1: video codec     
    GROUP 2: videoformat     
    GROUP 3: height and width
    GROUP 4: video  bitrate  
    GROUP 5: framerate</pre>*/
    private final Matcher videoStreamInfoMatcher;

    /**<pre>    GROUP 1: audio codec   
    GROUP 2: sample rate   
    GROUP 3: audio channels
    GROUP 4: audio  bitrate</pre>*/
    private final Matcher audioStreamInfoMatcher;

    
    
    public FfmpegVideoFileParser_4_0_5_1_1ubuntu1() {
        lineOfInterestMatcher =     LINE_OF_INTEREST_PATTERN     .matcher("foo");
        durationAndBitrateMatcher = DURATION_AND_BITRATE_PATTERN .matcher("foo");
        videoStreamInfoMatcher =    VIDEO_STREAM_METADATA_PATTERN.matcher("foo");
        audioStreamInfoMatcher =    AUDIO_STREAM_METADATA_PATTERN.matcher("foo");
    }
    
    
    @Override
    protected boolean isLineOfInterest(String lineOfMetaData) {
        return lineOfInterestMatcher.reset(lineOfMetaData).matches();
    }

    @Override
    protected int parseAudioBytesPerSecond(String lineOfMetaData) {
        if ( ! audioStreamInfoMatcher.reset(lineOfMetaData).find()) {
            return -1;
        }
        
        String kbpsStr = audioStreamInfoMatcher.group(4);
        
        if (kbpsStr == null) {
            // System.out.println("no group 4: >" + lineOfMetaData);
            return -1; // ok, attribute is optional
        }
        
        return Integer.parseInt(kbpsStr) * 1024;
    }


    @Override
    protected String parseAudioChannels(String lineOfMetaData) {
        if (audioStreamInfoMatcher.reset(lineOfMetaData).find()) {
            return audioStreamInfoMatcher.group(3);
        }
        return null;
    }


    @Override
    protected String parseAudioCodec(String lineOfMetaData) {
        if (audioStreamInfoMatcher.reset(lineOfMetaData).find()) {
            return audioStreamInfoMatcher.group(1);
        }
        return null;
    }


    @Override
    protected int parseAudioSampleRate(String lineOfMetaData) {
        if (audioStreamInfoMatcher.reset(lineOfMetaData).find()) {
            String number = audioStreamInfoMatcher.group(2).trim();
            return Integer.parseInt(number);
        }
        return -1;
    }


    @Override
    protected long parseDurationMillis(String lineOfMetaData) {
        if ( ! durationAndBitrateMatcher.reset(lineOfMetaData).find())
            return -1;

        int hours =      Integer.parseInt(durationAndBitrateMatcher.group(1));
        int minutes =    Integer.parseInt(durationAndBitrateMatcher.group(2));
        int seconds =    Integer.parseInt(durationAndBitrateMatcher.group(3));
        int hundredths = Integer.parseInt(durationAndBitrateMatcher.group(4));

        long millis = 0L;
        millis += 1000 * 60 * 60 * hours;
        millis += 1000 * 60 * minutes;
        millis += 1000 * seconds;
        millis += 10 * hundredths;
        
        return millis;
    }

    @Override
    protected int parseTotalBytesPerSecond(String lineOfMetaData) {
        if ( ! durationAndBitrateMatcher.reset(lineOfMetaData).find())
            return -1;
        
        try {
            int kbPerSec;
            kbPerSec = Integer.parseInt(durationAndBitrateMatcher.group(6));
            return kbPerSec * 1024;
            
        } catch (NumberFormatException e) {
            return -1; // ok, if string is "N/A"
        }
    }


    @Override
    protected int parseVideoBytesPerSecond(String lineOfMetaData) {
        if (!videoStreamInfoMatcher.reset(lineOfMetaData).find()) {
            // System.out.println("not matched: >"+lineOfMetaData);
            return -1;
        }
        
        String bitrateStr = videoStreamInfoMatcher.group(4);

        if (bitrateStr == null) {
            // ok, attribute is optional.
            return -1;
        }
        
        int kbPerSec = Integer.parseInt(bitrateStr);
        return 1024 * kbPerSec;
    }

    @Override
    protected long parseVideoStartOffsetMillis(String lineOfMetaData) {
        if ( ! durationAndBitrateMatcher.reset(lineOfMetaData).find())
            return -1;
        
        String startStr = durationAndBitrateMatcher.group(5);
        double startDbl = Double.parseDouble(startStr);
        startDbl *= 1000;
        return Double.valueOf(startDbl).longValue();
    }

    @Override
    protected String parseVideoCodec(String lineOfMetaData) {
        if ( ! videoStreamInfoMatcher.reset(lineOfMetaData).find())
            return null;
        
        return videoStreamInfoMatcher.group(1);
    }


    @Override
    protected String parseVideoFormat(String lineOfMetaData) {
        if ( ! videoStreamInfoMatcher.reset(lineOfMetaData).find())
            return null;
        
        return videoStreamInfoMatcher.group(2);
    }


    @Override
    protected double parseVideoFrameRate(String lineOfMetaData) {
        double fps = -1;
        
        if ( ! videoStreamInfoMatcher.reset(lineOfMetaData).find())
            return fps;

        String fpsStr = videoStreamInfoMatcher.group(5).trim();
        
        try {
            fps = Double.parseDouble(fpsStr);
            
        } catch (NumberFormatException e) {
            // System.out.println("not a double: " + fpsStr);
            // System.out.println("not a double: " + lineOfMetaData);
        }
        
        return fps;
    }


    @Override
    protected int parseVideoHeight(String lineOfMetaData) {
        if ( ! videoStreamInfoMatcher.reset(lineOfMetaData).find())
            return -1;
        
        String wXh = videoStreamInfoMatcher.group(3);
        String h = wXh.split("[xX]")[1].trim(); 
        return Integer.parseInt(h);
    }




    @Override
    protected int parseVideoWidth(String lineOfMetaData) {
        if ( ! videoStreamInfoMatcher.reset(lineOfMetaData).find())
            return -1;
        
        String wXh = videoStreamInfoMatcher.group(3);
        String w = wXh.split("[xX]")[0].trim(); 
        return Integer.parseInt(w);
    }



    @Override
    protected List<String> readVideoMetaData(File videoFile2) throws IOException {
        String[] cmds = new String[] {
            FFMPEG.getInstance().getApplication().getFfmpegCommand(), 
            "-i", 
            videoFile2.getAbsolutePath()
        };
        
        log.trace("          starting process: {}", Util.printArgs(cmds));
        ProcessBuilder pb = new ProcessBuilder().redirectErrorStream(true)
                                                .command(cmds);
        Process p = pb.start();
        BufferedReader input;
        input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        List<String> outputLines = new ArrayList<String>();
        
        for (String line = null; (line = input.readLine()) != null;) {
            outputLines.add(line);
            log.trace("          got output line:    > {}", line.trim());
        }
        
        return outputLines;
    }
    
    
    
                                                                              
                                                                              
    static {     
        NL = System.getProperty("line.separator");
        
        final String lineOfInterestRgx =                                              
            "\\s*  (  Input  |  Duration:  |  Stream  )  \\s+  .* ";          
                                                           
        
        
        final String durationAndBitrateRgx =                                          
            "    \\s*  [Dd]uration  \\s*  :  \\s*                           "+NL
                                                                            
            +"   ( [0-9] {2} )  :                     # GROUP 1: hours      "+NL
            +"   ( [0-9] {2} )  :                     # GROUP 2: minutes    "+NL
            +"   ( [0-9] {2} )  \\.                   # GROUP 3: seconds    "+NL
            +"   ( [0-9] {2} )                        # GROUP 4: hundredths "+NL
                                                                            
            +"   \\s*  ,  \\s*                                              "+NL
                                                                            
            +"   [Ss]tart  \\s*  :  \\s*                                    "+NL
            +"   ( [0-9]+  (?: \\. [0-9]+ )  ?  )     # GROUP 5: start      "+NL
                                                                            
            +"   \\s*  ,  \\s*                                              "+NL
                                                                            
            +"   [Bb]itrate  \\s*  :  \\s*                                  "+NL
            +"   ( [0-9]+ (?= \\s* kb\\/s) | \\s* N\\/A ) # GROUP 6: bitrate"+NL;
        
        final String videoStreamMetaDataRgx =                                         
            "    Video:  \\s*                                               "+NL
                                                                            
            +"   (                         # GROUP 1: video codec           "+NL
            +"      [a-zA-Z0-9]+                                            "+NL
            +"      (?:  \\s+  \\/  \\s+  0x[0-9]+  )  ?                    "+NL
            +"   )                                                          "+NL
                                                                            
            +"   \\s*  ,?  \\s*                                             "+NL
                                                                            
            +"   (                         # GROUP 2: videoformat           "+NL
            +"      [a-zA-Z0-9]+                                            "+NL
            +"   )  ?                                                       "+NL
                                                                            
            +"   \\s*  ,?  \\s*                                             "+NL
                                                                            
            +"   (                         # GROUP 3: height and width      "+NL
            +"      [0-9]+  x  [0-9]+                                       "+NL
            +"   )                                                          "+NL

            +"   \\s*   [^,]*?    ,    \\s*                                 "+NL 
                                                                            
            +"   (                         # GROUP 4: video  bitrate        "+NL
            +"      [0-9]+    (?= \\s* kb\\/s)                              "+NL 
            +"   )  ?                                                       "+NL
                                                                            
            +"   .*?                                                        "+NL 
                                                                            
            +"   (                         # GROUP 5:  framerate            "+NL
            +"      [0-9]+ (?:  \\.  [0-9]+  | k ) ? \\s+  (?= tbr )        "+NL
            +"   )                                                         "+NL;    
        
        final String audioStreamMetadataRgx =                                     
            "    [Aa]udio  \\s*  :  \\s*                                    "+NL
            
            +"   (  [a-zA-Z0-9_]+  )            # GROUP 1: audio codec      "+NL
            +"   \\s*  ,  \\s*                                              "+NL
                                                                          
            +"   (  [0-9]+  ) \\s* Hz           # GROUP 2: sample rate      "+NL
            +"   \\s*  ,  \\s*                                              "+NL
                                                                          
            +"   (mono|stereo)                  # GROUP 3: audio channels   "+NL
            +"   .*? s16                                                    "+NL
                                                                          
            +"   (?:                                                        "+NL
            +"        \\s*  ,  \\s*                                         "+NL
            +"        (  [0-9]+  )              # GROUP 4: audio  bitrate   "+NL
            +"        \\s*  kb\\/s                                          "+NL
            +"   ) ?                                                       "+NL;
        
        DURATION_AND_BITRATE_PATTERN =  compile(durationAndBitrateRgx,  COMMENTS);
        LINE_OF_INTEREST_PATTERN =      compile(lineOfInterestRgx,      COMMENTS);    
        VIDEO_STREAM_METADATA_PATTERN = compile(videoStreamMetaDataRgx, COMMENTS);  
        AUDIO_STREAM_METADATA_PATTERN = compile(audioStreamMetadataRgx, COMMENTS);   
        
        
    }                                       
}
