package com.myapp.videotools.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FFMPEGData {

    
    private static final Logger LOG = LoggerFactory.getLogger(FFMPEGData.class);
    private static final String NL = System.getProperty("line.separator");
    

    private String ffmpegCommand;
    private String montageCommand;
    private Map<String, String> supportedFileTypes = new TreeMap<String, String>();
    private Map<String, String> supportedCodecs = new TreeMap<String, String>();
    
    
    
    public FFMPEGData() {
    }

    
    
    public String getFfmpegCommand() {
        return ffmpegCommand;
    }

    public void setFfmpegCommand(String ffmpegCommand) {
        this.ffmpegCommand = ffmpegCommand;
    }

    public Map<String, String> getSupportedFileTypes() {
        if (isNoSupportedFileTypesLoaded()) {
            LOG.warn("no formats available, is ffmpeg program available?");
        }
        return supportedFileTypes;
    }

    public void setSupportedFileTypes(Map<String, String> supportedFileTypes) {
        if (supportedFileTypes == null)
            return;
        
        this.supportedFileTypes.clear();
        this.supportedFileTypes.putAll(supportedFileTypes);
    }

    public Map<String, String> getSupportedCodecs() {
        if (isNoCodecsLoaded()) {
            LOG.warn("no codecs available, is ffmpeg program available?");
        }
        return supportedCodecs;
    }

    public void setSupportedCodecs(Map<String, String> supportedCodecs) {
        if (supportedCodecs == null)
            return;
        
        this.supportedCodecs.clear();
        this.supportedCodecs.putAll(supportedCodecs);
    }

    public boolean isNoCodecsLoaded() {
        return supportedCodecs == null || supportedCodecs.isEmpty();
    }

    public boolean isNoSupportedFileTypesLoaded() {
        return supportedFileTypes == null || supportedFileTypes.isEmpty();
    }

    public boolean isCodecSupported(String codec) {
        return isNoCodecsLoaded() 
               && getSupportedCodecs().containsKey(codec);
    }

    public boolean isFileTypeSupported(String key) {
        return isNoSupportedFileTypesLoaded()
               && getSupportedFileTypes().containsKey(key);
    }

    public String getFileFormatDescription(String key) {
        return getSupportedFileTypes().get(key);
    }

    public String getCodecDescription(String key) {
        return getSupportedCodecs().get(key);
    }
    

    public String toString() {
        StringBuilder sb = new StringBuilder("  installation status:");
        sb.append(NL).append("           ").append(getSupportedCodecs().size());
        sb.append(" codecs installed: ").append(NL);
        
        for (Iterator<String> itr = getSupportedCodecs().keySet().iterator(); 
             itr.hasNext();) {
             sb.append(itr.next());
             if (itr.hasNext()) sb.append(", ");
        }
        
        sb.append(NL).append("           ").append(getSupportedFileTypes().size());
        sb.append(" file types installed: ").append(NL);
        
        for (Iterator<String> i = getSupportedFileTypes().keySet().iterator(); 
                 i.hasNext();) {
            sb.append(i.next());
            if (i.hasNext()) sb.append(", ");
        }
        
        return sb.toString();
    }

    public String getMontageCommand() {
        return montageCommand;
    }

    public void setMontageCommand(String property) {
        montageCommand = property;        
    }

}