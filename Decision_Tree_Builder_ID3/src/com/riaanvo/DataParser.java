package com.riaanvo;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataParser {
    private String[] attibutes;
    private LinkedList<LinkedList<String>> attibutesUniqueValues;
    private String[][] traininngDataSet;

    public DataParser(String trainDataFilePath){
        //Extract training data file into a string
        System.out.println("Loading training data set");

        String trainingCSVText = extractFileContents(trainDataFilePath);

        System.out.println("Extracting data");


        String[] rows = trainingCSVText.split("\n");

        //Split ito columns
        attibutes = rows[0].split(",");

        //Construct data set
        int numRows = rows.length - 1;
        int numCols = attibutes.length;
        traininngDataSet = new String[numRows][numCols];
        for(int i = 1; i < rows.length; i++){
            traininngDataSet[i - 1] = rows[i].split(",");
        }

        //Getting unique values
        attibutesUniqueValues = new LinkedList<LinkedList<String>>();
        for(int i = 0; i < numCols; i++){
            String s = attibutes[i] + ": ";
            LinkedList<String> uniqueValues = new LinkedList<String>();

            for(int r = 0; r < numRows; r ++){
                String value = traininngDataSet[r][i];
                if(!uniqueValues.contains(value)){
                    s += value + ", ";
                    uniqueValues.add(value);
                }
            }
            System.out.println(s);
        }


        printDataArray();
    }

    private void printDataArray(){
        //Print out the data
        String s = "\n\n\nData output\n";

        for(String a : attibutes){
            s += a + ", ";
        }
        s += "\n";

        for(int r = 0; r < 10; r++){
            for(int c = 0; c < attibutes.length; c++){
                s += traininngDataSet[r][c] + ", ";
            }
            s += "\n";
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

    private class DataElement{
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
}
