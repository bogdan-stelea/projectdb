package com.bogdan.projectdb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Enhanced Application Context that integrates with Oracle Application Context
 * Maintains backward compatibility while adding Oracle database-level security
 */
@Component
public class ApplicationContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<String> ipAddress = new ThreadLocal<>();
    private static final ThreadLocal<LocalDateTime> requestTime = new ThreadLocal<>();

    @Autowired
    private OracleApplicationContextService oracleContextService;

    /**
     * Set context for user (enhanced with Oracle integration)
     */
    public void setContext(String username) {
        setContext(username, "STUDENT"); // Default role
    }

    /**
     * Set context with user role (integrates with Oracle Application Context)
     */
    public void setContext(String username, String role) {
        // Set ThreadLocal context (for backward compatibility)
        currentUser.set(username);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes()).getRequest();
        ipAddress.set(request.getRemoteAddr());
        requestTime.set(LocalDateTime.now());
        
        // Set Oracle Application Context
        try {
            oracleContextService.initializeUserContext(username, role);
        } catch (Exception e) {
            // Log error but don't fail - graceful degradation
            System.err.println("Failed to set Oracle application context: " + e.getMessage());
        }
    }

    /**
     * Get current user from ThreadLocal
     */
    public static String getCurrentUser() {
        return currentUser.get();
    }

    /**
     * Get current user's IP address
     */
    public static String getCurrentIpAddress() {
        return ipAddress.get();
    }

    /**
     * Get request time
     */
    public static LocalDateTime getRequestTime() {
        return requestTime.get();
    }

    /**
     * Clear both ThreadLocal and Oracle contexts
     */
    public void clearContext() {
        currentUser.remove();
        ipAddress.remove();
        requestTime.remove();
        
        try {
            oracleContextService.clearUserContext();
        } catch (Exception e) {
            // Log error but don't fail
            System.err.println("Failed to clear Oracle application context: " + e.getMessage());
        }
    }

    /**
     * Get Oracle context information
     */
    public Map<String, String> getOracleContext() {
        try {
            return oracleContextService.getCurrentContext();
        } catch (Exception e) {
            return Map.of();
        }
    }

    /**
     * Check if current user can access sensitive data (via Oracle context)
     */
    public boolean canAccessSensitiveData() {
        try {
            return oracleContextService.canAccessSensitiveData();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current user's security level from Oracle
     */
    public String getSecurityLevel() {
        try {
            return oracleContextService.getSecurityLevel();
        } catch (Exception e) {
            return "LOW";
        }
    }

    /**
     * Get current user's role from Oracle
     */
    public String getUserRole() {
        try {
            return oracleContextService.getUserRole();
        } catch (Exception e) {
            return "STUDENT";
        }
    }
} 