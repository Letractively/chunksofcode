package com.myapp.videotools.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myapp.videotools.AppStatistics;
import com.myapp.videotools.DefaultVideoFileFilter;
import com.myapp.videotools.IImageMerger;
import com.myapp.videotools.IPathCalculator;
import com.myapp.videotools.IVideoFileParser;
import com.myapp.videotools.IVideoThumbnailer;
import com.myapp.videotools.VideoFile;
import com.myapp.videotools.IPathCalculator.FileHierarchyCopying;
import com.myapp.videotools.IPathCalculator.NextToSourceFile;
import com.myapp.videotools.Util.DirsFirstAlphabeticFileComparator;



public abstract class AbstractVideoThumbnailer implements IVideoThumbnailer {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractVideoThumbnailer.class);
    protected static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    protected static final String FILE_SEP = System.getProperty("file.separator");
    protected static final DirsFirstAlphabeticFileComparator FILE_CMP = new DirsFirstAlphabeticFileComparator();
    protected static final String NL = System.getProperty("line.separator");
  
    protected FileFilter videoFileFilter;
    protected IVideoFileParser videoFileParser;
    protected IPathCalculator targetPathGenerator;
    protected IImageMerger merger;
    protected AppStatistics statistics;
   
    protected int preferredWidth = DEFAULT_THUMB_WIDTH;
    protected int preferredHeight = DEFAULT_THUMB_HEIGHT;
    protected int bigPictureRows = DEFAULT_BIG_PIC_ROWS;
    protected int bigPictureCols = DEFAULT_BIG_PIC_COLS;
  
    protected VideoFile videoFile;
    protected File videoRootDir = null;

    
    
    
    public AbstractVideoThumbnailer() {
        videoFileFilter =  new DefaultVideoFileFilter();                     
        videoFileParser =  FFMPEG.getInstance().createVideoFileParser();  
        targetPathGenerator = new NextToSourceFile();                   
        merger = FFMPEG.getInstance().createImageMerger();                                         
        statistics = AppStatistics.getInstance();        
    }

    
    public abstract boolean createBigPicture(int rows,
                                             int cols,
                                             File out,
                                             int width,
                                             int height) throws IOException;
    
    public abstract List<File> createThumbnailSeries(int count,
                                                     int width,
                                                     int height,
                                                     File targetDir) throws IOException;
    
    
    
    public void createBigPictureRecursively(String folder) {
        File rootDir = new File(folder);
        setVideoRootDir(rootDir);
        createBigPictureRecursively0(rootDir);
    }

    public void createBigPictureRecursively() {
        createBigPictureRecursively0(videoRootDir);
    }

    
    public List<File> createThumbnailSeries(int amount, File targetDir) throws IOException {
        List<File> thumbs = createThumbnailSeries(amount,
                                                  preferredWidth,
                                                  preferredHeight,
                                                  targetDir);
        return thumbs;
    }
    
    public boolean createBigPicture(File out) throws IOException {
        return createBigPicture(bigPictureRows, bigPictureCols, out);
    }

    public boolean createBigPicture(int rows, 
                                    int cols, 
                                    File out) throws IOException {
        return createBigPicture(rows, cols, out, preferredWidth, preferredHeight);
    }
    
    /**
     * decides if a file is a directory or a regular file and invokes the
     * designated method for creating big pictures recursively.
     * 
     * @param file
     */
    private void createBigPictureRecursively0(File file) {
        if (file.isDirectory()) {
            createBigPictureRecursivelyForDirectory(file);
            LOG.info("now in   directory: {}", file.getParent());
            
        } else {
            createBigPictureRecursivelyForFile(file);
        }
    }

    /**
     * crawl into a directory recursively and invoke pig-picture process for all
     * children. during recursively creating thumbnails, files of type directory
     * will be handled by this method.
     * 
     * @param directory
     *            the directory to crawl into
     */
    private void createBigPictureRecursivelyForDirectory(File directory) {
        if ( ! directory.isDirectory() || ! directory.exists()) {
            throw new IllegalArgumentException("no such directory: "+directory);
        }
        
        LOG.info("entering directory: {}", directory);
        File[] children = directory.listFiles();
        Arrays.sort(children, FILE_CMP);
        
        for (int i = 0; i < children.length; i++) {
            File f = children[i];
            createBigPictureRecursively0(f);
        }
    
        LOG.info("exiting  directory: {}", directory);
    }

    /**
     * the file will be parsed and a big picture will be created from it.<br>
     * then the thumbnails will be created in a temp dir.<br>
     * the thumbnails will be merged to a big picture and then deleted.<br>
     * if the file argument
     * 
     * <ul>
     * <li>is not accepted by the current videoFileFilter</li>
     * <li>already has a thumbnail picture</li>
     * <li>an error occured while parsing the video metadata for this file</li>
     * </ul>
     * 
     * this method will do nothing.
     * 
     * @param file
     *            the file to create the big pic for.
     * @return true if, and only if a new big picture file was successfully
     *         created
     */
    private boolean createBigPictureRecursivelyForFile(File file) {
        if ( ! file.isFile() || ! file.exists()) {
            throw new IllegalArgumentException("not a regular file: "+file);
        }
        
        String fileName = file.getName();
        StringBuilder bui = new StringBuilder();
        bui.append("  handling file: ").append(fileName).append(" ... ");
        
        for (int i = fileName.length(); i++ < 60; bui.append(' '));
        
        if ( ! videoFileFilter.accept(file)) {
            LOG.info(bui.append("        SKIP. (filtered)").toString());
            statistics.incrementSkippedBecauseFiltered();
            return false;
        }
        
        VideoFile v = new VideoFile(file);
        setVideoFile(v);
        File bigPicFile = calculateBigPictureTargetFile();  
        
        if (bigPicFile.exists()) {
            LOG.info(bui.append("        SKIP. (already has big pic)").toString());
            statistics.incrementSkippedBecauseExistingTarget();
            return false;
        }
        
        File bigPicParent = bigPicFile.getParentFile();
        
        if ( ! bigPicParent.exists()) {
            bigPicParent.mkdirs();
        }
    
        LOG.info(bui.toString());
        
        try {
            getVideoFile().parse(videoFileParser);
            
        } catch (Exception e) {
            setVideoFile(null);
            LOG.error("    could not determine video-metadata for: {}", file);
            LOG.trace("    stacktrace:", e);
            LOG.error("  FAIL, handling file: {}"+NL, file);
            return false;
        }
        
        try {
            boolean ok = createBigPicture(bigPictureRows, bigPictureCols, bigPicFile);
            
            if ( ! ok) {
                LOG.error("  FAIL, handling file: {}"+NL, file);
                return false;
            }
    
        } catch (Exception e) {
            LOG.error("    could not create big picture for: {}", file);
            LOG.trace("    stacktrace:", e);
            LOG.error("  FAIL, handling file: {}"+NL, file);
            return false;
        }
        
        LOG.info("  OK, handled file: {}"+NL, file);
        return true;
    }
    
    


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VideoThumbnailer_4_0_5_1_1ubuntu1_OLD [file=");
        
        if (videoFile != null && videoFile.getFile() != null)
            builder.append(videoFile.getFile().getName());
        else 
            builder.append("<NULL>");
        
        builder.append(", bigPic r*c=");
        builder.append(bigPictureRows);
        builder.append("*");
        builder.append(bigPictureCols);
        builder.append(", thumbs: h*w=");
        builder.append(preferredHeight);
        builder.append("*");
        builder.append(preferredWidth);
        builder.append(", videoRootDir=");
        builder.append(videoRootDir.getName());
        builder.append("]");
        return builder.toString();
    }
    

    
    /**
     * calculates the path to a temporary dir where thumbnails of the current
     * videofiles will be created before merging to a big picture from them.
     * 
     * @return a directory where thumbnails will be cached
     */
    protected File calculateThumbnailTempDir() {
        StringBuilder b = new StringBuilder();
        b.append(TMP_DIR);
        b.append(FILE_SEP);
        b.append("thumbnails-tempdir-");
        b.append(videoFile.getFile().getName());
        return new File(b.toString());
    }
    
    /**
     * this will create the temporary targetdir, if not already existing. if
     * existing, all data will be deleted in the directory.
     * 
     * @param targetDir
     *            the directory used to temporarily cache the thumbnails.
     */
    protected void createOrWipeTempThumbnailDir(File targetDir) {
        if ( ! targetDir.exists()) {
            targetDir.mkdirs();
            
        } else {
            File[] subfiles = targetDir.listFiles();
            
            if (subfiles.length > 0) {
                LOG.warn("          TARGETDIR NOT EMPTY! sweeping {} files from: {}", subfiles.length, targetDir);
                
                for (int i = 0; 
                     i < subfiles.length; 
                     subfiles[i++].delete());
            }
        }      
    }
    
    /**
     * calculates the path of the pig picture file for the current video file
     * @return the path of the pig picture file
     */
    protected File calculateBigPictureTargetFile() {
        File vid = videoFile.getFile();
        String path = targetPathGenerator.getTargetFile(vid);
        return new File(path);
    }

    public VideoFile getVideoFile() {
        return videoFile;
    }

    public File getVideoRootDir() {
        return videoRootDir;
    }

    public void setVideoRootDir(File dir) {
        videoRootDir = dir;
    }

    public void setVideoFile(VideoFile vf) {
        videoFile = vf;
    }

    public int getPreferredWidth() {
        return preferredWidth;
    }

    public int getPreferredHeight() {
        return preferredHeight;
    }

    public void setPreferredWidth(int w) {
        LOG.debug("  picture width: '{}'", w);
        preferredWidth = w;
    }

    public void setPreferredHeight(int h) {
        LOG.debug("  picture height: '{}'", h);
        preferredHeight = h;
    }

    public void setPathCalculatingAlgorithm(IPathCalculator pc) {
        //FIXME: store prefix and suffix of targetPathGenerator elsewhere, they will be nuked here!:
        LOG.debug("  big picture path algorithm: '{}'", pc.getClass());
        targetPathGenerator = pc;
    }

    public int getBigPictureCols() {
        return bigPictureCols;
    }

    public int getBigPictureRows() {
        return bigPictureRows;
    }

    public void setBigPictureCols(int c) {
        LOG.debug("  big picture columns: '{}'", c);
        bigPictureCols = c;
    }

    public void setBigPictureRows(int r) {
        LOG.debug("  big picture rows: '{}'", r);
        bigPictureRows = r;
    }

    public void setBigPictureTargetRoot(File r) {
        if ( ! (targetPathGenerator instanceof FileHierarchyCopying)) { // XXX
            throw new IllegalStateException("you must not set a setVideoRootDir when "+
                                            FileHierarchyCopying.class.getSimpleName()+
                                            " is not the strategy to calculate target file names!");
        } 
            
        FileHierarchyCopying fhc = (FileHierarchyCopying) targetPathGenerator;
        fhc.setTargetRootDirFile(r);
    }

    public void setBigPicturePrefix(String p) {
        LOG.debug("  big picture prefix: '{}'", p);
        targetPathGenerator.setFileNamePrefix(p);
    }

    public FileFilter getFileFilter() {
        return videoFileFilter;
    }

    public void setFileFilter(FileFilter e) {
        videoFileFilter = e;
    }

}