package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Service
public class KeyStoreService {
    private static final String KEYSTORE_TYPE = "JCEKS"; // Use JCEKS for symmetric keys
    private static final String KEYSTORE_FILE = "mykeystore.jks";
    private static final String ALIAS = "myAESKey";

    @Value("${keystore.password}")
    private String keyStorePassword;

    @Value("${key.password}")
    private String keyPassword;

    @PostConstruct
    public void init() {
        System.out.println("KeyStore Password: " + keyStorePassword);
        System.out.println("Key Password: " + keyPassword);
    }

    public void saveKeyToKeyStore(SecretKey secretKey) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);

        // Load KeyStore, create if it doesn't exist
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
        } catch (IOException e) {
            keyStore.load(null, keyStorePassword.toCharArray()); // Create new KeyStore if it doesn't exist
        }

        // Check if the key already exists
        if (keyStore.containsAlias(ALIAS)) {
            System.out.println("Key already exists in KeyStore. Skipping save.");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            keyStore.setEntry(ALIAS, new KeyStore.SecretKeyEntry(secretKey), new KeyStore.PasswordProtection(keyPassword.toCharArray()));
            keyStore.store(fos, keyStorePassword.toCharArray());
            System.out.println("Key stored in KeyStore successfully.");
        } catch (IOException e) {
            System.err.println("Error saving KeyStore: " + e.getMessage());
        }
    }

    public SecretKey loadKeyFromKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            keyStore.load(fis, keyStorePassword.toCharArray());
            return (SecretKey) keyStore.getKey(ALIAS, keyPassword.toCharArray());
        }
    }

}
