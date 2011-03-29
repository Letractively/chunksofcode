package com.myapp.videotools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;



public interface IVideoThumbnailer {

    public static final int DEFAULT_THUMB_WIDTH = 270;
    public static final int DEFAULT_THUMB_HEIGHT = 220;
    public static final int DEFAULT_BIG_PIC_COLS = 3;
    public static final int DEFAULT_BIG_PIC_ROWS = 4;

    void createBigPictureRecursively(String folder);

    void createBigPictureRecursively();

    boolean createBigPicture(int rows,
                             int cols,
                             File out,
                             int width,
                             int height) throws IOException;

    List<File> createThumbnailSeries(int count,
                                     int width,
                                     int height,
                                     File targetDir) throws IOException;

    List<File> createThumbnailSeries(int amount, File targetDir) throws IOException;

    boolean createBigPicture(File out) throws IOException;

    boolean createBigPicture(int rows, int cols, File out) throws IOException;

    VideoFile getVideoFile();

    File getVideoRootDir();

    void setVideoRootDir(File dir);

    void setVideoFile(VideoFile vf);

    int getPreferredWidth();

    int getPreferredHeight();

    void setPreferredWidth(int w);

    void setPreferredHeight(int h);

    void setPathCalculatingAlgorithm(IPathCalculator pc);

    int getBigPictureCols();

    int getBigPictureRows();

    void setBigPictureCols(int c);

    void setBigPictureRows(int r);

    void setBigPictureTargetRoot(File r);

    void setBigPicturePrefix(String p);

    FileFilter getFileFilter();

    void setFileFilter(FileFilter e);

    /**
     * @param timeOffset
     *            seconds from start
     * @return
     * @throws IOException
     */
    void captureImage(double timeOffset, File out) throws IOException;
    
    /**
     * @param timeOffset
     *            seconds from start
     * @return
     * @throws IOException
     */
    void captureSingleImage(double timeOffset, 
                            int pWidth, 
                            int pHeight, 
                            File out) throws IOException;

    /**
     * @param timeOffset seconds from start
     * @return
     * @throws IOException
     */
    BufferedImage captureImage(double timeOffset) throws IOException;


    /**
     * @param timeOffset
     *            seconds from start
     * @return
     * @throws IOException
     */
    BufferedImage captureImage(double timeOffset, 
                               int pWidth, 
                               int pHeight) throws IOException;
    
        
}
