package com.myapp.util.security.crypt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author andre
 */
public final class CryptUtils {


    private CryptUtils() {super();}


    public static OutputStream encryptOutput(final OutputStream beingWrapped, final String password) {
        return new OutputStream() {
            private StreamCipherA51 cipher;

            {
                cipher = new StreamCipherA51();
                cipher.init(password);
            }

            @Override
            public final void write(int i) throws IOException {
                byte b = cipher.cipherNextByte((byte) i);
                beingWrapped.write(b);
            }

            @Override
            public final void close() throws IOException {
                cipher.clearAll();
                cipher = null;
                super.close();
            }
        };
    }


    public static InputStream decryptInput(final InputStream beingWrapped, final String password) {
        return new InputStream() {
            private StreamCipherA51 cipher;

            {
                cipher = new StreamCipherA51();
                cipher.init(password);
            }

            @Override
            public final int read() throws IOException {
                int i = beingWrapped.read();
                byte b = cipher.cipherNextByte((byte) i);
                return new Integer(b & 0xff).intValue();
            }

            @Override
            public final void close() throws IOException {
                cipher.clearAll();
                cipher = null;
                super.close();
            }
        };
    }


    public static String byteArrToHexString(byte[] bytes) {
        StringBuilder bui = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byteToHexString(bytes[i], bui);
            bui.append('-');
        }
        bui.setLength(bui.length() - 1);
        return bui.toString();
    }



    static final byte bitsToByte(Bit[] bits) {
        byte b = 0x0;

        for (int i = 0; i < bits.length; i++)
            if (bits[i].val)
                b |= Math.round(Math.pow(2, 7 - i));

        return b;
    }

    static final Bit[] byteToBits(byte byt) {
        Bit[] bits = new Bit[8];
        for (int i = 7; i >= 0; i--) {
            bits[i] = new Bit((byt & 1) == 1);
            byt >>= 1;
        }
        return bits;
    }

    public static final String byteToHexString(byte by) {
        String byteStr2 = Integer.toHexString(by & 0xff);

        if (byteStr2.length() < 2)
            return "0" + byteStr2;

        return byteStr2;
    }

    public static String byteToBinString(byte b) {
        StringBuilder bui = new StringBuilder();
        String bin = Integer.toBinaryString(b & 0xff);
        
        if (bin.matches("\\d{1,7}")) {
            for (int i = 0, times = 8 - bin.length(); i < times; i++) {
                bui.append('0');
            }
        }
        
        bui.append(bin);
        return bui.toString();
    }

    static final String bitArrToBinString(Bit[] bits) {
        if (bits.length != 8) throw new IllegalArgumentException("length != 8");

        StringBuilder bui = new StringBuilder();
        for (int i = 0; i < bits.length; i++)
            bui.append(bits[i]);

        return bui.toString();
    }



    private static void byteToHexString(byte b, StringBuilder bui) {
        String hex = Integer.toHexString(b & 0xff);
        if (hex.length() == 1)
            bui.append('0');
        bui.append(hex);
    }

}
