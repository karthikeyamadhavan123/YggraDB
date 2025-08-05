package com.yggra.models;


public class Column {
    public final String columnName;
    public final DataType type;
    public final int length;


    public Column(String columnName, DataType type, int length) {
        this.columnName = columnName;
        this.type = type;
        this.length = length;

        validateColumn();
    }

    private void validateColumn(){
        if(type==DataType.VARCHAR){
            if(length<=0){
                throw new IllegalArgumentException("💀 [FORBIDDEN LENGTH] The gods reject a VARCHAR of length " + length + ". Provide a positive length or be cast into the void!");
            }

        } else if (type==DataType.INT) {
            if(length!=-1){
                throw new IllegalArgumentException("⚔️ [UNNECESSARY ATTRIBUTE] INT columns do not wield length. Use -1, as decreed by Yggra!");
            }
        }
    }
}
