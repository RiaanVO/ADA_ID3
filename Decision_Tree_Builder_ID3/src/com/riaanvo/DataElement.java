package com.riaanvo;

import java.util.ArrayList;

public class DataElement {

    ArrayList<String> values;

    public DataElement(String[] values){
        this.values = new ArrayList<>();
        for(String value: values){
            this.values.add(value);
        }
    }

    public boolean matchesValue(int column, String value){
        return values.get(column).equals(value);
    }

    public String toString(){
        int numValues = values.size();
        String s = "";
        for(int i = 0; i < numValues; i++){
            s += values.get(i);
            if(i != numValues - 1) {
                s += ", ";
            }
        }
        return s;
    }

}
