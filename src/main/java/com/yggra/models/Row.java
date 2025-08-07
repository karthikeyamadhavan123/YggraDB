package com.yggra.models;
import java.util.List;

public class Row {
    //Implement Row class
    // Internally store data as List<Object> or Map<String, Object>.
    // Ensure values match column data types.
    public  final List<Object> values;

    public Row(List<Object> values) {
        this.values = values;
    }
    @Override
    public String toString(){
        return values.toString();
    }



}
