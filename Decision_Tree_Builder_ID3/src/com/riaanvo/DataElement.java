package com.riaanvo;

import java.util.ArrayList;

public class DataElement {
    private static DataDescriptor dataDescriptor;
    final ArrayList<Integer> values;

    public DataElement(int[] values){
        this.values = new ArrayList<>();
        for(int value: values){
            this.values.add(value);
        }
    }

    public String toString(boolean displayAsStrings){
        int numValues = values.size();
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < numValues; i++){
            if(displayAsStrings){
                s.append(dataDescriptor.indexToValue(i, values.get(i)));
            } else {
                s.append(values.get(i));
            }

            if(i != numValues - 1) {
                s.append(", ");
            }
        }

        return s.toString();
    }

    static void setDataDescriptor(DataDescriptor newDataDescriptor){
        dataDescriptor = newDataDescriptor;
    }

}
