package com.myapp.videotools.impl;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.myapp.util.file.FileUtils;
import com.myapp.util.format.TimeFormatUtil;
import com.myapp.util.image.ImageUtil;
import com.myapp.util.process.ProcessTimeoutKiller;
import com.myapp.videotools.Util;



final class VideoThumbnailer_Win7 extends AbstractVideoThumbnailer {


    public VideoThumbnailer_Win7() { super(); }
    

    @Override
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
    @Override
    public List<File> createThumbnailSeries(final int count,
                                            int width,
                                            int height,
                                            File targetDir) throws IOException {
        LOG.info("        create {} thumbnails for video     : {}", count, videoFile.getName());
        LOG.debug("          length of video file            : {}", TimeFormatUtil.getTimeLabel(videoFile.getLengthSeconds()));
        LOG.debug("          thumbnails will be created in   : {}", targetDir);

        
            // check preconditions:
        if (videoFile == null || count <= 0) 
            throw new IllegalStateException("file: "+videoFile+" count: "+count);
        
        createOrWipeTempThumbnailDir(targetDir);
        
        final double interval = videoFile.getLengthSeconds() / count;
        final String dimension = calculateThumbDimensionParameter(width, height);
        
        
        for (int i = 0; i < count; i++) { 
            double offset = i * interval;
            String targetName = calculateThumbsTargetNamesParameter(targetDir, i);

            List<String> commands = 
                createSingleThumbnailInSeriesCommand(offset,
                                                     dimension,
                                                     targetName);
            if (i == 0) LOG.trace("          starting process series ({})    : {}", count, Util.printArgs(commands));
            
            //-------------------------------------
            // start thumbnail process:
            //-------------------------------------
            
            long start = System.currentTimeMillis();
            int status;
            StringBuilder traceInfo = new StringBuilder(NL);
            
            try {        
                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.redirectErrorStream(true);
                Matcher interest = Pattern.compile("frame\\=\\s*([1-9][0-9]*)").matcher("foo");
                Process nailProcess = pb.start();
                ProcessTimeoutKiller.registerKillTimeout(nailProcess, 90000, "thumnailing video: "+targetDir.getName(), true);

                InputStream processStdOut = nailProcess.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(processStdOut));
                
                for (String s = null, last = null; (s = reader.readLine()) != null;)  {
                    if (s.length() < 10) // filter "*** drop frame" -output lines from ffmpeg
                        continue;
                    
                    traceInfo.append(s).append(NL);
                    
                    if (interest.reset(s).find()) {
                        String group1 = interest.group(1);
                        
                        if (last != null && last.equals(group1)) {
                            last = group1;
                            continue;
                        }

                        last = interest.group(1);
                    }
                }
                
                status = nailProcess.waitFor();
                ProcessTimeoutKiller.cancelKillTimeout(nailProcess);
                
                if ( ! new File(targetName).exists()) {
                    LOG.error("          no corresponding file created    :'{}'", targetName);
                    return null; // fatal
                }

                if (status != 0) {
                    LOG.error("          process returned with status {}  :'thumnailing video {}'", status, targetDir.getName());
                    
                    if (status == 255) { //XXX may vary
                        LOG.error("----- PROCESS WAS KILLED/INTERRUPTED! - EXIT VM --------");
                        System.exit(1);
                    }
                    
                    LOG.trace("          ffmpeg output: {}", traceInfo);
                    return null;
                    
                } else {
                    LOG.trace("          thumb created: {} / {}   @ {}", new Object[]{i+1, count, offset});
                }
                
            } catch (IOException e) {
                LOG.error("        FAIL, I/O problem while creating thumbnails for video  : {}, because: {}", videoFile.getName(), e);
                LOG.trace("stacktrace: ", e);
                statistics.incrementThumbnailFails();
                continue;
                
            } catch (InterruptedException e) {
                LOG.trace("          process was interrupted!", e);
                continue;
                
            } finally {
                statistics.addTimeSpentWithThumbnailing(System.currentTimeMillis() - start);
            }
        }
        
