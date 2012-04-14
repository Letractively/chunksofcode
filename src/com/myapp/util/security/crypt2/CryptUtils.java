/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.security.crypt2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Random;

/**
 *
 * @author andre
 */
public final class CryptUtils {

public static OutputStream encryptOutput(final OutputStream beingWrapped,
                                         final String password) {

    return new OutputStream() {

    StreamCipherA51 cipher = new StreamCipherA51();


    {
        cipher.init(password);
    }

    @Override
    public void write(int b) throws IOException {
        beingWrapped.write(cipher.cipherNextByte((byte) (b & 0xff)));
    }

    @Override
    public void close() throws IOException {
        cipher.clearAll();
        cipher = null;
        super.close();
    }

    };
}

public static InputStream decryptInput(final InputStream beingWrapped,
                                       final String password) {
    return new InputStream() {

    StreamCipherA51 cipher = new StreamCipherA51();


    {
        cipher.init(password);
    }

    @Override
    public int read() throws IOException {
        int nextInt = beingWrapped.read();
        if(nextInt ==-1)
            return -1;

        byte byt = cipher.cipherNextByte((byte)nextInt);

        return byt;
    }

    @Override
    public void close() throws IOException {
        cipher.clearAll();
        cipher = null;
        super.close();
    }

    };
}

/*package visible:*/
static final byte bitSetToByte(BitSet bits) {
    return intToByte(bitSetToInt(bits));
}

static final int bitSetToInt(BitSet bits) {
    int by = 0x00;

    if(bits.get(7))
        by +=1;
    if(bits.get(6))
        by +=2;
    if(bits.get(5))
        by +=4;
    if(bits.get(4))
        by +=8;

    if(bits.get(3))
        by +=16;
    if(bits.get(2))
        by +=32;
    if(bits.get(1))
        by +=64;
    if(bits.get(0))
        by +=128;

    return by;
}

static String byteArrToBinString(byte[] bytes) {
    StringBuilder bui = new StringBuilder();
                bui.append('[');
    int lastBytePos = bytes.length - 1;

    for (int i = 0; i < bytes.length; i++) {
        int b = bytes[i] & 0xff;

        for (int j = 0; j < 8; j++) {
            int x = b & 1;
            bui.append(x);
            b >>= 1;

            if (j == 3)
                bui.append(' ');
        }


        if (i != lastBytePos) {
            bui.append(',');
            bui.append(' ');
        }
    }
                bui.append(']');

    return bui.toString();
}

static final BitSet byteToBitSet(byte byt) {
    BitSet bits = new BitSet(8);

    for (int i = 7; i >= 0; i--) {
        if (0x1 == (byt & 0x1))
            bits.set(i);
        byt >>= 1;
    }
    return bits;
}

static final String byteToHexString(byte by) {
    String byteStr2 = Integer.toHexString(by & 0xff);

    if (byteStr2.length() < 2)
        return "0" + byteStr2;

    return byteStr2;
}

private static String bitSetToBinString(BitSet s2) {
    StringBuilder bui = new StringBuilder();

    for(int i = 0; i<8;i++)
        if(s2.get(i))
            bui.insert(0, '1');
        else
            bui.insert(0, '0');

    return bui.toString();
}

private static void byteToHexString(byte b, StringBuilder bui) {
    String hex = Integer.toHexString(b & 0xff);

    if (hex.length() == 1)
        bui.append('0');

    bui.append(hex);
}

public static String byteArrToHexString(byte[] bytes) {
    StringBuilder bui = new StringBuilder();

    for (int i = 0; i < bytes.length; i++) {
        byteToHexString(bytes[i], bui);

        if (i != bytes.length - 1)
            bui.append('-');
    }

    return bui.toString();
}

public static void main(String... args) {

    byte[] randombytes = new byte[15];
    new Random().nextBytes(randombytes);

    for (int i = 0; i < randombytes.length; i++) {
        byte input = randombytes[i];

        String before = CryptUtils.byteArrToBinString(new byte[]{input});
        BitSet s2 = CryptUtils.byteToBitSet(input);
        String middle = CryptUtils.bitSetToBinString(s2);
        byte byte2 = CryptUtils.bitSetToByte(s2);
        String after = CryptUtils.byteArrToBinString(new byte[]{byte2});

        System.out.println("before       "+before);
        System.out.println("middle       "+middle);
        System.out.println("after        "+after+"\n-----------------------------");
    } 
}

public static int byteToInt(byte b) {
    return b & 0xff;
}

public static byte intToByte(int i) {
    return (byte) i;
}

}
