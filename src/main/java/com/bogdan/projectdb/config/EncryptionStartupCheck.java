package com.bogdan.projectdb.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EncryptionStartupCheck {

    @Bean
    CommandLineRunner checkEncryptionKeys(Environment env) {
        return args -> {
            String masterKey = env.getProperty("MASTER_KEY");
            String salt = env.getProperty("KEY_SALT");
            
            if (masterKey == null || salt == null) {
                throw new IllegalStateException(
                    "Encryption keys not found. Please check your .env file"
                );
            }
            System.out.println("Encryption keys loaded successfully!");
            System.out.println(masterKey + " " + salt);
        };
    }
} 