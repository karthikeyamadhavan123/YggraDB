package com.yggra.models;

public enum ClauseType {
    FROM(1),
    WHERE(2),
    GROUP_BY(3),
    HAVING(4),
    SELECT(5),
    ORDER_BY(6),
    LIMIT(7);

    private final int priority;

    ClauseType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}

