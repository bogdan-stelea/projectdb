package com.bogdan.projectdb.security;

import org.springframework.stereotype.Component;

@Component
public class DataMaskingUtil {

    public String maskEmail(String email) {
        if (email == null || email.length() < 5) return email;
        if (DataMaskingContext.isAdminContext()) return email;
        
        int securityLevel = DataMaskingContext.getSecurityLevel() != null ? 
            DataMaskingContext.getSecurityLevel() : 1;
            
        int atIndex = email.indexOf('@');
        if (atIndex == -1) return email;
        
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        return switch (securityLevel) {
            case 1 -> "*****" + domain;
            case 2 -> localPart.charAt(0) + "****" + domain;
            case 3 -> localPart.substring(0, Math.min(3, localPart.length())) + "***" + domain;
            default -> localPart.substring(0, Math.min(2, localPart.length())) + "***" + domain;
        };
    }

    public String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) return phoneNumber;
        if (DataMaskingContext.isAdminContext()) return phoneNumber;
        
        String userRole = DataMaskingContext.getCurrentUserRole();
        if ("INSTRUCTOR".equals(userRole)) {
            return phoneNumber;
        }
        
        return "***-***-" + phoneNumber.substring(phoneNumber.length() - 4);
    }

    public String maskAddress(String address) {
        if (address == null || address.length() < 5) return address;
        if (DataMaskingContext.isAdminContext()) return address;
        
        int securityLevel = DataMaskingContext.getSecurityLevel() != null ? 
            DataMaskingContext.getSecurityLevel() : 1;

        return switch (securityLevel) {
            case 1 -> "********";
            case 2 -> address.substring(0, 3) + "****";
            case 3 -> address.substring(0, address.length() / 2) + "****";
            default -> address.substring(0, 3) + "****";
        };
    }
} 