package com.riaanvo;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataParser {
    private String[] attributes;
    private ArrayList<ArrayList<String>> uniqueAttributeValues;
    private ArrayList<DataElement> trainingDataSet;

    public DataParser(String trainDataFilePath){
        //Extract training data file into a string
        System.out.println("Loading training data set");

        String trainingCSVText = extractFileContents(trainDataFilePath);

        System.out.println("Extracting data");

        //Split into rows
        String[] rows = trainingCSVText.split("\n");

        //Split ito columns
        attributes = rows[0].split(",");

        //Construct data set
        trainingDataSet = new ArrayList<>();

        //Initialise the unique values array;
        uniqueAttributeValues = new ArrayList();
        for(int i = 0; i < attributes.length; i++){
            uniqueAttributeValues.add(new ArrayList<>());
        }

        //Create all the data elements
        for(int r = 1; r < rows.length; r++){

            //Split into the individual values
            String[] values = rows[r].split(",");

            //Create the unique values arraylist
            for(int c = 0; c < values.length; c++){
                if(!uniqueAttributeValues.get(c).contains(values[c])){
                    uniqueAttributeValues.get(c).add(values[c]);
                }
            }

            //Create a new dataElement
            trainingDataSet.add(new DataElement(values));
        }

        printUniqueValues();
        printDataArray(10);
    }

    private void printUniqueValues(){
        String s = "";
        for(int c = 0; c < attributes.length; c++){
            s += attributes[c] + ":\n\t";
            ArrayList<String> uniqueValues = uniqueAttributeValues.get(c);
            for(int v = 0; v < uniqueValues.size(); v++){
                s += uniqueValues.get(v) + ", ";
            }
            s += "\n";
        }
        System.out.println(s);
    }

    private void printDataArray(int numToShow){
        //Print out the data
        String s = "\nData output\n";

        for(String a : attributes){
            s += a + ", ";
        }

        s += "\n";
        for(int i = 0; i < numToShow; i ++){
            s += trainingDataSet.get(i).toString() + "\n";
        }
        System.out.println(s);
    }

    /**
     * Code from https://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-example/
     * @param filepath
     * @return
     */
    private String extractFileContents(String filepath){
        String output = "";
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);

            String nextLine;

            while ((nextLine = br.readLine()) != null) {
                output += nextLine + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return output;
    }

}
