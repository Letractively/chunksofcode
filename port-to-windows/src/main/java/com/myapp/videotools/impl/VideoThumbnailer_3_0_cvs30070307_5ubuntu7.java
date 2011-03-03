package com.myapp.videotools.impl;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;

import com.myapp.util.file.FileUtils;
import com.myapp.util.format.TimeFormatUtil;
import com.myapp.util.image.ImageUtil;
import com.myapp.util.process.ProcessTimeoutKiller;
import com.myapp.videotools.AppStatistics;
import com.myapp.videotools.Util;



final class VideoThumbnailer_3_0_cvs30070307_5ubuntu7 extends AbstractVideoThumbnailer {

    
    public VideoThumbnailer_3_0_cvs30070307_5ubuntu7()  { super(); }
    

    public boolean createBigPicture(int rows,
                                    int cols,
                                    File out,
                                    int width,
                                    int height) throws IOException {
        LOG.info("      create big picture for video      : {}", videoFile.getName());
        LOG.info("        big picture file                : {}", out);
        
        File tempDir = calculateThumbnailTempDir();
        final int numberOfThumbsExpected = rows * cols;
        
        
        // create the thumbnails first...
        //--------------------------------------

        List<File> thumbs = createThumbnailSeries(numberOfThumbsExpected,
                                                  width,
                                                  height,
                                                  tempDir);
        final int thumbsGotReally = thumbs == null ? 0 : thumbs.size();
        
        if (thumbs == null || thumbs.size() != numberOfThumbsExpected) {                
            LOG.error("      FAIL, pictures expected: {}, but got {}", numberOfThumbsExpected, thumbsGotReally);
            return false;
        }        
        
        
        // ... then merge them to one big picture ...
        //------------------------------------------------------
        
        try {
            merger.mergeImages(rows, cols, out, width, height, thumbs, videoFile);

        } catch (Exception e) {
            LOG.error("        could not merge thumbnails: {}", e);
            LOG.trace("        stacktrace:", e);
            LOG.error("      FAIL, no big picture for video : {}", videoFile);
            return false;
            
        } finally {
            FileUtils.deleteRecursively(tempDir);
            LOG.trace("        tmp files removed.");
        }

        LOG.info("      OK, created big picture for video : {}", videoFile);
        return true;
    }


