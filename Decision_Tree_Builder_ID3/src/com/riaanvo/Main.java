package com.riaanvo;

import org.docopt.Docopt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;


/**
 * This program takes in a file containing a categorical data set and can create an ID3 model.
 */
public class Main {
    /*
    private static final String doc = "Naval Fate.\n"
            + "\n"
            + "Usage:\n"
            + "  naval_fate ship new <name>...\n"
            + "  naval_fate ship <name> move <x> <y> [--speed=<kn>]\n"
            + "  naval_fate ship shoot <x> <y>\n"
            + "  naval_fate mine (set|remove) <x> <y> [--moored | --drifting]\n"
            + "  naval_fate (-h | --help)\n"
            + "  naval_fate --version\n"
            + "\n"
            + "Options:\n"
            + "  -h --help     Show this screen.\n"
            + "  --version     Show version.\n"
            + "  --speed=<kn>  Speed in knots [default: 10].\n"
            + "  --moored      Moored (anchored) mine.\n"
            + "  --drifting    Drifting mine.\n"
            + "\n";

             [<PredictFile>] [<TreeNodeDepth>] --binarise [<OutTreeFileName>] [<OutAnalysisFileName>] [<OutPredictFileName>]
*/
    private static final String doc = "ID3 Builder\n\n"
            + "Usage:\n"
            + "\tID3_Builder <TrainFile> [--oTreeFile=OTREEFILE]\n"
            + "\tID3_Builder <TrainFile> [--oTreeFile=OTREEFILE --binarise --treeDepth=TREEDEPTH]\n"
            + "\tID3_Builder <TrainFile> [--testfile=TESTFILE --oAnalysisFile=OANALYSISFILE]\n"
            + "\tID3_Builder <TrainFile> [--predictfile=PREDICTFILE --oPredictFile=OPREDICTFILE]\n"
            + "\tID3_Builder <TrainFile> [--testfile=TESTFILE --oAnalysisFile=OANALYSISFILE --predictfile=PREDICTFILE --oPredictFile=OPREDICTFILE --binarise --treeDepth=TREEDEPTH]\n"
            + "\tID3_Builder (-h | --help)\n"
            + "\tID3_Builder --version\n"
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
            + "\n";

    private static DataDescriptor dataDescriptor;
    private static ArrayList<DataElement> trainDataSet = new ArrayList<DataElement>();
    private static ArrayList<DataElement> testDataSet = new ArrayList<DataElement>();
    private static ArrayList<DataElement> predictDataSet = new ArrayList<DataElement>();

    public static void main(final String[] args) {

        final Map<String, Object> opts = new Docopt(doc).withVersion("ID3 Builder V1.0").parse(args);
        System.out.println(opts);

        System.out.println(opts.get("<TrainFile>"));
        System.out.println(opts.get("--oTreeFile"));

        System.out.println(opts.get("--binarise"));
        System.out.println(opts.get("--treeDepth"));

        System.out.println(opts.get("--testfile"));
        System.out.println(opts.get("--oAnalysisFile"));

        System.out.println(opts.get("--predictfile"));
        System.out.println(opts.get("--oPredictFile"));



        String trainFile = opts.get("<TrainFile>").toString(); //"/Users/riaanvo/ADA_ID3/ADA_A2/mushroom-train(1).csv";
        int nodeDepth = Integer.parseInt(opts.get("--treeDepth").toString()); //-1;
        boolean binarise = (opts.get("--binarise").toString().equals("true")); //false;

        // Extract arguments in to booleans for faster comparisons
        boolean hasOutputStructureFile = opts.get("--oTreeFile") != null;

        boolean hasTestData = opts.get("--testfile") != null;
        boolean hasOutputAnalysisFile = opts.get("--oAnalysisFile") != null;

        boolean hasPredictionData = opts.get("--predictfile") != null;
        boolean hasOutputPredictFile = opts.get("--oPredictFile") != null;

        DataParser dataParser = new DataParser();
        dataParser.parseData(trainFile, null);
        dataDescriptor = dataParser.getDataDescriptor();
        trainDataSet = dataParser.getDataSet();
        System.out.println();

        if (hasTestData) {
            dataParser.parseData(opts.get("--testfile").toString(), dataDescriptor);
            testDataSet = dataParser.getDataSet();
            System.out.println();
        }

        if (hasPredictionData) {
            dataParser.parseData(opts.get("--predictfile").toString(), dataDescriptor);
            predictDataSet = dataParser.getDataSet();
            System.out.println();
        }

        if (binarise) {
            binariseData(hasTestData, hasPredictionData);
        }

        // Build the ID3 tree
        ID3 id3Tree = new ID3(trainDataSet, nodeDepth);
        System.out.println();

        String diagramScript = id3Tree.createTreeDiagramScript();
        if(hasOutputStructureFile){
            writeToFile(opts.get("--oTreeFile").toString(), diagramScript);
        } else {
            System.out.println(diagramScript);
        }

        if (hasTestData) {
            String testInformation = id3Tree.testModel(testDataSet);
            if(hasOutputAnalysisFile){
                writeToFile(opts.get("--oAnalysisFile").toString(), testInformation);
            } else {
                System.out.println(testInformation + "\n");
                System.out.println();
            }
        }

        if (hasPredictionData) {
            String predictionInformation = id3Tree.predictClasses(predictDataSet);
            if(hasOutputPredictFile){
                writeToFile(opts.get("--oPredictFile").toString(), predictionInformation);
            } else {
                System.out.println(predictionInformation + "\n");
                System.out.println();
            }
        }
    }

    private static void binariseData(boolean hasTestData, boolean hasPredictionData) {

        CategoricalDataPreprocessor preprocessor = new CategoricalDataPreprocessor();
        preprocessor.processData(trainDataSet, dataDescriptor, null);
        DataDescriptor binarisedDataDescriptor = preprocessor.getDataDescriptor();
        trainDataSet = preprocessor.getDataSet();
        System.out.println();

        if (hasTestData) {
            preprocessor.processData(testDataSet, dataDescriptor, binarisedDataDescriptor);
            testDataSet = preprocessor.getDataSet();
            System.out.println();
        }

        if (hasPredictionData) {
            preprocessor.processData(predictDataSet, dataDescriptor, binarisedDataDescriptor);
            predictDataSet = preprocessor.getDataSet();
            System.out.println();
        }

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

