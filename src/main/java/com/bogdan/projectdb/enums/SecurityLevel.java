package com.bogdan.projectdb.enums;

import lombok.Getter;

@Getter
public enum SecurityLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    ADMIN(4);

    private final int level;

    SecurityLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}