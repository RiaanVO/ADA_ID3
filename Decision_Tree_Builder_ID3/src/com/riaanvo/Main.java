package com.riaanvo;


public class Main {

    public static void main(String[] args) {
        DataParser trainingDataParser = new DataParser("/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-train(1).csv", null);
        System.out.println();

        DataParser testingDataParser = new DataParser("/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-test(1).csv", trainingDataParser.getDataDescriptor());
        System.out.println();

        ID3 id3Tree = new ID3(trainingDataParser.getDataDescriptor(), trainingDataParser.getDataSet());
        System.out.println();

        System.out.println(id3Tree.testModel(testingDataParser.getDataSet()) + "\n");

        System.out.println(id3Tree.createTreeDiagramScript());



    }

}

