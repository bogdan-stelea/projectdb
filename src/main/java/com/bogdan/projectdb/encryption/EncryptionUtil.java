package com.bogdan.projectdb.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    private final SecretKeySpec secretKey;
    private static final String ALGORITHM = "AES";

    public EncryptionUtil(
            @Value("${encryption.key.master:#{null}}") String masterKey,
            @Value("${encryption.key.salt:#{null}}") String salt) {
        if (masterKey == null || salt == null) {
            throw new IllegalStateException("Encryption keys not found in environment");
        }
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(
                    masterKey.toCharArray(),
                    Base64.getDecoder().decode(salt),
                    65536,
                    256
            );
            byte[] key = factory.generateSecret(spec).getEncoded();
            this.secretKey = new SecretKeySpec(key, ALGORITHM);

        } catch (Exception e) {
            logger.error("Error initializing encryption: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize encryption", e);
        }
    }

    public String encrypt(String data) {
        try {
            if (data == null) return null;
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            logger.error("Error encrypting data: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            if (encryptedData == null) return null;
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(
                    Base64.getDecoder().decode(encryptedData)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error decrypting data: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }
}