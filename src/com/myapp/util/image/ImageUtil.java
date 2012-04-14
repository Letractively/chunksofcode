package com.myapp.util.image;

import java.awt.Dimension;

public class ImageUtil {
    
    /**
     * returns a dimension where width and height are inside the bounds of the
     * maxWidth and maxHeight parameters<br>
     * and the aspect ratio is the same as sourceWidth and sourceHeight.
     * 
     * @param maxWidth
     *            the maximum width of the target dimension
     * @param maxHeight
     *            the maximum height of the target dimension
     * @param sourceWidth
     *            the widht of the source image
     * @param sourceHeight
     *            the height of the source image
     * @return the target dimension that will be the greatest dimension that
     *         <ul>
     *         <li>will keep the ratio of the source images dimension</li>
     *         <li>fits in the bounds of the maximum dimension given</li>
     *         </ul>
     */
    public static Dimension scaleDimensions(int maxWidth,
                                     int maxHeight,
                                     int sourceWidth,
                                     int sourceHeight) {
        float vidH, vidW, maxH, maxW;
        vidW = Integer.valueOf(sourceWidth).floatValue();
        vidH = Integer.valueOf(sourceHeight).floatValue();
        maxW = Integer.valueOf(maxWidth).floatValue();
        maxH = Integer.valueOf(maxHeight).floatValue();
        
//        System.out.println("initial height / width ratio: " + vidH / vidW);
//        System.out.println();
//        System.out.println("vidW "+vidW+", vidH "+vidH);
//        System.out.println("maxW "+maxW+", maxH "+maxH);
//        System.out.println();

        float finalW, finalH;
        finalH = vidH / vidW * maxW;
        finalW = maxW;
        
        if (finalH > maxH) {
            float factor = maxH / finalH;
            finalH = maxH;
            finalW = finalW * factor;
        }

        int w = Float.valueOf(finalW).intValue();
        int h = Float.valueOf(finalH).intValue();
        
        Dimension d = new Dimension(w,h);

//        System.out.println();
//        System.out.println("finalW "+finalW+", finalH "+finalH);
//        System.out.println("final height / width ratio: " + finalH / finalW);
//        System.out.println();
//        System.out.println(d);
        
        return d;
    }

    
    
    // test scaleDimensions
    public static void main(String[] args) {
        int maxWidth = 250;
        int maxHeight = 200;
        int videoWidth = 320;
        int videoHeight = 240;
        Dimension d = scaleDimensions(maxWidth,
                                      maxHeight,
                                      videoWidth,
                                      videoHeight);
        System.out.println(d);
    }
}
