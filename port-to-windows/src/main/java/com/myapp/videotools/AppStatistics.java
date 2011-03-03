package com.myapp.videotools;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.myapp.util.format.TimeFormatUtil;

public class AppStatistics {

    private static final Format DECIMAL_FORMAT= new DecimalFormat("##0.0");
    private static final String NL = System.getProperty("line.separator");
    
    private long applicationStart = -1L;
    private long applicationExit = -1L;
    private AtomicInteger thumbnailsCreated = new AtomicInteger(0);
    private AtomicInteger bigPicturesMerged = new AtomicInteger(0);
    private AtomicInteger filesSkippedBecauseExistingTarget = new AtomicInteger(0);
    private AtomicInteger filesSkippedBecauseFiltered = new AtomicInteger(0);
    private AtomicInteger filesParsed = new AtomicInteger(0);
    
    private AtomicInteger thumbnailFails = new AtomicInteger(0);
    private AtomicInteger mergeFails = new AtomicInteger(0);
    private AtomicInteger parseFails = new AtomicInteger(0);

    private AtomicLong timeSpentWithThumbnailing = new AtomicLong(0);
    private AtomicLong timeSpentWithImageMerging = new AtomicLong(0);
    private AtomicLong timeSpentWithParsingMetadata = new AtomicLong(0);

    
    private static AppStatistics instance = null;
    
    public static AppStatistics getInstance() {
        if (instance == null)
            synchronized (AppStatistics.class) {
                if (instance == null)
                    instance = new AppStatistics();
            }
        return instance;
    }
    
    
    private AppStatistics() {}
    
    
    
    
    // total times
    
    
    public void setApplicationStart() {
        if (applicationStart != -1L)
            throw new IllegalStateException("application already started!");
        
        applicationStart = System.currentTimeMillis();
    }
    public void setApplicationExit() {
        if (applicationExit != -1L)
            throw new IllegalStateException("application already exited!");
        
        applicationExit = System.currentTimeMillis();
    }
    public long getApplicationStart() {
        return applicationStart;
    }
    public long getApplicationExit() {
        return applicationExit;
    }
    public long getTotalTimeNeeded() {
        if (applicationStart == -1 || applicationExit == -1)
            throw new IllegalStateException(applicationStart+" , "+applicationExit);
        
        return applicationExit - applicationStart;
    }
    
    
    
    

    // thumbnailing
    
    public final long addTimeSpentWithThumbnailing(long delta) {
        return timeSpentWithThumbnailing.addAndGet(delta);
    }
    public final long getTime() {
        return timeSpentWithThumbnailing.get();
    }
    public final int addThumbnailsCreated(int delta) {
        return thumbnailsCreated.addAndGet(delta);
    }
    public final int getThumbnailsCreated() {
        return thumbnailsCreated.get();
    }
    public final int incrementThumbnailsCreated() {
        return thumbnailsCreated.incrementAndGet();
    }
    public final long incrementThumbnailFails() {
        return thumbnailFails.incrementAndGet();
    }
    public final long getThumbnailFails() {
        return thumbnailFails.get();
    }

    
    

    // merging images
    
    
    public final long addTimeSpentWithImageMerging(long delta) {
        return timeSpentWithImageMerging.addAndGet(delta);
    }
    public final long getTimeSpentWithImageMerging() {
        return timeSpentWithImageMerging.get();
    }
    public final int getBigPicturesMerged() {
        return bigPicturesMerged.get();
    }
    public final int incrementBigPicturesMerged() {
        return bigPicturesMerged.incrementAndGet();
    }
    public final long incrementMergeFails() {
        return mergeFails.incrementAndGet();
    }
    public final long getMergeFails() {
        return mergeFails.get();
    }
    
    
    
    
    // parsing
    
    public final long addTimeSpentWithParsingMetadata(long delta) {
        return timeSpentWithParsingMetadata.addAndGet(delta);
    }
    public final long getTimeSpentWithParsingMetadata() {
        return timeSpentWithParsingMetadata.get();
    }
    public final long incrementFilesParsed() {
        return filesParsed.incrementAndGet();
    }
    public final long getFilesParsed() {
        return filesParsed.get();
    }
    public final long incrementParseFails() {
        return parseFails.incrementAndGet();
    }
    public final long getParseFails() {
        return parseFails.get();
    }

    
    
