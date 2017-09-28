package com.riaanvo;

import java.util.ArrayList;

public class DataDescriptor {
    private  ArrayList<String> attributeHeaders;
    private ArrayList<ArrayList<String>> uniqueAttributeValues;
    private int numberOfClasses = 0;
    private int classColumnIndex = 0;

    public DataDescriptor(String[] attributeHeaders){
        this.attributeHeaders = new ArrayList<>();
        for(String attribute: attributeHeaders){
            this.attributeHeaders.add(attribute);
        }

        //Set up the array for unique values
        uniqueAttributeValues = new ArrayList<>();
        for(int i = 0; i < attributeHeaders.length; i++){
            uniqueAttributeValues.add(new ArrayList<>());

            if(attributeHeaders[i].contains("#")){
                classColumnIndex = i;
            }
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

    public ArrayList<String> getAttributeValues(int columnIndex){
        return uniqueAttributeValues.get(columnIndex);
    }

    public int getClassIndex(){
        return classColumnIndex;
    }

    public int getNumberOfClasses(){
        if(numberOfClasses == 0){
            numberOfClasses = uniqueAttributeValues.get(classColumnIndex).size();
        }
        return numberOfClasses;
    }

    public int getNumberOfAttributes(){
        return attributeHeaders.size();
    }

    public String getAttributeHeaderValue(int columnIndex){
        if(columnIndex == -1) return "No Split";
        return attributeHeaders.get(columnIndex);
    }

    public void printUniqueValues(){
        String s = "";
        for(int c = 0; c < attributeHeaders.size(); c++){
            s += attributeHeaders.get(c) + ":\n";
            ArrayList<String> uniqueValues = uniqueAttributeValues.get(c);
            for(int v = 0; v < uniqueValues.size(); v++){
                s += "\t" + v + ": " + uniqueValues.get(v) + "\n";
            }
            s += "\n";
        }
        System.out.println(s);
    }
}
