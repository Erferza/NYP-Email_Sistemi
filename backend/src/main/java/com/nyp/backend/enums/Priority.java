package com.nyp.backend.enums;

public enum Priority {
    LOW("Düşük", 1),
    NORMAL("Normal", 2),
    HIGH("Yüksek", 3),
    URGENT("Acil", 4);

    private final String displayName;
    private final int level;

    Priority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}