    // skipping
    
    
    public final int getSkippedBecauseFiltered() {
        return filesSkippedBecauseFiltered.get();
    }
    public final int incrementSkippedBecauseFiltered() {
        return filesSkippedBecauseFiltered.incrementAndGet();
    }
    public final int getSkippedBecauseExistingTarget() {
        return filesSkippedBecauseExistingTarget.get();
    }
    public final int incrementSkippedBecauseExistingTarget() {
        return filesSkippedBecauseExistingTarget.incrementAndGet();
    }
    
    
    

    
    @Override
    public String toString() {
        Long totalMillis        = getTotalTimeNeeded();
        
        StringBuilder b = new StringBuilder();
        b.append("ApplicationStatistics:").append(NL).append(NL);
        b.append("  creation statistics:").append(NL);

        Long parsedMillis = Long.valueOf(timeSpentWithParsingMetadata.longValue());
        appendCountWithTimeForEachFile(b, "video-metadata parsed", filesParsed,       parsedMillis,  parseFails.intValue());
        
        Long mergedMillis = Long.valueOf(timeSpentWithImageMerging.longValue());
        appendCountWithTimeForEachFile(b, "big pictures merged",   bigPicturesMerged, mergedMillis,  mergeFails.intValue());
        
        Long thumbedMillis = Long.valueOf(timeSpentWithThumbnailing.longValue());
        appendCountWithTimeForEachFile(b, "thumbnails created",    thumbnailsCreated, thumbedMillis, thumbnailFails.intValue());
        
        b.append("    skipped because target file was existing       : ").append(filesSkippedBecauseExistingTarget).append(NL);
        b.append("    skipped because source file was filtered       : ").append(filesSkippedBecauseFiltered).append(NL).append(NL);
        b.append("  time statistics:").append(NL);
        
        appendTimeSpentWithPercent("with parsing metadata", totalMillis, parsedMillis, b);
        appendTimeSpentWithPercent("with thumbnailing videos", totalMillis, thumbedMillis, b);
        appendTimeSpentWithPercent("with image merging", totalMillis, mergedMillis, b);

        Long remainingMillis    = Long.valueOf(totalMillis - (mergedMillis + parsedMillis + thumbedMillis));
        appendTimeSpentWithPercent("with other activities", totalMillis, remainingMillis, b);
        
        b.append("    --------------------------------------------------------------------------").append(NL);
        b.append("    total time needed                              : ").append(TimeFormatUtil.getTimeLabel(totalMillis)).append(NL);
        b.append("    applicationExit                                : ").append(TimeFormatUtil.getDateLabel(applicationExit)).append(NL);
        b.append("    applicationStart                               : ").append(TimeFormatUtil.getDateLabel(applicationStart)).append(NL); 
        
        return b.toString();
    }
    
    private static void appendCountWithTimeForEachFile(StringBuilder b,
                                                       String description,
                                                       Number count,
                                                       Long millisNeeded,
                                                       int fails) {
        String timeLabel;
        
        if (count.intValue() != 0) {
            double millisDbl = millisNeeded.doubleValue();
            double countDbl = count.doubleValue();
            Long millisPerItem = Double.valueOf(millisDbl / countDbl).longValue();
            timeLabel = TimeFormatUtil.getTimeLabel(millisPerItem.longValue()); 
        
        } else {
            timeLabel = "N/A";
        }
        
        b.append("    ");
        b.append(description);
        
        for (int i = 46 - description.length(); i-- > 0; b.append(' '));

        b.append(" : ");
        b.append(count);

        for (int i = 5 - count.toString().length(); i-- > 0; b.append(' '));

        b.append("  (");
        b.append(timeLabel);
        for (int i = 10 - timeLabel.length(); i-- > 0; b.append(' '));
        b.append(" /file)");
        
        if (fails != 0) {
            b.append("      ");
            b.append(fails);
            b.append(" FAILS !");
        }
        
        b.append(NL);
    }
    
    private static void appendTimeSpentWithPercent(String action, Long totalTime, Long millis, StringBuilder b) {
        double percent = millis.doubleValue() / totalTime.doubleValue() * 100d;
        String percentString = DECIMAL_FORMAT.format(percent);
        String timeLabel = TimeFormatUtil.getTimeLabel(millis);
        
        b.append("    time spent ");
        b.append(action);
        
        for (int i = 35 - action.length(); i-- > 0; b.append(' '));
        
        b.append(" : ");
        b.append(timeLabel);
        
        for (int i = 15 - timeLabel.length(); i-- > 0; b.append(' '));
        for (int i = 8 - percentString.length(); i-- > 0; b.append(' '));
        
        b.append(percentString);
        b.append(" %");
        b.append(NL);
    }
    
    
    public static void main(String[] args) throws Throwable {
        AppStatistics s = getInstance();
        s.setApplicationStart();
        s.incrementBigPicturesMerged();
        s.incrementBigPicturesMerged();
        s.incrementBigPicturesMerged();
        s.addTimeSpentWithImageMerging(777);
        s.incrementThumbnailsCreated();
        s.incrementThumbnailsCreated();
        s.addTimeSpentWithThumbnailing(344);
        Thread.sleep(1234);
        s.setApplicationExit();
        System.out.println(s);
    }
}
