package com.bogdan.projectdb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing Oracle Application Context
 * Integrates with Oracle database-level security contexts
 */
@Service
public class OracleApplicationContextService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Cache for context values to avoid repeated database calls
    private final Map<String, String> contextCache = new ConcurrentHashMap<>();

    /**
     * Set user security context in Oracle database
     */
    public void setUserContext(String username, String role, String securityLevel) {
        try {
            // Clear cache first to ensure fresh values
            contextCache.clear();
            
            String ipAddress = getCurrentUserIpAddress();
            
            // Call Oracle stored procedure to set context
            jdbcTemplate.execute(String.format(
                "BEGIN security_manager.set_current_user('%s', '%s'); END;",
                username, role
            ));
            
            // Update local cache with basic info only
            contextCache.put("USERNAME", username);
            contextCache.put("USER_ROLE", role);
            contextCache.put("IP_ADDRESS", ipAddress);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Oracle application context", e);
        }
    }

    /**
     * Clear user security context
     */
    public void clearUserContext() {
        try {
            jdbcTemplate.execute("BEGIN security_manager.clear_current_user(); END;");
            contextCache.clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear Oracle application context", e);
        }
    }

    /**
     * Get current user's security level from Oracle context
     */
    public String getSecurityLevel() {
        if (contextCache.containsKey("SECURITY_LEVEL")) {
            return contextCache.get("SECURITY_LEVEL");
        }
        
        try {
            String securityLevel = jdbcTemplate.queryForObject(
                "SELECT security_manager.can_see_sensitive_data() FROM dual", 
                String.class
            );
            contextCache.put("SECURITY_LEVEL", securityLevel);
            return securityLevel;
        } catch (Exception e) {
            return "LOW"; // Default security level
        }
    }

    /**
     * Get current user's role from Oracle context
     */
    public String getUserRole() {
        if (contextCache.containsKey("USER_ROLE")) {
            return contextCache.get("USER_ROLE");
        }
        
        try {
            String role = jdbcTemplate.queryForObject(
                "SELECT security_manager.get_current_role() FROM dual", 
                String.class
            );
            contextCache.put("USER_ROLE", role);
            return role;
        } catch (Exception e) {
            return "STUDENT"; // Default role
        }
    }

    /**
     * Check if current user can access sensitive data
     */
    public boolean canAccessSensitiveData() {
        try {
            String result = jdbcTemplate.queryForObject(
                "SELECT CASE WHEN security_manager.can_see_sensitive_data() IN ('FULL', 'PARTIAL') THEN 'TRUE' ELSE 'FALSE' END FROM dual",
                String.class
            );
            return "TRUE".equals(result);
        } catch (Exception e) {
            return false; // Default to no access
        }
    }

    /**
     * Get data masking level for current user
     */
    public String getMaskingLevel() {
        if (contextCache.containsKey("MASKING_LEVEL")) {
            return contextCache.get("MASKING_LEVEL");
        }
        
        try {
            String maskingLevel = jdbcTemplate.queryForObject(
                "SELECT security_manager.can_see_sensitive_data() FROM dual",
                String.class
            );
            contextCache.put("MASKING_LEVEL", maskingLevel);
            return maskingLevel;
        } catch (Exception e) {
            return "LIMITED"; // Default to limited access
        }
    }

    /**
     * Get all current context values
     */
    public Map<String, String> getCurrentContext() {
        try {
            Map<String, String> context = new ConcurrentHashMap<>();
            
            String query = """
                SELECT SYS_CONTEXT('USER_SECURITY_CTX', 'CURRENT_USER') as username,
                       SYS_CONTEXT('USER_SECURITY_CTX', 'CURRENT_ROLE') as role,
                       SYS_CONTEXT('USER_SECURITY_CTX', 'ACCESS_LEVEL') as access_level,
                       SYS_CONTEXT('USER_SECURITY_CTX', 'ACCESS_LEVEL') as masking_level,
                       'N/A' as ip_address,
                       'N/A' as session_start
                FROM dual
                """;
            
            jdbcTemplate.query(query, rs -> {
                context.put("USERNAME", rs.getString("username"));
                context.put("USER_ROLE", rs.getString("role"));
                context.put("SECURITY_LEVEL", rs.getString("access_level"));
                context.put("MASKING_LEVEL", rs.getString("masking_level"));
                context.put("IP_ADDRESS", rs.getString("ip_address"));
                context.put("SESSION_START", rs.getString("session_start"));
            });
            
            return context;
        } catch (Exception e) {
            return new ConcurrentHashMap<>();
        }
    }

    /**
     * Initialize context for authenticated user
     */
    public void initializeUserContext(String username, String role) {
        // Determine security level based on role
        String securityLevel = determineSecurityLevel(role);
        setUserContext(username, role, securityLevel);
    }

    /**
     * Determine security level based on user role
     */
    private String determineSecurityLevel(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "FULL";
            case "INSTRUCTOR" -> "PARTIAL";
            case "STUDENT" -> "LIMITED";
            default -> "LIMITED";
        };
    }

    /**
     * Get current user's IP address from HTTP request
     */
    private String getCurrentUserIpAddress() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attrs.getRequest();
            
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            
            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Validate if context is properly set
     */
    public boolean isContextValid() {
        try {
            String username = jdbcTemplate.queryForObject(
                "SELECT SYS_CONTEXT('USER_SECURITY_CTX', 'CURRENT_USER') FROM dual",
                String.class
            );
            return username != null && !username.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}