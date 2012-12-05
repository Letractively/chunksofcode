package com.myapp.util.security.aes;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AesCipher {

    private static final String ALGORITHM = "AES";
    private static final int ITERATIONS = 2;
    
    private Key password;
    private final Cipher cipher;
    
    {
        try {
            cipher = Cipher.getInstance(ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AesCipher() {}
    
    public AesCipher(String keyValue) {
        setPassword(keyValue);
    }

    public void setPassword(String password) {
        if (password == null) {
            forgetPassword();
            return;
        }
        
        try { // use the 16 bit hash from md5 result as password:
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passwordBytes = password.getBytes();
            byte[] passwordHash = md.digest(passwordBytes);
            this.password = new SecretKeySpec(passwordHash, ALGORITHM);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void forgetPassword() {
        password = null;
    }
    
    public String encrypt(String plainTxt, String salt) throws Exception {
        if (password == null) {
            throw new IllegalStateException("no password was set");
        }
        String cipherTxt = plainTxt;

        synchronized (cipher) {
            cipher.init(Cipher.ENCRYPT_MODE, password);
            
            for (int i = 0; i < ITERATIONS; i++) {
                String concat = salt + cipherTxt;
                byte[] encValue = cipher.doFinal(concat.getBytes());
                cipherTxt = toBase64(encValue);
            }
        }
        
        return cipherTxt;
    }

    public String decrypt(String cipherTxt, final String salt) {
        if (password == null) {
            throw new IllegalStateException("no password was set");
        }
        
        String plainTxt = null;
        String decryptInput = cipherTxt;
        
        try {
            synchronized (cipher) {
                cipher.init(Cipher.DECRYPT_MODE, password);
                
                for (int i = 0; i < ITERATIONS; i++) {
                    byte[] decoded = fromBase64(decryptInput);
                    byte[] decryptedBytes = cipher.doFinal(decoded);
                    plainTxt = new String(decryptedBytes).substring(salt.length());
                    decryptInput = plainTxt;
                }
            }
        } catch (BadPaddingException e) {
            return null; // this happens when a wrong password was given
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return plainTxt;
    }
    
    public static void main(String[] args) throws Exception {
        String secret = "toppsy kret";
        String password = "12345"; // :-P
        String salt = "salz";
        
        AesCipher aesCipher = new AesCipher(password);
        String secretEncrypted = aesCipher.encrypt(secret, salt);

        aesCipher = new AesCipher(password);
        String secretDecrypted = aesCipher.decrypt(secretEncrypted, salt);

        System.out.println("Salt Text : " + salt);
        System.out.println("Plain Text : " + secret);
        System.out.println("secretEncrypted : " + secretEncrypted);
        System.out.println("secretDecrypted : " + secretDecrypted);
    }

    private static String toBase64(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }
    
    private static byte[] fromBase64(String base64) {
        return Base64.decodeBase64(base64);
    }
}