    /**
     * takes a set of thumbnails of the current videofile and stores in the
     * given directory.
     * 
     * @param count
     *            the desired number of thumbnails to create. the time between
     *            the snapshots will be calculated based on the length of the
     *            video and the count value.
     * @param width
     *            the desired width of each thumbnail picture
     * @param height
     *            the desired height of each thumbnail picture
     * @param targetDir
     *            the directory where the files will be created
     * @return the list of created thumbnails, in chronologic order, null if the 
     *            creation of the thumbnailing process did not exit properly.
     * @throws IOException
     *             if the process fails
     */
    public List<File> createThumbnailSeries(final int count,
                                            int width,
                                            int height,
                                            File targetDir) throws IOException {
        LOG.info("        create {} thumbnails for video    : {}", count, videoFile.getName());
        LOG.debug("          length of video file            : {}", TimeFormatUtil.getTimeLabel(videoFile.getLengthSeconds()));
        LOG.debug("          thumbnails will be created in   : {}", targetDir);

        
            // check preconditions:
        if (videoFile == null || count <= 0) 
            throw new IllegalStateException("file: "+videoFile+" count: "+count);
        
        createOrWipeTempThumbnailDir(targetDir);


        //-------------------------------------
        // build command...
        //-------------------------------------
        
        List<String> commands = new ArrayList<String>();
        commands.add(FFMPEG.getInstance().getApplication().getFfmpegCommand());

        // specify input file:
        commands.add("-i");
        commands.add(videoFile.getFile().getAbsolutePath());

        // specify verbosity of process:
        commands.add("-v");
        commands.add("3");

        // specify step size (time between two snapshots)
        commands.add("-r");
        commands.add(calculateStepSizeParameter(count));
//        commands.add("-vframes");
//        commands.add(Integer.toString(count));
        
        // specify size of the thumbnail images:
        commands.add("-s");
        commands.add(calculateThumbDimensionParameter(width, height));
        
        // specify names of the thumbnail dirs:
        commands.add(calculateThumbsTargetNamesParameter(targetDir));
        
        
        //-------------------------------------
        // start thumbnail process:
        //-------------------------------------
        
        int status;
        long start = System.currentTimeMillis();
        
        try {
            status = executeThumbnailProcess(commands, targetDir);
            
            if (status != 0) {
                LOG.error("        FAIL, thumbnailing process did not finish properly     : status = {}, file = {}", status, videoFile.getName());
                /* TODO maybe check if there is something like --try-harder to enable continuing with the files if there were any. -- */
                statistics.incrementThumbnailFails();
                return null;
            }
            
            List<File> thumbnails = collectCreatedThumbnails(targetDir, count);
            
            if (thumbnails.size() < count) {
                // FIXME: implement --try-harder option !
                LOG.warn("        FAIL thumbnailing process did not create enough pix   : requested: {}, got: {} file: {}", new Object[]{count, thumbnails.size(), videoFile.getName()});
                statistics.incrementThumbnailFails();
                return null;
            }
            
            LOG.info("        OK, created {} thumbnails for video  : {}", thumbnails.size(), videoFile.getName());
            statistics.addThumbnailsCreated(thumbnails.size());
            return thumbnails;
            
        } catch (IOException e) {
            LOG.error("        FAIL, I/O problem while creating thumbnails for video  : {}, because: {}", videoFile.getName(), e);
            LOG.trace("stacktrace: ", e);
            statistics.incrementThumbnailFails();
            return null;
            
        } finally {
            statistics.addTimeSpentWithThumbnailing(System.currentTimeMillis() - start);
        }
    }
    
    
    
    

    
    /**
     * calculates a parameter that tells ffmpeg to create thumbnail pictures
     * with a index number using the prefix and the suffix from the current
     * targetPathGenerator
     * 
     * @param destinationDir
     *            the dir where the thumbnails will be created.
     * @return parameter that tells ffmpeg to create thumbnail pictures with a
     *         index number
     */
    private String calculateThumbsTargetNamesParameter(File destinationDir) {
        StringBuilder bui = new StringBuilder();
        bui.append(destinationDir.getAbsolutePath());
        
        if ( File.separatorChar != bui.charAt(bui.length() - 1)) {
            bui.append(File.separatorChar);
        }
        
        bui.append(targetPathGenerator.getFileNamePrefix());
        bui.append(videoFile.getFile().getName());
        bui.append(".frame.%03d"); // incrementing number XXX ffmpeg specific
        bui.append(targetPathGenerator.getFileNameSuffix());
        
        return bui.toString().trim();
    }

    

    /**
     * executes the thumbnail process.
     * 
     * @param cmd
     *            the command array to execute
     * @param targetDir
     *            the directory where the thumbs will be created in
     * @throws IOException
     *             may be thrown if the process fails
     * @throws InterruptedException
     *             when the current thread was interrupted while waiting for the
     *             process to return.
     * @return the exit status of the process.
     */
    private int executeThumbnailProcess(List<String> cmd, File targetDir) throws IOException {
        LOG.trace("          starting process                : "+Util.printArgs(cmd));
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(cmd);
        pb.redirectErrorStream(true);
            // suppose taking thumbnails will not take more time than a half of the
            // video file's play length. the process will be killed by a timeout,
            // because sometimes ffmpeg locks.
        long wait = videoFile.getLengthMillis() / 2L;
        Matcher interest = Pattern.compile("frame\\=\\s*([1-9][0-9]*)").matcher("foo");/*/////////////TODO */ 
        Process nailProcess = pb.start();
        
        try {
            StringBuilder traceInfo = null;
            boolean trace = LOG.isTraceEnabled();
            
            if (trace) traceInfo = new StringBuilder(NL);
            
            InputStream processStdOut = nailProcess.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processStdOut));
            ProcessTimeoutKiller.registerKillTimeout(nailProcess, wait, "thumnailing video: "+targetDir.getName() ,true);
            
