package com.myapp.util.security.aes;

import static org.junit.Assert.*;

import org.junit.Test;

public class AesCipherTest {

    @Test
    public void test() throws Exception {
        final String secret = "toppsy kret";
        final String password = "12345"; // :-P
        final String salt = "uh3198bb";
        
        AesCipher aesCipher = new AesCipher(password);
        String secretEncrypted = aesCipher.encrypt(secret, salt);

        aesCipher = new AesCipher("wrong");
        String secretDecrypted = aesCipher.decrypt(secretEncrypted, salt);
        
        assertFalse("original  ='"+secret+"', "+
                    "decrypted ='"+secretDecrypted+"'",
                    secret.equals(secretDecrypted));

        aesCipher = new AesCipher(password);
        secretDecrypted = aesCipher.decrypt(secretEncrypted, salt);
        
        assertEquals("original  ='"+secret+"', "+
                     "decrypted ='"+secretDecrypted+"'",
                     secret, secretDecrypted);
    }
}
