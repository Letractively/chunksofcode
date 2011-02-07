package com.myapp.videotools.impl;

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
ffmpeg (3:0.cvs20070307-5ubuntu7) hardy;

Publishing details
Published on 2008-03-12

Built packages
    * ffmpeg multimedia player, server and encoder
    * libavcodec-dev development files for libavcodec
    * libavcodec1d ffmpeg codec library
    * libavformat-dev development files for libavformat
    * libavformat1d ffmpeg file format library
    * libavutil-dev development files for libavutil
    * libavutil1d ffmpeg utility library
    * libpostproc-dev development files for libpostproc
    * libpostproc1d ffmpeg video postprocessing library
    * libswscale-dev development files for libswscale
    * libswscale1d ffmpeg video scaling library

Package files
    * ffmpeg_0.cvs20070307-5ubuntu7.diff.gz (37.8 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7.dsc (1.3 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_amd64.deb (191.0 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_hppa.deb (205.9 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_i386.deb (187.4 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_ia64.deb (245.3 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_lpia.deb (189.1 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_powerpc.deb (217.4 KiB)
    * ffmpeg_0.cvs20070307-5ubuntu7_sparc.deb (193.6 KiB)
    * ffmpeg_0.cvs20070307.orig.tar.gz (2.5 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_amd64.deb (1.7 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_hppa.deb (1.9 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_i386.deb (1.7 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_ia64.deb (2.7 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_lpia.deb (1.7 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_powerpc.deb (1.7 MiB)
    * libavcodec-dev_0.cvs20070307-5ubuntu7_sparc.deb (1.7 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_amd64.deb (1.5 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_hppa.deb (1.7 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_i386.deb (1.5 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_ia64.deb (2.5 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_lpia.deb (1.6 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_powerpc.deb (1.5 MiB)
    * libavcodec1d_0.cvs20070307-5ubuntu7_sparc.deb (1.6 MiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_amd64.deb (337.9 KiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_hppa.deb (383.4 KiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_i386.deb (324.9 KiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_ia64.deb (523.8 KiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_lpia.deb (325.1 KiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_powerpc.deb (366.5 KiB)
    * libavformat-dev_0.cvs20070307-5ubuntu7_sparc.deb (332.3 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_amd64.deb (268.6 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_hppa.deb (319.7 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_i386.deb (279.9 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_ia64.deb (415.1 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_lpia.deb (276.6 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_powerpc.deb (301.2 KiB)
    * libavformat1d_0.cvs20070307-5ubuntu7_sparc.deb (274.3 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_amd64.deb (50.1 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_hppa.deb (54.9 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_i386.deb (49.7 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_ia64.deb (60.5 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_lpia.deb (49.7 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_powerpc.deb (54.0 KiB)
    * libavutil-dev_0.cvs20070307-5ubuntu7_sparc.deb (50.9 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_amd64.deb (36.7 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_hppa.deb (42.8 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_i386.deb (38.0 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_ia64.deb (45.5 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_lpia.deb (37.9 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_powerpc.deb (44.4 KiB)
    * libavutil1d_0.cvs20070307-5ubuntu7_sparc.deb (39.0 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_amd64.deb (66.7 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_hppa.deb (45.7 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_i386.deb (71.7 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_ia64.deb (50.6 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_lpia.deb (73.9 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_powerpc.deb (63.3 KiB)
    * libpostproc-dev_0.cvs20070307-5ubuntu7_sparc.deb (42.1 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_amd64.deb (66.0 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_hppa.deb (45.1 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_i386.deb (71.6 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_ia64.deb (50.5 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_lpia.deb (74.2 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_powerpc.deb (63.2 KiB)
    * libpostproc1d_0.cvs20070307-5ubuntu7_sparc.deb (42.0 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_amd64.deb (110.8 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_hppa.deb (89.1 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_i386.deb (109.5 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_ia64.deb (100.9 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_lpia.deb (114.1 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_powerpc.deb (107.1 KiB)
    * libswscale-dev_0.cvs20070307-5ubuntu7_sparc.deb (78.0 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_amd64.deb (93.6 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_hppa.deb (69.4 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_i386.deb (95.2 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_ia64.deb (84.4 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_lpia.deb (99.1 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_powerpc.deb (84.9 KiB)
    * libswscale1d_0.cvs20070307-5ubuntu7_sparc.deb (63.5 KiB)
</pre>*/
final class FfmpegVideoFileParser_3_0_cvs30070307_5ubuntu7 extends AbstractVideoFileParser {
    

    private static final Pattern LINE_OF_INTEREST_PATTERN ;
    private static final Pattern INPUT_TYPE_PATTERN ;
    private static final Pattern DURATION_PATTERN ;
    private static final Pattern BITRATE_PATTERN ;
    
    private static final Pattern VIDEO_DETAIL_PATTERN ;
    private static final Pattern AUDIO_DETAIL_PATTERN ;
    
    private static final String NL = System.getProperty("line.separator");
    
    static {
        LINE_OF_INTEREST_PATTERN =  Pattern.compile("\\s*(Input|Duration:|Stream)\\s+.*");
        INPUT_TYPE_PATTERN =        Pattern.compile("Input #0, ([a-zA-Z0-9]{1,}),");
        DURATION_PATTERN =          Pattern.compile("Duration: ([\\.:0-9]{1,})");
        BITRATE_PATTERN =           Pattern.compile("bitrate: ([.0-9]{1,} kb/s|N/A)");
        
        VIDEO_DETAIL_PATTERN =      Pattern.compile(NL+
          "Video:\\s+                                                   # uninteresting prefix" +NL+
          "([a-zA-Z0-9]{1,} | [a-zA-Z0-9]+ \\s \\/ \\s 0x[0-9]+ ),\\s+  # will match the video codec           " +NL+
          "([a-zA-Z0-9]{1,})?,\\s+                                      # will match the file format (optional)" +NL+
          "([0-9]{2,}x[0-9]{2,}),                                       # will match the height and width      " +NL+
          "(\\s+[0-9]{1,} kb/s,)?                                       # will match the stream bitrate        " +NL+
          "\\s+([\\.0-9]{1,}\\s+(fps|tb)?)                              # will match the framerate             ", 
          Pattern.COMMENTS);

        AUDIO_DETAIL_PATTERN =      Pattern.compile("Audio: ([_0-9a-zA-Z]+), ([ 0-9a-zA-Z]+), ([0-9a-zA-Z]+)");
    }


    private Matcher lineOfInterestMatcher;
    private Matcher inputTypeMatcher;
    private Matcher durationMatcher;
    private Matcher bitrateMatcher;
    private Matcher videoDetailMatcher;
    private Matcher audioDetailMatcher;
    

    public FfmpegVideoFileParser_3_0_cvs30070307_5ubuntu7() {
        lineOfInterestMatcher = LINE_OF_INTEREST_PATTERN.matcher("foo");
        inputTypeMatcher = INPUT_TYPE_PATTERN.matcher("foo");
        durationMatcher = DURATION_PATTERN.matcher("foo");
        bitrateMatcher = BITRATE_PATTERN.matcher("foo");
        videoDetailMatcher = VIDEO_DETAIL_PATTERN.matcher("foo");
        audioDetailMatcher = AUDIO_DETAIL_PATTERN.matcher("foo");
    }
    
    @Override
    protected List<String> readVideoMetaData(File videoFile2) throws IOException {
        String[] cmds = new String[] {
            FFMPEG.getInstance().getApplication().getFfmpegCommand(), 
            "-i", 
            videoFile2.getAbsolutePath()
        };
        
        log.trace("          starting process: {}", Util.printArgs(cmds));
        
        ProcessBuilder pb = new ProcessBuilder()
                                    .redirectErrorStream(true)
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
    
    
    @Override
    protected boolean isLineOfInterest(String lineOfMetaData) {
        return lineOfInterestMatcher.reset(lineOfMetaData).matches(); 
    }
    
    
    /**
       Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
       Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
       Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
       Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
       Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    @Override
    protected int parseVideoBytesPerSecond(String lineOfMetaData) {
        return -1; // since not mandatory, left unimplemented
    }
    
    /**
    Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
    Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
    Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
    Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
    Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    @Override
    protected String parseVideoCodec(String lineOfMetaData) {
        // Matcher videoDetailMatcher =
        // VIDEO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!videoDetailMatcher.find())
            return null;

        String codec = videoDetailMatcher.group(1).trim();
        return codec;
    }

    /**
    Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
    Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
    Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
    Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
    Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    @Override
    protected String parseVideoFormat(String lineOfMetaData) {
        // Matcher inputTypeMatcher =
        // INPUT_TYPE_PATTERN.matcher(lineOfMetaData);
        if (!inputTypeMatcher.find())
            return null;
        
        String type = inputTypeMatcher.group(1);
        return type;
    }

    /** Duration: 00:03:50.2, start: 0.000000, bitrate: 32 kb/s */
    @Override
    protected long parseDurationMillis(String lineOfMetaData) {
        // Matcher durationMatcher = DURATION_PATTERN.matcher(lineOfMetaData);
        if (!durationMatcher.find())
            return -1;
        
        String duration = durationMatcher.group(1);
        long lengthMillis = getTimeFromDuration(duration);
        return lengthMillis;
    }

    /** Duration: 00:03:50.2, start: 0.000000, bitrate: 32 kb/s */
    @Override
    protected int parseTotalBytesPerSecond(String lineOfMetaData) {
        // Matcher bitrateMatcher = BITRATE_PATTERN.matcher(lineOfMetaData);
        if (!bitrateMatcher.find())
            return -1;
            
        String bitrate = bitrateMatcher.group(1).trim();
        
        if ("N/A".equalsIgnoreCase(bitrate)) {
            log.debug("          bytesPerSecond = N/A           (raw    = {} )", bitrate);
            return -1;
        } 
        
        bitrate = bitrate.split(" ")[0];
        int bytesPerSecond = 1000 * Integer.parseInt(bitrate);
        return bytesPerSecond;
    }
    
    /**
    Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
    Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
    Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
    Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
    Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    @Override
    protected double parseVideoFrameRate(String lineOfMetaData) {
        // Matcher videoDetailMatcher =
        // VIDEO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!videoDetailMatcher.find())
            return -1;

        String fps = videoDetailMatcher.group(5).trim().split(" ")[0];
        Double framesPerSec = Double.parseDouble(fps);
        return framesPerSec.doubleValue();
    }
    
    /**
    Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
    Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
    Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
    Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
    Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    @Override
    protected int parseVideoHeight(String lineOfMetaData) {
        // Matcher videoDetailMatcher =
        // VIDEO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!videoDetailMatcher.find())
            return -1;

        String resolution = videoDetailMatcher.group(3).trim();
        Matcher wh = Pattern.compile("([0-9]{1,})x([0-9]{1,})").matcher(resolution);
        
        if ( ! wh.find())
            return -1;
        
        int height = Integer.parseInt(wh.group(2));
        return height;
    }
    
    /**
    Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
    Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
    Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
    Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
    Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    @Override
    protected int parseVideoWidth(String lineOfMetaData) {
        // Matcher videoDetailMatcher =
        // VIDEO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!videoDetailMatcher.find())
            return -1;

        String resolution = videoDetailMatcher.group(3).trim();
        Matcher wh = Pattern.compile("([0-9]{1,})x([0-9]{1,})").matcher(resolution);
        
        if ( ! wh.find())
            return -1;
        
        int width = Integer.parseInt(wh.group(1));
        
        return width;
    }

    /** Duration: 00:03:50.2, start: 0.000000, bitrate: 32 kb/s */
    @Override
    protected long parseVideoStartOffsetMillis(String lineOfMetaData) {
        return -1; // since not mandatory, left unimplemented
    }
    
    /** Stream #0.1: Audio: mp3, 22050 Hz, stereo, 32 kb/s 
        Stream #0.0: Audio: wmav2, 44100 Hz, stereo, 88 kb/s
        Stream #0.1: Audio: pcm_u8, 11025 Hz, mono, 88 kb/s*/
    @Override
    protected int parseAudioBytesPerSecond(String lineOfMetaData) {
        return -1; // since not mandatory, left unimplemented
    }

    /** Stream #0.1: Audio: mp3, 22050 Hz, stereo, 32 kb/s 
        Stream #0.0: Audio: wmav2, 44100 Hz, stereo, 88 kb/s
        Stream #0.1: Audio: pcm_u8, 11025 Hz, mono, 88 kb/s*/
    @Override
    protected String parseAudioChannels(String lineOfMetaData) {
        // Matcher audioDetailMatcher =
        // AUDIO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!audioDetailMatcher.find())
            return null;

        String audioChannel = audioDetailMatcher.group(3).trim();
        return audioChannel;
    }

    /** Stream #0.1: Audio: mp3, 22050 Hz, stereo, 32 kb/s 
        Stream #0.0: Audio: wmav2, 44100 Hz, stereo, 88 kb/s
        Stream #0.1: Audio: pcm_u8, 11025 Hz, mono, 88 kb/s*/
    @Override
    protected String parseAudioCodec(String lineOfMetaData) {
        // Matcher audioDetailMatcher =
        // AUDIO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!audioDetailMatcher.find())
            return null;

        String codec = audioDetailMatcher.group(1).trim();
        return codec;
    }

    /** Stream #0.1: Audio: mp3, 22050 Hz, stereo, 32 kb/s 
        Stream #0.0: Audio: wmav2, 44100 Hz, stereo, 88 kb/s
        Stream #0.1: Audio: pcm_u8, 11025 Hz, mono, 88 kb/s*/
    @Override
    protected int parseAudioSampleRate(String lineOfMetaData) {
        // Matcher audioDetailMatcher =
        // AUDIO_DETAIL_PATTERN.matcher(lineOfMetaData);
        if (!audioDetailMatcher.find())
            return -1;
        
        String sampleRate = audioDetailMatcher.group(2).trim();
        int audioSampleRate = Integer.parseInt(sampleRate.split(" ")[0]);
        return audioSampleRate;
    }
}
