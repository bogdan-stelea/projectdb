package com.bogdan.projectdb.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@Order(1)
public class SqlInjectionFilter implements Filter {
    private static final Logger logger = Logger.getLogger(SqlInjectionFilter.class.getName());

    private final SqlSecurityConfig sqlSecurityConfig;

    public SqlInjectionFilter(SqlSecurityConfig sqlSecurityConfig) {
        this.sqlSecurityConfig = sqlSecurityConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();
        
        if (requestUri.contains("/api")) {
            if (!isRequestSafe(httpRequest)) {
                logger.warning("SQL Injection attempt detected from IP: " + request.getRemoteAddr());
                ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_BAD_REQUEST, 
                    "Invalid input detected"
                );
                return;
            }
        }
        
        chain.doFilter(request, response);
    }

    private boolean isRequestSafe(HttpServletRequest request) {
        if (!validateQueryParams(request)) {
            return false;
        }

        return sqlSecurityConfig.isSqlInjectionSafe(request.getRequestURI());
    }

    private boolean validateQueryParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
            .allMatch(entry -> entry.getValue() != null && 
                     sqlSecurityConfig.isSqlInjectionSafe(String.join(",", entry.getValue())));
    }
} 