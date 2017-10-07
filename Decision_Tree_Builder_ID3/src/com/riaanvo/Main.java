package com.riaanvo;

import org.docopt.Docopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;


/**
 * This program takes in a file containing a categorical data set and can create an ID3 model. Different arguments will
 * change the behaviour of the program to include model testing and data classification based on the model.
 */
public class Main {

    private static final String doc = "ID3 Builder\n\n"
            + "Usage:\n"
            + "  ID3_Builder <TrainFile> [--oTreeFile=OTREEFILE]\n"
            + "  ID3_Builder <TrainFile> [--oTreeFile=OTREEFILE --binarise --treeDepth=TREEDEPTH --showEmptyLeaves --debug]\n"
            + "  ID3_Builder <TrainFile> [--testfile=TESTFILE --oAnalysisFile=OANALYSISFILE]\n"
            + "  ID3_Builder <TrainFile> [--predictfile=PREDICTFILE --oPredictFile=OPREDICTFILE]\n"
            + "  ID3_Builder <TrainFile> [--testfile=TESTFILE --oAnalysisFile=OANALYSISFILE --predictfile=PREDICTFILE --oPredictFile=OPREDICTFILE --binarise --treeDepth=TREEDEPTH]\n"
            + "  ID3_Builder (-h | --help)\n"
            + "  ID3_Builder --version\n"
            + "\n"

            + "Options:\n"
            + "  -h --help                       Show this screen.\n"
            + "  --version                       Show version.\n"
            + "  --testfile=TESTFILE             Test data set file. \n"
            + "  --predictfile=PREDICTFILE       Data set file to be predicted. \n"
            + "  --oTreeFile=OTREEFILE           Filename for the tree output. \n"
            + "  --oAnalysisFile=OANALYSISFILE   Filename for the analysis output. \n"
            + "  --oPredictFile=OPREDICTFILE     Filename for the prediction output. \n"
            + "  --binarise                      Converts all categorical data to binary attributes. \n"
            + "  --treeDepth=TREEDEPTH           The number of decisions the tree is restricted to. [default: -1]\n"
            + "  --showEmptyLeaves               Includes the empty leaves in the model. \n"
            + "  --debug                         Prints out the data sets for debugging \n"
            + "\n";

    private static DataDescriptor dataDescriptor;
    private static ArrayList<DataElement> trainDataSet;
    private static ArrayList<DataElement> testDataSet;
    private static ArrayList<DataElement> predictDataSet;
    private static boolean debugMode;

    /**
     * This is the starting point of the ID3 Builder program. It takes in the options and arguments for building and
     * outputting the decision tree.
     * @param args The options and arguments for the ID3 builder
     */
    public static void main(final String[] args) {

        // Extract the arguments from the commandline into the disired tokens
        final Map<String, Object> opts = new Docopt(doc).withVersion("ID3 Builder V1.0").parse(args);

        // Extract the file path, node depth and whether to binarise from the arguments
        String trainFile = opts.get("<TrainFile>").toString();
        int nodeDepth = Integer.parseInt(opts.get("--treeDepth").toString());
        boolean binarise = (opts.get("--binarise").toString().equals("true"));
        boolean showEmptyLeaves = (opts.get("--showEmptyLeaves").toString().equals("true"));
        debugMode = (opts.get("--debug").toString().equals("true"));


        // Check if the aditional options have been included and store the result in booleans
        boolean hasOutputStructureFile = opts.get("--oTreeFile") != null;
        boolean hasTestData = opts.get("--testfile") != null;
        boolean hasOutputAnalysisFile = opts.get("--oAnalysisFile") != null;
        boolean hasPredictionData = opts.get("--predictfile") != null;
        boolean hasOutputPredictFile = opts.get("--oPredictFile") != null;

        // Create a data parser to convert the csv file into data objects
        DataParser dataParser = new DataParser();

        // Extract the training data set
        dataParser.parseData(trainFile, null);
        dataDescriptor = dataParser.getDataDescriptor();
        trainDataSet = dataParser.getDataSet();

        if(debugMode){
            displayDataSet(trainDataSet, dataDescriptor);
        }

        // If there is a test data set extract the contents
        if (hasTestData) {

            dataParser.parseData(opts.get("--testfile").toString(), dataDescriptor);
            testDataSet = dataParser.getDataSet();
        }

        // If there is a data set to predict classes for extract the contents
        if (hasPredictionData) {

            dataParser.parseData(opts.get("--predictfile").toString(), dataDescriptor);
            predictDataSet = dataParser.getDataSet();
        }

        // If the binarise option was included, convert all categorical data into binarised attributes
        if (binarise) {

            binariseDataSets(hasTestData, hasPredictionData);
        }

        // Build the ID3 decision tree model
        ID3 id3Tree = new ID3(trainDataSet, nodeDepth);

        // Create a text layout of the model
        String diagramScript = id3Tree.createTreeDiagramScript(showEmptyLeaves);

        // If there is an output file, write out the text model, else print it to the CLI
        if (hasOutputStructureFile) {

            writeToFile(opts.get("--oTreeFile").toString(), diagramScript);
        } else {

            System.out.println(diagramScript);
        }

        // If there is a test data set, test the performance of the model
        if (hasTestData) {

            String testInformation = id3Tree.testModel(testDataSet);

            // If an output file was provided write the analysis to it, else print it to the CLI
            if (hasOutputAnalysisFile) {

                writeToFile(opts.get("--oAnalysisFile").toString(), testInformation);
            } else {

                System.out.println(testInformation + "\n");
            }
        }

        // If there is a data set to predict classes for, use the model to predict the classes
        if (hasPredictionData) {

            String predictionInformation = id3Tree.predictClasses(predictDataSet);

            // If there is an output file, write the predictions to that file, else display to the CLI
            if (hasOutputPredictFile) {

                writeToFile(opts.get("--oPredictFile").toString(), predictionInformation);
            } else {

                System.out.println(predictionInformation + "\n");
            }
        }
    }

