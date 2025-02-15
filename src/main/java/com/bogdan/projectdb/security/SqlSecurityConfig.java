package com.bogdan.projectdb.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.regex.Pattern;

@Configuration
public class SqlSecurityConfig {

    private final Pattern sqlInjectionPattern = Pattern.compile(
        "(?i).*(\\b(and|or|union|select|insert|update|delete|drop|alter)\\b|[;'\"=]).*"
    );

    public SqlSecurityConfig(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean isSqlInjectionSafe(String input) {
        return !sqlInjectionPattern.matcher(input).matches();
    }

    public String sanitizeInput(String input) {
        if (input == null) return null;
        return input.replaceAll("[;'\"=]", "");
    }
} 