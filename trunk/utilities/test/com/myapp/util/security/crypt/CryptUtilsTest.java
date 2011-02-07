/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.security.crypt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.myapp.util.timedate.StopWatch;


import static com.myapp.util.security.crypt.CryptUtils.*;
/**
 * 
 * @author andre
 */
public class CryptUtilsTest {

    private static final boolean verbose = false;
    private static byte[] allPossibleBytes = new byte[256];

    static {
        for (int i = 0; i < allPossibleBytes.length; i++)
            allPossibleBytes[i] = (byte) (Byte.MIN_VALUE + i);
    }

    public CryptUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of encryptOutput method and decryptInput method, of class
     * CryptUtils.
     */
    @Test
    public void testEncryptOutputAndInput() throws IOException, ClassNotFoundException {
        log("encryptOutputAndInput");

        ArrayList<Serializable> list = new ArrayList<Serializable>();
        for (int i = 0; i < allPossibleBytes.length; i++)
            list.add(allPossibleBytes[i]);

        Serializable[] sampleObjects = new Serializable[] { new String(
                "some text in a string object..."/* , new Integer(12345), list */) };

        final String password = "s3cret";
        ByteArrayOutputStream baos;
        byte[] cryptedTestData;

        for (int i = 0; i < sampleObjects.length; i++) {
            Object testData = sampleObjects[i];

            baos = new ByteArrayOutputStream();
            byte[] testDateBytes = ((String) testData).getBytes();
            
            log("\ntestdata        : type='"
                    + testData.getClass() + "', value='" + testData + "'");
            log("testdata        : bytes=[" + byteArrToHexString(testDateBytes) + "]");

            /* xor serialized object into bytearrayoutputstream */
            Bit.setCreationCounter(0);
            OutputStream cipherOut = encryptOutput(baos, password);
            ObjectOutputStream oos = new ObjectOutputStream(cipherOut);
            oos.writeObject(testData);
            oos.flush();
            oos.close();
            log("   " + Bit.getCreationCounter() + " Bit instances created.");

            cryptedTestData = baos.toByteArray();
            log("cryptedTestData : " + byteArrToHexString(cryptedTestData));

            /*
             * note that we may use the baos again since baos.toByteArray() is
             * copying the bytes into a new independent byte[]
             */
            baos = new ByteArrayOutputStream(256);

            /* read back object using the same password */
            Bit.setCreationCounter(0);
            InputStream inStream = new ByteArrayInputStream(cryptedTestData);
            InputStream decipherIn = decryptInput(inStream, password);
            ObjectInputStream ois = new ObjectInputStream(decipherIn);
            
            Object obj = ois.readObject();
            log("   " + Bit.getCreationCounter() + " Bit instances created.");
            log("decrypted obj   :" + byteArrToHexString(((String) obj).getBytes()));
            log("decrypted obj   : type='" + obj.getClass() + "', value='" + obj + "'");

            assertEquals(testData, obj);
        }
        log("");
    }

    @Test
    public void testLargeAmountOfData() throws FileNotFoundException, IOException {
        log("largeAmountOfData");
        final String password = "secr3t";

        File origiginalFile = File.createTempFile("orig.foo.", ".bar");
        File cryptFile = File.createTempFile("crypt.foo.", ".bar");
        origiginalFile.deleteOnExit();
        cryptFile.deleteOnExit();


        final int byteCount = 4096;
        StopWatch sw = new StopWatch();

        

        // generating random test data
        byte[] originalBytes = new byte[byteCount];
        new Random().nextBytes(originalBytes);

        
        
        log("writing original data to file " + origiginalFile.getAbsolutePath() + " ...");
        Bit.setCreationCounter(0);
        sw.start();
        
        OutputStream origFos = new FileOutputStream(origiginalFile);
        origFos.write(originalBytes);
        origFos.close();

        sw.stop();
        log(" OK. [ " + byteCount + " bytes | "
                + sw.durationString() + " | "
                + ((byteCount / sw.durationSeconds()) / 1024) + " KB/sec]");
        log("   " + Bit.getCreationCounter() + " Bit instances created.");

        
        
        
        log("crypting data to file " + cryptFile.getAbsolutePath()  + " ...");
        Bit.setCreationCounter(0);
        sw.start();
        
        OutputStream cryptFos = new FileOutputStream(cryptFile);
        OutputStream cipherOut = encryptOutput(cryptFos, password);
        cipherOut.write(originalBytes);
        cipherOut.close();

        sw.stop();
        log("  OK. [ " + byteCount + " bytes | "
                + sw.durationString() + " | "
                + ((byteCount / sw.durationSeconds()) / 1024) + " KB/sec]");
        log("   " + Bit.getCreationCounter() + " Bit instances created.");

        
        
        
        log("encrypting data from file "
                + cryptFile.getAbsolutePath() + " into a byte array ...");

        byte[] decipheredBytes = new byte[byteCount];
        Bit.setCreationCounter(0);
        sw.start();
        
        FileInputStream fis = new FileInputStream(cryptFile);
        InputStream decipherInput = decryptInput(fis, password);
        int decipheredCount = decipherInput.read(decipheredBytes);
        decipherInput.close();

        sw.stop();
        log("  OK. [ " + decipheredCount + " bytes | "
                + sw.durationString() + " | "
                + ((decipheredCount / sw.durationSeconds()) / 1024) + " KB/sec]");
        log("   " + Bit.getCreationCounter() + " Bit instances created.");

        assertArrayEquals(originalBytes, decipheredBytes);
    }
    @Test
    public void testLargeAmountOfData2() throws FileNotFoundException, IOException {
        log("largeAmountOfData");
        final String password = "secr3t";
        final int byteCount = 1024 * 256;
        StopWatch sw = new StopWatch();
        
        byte[] originalByteArray = new byte[byteCount]; 
        byte[] cryptedByteArray;
        byte[] encryptedByteArray = new byte[byteCount];

        
        // generating random test data       
        new Random().nextBytes(originalByteArray);

        

        
        log("crypting data to cryptedByteArray ...");
        Bit.setCreationCounter(0);
        sw.start();
        
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
        OutputStream cryptedOutputStream = encryptOutput(bytesOutputStream, password);
        cryptedOutputStream.write(originalByteArray);
        cryptedOutputStream.close();

        sw.stop();
        log("  OK. [ " + byteCount + " bytes | "
                + sw.durationString() + " | "
                + ((byteCount / sw.durationSeconds()) / 1024) + " KB/sec]");
        log("   " + Bit.getCreationCounter() + " Bit instances created.");

        cryptedByteArray = bytesOutputStream.toByteArray();
        
        
        
        
        log("encrypting data from cryptedByteArray into encryptedByteArray ...");

        Bit.setCreationCounter(0);
        sw.start();
        
        InputStream bytesInputStream = new ByteArrayInputStream(cryptedByteArray);
        
        InputStream decipheredInput = decryptInput(bytesInputStream, password);
        int decipheredCount = decipheredInput.read(encryptedByteArray);
        decipheredInput.close();

        sw.stop();
        log("  OK. [ " + decipheredCount + " bytes | "
                + sw.durationString() + " | "
                + ((decipheredCount / sw.durationSeconds()) / 1024) + " KB/sec]");
        log("   " + Bit.getCreationCounter() + " Bit instances created.");

        assertArrayEquals(originalByteArray, encryptedByteArray);
    }
    
    private static void log(String s) {
        if (verbose)
            System.out.println(s);
    }

}
