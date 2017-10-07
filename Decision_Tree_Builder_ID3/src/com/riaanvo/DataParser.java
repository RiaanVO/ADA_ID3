package com.riaanvo;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is used to read in data sets in the comma separated value (CSV) file type and create a data descriptor
 * and a list of data elements. It can be used to extract data and generate a data descriptor or use a provided
 * data descriptor.
 */
public class DataParser {

    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> dataSet;


    public DataParser() {}

    /**
     * Takes in the file path of the data set and the data descriptor used
     * to convert the string values to integers. If no data descriptor is provided, one will be created.
     *
     * @param filePath       The file path of the data set
     * @param dataDescriptor The data descriptor to decode the data sets values
     */
    public void parseData(String filePath, DataDescriptor dataDescriptor){

        this.dataDescriptor = dataDescriptor;
        long previousTime = System.currentTimeMillis();

        System.out.println("File to load: " + filePath);
        System.out.print("Loading data set:");

        // Extract out the contents of the file
        String trainingCSVText = extractFileContents(filePath);

        System.out.println("\t| TIME TAKEN: " + (System.currentTimeMillis() - previousTime) + "ms");
        previousTime = System.currentTimeMillis();
        System.out.print("Extracting data:");

        //Split into rows and extract the data elements
        String[] rows = trainingCSVText.split("\n");
        extractDataSet(rows, dataDescriptor);

        System.out.println("\t| TIME TAKEN: " + (System.currentTimeMillis() - previousTime) + "ms\n");

    }


    /**
     * Extracts the contents of a file into a single string which is returned.
     *
     * @param filepath File path of the CSV file to extract
     * @return A string containing all the CSV files contents
     */
    private String extractFileContents(String filepath) {

        StringBuilder output = new StringBuilder();

        // Attempt to open the file and read the data
        try (FileReader fr = new FileReader(filepath); BufferedReader br = new BufferedReader(fr)) {

            String nextLine;
            // Add the next line to the output string if it
            while ((nextLine = br.readLine()) != null) {
                output.append(nextLine).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    /**
     * Converts an array of string rows to a list of data elements which can be used for data mining.
     *
     * @param rows           An array of data values in string CSV form
     * @param dataDescriptor The data descriptor used to convert the data values
     */
    private void extractDataSet(String[] rows, DataDescriptor dataDescriptor) {

        dataSet = new ArrayList<>();

        // Check if the data descriptor was given and create a new one if not
        boolean dataDescriptorPredefined = true;
        if (dataDescriptor == null) {

            this.dataDescriptor = new DataDescriptor(rows[0].split(","));
            dataDescriptorPredefined = false;
        }

        //Create all the data elements
        for (int r = 1; r < rows.length; r++) {

            //Split into the individual values
            String[] values = rows[r].split(",");

            // Don't try to add the value to the attributes unique values if the descriptor exists
            if (!dataDescriptorPredefined) {

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

    /**
     * Returns the data descriptor used for this data parser
     *
     * @return The data descriptor
     */
    public DataDescriptor getDataDescriptor() {

        return dataDescriptor;
    }

    /**
     * Returns the list of data elements extracted from the CSV.
     *
     * @return Arraylist of data elements
     */
    public ArrayList<DataElement> getDataSet() {

        return dataSet;
    }
}
