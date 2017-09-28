package com.riaanvo;


public class Main {

    public static void main(String[] args) {
        DataParser dataParser = new DataParser("/Users/riaanvo/Desktop/ADA_A2/mushroom-train(1).csv");

        ID3 id3Tree = new ID3(dataParser.getDataDescriptor(), dataParser.getTrainingDataSet());
    }

}

