package com.riaanvo;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataParser {
    //private String[] attributes;
    //private ArrayList<ArrayList<String>> uniqueAttributeValues;
    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> trainingDataSet;

    public DataParser(String trainDataFilePath){
        //Extract training data file into a string
        System.out.println("Loading training data set");

        String trainingCSVText = extractFileContents(trainDataFilePath);

        System.out.println("Extracting data");

        //Split into rows
        String[] rows = trainingCSVText.split("\n");

        dataDescriptor = new DataDescriptor(rows[0].split(","));
        trainingDataSet = new ArrayList<>();

        //Create all the data elements
        for(int r = 1; r < rows.length; r++){
            //Split into the individual values
            String[] values = rows[r].split(",");

            //Create the unique values arraylist
            for(int c = 0; c < values.length; c++){
                dataDescriptor.tryAddUniqueValue(c, values[c]);
            }

            //Create a new dataElement
            trainingDataSet.add(new DataElement(dataDescriptor.convertStringValuesToInt(values)));
        }
        //Set all elements data descriptors to this
        trainingDataSet.get(0).setDataDescriptor(dataDescriptor);

        dataDescriptor.printUniqueValues();
        printDataArray(10, false);
    }

    private void printDataArray(int numToShow, boolean asStrings){
        //Print out the data
        String s = "\nData output\n";

        //for(String a : attributes){
          //  s += a + ", ";
        //}

        s += "\n";
        for(int i = 0; i < numToShow; i ++){
            s += trainingDataSet.get(i).toString(asStrings) + "\n";
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
