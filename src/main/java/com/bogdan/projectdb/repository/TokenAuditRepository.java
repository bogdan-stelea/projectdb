package com.bogdan.projectdb.repository;

import com.bogdan.projectdb.model.TokenAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TokenAuditRepository extends JpaRepository<TokenAudit, Integer> {
    List<TokenAudit> findByUsername(String username);
    List<TokenAudit> findByRevoked(boolean revoked);
}