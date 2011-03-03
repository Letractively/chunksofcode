package com.myapp.videotools.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.util.format.FileFormatUtil;
import com.myapp.util.format.TimeFormatUtil;
import com.myapp.util.process.ProcessTimeoutKiller;
import com.myapp.videotools.AppStatistics;
import com.myapp.videotools.Configuration;
import com.myapp.videotools.IImageMerger;
import com.myapp.videotools.Util;
import com.myapp.videotools.VideoFile;

/**
 * merges a bunch of thumbnail images of a video file to a nice and titled grid
 * by invoking a "montage" process. ( http://www.imagemagick.org)
 * 
 * <pre>
    andre@buenosaires ~ % montage -version
    Version: ImageMagick 6.5.7-8 2009-11-26 Q16 http://www.imagemagick.org
    Copyright: Copyright (C) 1999-2009 ImageMagick Studio LLC
    Features: OpenMP
   </pre> 
 * 
 * @author andre
 * 
 */
class ImageMagickImageMerger implements IImageMerger {

    private static final Logger log = LoggerFactory.getLogger(ImageMagickImageMerger.class);
    private static final AppStatistics statistics = AppStatistics.getInstance();

    
    public ImageMagickImageMerger() {
    }
    
    
    public void mergeImages(int rows,
                     int cols,
                     File out,
                     int tileWidth,
                     int tileHeight,
                     List<File> imageList,
                     VideoFile videoFile) throws IOException  {
        final int elementCount = imageList == null ? 0 : imageList.size();
        log.info("        merging {} pictures ...", elementCount);
        
        if (elementCount <= 0) {
            throw new RuntimeException("no files specified!");
        }

        List<String> args = new ArrayList<String>();
        
        String cmd = Configuration.getInstance().getProperty(
                                                FFMPEG.MONTAGE_COMMAND_PROPKEY);
        args.add(cmd);
        
        if (videoFile != null) {
            // specify the files with their time labels according to the videofile:
            //--------------------------------------
            double stepSize = videoFile.getLengthSeconds();
            stepSize = stepSize / Integer.valueOf(rows * cols).doubleValue();
            double currentTime = 0;
            
            for (int i = 0; i < elementCount; i++) {
                // FIXME: calculating times this may be inconsistent (depends on the 
                // count of files ffmpeg REALLY produced), but just a minor issue.
                File f = imageList.get(i);
                args.add("-label");
                args.add(TimeFormatUtil.getTimeLabel(currentTime));
                args.add(f.getAbsolutePath());
                currentTime += stepSize;
            }
        
        } else {
            // specify just the files:
            //--------------------------------------
            for (int i = 0; 
                 i < elementCount; i++) 
                 args.add(imageList.get(i).getAbsolutePath());
        }
        
        // specify layout: 
        //--------------------------------------
        args.add("-tile");
        args.add(cols+"x"); // (e.g. -tile 5 will result in rows of size 5)
        args.add("-geometry");
        args.add(tileWidth+"x"+tileHeight+"+1+1");
        

        if (videoFile != null) {
            // specify filename as header at top of image:
            //--------------------------------------
            StringBuilder titleBuilder = new StringBuilder();
            titleBuilder.append(Util.squeezeFileName(videoFile.getName()));
            titleBuilder.append(" (size: ");
            titleBuilder.append(FileFormatUtil.getHumanReadableFileSize(videoFile.getFile()));
            titleBuilder.append(") (length: ");
            titleBuilder.append(TimeFormatUtil.getTimeLabel(videoFile.getLengthSeconds()));
            titleBuilder.append(")");
            args.add("-title");
            args.add(titleBuilder.toString());
        }
        
        // specify outputfile:
        //--------------------------------------
        String outputFilePath = out.getAbsolutePath();
        String nameLower = out.getName().toLowerCase();
        
        if ( ! (nameLower.endsWith(".jpeg") || nameLower.endsWith(".jpg"))) {
            outputFilePath += ".jpeg";
        }
        
        args.add(outputFilePath);
        log.debug("          number of pictures to merge     : {}", imageList.size());
        
        if (log.isTraceEnabled()) log.trace("          starting process              : {}", Util.printArgs(args));
        
        
        // start montage process:
        //--------------------------------------
        ProcessBuilder pb = new ProcessBuilder(args);
        Process p = pb.start();
        ProcessTimeoutKiller.registerKillTimeout(p, 10000, "merge images to: "+out.getName(), false);
        long start = System.currentTimeMillis();
        
        try {
            int i = p.waitFor();
            ProcessTimeoutKiller.cancelKillTimeout(p);
            
            if (i != 0) {
                log.warn("          process: {} returned with ", args.get(0) , i);
            }
            
        } catch (InterruptedException e) {
            log.error("        could not merge thumbnails");
            log.trace("        stacktrace:", e);
            statistics.incrementMergeFails();
            return;
            
        } finally {
            p.destroy();
            ProcessTimeoutKiller.cancelKillTimeout(p);
            log.trace("          process cleaned up.");
            statistics.addTimeSpentWithImageMerging(System.currentTimeMillis() - start);
        }

        statistics.incrementBigPicturesMerged();
        log.info("        OK, {} pictures were merged to file: {}", elementCount, outputFilePath);
    }
}
