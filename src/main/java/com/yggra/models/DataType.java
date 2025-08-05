package com.yggra.models;

public enum DataType {
    // ⚔️ ACCEPTED TYPES FOR THE REALM
    INT,
    VARCHAR;

    public static DataType Validate(String dataType){
        // 🔥 OLYMPIAN WRATH FOR UNKNOWN TYPES
        return switch (dataType.toUpperCase()) {
            case "INT" -> INT;
            case "VARCHAR" -> VARCHAR;
            default ->
                    throw new IllegalArgumentException("💀 [PARSING CATASTROPHE] ⚠️ Unknown data type encountered: '" + dataType + "' — only INT or VARCHAR may stand in this realm!");
        };
    }
}
