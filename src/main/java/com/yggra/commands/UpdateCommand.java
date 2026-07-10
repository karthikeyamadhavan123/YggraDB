package com.yggra.commands;

import com.yggra.common_models.Condition;

import java.util.HashMap;

public class UpdateCommand extends SQLCommand {
    public final String tableName;
    public final HashMap<String, ValueDefinition> map;
    public final Condition condition;

    public UpdateCommand(String tableName, HashMap<String, ValueDefinition> map, Condition condition) {
        this.tableName = tableName;
        this.map = map;
        this.condition = condition;
    }

}
