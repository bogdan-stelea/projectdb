package com.bogdan.projectdb.security;

import com.bogdan.projectdb.audit.AuditService;
import com.bogdan.projectdb.model.TokenAudit;
import com.bogdan.projectdb.repository.TokenAuditRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private static final String SECRET_KEY = "your_secret_key_here_make_it_at_least_256_bits";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours

    private final TokenAuditRepository tokenAuditRepository;
    private final AuditService auditService;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String username, String role, String department) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("department", department);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        // Create token audit record
        TokenAudit tokenAudit = new TokenAudit();
        tokenAudit.setUsername(username);
        tokenAudit.setToken(token);
        tokenAudit.setRole(role);
        tokenAudit.setIssuedAt(LocalDateTime.now());
        tokenAudit.setExpiresAt(LocalDateTime.now().plusDays(1));
        tokenAudit.setRevoked(false);
        
        TokenAudit savedToken = tokenAuditRepository.save(tokenAudit);

        // Log the token generation in audit logs
        auditService.logActivity(
            "Token",
            savedToken.getId().intValue(),
            "GENERATE",
            null,
            Map.of(
                "username", username,
                "role", role,
                "department", department,
                "expiresAt", tokenAudit.getExpiresAt()
            ),
            username
        );

        return token;
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
} 