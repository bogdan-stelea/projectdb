package com.bogdan.projectdb.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SecurityLevelAssignmentService securityLevelService;

    public JwtAuthenticationFilter(JwtService jwtService, 
                                 SecurityLevelAssignmentService securityLevelService) {
        this.jwtService = jwtService;
        this.securityLevelService = securityLevelService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        if (jwtService.isTokenValid(jwt)) {
            Claims claims = jwtService.extractAllClaims(jwt);
            
            Map<String, Object> details = new HashMap<>();
            details.put("department", claims.get("department"));
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    Collections.singleton(() -> "ROLE_" + claims.get("role"))
                );
            
            authentication.setDetails(details);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            securityLevelService.assignSecurityLevel(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
} 