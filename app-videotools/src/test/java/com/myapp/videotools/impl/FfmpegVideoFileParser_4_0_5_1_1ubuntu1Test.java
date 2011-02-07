package com.myapp.videotools.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FfmpegVideoFileParser_4_0_5_1_1ubuntu1Test {

    private FfmpegVideoFileParser_4_0_5_1_1ubuntu1 instance; 
    private static final Data data = new Data();
    
    @Before
    public void setUp() throws Exception {
        instance = new FfmpegVideoFileParser_4_0_5_1_1ubuntu1();
    }

    @Test
    public void testReadVideoMetaData() {
         // TODO
    }

    @Test
    public void testIsLineOfInterest() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        for (String s : data.getLineOfInterest()) {
            total ++;
            if ( ! instance.isLineOfInterest(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseDurationMillis() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        for (String s : data.getDurationAndBitrate()) {
            total ++;
            if (0 > instance.parseDurationMillis(s)) {
                fails.add(s);
//                System.out.println(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseVideoStartOffsetMillis() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        for (String s : data.getDurationAndBitrate()) {
            total ++;
            if (0 > instance.parseVideoStartOffsetMillis(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseTotalBytesPerSecond() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        for (String s : data.getDurationAndBitrate()) {
            total ++;
            if (0 > instance.parseTotalBytesPerSecond(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertTrue("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                   fails.size() <= 4); // 4 lines with "N/A" as bitrate
    }

    @Test
    public void testParseVideoCodec() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getVideoStreamInfo()) {
            total ++;
            if (null == instance.parseVideoCodec(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseVideoFormat() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getVideoStreamInfo()) {
            total ++;
            if (null == instance.parseVideoFormat(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseVideoHeight() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getVideoStreamInfo()) {
            total ++;
            if (0 > instance.parseVideoHeight(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseVideoWidth() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getVideoStreamInfo()) { 
            total ++;
            if (0 > instance.parseVideoWidth(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseVideoBytesPerSecond() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getVideoStreamInfo()) { 
            total ++;
            if (0 > instance.parseVideoBytesPerSecond(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        
        assertTrue("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     686 >= fails.size());
    }

    @Test
    public void testParseVideoFrameRate() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getVideoStreamInfo()) { 
            total ++;
            if (0 > instance.parseVideoFrameRate(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertTrue("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     fails.size() <= 6);
    }

    @Test
    public void testParseAudioCodec() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getAudioStreamInfo()) { 
            total ++;
            if (null == instance.parseAudioCodec(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseAudioSampleRate() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getAudioStreamInfo()) { 
            total ++;
            if (0 > instance.parseAudioSampleRate(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseAudioChannels() {
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getAudioStreamInfo()) { 
            total ++;
            if (null == instance.parseAudioChannels(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertEquals("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                     0, fails.size());
    }

    @Test
    public void testParseAudioBytesPerSecond() { 
        List<String> fails = new ArrayList<String>();
        int success = 0;
        int total = 0;
        
        
        for (String s : data.getAudioStreamInfo()) { 
            total ++;
            if (0 > instance.parseAudioBytesPerSecond(s)) {
                fails.add(s);
            } else {
                success ++;
            }
        }
        assertTrue("fails: "+ fails.size() + ", success: "+success+" total: "+total, 
                   fails.size() <= 13);
    }
}
