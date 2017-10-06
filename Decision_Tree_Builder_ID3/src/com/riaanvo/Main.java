package com.riaanvo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;


/**
 * This program takes in a file containing a categorical data set and can create an ID3 model.
 */
public class Main {

    public static void main(String[] args) {
        String trainFile  = "/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-train(1).csv";
        String testFile = "/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-test(1).csv";
        boolean binarise = true;
        int nodeDepth = -1;

        DataDescriptor dataDescriptor;
        ArrayList<DataElement> trainDataSet;
        ArrayList<DataElement> testDataSet;



        DataParser trainingDataParser = new DataParser(trainFile, null);
        dataDescriptor = trainingDataParser.getDataDescriptor();
        trainDataSet = trainingDataParser.getDataSet();
        DataParser testingDataParser = new DataParser(testFile, dataDescriptor);
        testDataSet = testingDataParser.getDataSet();

        if(binarise){
            CategoricalDataPreprocessor trainPreProcessor = new CategoricalDataPreprocessor(trainDataSet, dataDescriptor, null);
            DataDescriptor newDataDescriptor = trainPreProcessor.getDataDescriptor();
            trainDataSet = trainPreProcessor.getDataSet();

            CategoricalDataPreprocessor testPreProcessor = new CategoricalDataPreprocessor(testDataSet, dataDescriptor, newDataDescriptor);
            testDataSet = testPreProcessor.getDataSet();
            dataDescriptor = newDataDescriptor;
        }


        ID3 id3Tree = new ID3(dataDescriptor, trainDataSet, nodeDepth);

        System.out.println("\n" + id3Tree.createTreeDiagramScript());
        System.out.println(id3Tree.testModel(testDataSet) + "\n");

        //System.out.println(id3Tree.predictClasses(testPreProcessor.getDataSet()) + "\n");

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

