package com.bogdan.projectdb.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

@Component
public class KeyEncryption {
    private static final Logger logger = LoggerFactory.getLogger(KeyEncryption.class);
    
    @Value("${encryption.key.master}")
    private String masterKey;
    
    @Value("${encryption.key.salt}")
    private String salt;

    public KeyEncryption() {
        logger.info("KeyEncryption initialized with MASTER_KEY present: {}, SALT present: {}",
            masterKey != null, salt != null);
    }

    public String decryptKey(String encryptedKey) throws Exception {
        try {
            if (masterKey == null || salt == null) {
                throw new IllegalStateException("Encryption keys not properly configured");
            }
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            PBEKeySpec spec = new PBEKeySpec(
                masterKey.toCharArray(),
                Base64.getDecoder().decode(salt),
                65536,
                256
            );
            SecretKey tmp = factory.generateSecret(spec);
            return new String(Base64.getDecoder().decode(encryptedKey));
        } catch (Exception e) {
            logger.error("Error decrypting key: {}", e.getMessage());
            throw e;
        }
    }
} 