    /**
     * This method converts all the stored data sets to binarised attribute data sets. This can be used to create
     * decision trees with only true/false decisions and not multiple route decisions
     *
     * @param hasTestData       Does the test set exist
     * @param hasPredictionData Does the prediction set exist
     */
    private static void binariseDataSets(boolean hasTestData, boolean hasPredictionData) {

        // Create a data preprocessor to convert to binarised data
        DataPreprocessor preprocessor = new DataPreprocessor();

        // Convert the training data set to the new binarised data set
        preprocessor.binariseDataSet(trainDataSet, dataDescriptor);
        trainDataSet = preprocessor.getDataSet();

        if(debugMode) {
            displayDataSet(trainDataSet, preprocessor.getDataDescriptor());
        }

        // If a test set was provided, binarise the values and store the new data set
        if (hasTestData) {

            preprocessor.binariseDataSet(testDataSet, dataDescriptor);
            testDataSet = preprocessor.getDataSet();
        }

        // If a prediction set was provided, binarise the values and store the new data set
        if (hasPredictionData) {

            preprocessor.binariseDataSet(predictDataSet, dataDescriptor);
            predictDataSet = preprocessor.getDataSet();
        }
    }

    /**
     * Writes the passed in string to the desired file name.
     *
     * @param fileName     Name of the output file
     * @param fileContents Contents to be placed in the file
     */
    private static void writeToFile(String fileName, String fileContents) {
        System.out.print("Writing to file: " + fileName);
        try (FileWriter fileWriter = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fileWriter)) {
            bw.write(fileContents);
            System.out.println(" | COMPLETE");
        } catch (Exception e) {
            System.out.println("\nWriting Failed");
            e.printStackTrace();
        }
    }


    /**
     * Displays the first desired number of data elements from the data set and the data descriptor.
     *
     * @param dataSet Data set to be displayed
     * @param dataDescriptor Data descriptor to be displayed
     */
    private static void displayDataSet(ArrayList<DataElement> dataSet, DataDescriptor dataDescriptor) {

        // Set the number of elements to show to 10 or less depending on the data set
        int numberOfElementsToShow = 10;
        if(numberOfElementsToShow > dataSet.size()) {

            numberOfElementsToShow = dataSet.size();
        }

        StringBuilder s = new StringBuilder();

        //Check if the data descriptor is null and add the output if it is not
        if (dataDescriptor != null) {

            s.append(dataDescriptor.toString());
        }

        // Print out the desired number of rows of data
        s.append("\n\nData output Actual:\n");
        for (int i = 0; i < numberOfElementsToShow; i++) {

            s.append(dataSet.get(i).toStringInts()).append("\n");
        }

        // Print out the desired number of rows of data
        s.append("\n\nData output String conversion:\n");
        for (int i = 0; i < numberOfElementsToShow; i++) {

            s.append(dataSet.get(i).toString()).append("\n");
        }

        System.out.println(s.toString());
    }

}

