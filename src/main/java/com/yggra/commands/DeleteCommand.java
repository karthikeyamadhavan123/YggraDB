package com.yggra.commands;

import com.yggra.common_models.Condition;

public class DeleteCommand extends SQLCommand{
    public final String tableName;
    public final Condition condition;

    public DeleteCommand(String tableName, Condition condition) {
        this.tableName = tableName;
        this.condition = condition;
    }
}
