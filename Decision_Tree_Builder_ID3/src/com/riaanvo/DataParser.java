package com.riaanvo;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataParser {
    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> dataSet;

    public DataParser(String filePath, DataDescriptor dataDescriptor){
        this.dataDescriptor = dataDescriptor;
        long previousTime = System.currentTimeMillis();

        System.out.println("To load: " + filePath);

        //Extract training data file into a string
        System.out.print("Loading data set:");

        String trainingCSVText = extractFileContents(filePath);

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");
        previousTime = System.currentTimeMillis();
        System.out.print("Extracting data:");

        //Split into rows
        String[] rows = trainingCSVText.split("\n");
        extractDataSet(rows, dataDescriptor);

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");



    }


    /**
     * Code from https://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-example/
     * @param filepath
     * @return
     */
    private String extractFileContents(String filepath){
        StringBuilder output = new StringBuilder();
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);

            String nextLine;

            while ((nextLine = br.readLine()) != null) {
                output.append(nextLine).append("\n");
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

        return output.toString();
    }

    private void extractDataSet(String[] rows, DataDescriptor dataDescriptor){
        dataSet = new ArrayList<>();
        boolean dataDescriptorPredefined = true;
        if(dataDescriptor == null){
            this.dataDescriptor = new DataDescriptor(rows[0].split(","));
            dataDescriptorPredefined = false;
        }

        //Create all the data elements
        for(int r = 1; r < rows.length; r++){
            //Split into the individual values
            String[] values = rows[r].split(",");

            if(!dataDescriptorPredefined) {
                //Create the unique values  Array List
                for (int c = 0; c < values.length; c++) {
                    this.dataDescriptor.tryAddUniqueValue(c, values[c]);
                }
            }

            //Create a new dataElement
            dataSet.add(new DataElement(this.dataDescriptor.convertStringValuesToInt(values)));
        }
        //Set all elements data descriptors to this
        DataElement.setDataDescriptor(this.dataDescriptor);
    }

    public DataDescriptor getDataDescriptor() {
        return dataDescriptor;
    }

    public ArrayList<DataElement> getDataSet() {
        return dataSet;
    }

    public String toString(int numToShow){
        StringBuilder s = new StringBuilder();

        if(dataDescriptor != null){
            s.append(dataDescriptor.toString());
        }

        s.append("\n\nData output\n");
        for(int i = 0; i < numToShow; i ++){
            s.append(dataSet.get(i).toString()).append("\n");
        }
        return s.toString();
    }
}
