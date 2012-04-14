/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.util.security.crypt2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author andre
 */
public final class StreamCipherA51 {

private final ShiftRegister[] registers;
private final List<ShiftRegister> registersList;

public StreamCipherA51() {
    registers = new ShiftRegister[]{
                new ShiftRegister(19, new int[]{18, 17, 16, 13}, 8),
                new ShiftRegister(22, new int[]{21, 20}, 10),
                new ShiftRegister(23, new int[]{22, 21, 20, 7}, 10)
            };

    registersList = Arrays.asList(registers);
}

public void clearAll() {
    for (ShiftRegister shiftRegister : registers)
        shiftRegister.clear();
}

@SuppressWarnings("unused")
private synchronized void init(byte[] cipherPassword) {
    BitSet bytesBits = null;

eachByteOfPassword:
    for (int i = 0; i < cipherPassword.length; i++) {
        bytesBits = CryptUtils.byteToBitSet(cipherPassword[i]);


eachBitOfPasswordByte:
        for (int j = 0; j < 8; j++) {
            final boolean bit = bytesBits.get(i);


eachRegister:
            for (ShiftRegister register : registers)
                register.shift(bit ^ register.getFeedBack());
        }
    }

hundredTimes:
    for (int i = 0; i < 100; i++)
        eachRegister:
        for (ShiftRegister register : registers)
            register.shift(register.getFeedBack());
}

private boolean shiftRegisters(List<ShiftRegister> regs2shift) {
    boolean firstLoop = true;
    boolean retval = false;

    for (ShiftRegister var : regs2shift)
        if (firstLoop) {
            firstLoop = false;
            retval = var.shift();
        }
        else
            retval ^= var.shift();

    return retval;
}

/**the mayority of the registers with the same shiftcontrolflag is being shifted*/
private boolean shiftNextBit() {
    List<ShiftRegister> maybeShifted = new ArrayList<ShiftRegister>(3);

    for (ShiftRegister r : registers)
        if (r.getShiftFlag())
            maybeShifted.add(r);

    int size = maybeShifted.size();
    switch (size) {

        case 0:  /*shift all*/
        case 3:  /*shift all*/
            return shiftRegisters(Arrays.asList(registers));


        case 1: { /*shift only others*/
            List<ShiftRegister> all = new ArrayList<ShiftRegister>(2);
            all.addAll(registersList);
            all.removeAll(maybeShifted);

            return shiftRegisters(all);
        }

        case 2: { /*shift maybeShifted only*/
            return shiftRegisters(maybeShifted);
        }
    }

    throw new RuntimeException("number of registers bigger than 3 !");
}

private BitSet tempBitSet1 = null;
private BitSet tempBitSet2 = null;

/**bitwise xor of plainbyte and cipherbyte*/
byte cipherNextByte(byte input) {
    tempBitSet1 = CryptUtils.byteToBitSet(input);
    tempBitSet2 = new BitSet();

    for (int i = 0; i < 8; i++)
        tempBitSet2.set(i, shiftNextBit());

    for (int i = 0; i < 8; i++)
        tempBitSet1.set(i, tempBitSet2.get(i) ^ tempBitSet1.get(i));

    return CryptUtils.bitSetToByte(tempBitSet1);
}

int cipherNextInt(int input) {
    BitSet bs = CryptUtils.byteToBitSet(CryptUtils.intToByte(input));

    for (int i = 0; i < 8; i++) 
        bs.set(i, shiftNextBit() ^ bs.get(i));

    return CryptUtils.bitSetToInt(bs);
}

public void init(String password) {
    clearAll();

    MessageDigest md;
    try {/*generate a sha1-byte[] from the password to init registers with*/
        md = MessageDigest.getInstance("SHA1");
    }
    catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
    }

    byte[] registerInitBytes = md.digest(password.getBytes());
    init(registerInitBytes);
}

public static void main(String... args) {

    StreamCipherA51 a51 = new StreamCipherA51();
    com.myapp.util.security.crypt.StreamCipherA51 referenecA51 = new com.myapp.util.security.crypt.StreamCipherA51();
    referenecA51.init("secr3t");

    int len = 5;

    byte[] barrOrigin = new byte[len];
    byte[] barrCryptd = new byte[len];
    byte[] barrEncryp = new byte[len];
    byte[] barrCryptd2 = new byte[len];
    byte[] barrEncryp2 = new byte[len];

    new Random().nextBytes(barrOrigin);


    System.out.println("barrOrigin :" +
                       CryptUtils.byteArrToBinString(barrOrigin));





    a51.init("secr3t");
    for (int i = 0; i < len; i++)
        barrCryptd[i] = a51.cipherNextByte(barrOrigin[i]);

    System.out.println("barrCryptd :" +
                       CryptUtils.byteArrToBinString(barrCryptd));


    a51.init("secr3t");
    for (int i = 0; i < len; i++)
        barrEncryp[i] = a51.cipherNextByte(barrCryptd[i]);

    System.out.println("barrEncryp :" +
                       CryptUtils.byteArrToBinString(barrEncryp));




    System.out.println("----------------------------------");


    System.out.println("barrOrigin :" +
                       CryptUtils.byteArrToBinString(barrOrigin));

    referenecA51.init("secr3t");
    for (int i = 0; i < len; i++)
        barrCryptd2[i] = referenecA51.cipherNextByte(barrOrigin[i]);

    System.out.println("barrCryptd2:" +
                       CryptUtils.byteArrToBinString(barrCryptd2));


    referenecA51.init("secr3t");
    for (int i = 0; i < len; i++)
        barrEncryp2[i] = referenecA51.cipherNextByte(barrCryptd2[i]);

    System.out.println("barrEncryp2:" +
                       CryptUtils.byteArrToBinString(barrEncryp2));
}

}
