package com.myapp.videotools.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.videotools.IVideoFileParser;
import com.myapp.videotools.Util;
import com.myapp.videotools.VideoFile;

class OLD_FfmpegVideoFileParser_OLD implements IVideoFileParser {
    

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
        
        /* 
         * Video: mpeg1video, yuv420p, 320x176, 600 kb/s, 24.00 fps(r) 
         * Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)
         */
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
    
    
    
    
    
    private static final Logger log = LoggerFactory.getLogger(OLD_FfmpegVideoFileParser_OLD.class);

    private File file = null;
    private VideoFile videoFile = null;
    
    
    
    public synchronized void parse(VideoFile vidfile) throws IOException {
        videoFile = vidfile;
        file = videoFile.getFile();
        String videoFilePath = file.getAbsolutePath();
        
        log.debug("      parsing metadata for: '{}' ...",videoFilePath);
    
        List<String> ffmpegOutputLines = readFfmpegOutput();
        initMetaDataFromLines(ffmpegOutputLines);
        log.debug("      OK, metadata for: '{}' parsed.", file.getName()); 
    }
    
    
    
    private List<String> readFfmpegOutput() throws IOException {
        log.trace("        will now read std-out from ffmpeg-process...");
        
        String[] cmds = new String[] {
            FFMPEG.getInstance().getApplication().getFfmpegCommand(), 
            "-i", 
            file.getAbsolutePath()
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
            if (LINE_OF_INTEREST_PATTERN.matcher(line).matches()) {
                outputLines.add(line);
                log.trace("          got output line:    > {}",line.trim());
                
            } else { 
//                log.trace("          uninteresting line  > {}",line.trim());
            }
        }
        
        log.trace("        OK, std-out from process read.");
        return outputLines;
    }    
    
    private void initMetaDataFromLines(List<String> lines) {
        log.debug("        will now parse collected output from ffmpeg...");

        for (Iterator<String> i = lines.iterator(); 
             i.hasNext(); 
             parseMetaDataLine(i.next()));
        
        log.debug("        OK, collected output parsed. ({} lines)", lines.size());
    }  
    
    private void parseMetaDataLine(String line) {
        line = line.trim();
        
        if (line.startsWith("Input ")) {
            parseInputHeaderLine(line);
            
        } else if (line.startsWith("Duration: ")) {
            parseDurationLine(line);
        
        } else if (line.startsWith("Stream ")) {
            if (line.contains(" Video: ")) {
                parseVideoStreamLine(line);
                
            } else if (line.contains(" Audio: ")) {
                parseAudioStreamLine(line);
                
            } else {
                throw new RuntimeException(line);
            }
        }
    }

    /** Input #0, flv, from '/data/stuff/tmp/firefox-downloads/398209.flv */
    private void parseInputHeaderLine(String line) {
        Matcher matcher = INPUT_TYPE_PATTERN.matcher(line);

        if ( ! matcher.find()) {
            warnNotMatch(matcher, line);
            return;
        }
        
        String type = matcher.group(1);
        videoFile.setFileType(type);
        
        log.debug("          video type     = {}    (description: {})", videoFile.getFileType(), FFMPEG.getInstance().getApplication().getFileFormatDescription(type));
        //SUPPORTED_FILE_FORMATS.get(type) + ")");
    }

    /** Duration: 00:03:50.2, start: 0.000000, bitrate: 32 kb/s */
    private void parseDurationLine(String line) {
        Matcher matcher = DURATION_PATTERN.matcher(line);
        if ( ! matcher.find()) {
            warnNotMatch(matcher, line);
            return;
        }
        
        String duration = matcher.group(1);
        long lengthMillis = getTimeFromDuration(duration);
        videoFile.setLengthMillis(lengthMillis);
        log.debug("          duration       = {}    ({} ms)", duration, videoFile.getLengthMillis());
        
        matcher = BITRATE_PATTERN.matcher(line);
        if ( ! matcher.find()) {
            warnNotMatch(matcher, line);
            return;
        }

        String bitrate = matcher.group(1).trim();
        if ("N/A".equalsIgnoreCase(bitrate)) {
            log.debug("          bytesPerSecond = N/A           (raw    = {} )", bitrate);
        
        } else {
            bitrate = bitrate.split(" ")[0];
            int bytesPerSecond = 1000 * Integer.parseInt(bitrate);
            videoFile.setTotalBytesPerSecond(bytesPerSecond);
            if (log.isDebugEnabled()) log.debug("          bytesPerSecond = {}        (raw    = {}, {} kb/s)", new Object[] {Integer.toString(bytesPerSecond), bitrate, Integer.toString(videoFile.getTotalBytesPerSecond() / 1000)});
        }
            
    }
    /** duration = "00:03:50.2"; */
    private static long getTimeFromDuration(String duration) {
        duration = duration.trim();

        String[] parts = duration.split(":");
        if (parts.length != 3)
            throw new RuntimeException("could not parse " + duration);

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        double seconds = Double.parseDouble(parts[2]);
        
        long durationValue = new Double(seconds * 1000d).longValue();
        durationValue += (minutes * 60 * 1000);
        durationValue += (hours * 60 * 60 * 1000);
        return durationValue;
    }

