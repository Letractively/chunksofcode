package com.myapp.tools.media.renamer.view.swing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import com.myapp.tools.media.renamer.model.IRenamable;

/**
 * creates and contains thumbnail images mapped to strings (path of image file)
 * 
 * @author andre
 * 
 */
/**
 * @author andre
 *
 */
class ThumbnailStore {
    
    private static final ExecutorService POOL = Executors.newFixedThreadPool(1);
    
    private final Map<String, Image> cache;
    private final Set<String> nonImageFiles;
    private final int width, height;
    private final int maxSize;

    /**
     * creates a new thumbnailstore with a given maximum size which will create
     * tumbnails with the specified dimensions.
     * 
     * @param thumbNailWidth
     *            the width of the cached thumbnails
     * @param thumbNailHeight
     *            the height of the cached thumbnails
     * @param maxCacheSize
     *            the maximum elements of the cache
     */
    ThumbnailStore(int thumbNailWidth,
                   int thumbNailHeight,
                   int maxCacheSize) {
        cache = new Hashtable<String, Image>();
        nonImageFiles = new HashSet<String>();
        width = thumbNailWidth;
        height = thumbNailHeight;
        maxSize = maxCacheSize;
    }
    
    
    /**
     * get the cached image for the given image path if exists. if not, the
     * image will be cached and returned. if the file contains no image data,
     * null will be returned.
     * 
     * @param filePath
     *            the path to the image
     * @return the cached image, or null if not a valid image file.
     */
    public Image getCachedImage(String filePath) {
        synchronized (cache) {
            Image img = cache.get(filePath);
            
            if (img == null) return cache(new File(filePath));
            
            return img;
        }
    }

    /**
     * creates a thumbnail image and stores it into the cache, if not full.
     * 
     * @param f
     *            the file to cache
     * @return the cached thumbnail. a not cached thumbnail if full. null if not
     *         a valid image.
     */
    private Image cache(File f) {
        synchronized (cache) {
            final String path = f.getAbsolutePath();
            if (nonImageFiles.contains(path)) return null; //invalid image file
            
            Image img = cache.get(path);
            if (img != null) return img;                   //cache hit !
            
            try {
                img = scaleImage(path, width, height);
                
                if (img == null) {
                    nonImageFiles.add(path);               //mark as invalid
                    return null;
                }
                
                if (cache.size() < maxSize) cache.put(path, img); //add to cache
                
//                System.out.println("thumbnailstore stored img: " + path);
                return img;
                
            } catch (Exception e) {
                e.printStackTrace();
                assert false;
                return null;
            }
            
        }
    }
    
    /**
     * drops the cache.
     */
    void dropCache() {
        synchronized (cache) {
            boolean moreThanTenItems = cache.size() > 10;
            
            cache.clear();
            nonImageFiles.clear();

            if (moreThanTenItems) // performance bugfix
                System.gc();
        }
    }

    /**
     * creates and caches thumbnails from a list of files in the background.
     * 
     * @param list
     *            the files to cache.
     */
    public void cacheImagesFromRenamables(List<IRenamable> list) {
        synchronized (cache) {
            for (final IRenamable renamable : list) {
                final File f = renamable.getSourceObject();

                POOL.execute(new Runnable() {
                    public void run() {
                        cache(f);
                    }
                });
            }
        }
    }
    
    
    
    // scaling utils ************************************************

    
    /**
     * creates a scaled image from a picture file with the given dimensions.
     * 
     * @param pathToFile
     *            the path to the file containing image data
     * @param maxWidth
     *            the width of the returned img
     * @param maxHeight
     *            the height of the returned img
     * @return a scaled image from the given file
     */
    static Image scaleImage(String pathToFile, int maxWidth, int maxHeight) {
        BufferedImage src = null;
        
        try {
            src = ImageIO.read(new File(pathToFile));
            
        } catch (IOException e) {
            // this may happen when the user exits the vm at the right moment ;)
            return null;
        }
        
        if (src == null) return null;
        
        Dimension dim = getScaledDimension(src.getWidth(),
                                           src.getHeight(),
                                           maxWidth,
                                           maxHeight);
        BufferedImage dest = new BufferedImage(dim.width,
                                               dim.height,
                                               src.getType());        

        Graphics2D g = dest.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 
                    0, 0, dim.width, dim.height, 
                    0, 0, src.getWidth(), src.getHeight(), 
                    null);
        g.dispose();
        
        assert dest.getWidth() == dim.width && dest.getHeight() == dim.height;
        
        return dest;
    }

    /**
     * calculates the dimension of an image that will be scaled without changing
     * its width and height relation. the dimension will fit in the max bounds,
     * and be as big as possible.
     * 
     * @param origW
     *            the width of the original image
     * @param origH
     *            the height of the original image
     * @param maxWidth
     *            the maximum width of the scaled image
     * @param maxHeight
     *            the maximum height of the scaled image
     * @return a dimension with the corrected dimensions of the image.
     */
    private static Dimension getScaledDimension(int origW,
                                                int origH, 
                                                int maxWidth, 
                                                int maxHeight) {
        double widthFactor = new Integer(maxWidth).doubleValue() / origW;
        double heightFactor = new Integer(maxHeight).doubleValue() / origH;
        
        double factor = Math.min(widthFactor, heightFactor);
        Dimension dim = new Dimension();
        
        if (heightFactor == factor)
            dim.setSize(origW * factor, maxHeight);
        else
            dim.setSize(maxWidth, origH * factor);
        
        return dim;
    }
}