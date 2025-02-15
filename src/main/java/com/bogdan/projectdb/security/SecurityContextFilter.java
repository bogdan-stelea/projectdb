package com.bogdan.projectdb.security;

import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class SecurityContextFilter implements Filter {

    private final SecurityLevelAssignmentService securityLevelService;

    public SecurityContextFilter(SecurityLevelAssignmentService securityLevelService) {
        this.securityLevelService = securityLevelService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                securityLevelService.assignSecurityLevel(authentication);
            }
            chain.doFilter(request, response);
        } finally {
            DataMaskingContext.clearContext();
        }
    }
} 