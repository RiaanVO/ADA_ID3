package com.riaanvo;

import java.io.BufferedWriter;
import java.io.FileWriter;


/**
 * This program takes in a file containing a categorical data set and can create an ID3 model.
 */
public class Main {

    public static void main(String[] args) {
        DataParser trainingDataParser = new DataParser("/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-train(1).csv", null);
        System.out.println();

        DataParser testingDataParser = new DataParser("/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-test(1).csv", trainingDataParser.getDataDescriptor());
        System.out.println();

        ID3 id3Tree = new ID3(trainingDataParser.getDataDescriptor(), trainingDataParser.getDataSet());

        System.out.println(id3Tree.testModel(testingDataParser.getDataSet()) + "\n");

        System.out.println(id3Tree.predictClasses(testingDataParser.getDataSet()) + "\n");

        System.out.println(id3Tree.createTreeDiagramScript());

    }

    /**
     * Writes the passed in string to the desired file name.
     *
     * @param fileName     Name of the output file
     * @param fileContents Contents to be placed in the file
     */
    private static void writeToFile(String fileName, String fileContents) {
        System.out.println("Writing to file: " + fileName);
        try (FileWriter fileWriter = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fileWriter)) {
            bw.write(fileContents);
            System.out.println("Writing Complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

