package com.bogdan.projectdb.security;

import com.bogdan.projectdb.enums.SecurityLevel;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.HashMap;

@Service
public class SecurityLevelAssignmentService {
    private final Map<String, SecurityLevel> roleSecurityLevels;

    public SecurityLevelAssignmentService() {
        roleSecurityLevels = new HashMap<>();
        initializeSecurityLevels();
    }

    private void initializeSecurityLevels() {
        roleSecurityLevels.put("ROLE_ADMIN", SecurityLevel.ADMIN);
        roleSecurityLevels.put("ROLE_INSTRUCTOR", SecurityLevel.HIGH);
        roleSecurityLevels.put("ROLE_STUDENT", SecurityLevel.MEDIUM);
        roleSecurityLevels.put("ROLE_USER", SecurityLevel.LOW);
    }

    public void assignSecurityLevel(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");

        SecurityLevel roleLevel = roleSecurityLevels.getOrDefault(role, SecurityLevel.LOW);
        
        DataMaskingContext.setSecurityLevel(roleLevel.getLevel());
        DataMaskingContext.setCurrentUserRole(role);
        DataMaskingContext.setAdminContext(roleLevel == SecurityLevel.ADMIN);
    }
} 