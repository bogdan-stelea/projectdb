package com.bogdan.projectdb.encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // Generate Master Key
        SecureRandom secureRandom = new SecureRandom();
        byte[] masterKey = new byte[32]; // 256 bits
        secureRandom.nextBytes(masterKey);
        String encodedMasterKey = Base64.getEncoder().encodeToString(masterKey);
        
        // Generate Salt
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        
        // Generate AES Key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();
        String encodedAesKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());

        System.out.println("Export these as environment variables:");
        System.out.println("export MASTER_KEY=\"" + encodedMasterKey + "\"");
        System.out.println("export KEY_SALT=\"" + encodedSalt + "\"");
        System.out.println("export ENCRYPTED_KEY=\"" + encodedAesKey + "\"");
    }
} 