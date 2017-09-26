package com.riaanvo;

import java.util.ArrayList;

public class DataElement {
    private static DataDescriptor dataDescriptor;
    ArrayList<Integer> values;

    public DataElement(int[] values){
        this.values = new ArrayList<>();
        for(int value: values){
            this.values.add(value);
        }
    }

    public boolean matchesValue(int column, int value){
        return values.get(column) == value;
    }

    public String toString(boolean displayAsStrings){
        int numValues = values.size();
        String s = "";
        for(int i = 0; i < numValues; i++){
            if(displayAsStrings){
                s += dataDescriptor.indexToValue(i, values.get(i));
            } else {
                s += values.get(i);
            }

            if(i != numValues - 1) {
                s += ", ";
            }
        }

        return s;
    }

    public void setDataDescriptor(DataDescriptor dataDescriptor){
        this.dataDescriptor = dataDescriptor;
    }

}
