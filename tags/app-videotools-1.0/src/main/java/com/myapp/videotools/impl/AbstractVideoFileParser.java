package com.myapp.videotools.impl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.videotools.IVideoFileParser;
import com.myapp.videotools.VideoFile;


public abstract class AbstractVideoFileParser implements IVideoFileParser {

    protected final Logger log;
    
    private File file = null;
    private VideoFile videoFile = null;
    
    
    protected AbstractVideoFileParser() {
        log = LoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    public synchronized void parse(VideoFile vidfile) throws IOException {
        if (vidfile == null) 
            throw new NullPointerException();
        if (vidfile.getFile() == null || ! vidfile.getFile().exists()) 
            throw new RuntimeException("cannot live without an existing file: "+vidfile);
        
        this.videoFile = vidfile;
        this.file = videoFile.getFile();
        
        String videoFilePath = file.getAbsolutePath();
        log.debug("      parsing metadata for: '{}' ...",videoFilePath);
        List<String> ffmpegOutputLines = readFfmpegOutput();
        initMetaDataFromLines(ffmpegOutputLines);
        log.debug("      OK, metadata for: '{}' parsed.", file.getName()); 
    }

    
    protected abstract List<String> readVideoMetaData(File videoFile2) throws IOException;
    
    protected abstract boolean isLineOfInterest(String lineOfMetaData);
    
    protected abstract long parseDurationMillis(String lineOfMetaData);
    protected abstract long parseVideoStartOffsetMillis(String lineOfMetaData);
    protected abstract int parseTotalBytesPerSecond(String lineOfMetaData);

    protected abstract String parseVideoCodec(String lineOfMetaData);
    protected abstract String parseVideoFormat(String lineOfMetaData);
    protected abstract int parseVideoHeight(String lineOfMetaData);
    protected abstract int parseVideoWidth(String lineOfMetaData);
    protected abstract int parseVideoBytesPerSecond(String lineOfMetaData);
    protected abstract double parseVideoFrameRate(String lineOfMetaData);

    protected abstract String parseAudioCodec(String lineOfMetaData);
    protected abstract int parseAudioSampleRate(String lineOfMetaData);
    protected abstract String parseAudioChannels(String lineOfMetaData);
    protected abstract int parseAudioBytesPerSecond(String lineOfMetaData);
    
    
    
    private List<String> readFfmpegOutput() throws IOException {
        log.trace("        will now read std-out from ffmpeg-process...");
        List<String> outputLines = readVideoMetaData(file);
        
        log.trace("        OK, std-out from process read.");
        return outputLines;
    }    
    
    private void initMetaDataFromLines(List<String> lines) {
        log.debug("        will now parse collected output from ffmpeg...");

        for (Iterator<String> i = lines.iterator(); i.hasNext();) {
            final String line = i.next();
            
            if ( ! isLineOfInterest(line))
                continue;

            gatherGeneralMetaData(line);
            gatherVideoMetaData(line);
            gatherAudioMetaData(line);
        }
        
        log.debug("        OK, collected output parsed. ({} lines)", lines.size());
    }  

    private void gatherGeneralMetaData(String line) {
        long durationMillis = parseDurationMillis(line);
        long offsetMillis = parseVideoStartOffsetMillis(line);
        int totalBytesPerSecond = parseTotalBytesPerSecond(line);
        
        if (durationMillis >= 0) {
            videoFile.setLengthMillis(durationMillis);
            log.debug("          duration       = {} ms", durationMillis);
        }
        if (offsetMillis >= 0) {
            videoFile.setVideoStartOffsetMillis(offsetMillis);            
            log.debug("          offsetMillis   = {} ms", offsetMillis);
        }
        if (totalBytesPerSecond >= 0) {
            videoFile.setTotalBytesPerSecond(totalBytesPerSecond);
            log.debug("          totalBitrate   = {} b/s", offsetMillis);
        }
    }
    
    private void gatherVideoMetaData(String line) {
        String videoCodec            =  parseVideoCodec         (line);
        String videoFormat           =  parseVideoFormat        (line);
        int    videoHeight           =  parseVideoHeight        (line);
        int    videoWidth            =  parseVideoWidth         (line);
        int    videoBytesPerSecond   =  parseVideoBytesPerSecond(line);
        double videoFrameRate        =  parseVideoFrameRate     (line);

        if (videoCodec != null) {
            videoFile.setVideoCodec(videoCodec);
            log.debug("          vidCodec       = {} ", videoCodec);
        }
        if (videoFormat != null) {
            videoFile.setFileType(videoFormat);
            log.debug("          vidFormat      = {} ", videoFormat);
        }
        if (videoHeight >= 0) {
            videoFile.setVideoHeight(videoHeight);
            log.debug("          vidHeight      = {} pixel", videoHeight);
        }
        if (videoWidth >= 0) {
            videoFile.setVideoWidth(videoWidth);
            log.debug("          vidWidth       = {} pixel", videoWidth);
        }
        if (videoBytesPerSecond >= 0) {
            videoFile.setVideoBytesPerSecond(videoBytesPerSecond);
            log.debug("          vidBytesPerSec = {} b/s", videoBytesPerSecond);
        }
        if (videoFrameRate >= 0) {
            videoFile.setVideoFramesPerSecond(videoFrameRate);
            log.debug("          vidFrameRate   = {} fps", videoFrameRate);
        }
    }
    
    private void gatherAudioMetaData(String line) {
        String  audioCodec          =  parseAudioCodec         (line);
        int     audioSampleRate     =  parseAudioSampleRate    (line);
        String  audioChannels       =  parseAudioChannels      (line);
        int     audioBytesPerSecond =  parseAudioBytesPerSecond(line);
        
        if (audioCodec != null) {
            videoFile.setAudioCodec(audioCodec);
            log.debug("          audioCodec     = {} fps", audioCodec);
        }
        if (audioSampleRate >= 0) {
            videoFile.setAudioSampleRate(audioSampleRate);
            log.debug("          audioSampleRate= {} Hz", audioSampleRate);
        }
        if (audioChannels != null) {
            videoFile.setAudioChannelType(audioChannels);
            log.debug("          audioChannels  = {}", audioChannels);
        }
        if (audioBytesPerSecond >= 0) {
            videoFile.setAudioBytesPerSecond(audioBytesPerSecond);
            log.debug("          audioBitrate   = {} b/s", audioBytesPerSecond);
        }
    }



    /** duration = "00:03:50.2"; */
    protected static long getTimeFromDuration(String duration) {
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

    
    static Matcher matcher(String pattern, int... flags) {
        int flag = 0;
        
        for (int i = 0, len = flags.length; i < len; i++)
            flag |= flags[i];
        
        return Pattern.compile(pattern, flag).matcher("foo");
    }
}
