#!/bin/sh

td=Data.java

echo "" > $td




echo '

package com.myapp.videotools.impl;

import java.util.*;

final class Data {


    private final List<String> lineOfInterest      ;
    private final List<String> durationAndBitrate  ;
    private final List<String> videoStreamInfo     ;
    private final List<String> audioStreamInfo     ;


    public Data() {
        List<String> l = new ArrayList<String>();
' >> $td

cat audioStreamInfoLines.txt | sed 's!$!");!' | sed 's!^!            l.add("!' >> $td
 
echo '
        audioStreamInfo = new ArrayList<String>(l);
        l.clear();
' >> $td
 
cat videoStreamInfoLines.txt  | sed 's!$!");!' | sed 's!^!            l.add("!' >> $td
 
echo '        
        videoStreamInfo = new ArrayList<String>(l);
        l.clear();
' >> $td
 
cat durationInfoLines.txt  | sed 's!$!");!' | sed 's!^!            l.add("!' >> $td

echo '        
    
        durationAndBitrate = new ArrayList<String>(l);
        
        
        l.clear();
        l.addAll(audioStreamInfo);
        l.addAll(videoStreamInfo);
        l.addAll(durationAndBitrate);
        lineOfInterest = new ArrayList<String>(l);

        // System.out.println("Data.getLineOfInterest() instance created: audioStreamInfo     " +audioStreamInfo.size());
        // System.out.println("Data.getLineOfInterest() instance created: videoStreamInfo     " +videoStreamInfo.size());
        // System.out.println("Data.getLineOfInterest() instance created: durationAndBitrate  " +durationAndBitrate.size());
        // System.out.println("Data.getLineOfInterest() instance created: lineOfInterest      " +lineOfInterest.size());
    }

    public List<String> getLineOfInterest() { return lineOfInterest; }
    public List<String> getDurationAndBitrate() { return durationAndBitrate; }
    public List<String> getVideoStreamInfo() { return videoStreamInfo; }
    public List<String> getAudioStreamInfo() { return audioStreamInfo; }
}

' >> $td





