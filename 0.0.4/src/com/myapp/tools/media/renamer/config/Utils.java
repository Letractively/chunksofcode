package com.myapp.tools.media.renamer.config;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * encapluates common methods
 * 
 * @author andre
 * 
 */
public final class Utils {

    private static final int CARRIAGE_RETURN = 0xD;
    private static final int LINE_FEED = 0xA;
    private static final int NULL_BYTE = 0x0;
    
    /**
     * no instance needed.
     */
    private Utils() {}
    
    /**
     * reads the next line from an given inputstream. lines are be separated as
     * specified in the http-protocol. (0x0, '\n' or '\r' or "\r\n" (god damn
     * it)
     * 
     * @param in
     *            the inputstream to read from
     * @return the next line
     * @throws IOException
     */
    public static String readLine(BufferedInputStream in) throws IOException {
        assert in != null && in.markSupported() : in;

        StringBuilder bui = new StringBuilder();
        int b;

        /*return null if byte represents no data*/
        in.mark(1);
        if (in.read() == -1) return null;

        in.reset();

        /*read bytes until line terminator*/
        while ((b = in.read()) >= 0)
            if (b == CARRIAGE_RETURN || b == LINE_FEED || b == NULL_BYTE)
                break;
            else
                bui.append((char) b);

        /*check if a '\r' follows after  a '\n' char*/
        if (b == CARRIAGE_RETURN) {
            in.mark(1);
            if (in.read() != LINE_FEED)
                /*CR without following NL, jump back one byte*/
                in.reset();
        }

        return bui.toString();
    }
}
