package com.riaanvo;

import java.util.ArrayList;

public class DataDescriptor {
    String[] attributeHeaders;
    ArrayList<ArrayList<String>> uniqueAttributeValues;

    public DataDescriptor(String[] attributeHeaders){
        this.attributeHeaders = attributeHeaders;

        //Set up the array for unique values
        uniqueAttributeValues = new ArrayList();
        for(int i = 0; i < attributeHeaders.length; i++){
            uniqueAttributeValues.add(new ArrayList<>());
        }
    }

    public void tryAddUniqueValue(int columnIndex, String value){
        if(!uniqueAttributeValues.get(columnIndex).contains(value)){
            uniqueAttributeValues.get(columnIndex).add(value);
        }
    }

    public int[] convertStringValuesToInt(String[] sValues){
        int[] iValues = new int[sValues.length];
        for(int i = 0; i < iValues.length; i++){
            iValues[i] = uniqueAttributeValues.get(i).indexOf(sValues[i]);
        }
        return iValues;
    }

    public String[] convertIntValuesToString(int[] iValues){
        String[] sValues = new String[iValues.length];
        for(int i = 0; i < sValues.length; i++){
            sValues[i] = uniqueAttributeValues.get(i).get(iValues[i]);
        }
        return sValues;
    }

    public String indexToValue(int columnIndex, int valueIndex){
        return uniqueAttributeValues.get(columnIndex).get(valueIndex);
    }


    public void printUniqueValues(){
        String s = "";
        for(int c = 0; c < attributeHeaders.length; c++){
            s += attributeHeaders[c] + ":\n";
            ArrayList<String> uniqueValues = uniqueAttributeValues.get(c);
            for(int v = 0; v < uniqueValues.size(); v++){
                s += "\t" + v + ": " + uniqueValues.get(v) + "\n";
            }
            s += "\n";
        }
        System.out.println(s);
    }
}
