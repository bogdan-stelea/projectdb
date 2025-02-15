package com.bogdan.projectdb.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class ApplicationContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<String> ipAddress = new ThreadLocal<>();
    private static final ThreadLocal<LocalDateTime> requestTime = new ThreadLocal<>();

    public void setContext(String username) {
        currentUser.set(username);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .currentRequestAttributes()).getRequest();
        ipAddress.set(request.getRemoteAddr());
        requestTime.set(LocalDateTime.now());
    }
} 