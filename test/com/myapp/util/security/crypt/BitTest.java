/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.security.crypt;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andre
 */
public class BitTest {

    public static byte[] allPossibleBytes = new byte[256];


    static {
        for (int i = 0; i < allPossibleBytes.length; i++)
            allPossibleBytes[i] = (byte) (Byte.MIN_VALUE + i);
    }

    public BitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    static final Bit TRUE = new Bit(true);
    static final Bit FALSE = new Bit(false);
    private static final boolean verbose = false;

    /**
     * Test of xor method, of class Bit.
     */
    @Test
    public void testXor() {
        log("xor");

        Bit b;

        b = TRUE.xor(TRUE);
        assertEquals(FALSE, b);

        b = FALSE.xor(FALSE);
        assertEquals(FALSE, b);

        b = TRUE.xor(FALSE);
        assertEquals(TRUE, b);

        b = FALSE.xor(TRUE);
        assertEquals(TRUE, b);

    }

    /**
     * Test of and method, of class Bit.
     */
    @Test
    public void testAnd() {
        log("and");
        Bit b;

        b = TRUE.and(TRUE);
        assertEquals(TRUE, b);

        b = FALSE.and(FALSE);
        assertEquals(FALSE, b);

        b = TRUE.and(FALSE);
        assertEquals(FALSE, b);

        b = FALSE.and(TRUE);
        assertEquals(FALSE, b);
    }

    /**
     * Test of or method, of class Bit.
     */
    @Test
    public void testOr() {
        log("or");
        Bit b;

        b = TRUE.or(TRUE);
        assertEquals(TRUE, b);

        b = FALSE.or(FALSE);
        assertEquals(FALSE, b);

        b = TRUE.or(FALSE);
        assertEquals(TRUE, b);

        b = FALSE.or(TRUE);
        assertEquals(TRUE, b);
    }

    /**
     * Test of not method, of class Bit.
     */
    @Test
    public void testNot() {
        log("not");

        assertTrue(TRUE.not().equals(FALSE));
        assertTrue(FALSE.not().equals(TRUE));
    }

    /**
     * Test of nand method, of class Bit.
     */
    @Test
    public void testNand() {
        log("nand");
        Bit b;

        b = TRUE.nand(TRUE);
        assertEquals(FALSE, b);

        b = FALSE.nand(FALSE);
        assertEquals(TRUE, b);

        b = TRUE.nand(FALSE);
        assertEquals(TRUE, b);

        b = FALSE.nand(TRUE);
        assertEquals(TRUE, b);
    }

    /**
     * Test of nor method, of class Bit.
     */
    @Test
    public void testNor() {
        log("nor");
        Bit b;

        b = TRUE.nor(TRUE);
        assertEquals(FALSE, b);

        b = FALSE.nor(FALSE);
        assertEquals(TRUE, b);

        b = TRUE.nor(FALSE);
        assertEquals(FALSE, b);

        b = FALSE.nor(TRUE);
        assertEquals(FALSE, b);
    }

    /**
     * Test of converting bits and bytes and strings.
     */
    @Test
    public void testConvertingTypes() {
        log("testConvertingTypes");

        byte originalByte;
        byte backConvertedByte;

        String originalByteString;

        for (byte b : allPossibleBytes) {
            originalByte = b;

            originalByteString = Integer.toBinaryString(b & 0xff);
            for (int i = originalByteString.length(); i <= 7; i++)
                originalByteString = "0" + originalByteString;


            Bit[] bitsOfOriginalByte = CryptUtils.byteToBits(b);
            String bitsOfOriginalByteString = CryptUtils.bitArrToBinString(bitsOfOriginalByte);


            assertEquals(bitsOfOriginalByteString, originalByteString);



            backConvertedByte = CryptUtils.bitsToByte(bitsOfOriginalByte);
            String backConvertedByteString = Integer.toBinaryString(backConvertedByte & 0xff);
            for (int i = backConvertedByteString.length(); i <= 7; i++)
                backConvertedByteString = "0" + backConvertedByteString;


            assertEquals(
                    "originalByte != backConvertedByte " +
                    "[originalByte=" + originalByte + "] " +
                    "[backConvertedByte=" + backConvertedByte + "]",
                    originalByte,
                    backConvertedByte);
        }
    }

    /**
     * Test of main method, of class Bit.
     */
//    @Test
    public void testConvertingTypesVerbose() {
        log("complex");
        StringBuilder bui;

        for (byte b : allPossibleBytes) {

            final byte byteBefore = b;

            bui = new StringBuilder();

            String byteStr = Integer.toBinaryString(b & 0xff);
            for (int i = byteStr.length(); i <= 7; i++)
                byteStr = "0" + byteStr;

            bui.append("----------\n");
            bui.append("new byte  [bin=");
            bui.append(byteStr);
            bui.append("] [hex=");
            bui.append(Integer.toHexString(b & 0xff));
            bui.append("] [dec=");
            bui.append(new Integer(b & 0xff));
            bui.append("]\n");

            Bit[] bits = CryptUtils.byteToBits(b);
            bui.append("converted [bin=");

            String bitsStr = CryptUtils.bitArrToBinString(bits);
            bui.append(bitsStr);

            if (!bitsStr.equals(byteStr))
                throw new RuntimeException("! bitsStr.equals(byteStr) -->[bitsStr=" + bitsStr + "] [byteStr=" + byteStr + "]");

            bui.append("]\nconv.back [bin=");
            byte convBack = CryptUtils.bitsToByte(bits);

            String byteStr2 = Integer.toBinaryString(convBack & 0xff);
            for (int i = byteStr2.length(); i <= 7; i++)
                byteStr2 = "0" + byteStr2;
            bui.append(byteStr2);

            bui.append("] [hex=");
            bui.append(Integer.toHexString(b & 0xff));
            bui.append("] [dec=");
            bui.append(new Integer(b & 0xff));
            bui.append("]\n");

            if (byteBefore != convBack)
                throw new RuntimeException("byteBefore != convBack [before=" + byteBefore + "] [after=" + convBack + "]");

            log(bui.toString());
        }
    }
    
    private static void log(String s) {
        if (verbose)
            System.out.println(s);
    }
}
