package com.myapp.util.security.aes;

import java.util.Properties;

import org.junit.Test;

public class CryptedPropertiesTest {

    @Test
    public void testEncryptProperties() {
        Properties test = new Properties();
        test.setProperty("firstname", "andre");
        test.setProperty("hobbies", "nasenbohren");
        test.setProperty("lieblingsessen", "pizza");
        
        Properties encrypted = CryptedProperties.encryptProperties(test, "s3cret");
        
        Properties decryptProperties = CryptedProperties.decryptProperties(encrypted, "s3cret");

        System.out.println("test:              "+test);
        System.out.println("encrypted:         "+encrypted);
        System.out.println("decryptProperties: "+decryptProperties);
    }
}