            for (String s = null, last = null; (s = reader.readLine()) != null;)  {
                if (s.length() < 10) // filter "*** drop frame" -output lines from ffmpeg
                    continue;
                
                if (trace) traceInfo.append(s).append(NL);
                
                if (interest.reset(s).find()) {
                    String group1 = interest.group(1);
                    
                    if (last != null && last.equals(group1)) {
                        last = group1;
                        continue;
                    }

                    LOG.debug("          [ffmpeg created] : "+group1);
                    last = interest.group(1);
                }
            }
            
            final int status = nailProcess.waitFor();
            ProcessTimeoutKiller.cancelKillTimeout(nailProcess);
            
            if (status != 0) {
                LOG.error("          process returned with status {}  :'thumnailing video {}'", status, targetDir.getName());
                
                if (status == 255) { // XXX this value may vary between different ffmpeg versions
                    LOG.error("----- PROCESS WAS KILLED/INTERRUPTED! - EXIT VM --------");
                    System.exit(1);
                    return "dead code".hashCode();
                }
                
                if (trace) LOG.trace("ffmpeg output: {}", traceInfo.toString());
            }
            
            return status;
            
        } catch (InterruptedException e) {
            LOG.trace("          process was interrupted!", e);
            throw new RuntimeException(e);
            
        } finally {
            nailProcess.destroy();
            LOG.trace("          process 'thumbnails' cleaned up. ({})", targetDir.getName());
        }
    }
    
    /**
     * collects the created thumbnail image files from the given directory. if
     * ffmpeg created more than the desired number of files, the remaining files
     * will be deleted.
     * 
     * @param targetDir
     *            the dir to search for image files
     * @param count
     *            the number of files we want to get
     * @return the created thumbnail files
     */
    private List<File> collectCreatedThumbnails(File targetDir, int count) {        
        List<File> thumbnails = new ArrayList<File>(32);
        File[] files = targetDir.listFiles();
        String prefix = targetPathGenerator.getFileNamePrefix();
        String origName = videoFile.getName();
        
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            String n = f.getName();
            
            if (f.isFile() && n.startsWith(prefix) && n.contains(origName)) {
                thumbnails.add(f);
            }
        }
        
        if (thumbnails.size() > 0) {
            Collections.sort(thumbnails, FILE_CMP);
            // XXX workaround: ffmpeg always creates two redundant images at begin of movie...
            thumbnails.remove(0).delete(); 
    
            // XXX workaround: sometimes ffmpeg creates too many pix
            // XXX maybe at least take an interpolated subset instead of taking the first n files...
            while (thumbnails.size() > count) {
                thumbnails.remove(thumbnails.size() -1).delete();
            }
        }
        
        return thumbnails;
    }

    
    /**
     * calculates the parameter telling ffmpeg the dimensions of the thumbnail
     * pictures based on the dimension of the video and the given maximum
     * bounds. the calculated dimension will fit in the max bounds and keep the
     * ratio of the original dimensions.
     * 
     * @param maxPictureWidth
     *            the maximum width of the thumbnails
     * @param maxPictureHeight
     *            the maximum height of the thumbnails
     * @return parameter string telling ffmpeg the dimensions of the desired
     *         thumbnail pictures
     */
    private String calculateThumbDimensionParameter(int maxPictureWidth, 
                                                    int maxPictureHeight) {
        Dimension size = ImageUtil.scaleDimensions(maxPictureWidth, 
                                                   maxPictureHeight, 
                                                   videoFile.getVideoWidth(), 
                                                   videoFile.getVideoHeight());
        // XXX workaround, height and width must be even:
        size.height += (size.height % 2 == 1) ? 1 : 0; 
        size.width +=  (size.width  % 2 == 1) ? 1 : 0;  

        String thumbDimensions = size.width+"*"+size.height;
        LOG.debug("          thumbnails size will be         : {}", thumbDimensions);
        return thumbDimensions;
    }

    
    /**
     * creates a ffmpeg parameter specifying the time between two thumbnails
     * based on the duration of the current videofile and the number of
     * thumbnails to create.
     * 
     * @param numberOfThumbsToCreate
     * @return
     */
    private String calculateStepSizeParameter(int numberOfThumbsToCreate) {
        double stepSize = videoFile.getLengthSeconds(); 
        stepSize /= Integer.valueOf(numberOfThumbsToCreate).doubleValue();
        stepSize = stepSize < 1.0 ? 1.0 : stepSize; // XXX workaround, must be greater than 1.0

        return "1/"+TimeFormatUtil.formatTimeTo2Digits(stepSize);
    }


    @Override
    public void captureImage(double timeOffset, File out) throws IOException {
        captureSingleImage(timeOffset, 
                           videoFile.getVideoHeight(), 
                           videoFile.getVideoWidth(), 
                           out);
    }


    @Override
    public BufferedImage captureImage(double timeOffset) throws IOException {
        return captureImage(timeOffset, 
                            videoFile.getVideoWidth(), 
                            videoFile.getVideoHeight());
    }


    @Override
    public BufferedImage captureImage(double timeOffset, 
                                      int pWidth, 
                                      int pHeight) throws IOException {
        File tempFile = File.createTempFile("screenshot", ".jpeg");
        captureSingleImage(timeOffset, pWidth, pHeight, tempFile);
        return ImageIO.read(tempFile);
    }


    @Override
    public void captureSingleImage(double timeOffset,
                                   int pWidth,
                                   int pHeight,
                                   File out) throws IOException {
        Logger log = LOG;
        log.info("capturing image of                : " + videoFile.getFile().getAbsolutePath());
        log.info("  target file                     : " + out.getAbsolutePath());
        log.info("  offset in seconds of screenshot : " + TimeFormatUtil.getTimeLabel(timeOffset));
        
        String[] cmd = createScreenshotCommandArray(timeOffset,
                                                    pWidth,
                                                    pHeight,
                                                    true,
                                                    out);
        Process p = Runtime.getRuntime().exec(cmd);
        long maxTime = new Double(timeOffset / 5).longValue();
        
        if (log.isDebugEnabled()) {
            log.debug("  starting process               : {}", Util.printArgs(cmd));
        }
        
        ProcessTimeoutKiller.registerKillTimeout(p, maxTime, "capturing image to: "+out.getName(), false);
        
        try {
            p.waitFor();
            ProcessTimeoutKiller.cancelKillTimeout(p);
            AppStatistics.getInstance().incrementThumbnailsCreated();

        } catch (InterruptedException e) {
            AppStatistics.getInstance().incrementThumbnailFails();
            log.error("  could not capture image: {}", out.getName());
            log.trace("stacktrace:", e);
            
        } finally {
            p.destroy();
            log.trace("  process cleaned up.");
        }

        log.info("OK, image captured to file : {}", out);
    }
    

    private String[] createScreenshotCommandArray(double offset,
                                                  int pWidth,
                                                  int pHeight,
                                                  boolean overwriteExisting,
                                                  File outputFile) {
        Logger log = LOG;
        // "ffmpeg -i /my_video_file_dir/video.flv -y -f image2 -ss 8 -sameq -t 0.001 -s 320*240 /image_dir/screenshot.jpg"
        // 320*240 : image dimension is 320 pixels width and 240 pixels height
        // -ss 8 : screenshot will be taken at 8 second after video starts.
        
        List<String> args = new ArrayList<String>();
        
        // commandname
        args.add(FFMPEG.getInstance().getApplication().getFfmpegCommand());

        // specify inputfile
        args.add("-i");
        args.add(videoFile.getFile().getAbsolutePath());

        // overwrite existing ?
        if (overwriteExisting)
            args.add("-y");

        // set outputformat
        args.add("-f");
        args.add("image2");           
        
        // time of capturing:
        if (log.isTraceEnabled()) log.trace("formatTime: {}", TimeFormatUtil.formatTimeTo2Digits(offset));
        args.add("-ss");
        args.add(TimeFormatUtil.formatTimeTo2Digits(offset));
        
        args.add("-sameq");                // see man ffmpeg :-P
        args.add("-t");                    // see man ffmpeg :-P
        args.add("0.001");                 // see man ffmpeg :-P
        args.add("-s");                    // see man ffmpeg :-P
        args.add(pWidth + "*" + pHeight);
        args.add(outputFile.getAbsolutePath());
        
        String[] arr = args.toArray(new String[]{});
        return arr;
    }
    
    
}
