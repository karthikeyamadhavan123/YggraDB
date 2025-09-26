package com.yggra.commands;

import com.yggra.common_models.Condition;

import java.util.List;

/**
 * 🔍 [RUNIC INSCRIPTION] 🔍
 * The seeker’s command — SELECT — used to retrieve wisdom from the depths of Yggra’s memory.
 * This class represents a parsed `SELECT` query in YggraDB.
 * It is created by the SQL parser and later executed by the SQL engine.
 * ⚡ Responsibilities:
 *  - Holds the name of the target table.
 *  - Holds the list of columns requested by the query.
 *  - (Future) Will also carry filtering conditions (WHERE) and other modifiers.
 */

public class SelectCommand extends SQLCommand {

    /**
     * 🏛️ The table from which data should be retrieved.
     * Example:
     *   In `SELECT id, name FROM user;` → tableName = "user"
     */
    public final String tableName;

    /**
     * 📜 The list of columns the query wishes to retrieve.
     * Example:
     *   In `SELECT id, name FROM user;` → columns = ["id", "name"]
     * Special Case:
     *   If the query is `SELECT ALL FROM user;` (or `SELECT *` later),
     *   this list may contain a single keyword ("ALL") to signal that
     *   all columns should be returned.
     */

    public final List<String> columns;

    // 🔮 Future expansion:
    // public final List<Condition> conditions;
    // Example:
    //   In `SELECT id FROM user WHERE age > 18;`
    //   conditions = [ Condition("age", ">", 18) ]

    public List<Condition> conditions;
    /**
     * 🏗️ Constructs a new SELECT command representation.
     *
     * @param tableName The target table from which to fetch rows.
     * @param columns   The specific columns requested in the query.
     */

    public SelectCommand(String tableName, List<String> columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public SelectCommand(String tableName, List<String> columns, List<Condition> conditions){
        this.tableName = tableName;
        this.columns = columns;
        this.conditions=conditions;
    }
}
