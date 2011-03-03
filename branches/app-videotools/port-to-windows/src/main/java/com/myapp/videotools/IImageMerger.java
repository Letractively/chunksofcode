package com.myapp.videotools;

import java.io.File;
import java.io.IOException;
import java.util.List;



public interface IImageMerger {

    /**
     * arranges a bunch of images in a grid layout and creates a image file from
     * them, titled with metadata obtained from a videofile.
     * 
     * @param rows
     *            the desired rows of the grid
     * @param cols
     *            the desired cols of the grid
     * @param out
     *            the target file where the merged image will be saved
     * @param tileWidth
     *            the height of each grid element
     * @param tileHeight
     *            the height of each grid element
     * @param thumbs
     *            the list of files to be merged
     * @param videoFile
     *            the videofile to extract the title, and duration and size. may
     *            be null if you just want to merge some images
     * @throws IOException
     */
    public abstract void mergeImages(int rows,
                                     int cols,
                                     File out,
                                     int tileWidth,
                                     int tileHeight,
                                     List<File> thumbs,
                                     VideoFile videoFile) throws IOException;
}