package com.riaanvo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;


/**
 * This program takes in a file containing a categorical data set and can create an ID3 model.
 */
public class Main {

    public static void main(String[] args) {
        String trainFile = "/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-train(1).csv";
        String testFile = "/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-test(1).csv";
        String predictFile = "/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-test(1).csv";

        DataDescriptor dataDescriptor;
        ArrayList<DataElement> trainDataSet = new ArrayList<DataElement>();
        ArrayList<DataElement> testDataSet = new ArrayList<DataElement>();
        ArrayList<DataElement> predictDataSet = new ArrayList<DataElement>();

        int nodeDepth = -1;
        boolean binarise = true;
        boolean hasTestData = true;
        boolean hasPredictionData = true;
        boolean writeOutTreeStructure = false;
        boolean writeOutAnalysis = false;
        boolean writeOutPredictions = false;


        DataParser trainingDataParser = new DataParser(trainFile, null);
        dataDescriptor = trainingDataParser.getDataDescriptor();
        trainDataSet = trainingDataParser.getDataSet();
        System.out.println();

        if (hasTestData) {
            DataParser testingDataParser = new DataParser(testFile, dataDescriptor);
            testDataSet = testingDataParser.getDataSet();
            System.out.println();
        }

        if(hasPredictionData){
            DataParser predictDataParser = new DataParser(predictFile, dataDescriptor);
            predictDataSet = predictDataParser.getDataSet();
            System.out.println();
        }

        if (binarise) {
            CategoricalDataPreprocessor trainPreProcessor = new CategoricalDataPreprocessor(trainDataSet, dataDescriptor, null);
            DataDescriptor newDataDescriptor = trainPreProcessor.getDataDescriptor();
            trainDataSet = trainPreProcessor.getDataSet();
            System.out.println();

            if(hasTestData) {
                CategoricalDataPreprocessor testPreProcessor = new CategoricalDataPreprocessor(testDataSet, dataDescriptor, newDataDescriptor);
                testDataSet = testPreProcessor.getDataSet();
                System.out.println();
            }

            if(hasPredictionData) {
                CategoricalDataPreprocessor predictPreProcessor = new CategoricalDataPreprocessor(predictDataSet, dataDescriptor, newDataDescriptor);
                predictDataSet = predictPreProcessor.getDataSet();
                System.out.println();
            }

            dataDescriptor = newDataDescriptor;
        }

        ID3 id3Tree = new ID3(dataDescriptor, trainDataSet, nodeDepth);
        System.out.println();

        if(hasTestData) {
            System.out.println(id3Tree.testModel(testDataSet) + "\n");
            System.out.println();
        }

        if(hasPredictionData){
            System.out.println(id3Tree.predictClasses(predictDataSet) + "\n");
            System.out.println();
        }

        System.out.println("\n" + id3Tree.createTreeDiagramScript());

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

