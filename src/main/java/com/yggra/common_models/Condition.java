package com.yggra.common_models;

import com.yggra.commands.ValueDefinition;
import com.yggra.parser.TokenType;


public class Condition {
    public final String columnName;
    public final TokenType condition;
    public final ValueDefinition conditionValue;

    //helps in where age >=18 like that scenarios where columName is age condition is >= value is 18;
    public Condition(String columnName, TokenType condition, ValueDefinition conditionValue) {
        this.columnName = columnName;
        this.condition = condition;
        this.conditionValue = conditionValue;
    }



    @Override
    public String toString(){

        return columnName +" " +
                condition.toString() + " " +
                conditionValue.toString();
    }
}
