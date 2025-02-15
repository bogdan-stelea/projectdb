package com.bogdan.projectdb.security;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class DataMaskingContext {
    private static DataMaskingUtil maskingUtil;
    private final DataMaskingUtil autowiredMaskingUtil;
    private static final ThreadLocal<String> currentUserRole = new ThreadLocal<>();
    private static final ThreadLocal<Integer> securityLevel = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isAdminContext = new ThreadLocal<>();

    public DataMaskingContext(DataMaskingUtil autowiredMaskingUtil) {
        this.autowiredMaskingUtil = autowiredMaskingUtil;
    }

    @PostConstruct
    private void init() {
        DataMaskingContext.maskingUtil = this.autowiredMaskingUtil;
    }

    public static void setCurrentUserRole(String role) {
        currentUserRole.set(role);
    }

    public static String getCurrentUserRole() {
        return currentUserRole.get();
    }

    public static void setSecurityLevel(Integer level) {
        securityLevel.set(level);
    }

    public static Integer getSecurityLevel() {
        return securityLevel.get();
    }

    public static void setAdminContext(Boolean isAdmin) {
        isAdminContext.set(isAdmin);
    }

    public static Boolean isAdminContext() {
        return isAdminContext.get();
    }

    public static void clearContext() {
        currentUserRole.remove();
        securityLevel.remove();
        isAdminContext.remove();
    }

    public static DataMaskingUtil getMaskingUtil() {
        if (maskingUtil == null) {
            throw new IllegalStateException("DataMaskingUtil not initialized");
        }
        return maskingUtil;
    }
} 