        List<File> thumbnails = collectCreatedThumbnails(targetDir);
        
        if (thumbnails.size() < count) {
            // FIXME: implement --try-harder option !
            LOG.warn("        FAIL thumbnailing process did not create enough pix   : requested: {}, got: {} file: {}", new Object[]{count, thumbnails.size(), videoFile.getName()});
            statistics.incrementThumbnailFails();
            return null;
        }
        
        LOG.info("        OK, created {} thumbnails for video  : {}", thumbnails.size(), videoFile.getName());
        statistics.addThumbnailsCreated(thumbnails.size());
        return thumbnails;
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
    private String calculateThumbsTargetNamesParameter(File destinationDir, int count) {
        StringBuilder bui = new StringBuilder();
        bui.append(destinationDir.getAbsolutePath());
        
        if ( File.separatorChar != bui.charAt(bui.length() - 1)) {
            bui.append(File.separatorChar);
        }
        
        String prefix = targetPathGenerator.getFileNamePrefix();
        bui.append(prefix);
        
        if (prefix.length() > 0)
            bui.append(".");
        
        bui.append("frame.");
        if (count < 10000) bui.append("0");
        if (count < 1000)  bui.append("0");
        if (count < 100)   bui.append("0");
        if (count < 10)    bui.append("0");
        bui.append(count);
        bui.append(targetPathGenerator.getFileNameSuffix());
        
        return bui.toString().trim();
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
    private List<File> collectCreatedThumbnails(File targetDir) {        
        List<File> thumbnails = new ArrayList<File>(32);
        File[] files = targetDir.listFiles();
        Arrays.sort(files, FILE_CMP);
        
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            
            if (f.isFile()) {
                thumbnails.add(f);
            }
        }
        
        Collections.sort(thumbnails, FILE_CMP);
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

        String thumbDimensions = size.width+"x"+size.height;
        LOG.debug("          thumbnails size will be         : {}", thumbDimensions);
        return thumbDimensions;
    }

    
    private List<String> createSingleThumbnailInSeriesCommand(double offset,
                                                              String dimension,
                                                              String thumbnailName) {
        List<String> commands = new ArrayList<String>();
        
        //-------------------------------------
        // build command...
        //-----------------------------------
        // ffmpeg  -ss 10.234 
        //    -i /media/datadisk/porn/videos/to_sort/915.flv 
        //    -vframes 1      -s 320x240 
        //    /tmp/ffoooo.jpg
        
        commands.add(FFMPEG.getInstance().getApplication().getFfmpegCommand());
        
        // specify time offset for capturing: (XXX BEFORE "-i" ARGUMENT !!!)
        commands.add("-ss");
        commands.add(Double.toString(offset));
        
        // specify input file:
        commands.add("-i");
        commands.add(videoFile.getFile().getAbsolutePath());
        
        // specify number of frames to capture (1)
        commands.add("-vframes");
        commands.add("1");
        
        // specify size of the thumbnail images:
        commands.add("-s");
        commands.add(dimension);

        // specify verbosity of process:
        commands.add("-v");
        commands.add("3");
        
        // force jpeg output format:
        commands.add("-f");
        commands.add("image2");
        
        // specify names of the thumbnail dirs:
        commands.add(thumbnailName);
        
        return commands;
    }


    @Override
    public void captureImage(double timeOffset, File out) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }//TODO


    @Override
    public BufferedImage captureImage(double timeOffset) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    } //TODO


    @Override
    public BufferedImage captureImage(double timeOffset, 
                                      int pWidth, 
                                      int pHeight) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }//TODO


    @Override
    public void captureSingleImage(double timeOffset,
                                   int pWidth,
                                   int pHeight,
                                   File out) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }//TODO
}
