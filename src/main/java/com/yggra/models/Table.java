package com.yggra.models;

import java.util.List;

public class Table {
    public final List<Column> columnList;
    public final List<Row> rowList;


    public Table(List<Column> columnList, List<Row> rowList) {
        this.columnList = columnList;
        this.rowList = rowList;
    }

    // Method: addRow(Row row)
    // Method: validateRow(Row row)
    // Method: getColumnIndexByName(String name)
}