    /**
     Pattern.compile("Video: ([a-zA-Z0-9]{1,}), ([a-zA-Z0-9]{1,}), ([0-9]{2,}x[0-9]{2,}),(\\s+[0-9]{1,} kb/s,)?\\s+([\\.0-9]{1,} fps)");
     
               Stream #0.0: Video: vp6f, yuv420p, 640x480, 30.00 fps(r)
               Stream #0.1: Video: wmv3, yuv420p, 320x240, 25.00 fps(r) 
               Stream #0.0[0x1e0]: Video: mpeg1video, yuv420p, 352x240, 1120 kb/s, 29.97 fps(r)
               Stream #0.1: Video: RV40 / 0x30345652, 240x180, 212 kb/s, 12.00 fps(r)
               Stream #0.0: Video: IV50 / 0x30355649, 320x240, 14.89 fps(r)*/
    private void parseVideoStreamLine(String line) {
        Matcher matcher = VIDEO_DETAIL_PATTERN.matcher(line);

        if ( ! matcher.find()) {
            warnNotMatch(matcher, line);
            return;
        }
        
        String codec = matcher.group(1).trim();
        videoFile.setVideoCodec(codec);
        log.debug("          videoCodec     = {}         ({}installed)", videoFile.getVideoCodec(), (FFMPEG.getInstance().getApplication().isCodecSupported(codec) ? "" : "NOT "));

        String resolution = matcher.group(3).trim();
        Matcher wh = Pattern.compile("([0-9]{1,})x([0-9]{1,})").matcher(resolution);

        if ( ! wh.find()) {
            warnNotMatch(wh, line);
            return;
        }
        
        int width = Integer.parseInt(wh.group(1));
        int height = Integer.parseInt(wh.group(2));
        videoFile.setVideoWidth(width);
        videoFile.setVideoHeight(height);
        
        if (log.isDebugEnabled()) log.debug("          resolution     = {}        (h={}, w={})", new Object[]{resolution, videoFile.getVideoHeight(), videoFile.getVideoWidth()});
        
        Double framesPerSec = Double.parseDouble(matcher.group(5).trim().split(" ")[0]);
        videoFile.setVideoFramesPerSecond(framesPerSec.doubleValue());
        
        log.debug("          framesPerSecond= {}          ({} fps)", videoFile.getVideoFramesPerSecond(), videoFile.getVideoFramesPerSecond());
    }
    
    /** Stream #0.1: Audio: mp3, 22050 Hz, stereo, 32 kb/s 
        Stream #0.0: Audio: wmav2, 44100 Hz, stereo, 88 kb/s
        Stream #0.1: Audio: pcm_u8, 11025 Hz, mono, 88 kb/s*/
    private void parseAudioStreamLine(String line) {
        Matcher matcher = AUDIO_DETAIL_PATTERN.matcher(line);
        
        if ( ! matcher.find()) {
            warnNotMatch(matcher, line);
            return;
        }
        
        String codec = matcher.group(1).trim();
        videoFile.setAudioCodec(codec);
        if (log.isDebugEnabled()) log.debug("          audioCodec     = {}          {}", videoFile.getAudioCodec(), (FFMPEG.getInstance().getApplication().isCodecSupported(codec) ? "(installed)" : " (NOT installed)"));

        String sampleRate = matcher.group(2).trim();
        int audioSampleRate = Integer.parseInt(sampleRate.split(" ")[0]);
        videoFile.setAudioSampleRate(audioSampleRate);
        if (log.isDebugEnabled()) log.debug("          audioSampleRate= {}      ({} Hz)", sampleRate, videoFile.getAudioSampleRate());
        
        String audioChannel = matcher.group(3).trim();
        videoFile.setAudioChannelType(audioChannel);
        if (log.isDebugEnabled()) log.debug("          audioChannel   = {}", videoFile.getAudioChannelType());
    }

    private static void warnNotMatch(Matcher m, String s) {
        if ( ! log.isDebugEnabled())
            return;
        
        log.debug("      WARNING: matcher did not match: pattern:");
        log.debug("      WARNING:   " +m);
        log.debug("      WARNING: for input string:");
        log.debug("      WARNING:   " + s);
    }
}
