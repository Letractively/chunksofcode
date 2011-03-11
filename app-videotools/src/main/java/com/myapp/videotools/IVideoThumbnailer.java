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

    public abstract void createBigPictureRecursively(String folder);

    public abstract void createBigPictureRecursively();

    public abstract boolean createBigPicture(int rows,
                                             int cols,
                                             File out,
                                             int width,
                                             int height) throws IOException;

    public abstract List<File> createThumbnailSeries(int count,
                                                     int width,
                                                     int height,
                                                     File targetDir)
            throws IOException;

    public abstract List<File> createThumbnailSeries(int amount, File targetDir)
            throws IOException;

    public abstract boolean createBigPicture(File out) throws IOException;

    public abstract boolean createBigPicture(int rows, int cols, File out)
            throws IOException;

    public abstract VideoFile getVideoFile();

    public abstract File getVideoRootDir();

    public abstract void setVideoRootDir(File dir);

    public abstract void setVideoFile(VideoFile vf);

    public abstract int getPreferredWidth();

    public abstract int getPreferredHeight();

    public abstract void setPreferredWidth(int w);

    public abstract void setPreferredHeight(int h);

    public abstract void setPathCalculatingAlgorithm(IPathCalculator pc);

    public abstract int getBigPictureCols();

    public abstract int getBigPictureRows();

    public abstract void setBigPictureCols(int c);

    public abstract void setBigPictureRows(int r);

    public abstract void setBigPictureTargetRoot(File r);

    public abstract void setBigPicturePrefix(String p);

    public abstract FileFilter getFileFilter();

    public abstract void setFileFilter(FileFilter e);

    /**
     * @param timeOffset
     *            seconds from start
     * @return
     * @throws IOException
     */
    public void captureImage(double timeOffset, File out) throws IOException;
    
    /**
     * @param timeOffset
     *            seconds from start
     * @return
     * @throws IOException
     */
    public void captureSingleImage(double timeOffset, 
                                   int pWidth, 
                                   int pHeight, 
                                   File out) throws IOException;

    /**
     * @param timeOffset seconds from start
     * @return
     * @throws IOException
     */
    public BufferedImage captureImage(double timeOffset) throws IOException;


    /**
     * @param timeOffset
     *            seconds from start
     * @return
     * @throws IOException
     */
    public BufferedImage captureImage(double timeOffset, 
                                      int pWidth, 
                                      int pHeight) throws IOException;
        
        
}
