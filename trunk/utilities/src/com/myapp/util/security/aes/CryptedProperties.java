package com.myapp.util.security.aes;

import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CryptedProperties {

    
    private static final String validChars;
    private static final Random random = new SecureRandom();
    
    static {
        StringBuilder bui = new StringBuilder();

        for (char c = 0; c < 128; c++) {
            if (c == '|' 
                || c == '='
                || c == '\\'
                || Character.isWhitespace(c) 
                || Character.isISOControl(c)) {
                continue;
            }
            bui.append(c);
        }
        
        validChars = bui.toString();
    }
    
    public static Properties encryptProperties(Properties plaintext, String passphrase) {
        Properties result = new Properties();
        AesCipher cipher = new AesCipher(passphrase);
        
        for (String key : plaintext.stringPropertyNames()) {
            String plaintextProp = plaintext.getProperty(key);
            String salt = getRandomSalt(6 + random.nextInt(3));
            String encryptedProp;
            
            try {
                encryptedProp = cipher.encrypt(plaintextProp, salt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
            String put = "SALT|"+salt+"|BASE64DATA|"+encryptedProp+"|";
            result.setProperty(key, put);
        }
        
        return result;
    }
    public static Properties decryptProperties(Properties ciphertext, String passphrase) {
        Properties result = new Properties();
        AesCipher cipher = new AesCipher(passphrase);
        Matcher parser = Pattern.compile(
            "(?x) ^ \\s* " +
            "SALT       \\s* \\| ([^|]+) \\| \\s* " +
            "BASE64DATA \\s* \\| ([^|]+) \\| \\s* " +
            "$"
        ).matcher("foo");
        
        for (String key : ciphertext.stringPropertyNames()) {
            String cryptedProp = ciphertext.getProperty(key);
            
            if (! parser.reset(cryptedProp).matches()) {
                throw new RuntimeException(
                    "property does not match pattern:\npattern:\n"+
                    parser.pattern().pattern()+
                    "\ninput:\n"+cryptedProp
                );
            }
            
            String salt = parser.group(1);
            String data = parser.group(2);
            try {
                String decrypt = cipher.decrypt(data, salt);
                result.setProperty(key, decrypt);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
        }
        return result;
    }
    
    private static String getRandomSalt(int length) {
        StringBuilder bui = new StringBuilder();
        int len = validChars.length();
        
        for (int i = 0; i < length; i++) {
            bui.append(validChars.charAt(random.nextInt(len)));
        }
        
        return bui.toString();
    }
    
    public static void main(String[] args) {
        Properties test = new Properties();
        test.setProperty("firstname", "andre");
        test.setProperty("hobbies", "nasenbohren");
        
        Properties encrypted = encryptProperties(test, "s3cret");
        
        Properties decryptProperties = decryptProperties(encrypted, "s3cret");

        System.out.println("test:              "+test);
        System.out.println("encrypted:         "+encrypted);
        System.out.println("decryptProperties: "+decryptProperties);
    }
}
