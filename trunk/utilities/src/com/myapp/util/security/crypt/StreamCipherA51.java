package com.myapp.util.security.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andre
 */
public final class StreamCipherA51 {

private ShiftRegister[] registers;

    public StreamCipherA51() {
        registers = new ShiftRegister[]{
                    new ShiftRegister(19, new int[]{18, 17, 16, 13}, 8),
                    new ShiftRegister(22, new int[]{21, 20}, 10),
                    new ShiftRegister(23, new int[]{22, 21, 20, 7}, 10)
                };
    }
    
    public void clearAll() {
        for (ShiftRegister shiftRegister : registers)
            shiftRegister.clear();
    }

    @SuppressWarnings("unused")
    private synchronized void init(byte[] cipherPassword) {
        eachByteOfPassword:
            for (int i = 0; i < cipherPassword.length; i++) {
        
                Bit[] bytesBits = CryptUtils.byteToBits(cipherPassword[i]);
        
        eachBitOfPasswordByte:
                for (int j = 0; j < bytesBits.length; j++) {
        
                    final Bit bit = bytesBits[j];
        
        eachRegister:
                    for (ShiftRegister register : registers)
                        register.shift(bit.xor(register.getFeedback()));
                }
            }
        
        hundredTimes:
            for (int i = 0; i < 100; i++)
                eachRegister:
                for (ShiftRegister register : registers)
                    register.shift(register.getFeedback());
    }

    /**the mayority of the registers with the same shiftcontrolflag is being shifted*/
    private Bit shiftNextBit() {
        Bit retval = null;
    
        List<ShiftRegister> maybeShifted = new ArrayList<ShiftRegister>(3);
    
        for (ShiftRegister r : registers)
            if (r.isControlFlag())
                maybeShifted.add(r);
    
        int size = maybeShifted.size();
        switch (size) {
            case 0:
            case 3: { /*shift all*/
                for (ShiftRegister r : registers)
                    if (retval == null)
                        retval = r.shift();
                    else
                        retval = retval.xor(r.shift());
                break;
            }
    
            case 1: { /*shift only others*/
                for (ShiftRegister r : registers)
                    if (!maybeShifted.contains(r))
                        if (retval == null)
                            retval = r.shift();
                        else
                            retval = retval.xor(r.shift());
                break;
            }
    
            case 2: { /*shift maybeShifted only*/
                for (ShiftRegister r : maybeShifted)
                    if (retval == null)
                        retval = r.shift();
                    else
                        retval = retval.xor(r.shift());
                break;
            }
        }
        return retval;
    }

    /**bitwise xor of plainbyte and cipherbyte*/
    public byte cipherNextByte(byte input) {
        Bit[] bits = CryptUtils.byteToBits(input);
    
        Bit[] cipherBits = new Bit[bits.length];
        for (int i = 0; i < cipherBits.length; i++)
            cipherBits[i] = shiftNextBit();
    
        for (int i = 0; i < cipherBits.length; i++)
            bits[i] = cipherBits[i].xor(bits[i]);
    
        return CryptUtils.bitsToByte(bits);
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

}
