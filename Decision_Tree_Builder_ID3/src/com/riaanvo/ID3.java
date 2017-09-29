package com.riaanvo;

import java.util.ArrayList;

public class ID3 {
    private static int currentNodeIndex = 0;
    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> trainingData;

    private Node rootNode;

    public ID3(DataDescriptor dataDescriptor, ArrayList<DataElement> trainingData){
        this.dataDescriptor = dataDescriptor;
        this.trainingData = trainingData;
        buildModel();
    }

    private void buildModel(){
        long previousTime = System.currentTimeMillis();
        System.out.print("Building ID3 Tree:");

        rootNode = new Node(trainingData);

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");
    }

    public String createTreeDiagramScript(){
        String s = "";
        s += "digraph Tree {\n" + "node [shape=box, style=\"filled\", color=\"black\"];\n";
        s += rootNode.toString();
        s += "}";
        return s;
    }

    public String testDataSet(ArrayList<DataElement> testingData){
        long previousTime = System.currentTimeMillis();
        System.out.print("Testing model:");

        int numberOfClasses = dataDescriptor.getNumberOfClasses();
        int classIndex = dataDescriptor.getClassIndex();

        int[][] confusionMatrix = new int[numberOfClasses][numberOfClasses];

        for(DataElement dataElement: testingData){
            confusionMatrix[dataElement.values.get(classIndex)][rootNode.determineClass(dataElement)] ++;
        }

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms\n");
        String s = "";
        s += "Number of samples: " + testingData.size() + "\n";

        s += "Tr \\ Pr\n";
        for(int r = 0; r < confusionMatrix.length; r++){
            for(int c = 0; c < confusionMatrix[0].length; c++){
                s += confusionMatrix[r][c] + "\t";
            }

            if(r < confusionMatrix.length - 1) {
                s += "\n";
            }
        }

        if(confusionMatrix.length == 2 && confusionMatrix[0].length == 2){
            double accuracy = (double) (confusionMatrix[0][0] + confusionMatrix[1][1]) / (double) testingData.size() * 100;
            s += "\nAccuracy: " + accuracy + "%";
            double errorRate = (double) (confusionMatrix[0][1] + confusionMatrix[1][0]) / (double) testingData.size() * 100;
            s += "\nError Rate: " + errorRate + "%";
            double FAR = (double) (confusionMatrix[0][1]) / (double) (confusionMatrix[0][1] + confusionMatrix[0][0]) * 100;
            s += "\nFalse Alarm Rate: " + FAR + "%";
            double DR = (double) (confusionMatrix[1][1]) / (double) (confusionMatrix[1][1] + confusionMatrix[1][0]) * 100;
            s += "\nDetection Rate: " + DR + "%";
            double precision = (double) (confusionMatrix[1][1]) / (double) (confusionMatrix[0][1] + confusionMatrix[1][1]) * 100;
            s += "\nPrecision: " + precision + "%";
            double recall = DR;
            s += "\nRecall: " + recall + "%";
            double F1 = 2 * precision * recall / (precision + recall);
            s += "\nF1 score: " + F1 + "%";
        }

        return s;
    }

    private class Node{
        private int nodeIndex;
        private int attributeSplitIndex = -1;
        private double currentSampleEntropy = 1;
        private int sampleCount = 0;
        private int[] classCounts;
        private int currentClassValue = 0;
        private ArrayList<Node> subNodes;

        public Node(ArrayList<DataElement> samples){
            //Self assign a node index and increment the value
            nodeIndex = currentNodeIndex;
            currentNodeIndex ++;

            determineClassCounts(samples);

            determineMostCommonClass();

            currentSampleEntropy = getEntropy(samples);

            //If there are no samples don't do anything
            sampleCount = samples.size();
            if(sampleCount == 0) return;

            //Don't do recursion if there is no more
            if(isSingleClass()) return;

            constructSubNodes(samples);
        }

        private void determineClassCounts(ArrayList<DataElement> samples){
            //Generate the current class counts based on the samples
            classCounts = new int[dataDescriptor.getNumberOfClasses()];
            for(DataElement dataElement : samples){
                classCounts[dataElement.values.get(dataDescriptor.getClassIndex())] ++;
            }
        }

        private void determineMostCommonClass(){
            //Determine the current class with the most values
            for(int i = 0; i < classCounts.length; i ++){
                if(classCounts[i] > classCounts[currentClassValue]){
                    currentClassValue = i;
                }
            }
        }

        private boolean isSingleClass(){
            boolean isSingleClass = false;
            for(int i = 0; i < classCounts.length; i ++){
                if(classCounts[i] != 0){
                    if(!isSingleClass){
                        isSingleClass = true;
                    } else {
                        isSingleClass = false;
                        break;
                    }
                }
            }
            return isSingleClass;
        }

        private void constructSubNodes(ArrayList<DataElement> samples){
            subNodes = new ArrayList<Node>();

            int largestInfoGain = 0;
            double currentLargestInfoGain = Double.MIN_VALUE;
            double infoGain;

            for(int i = 0; i < dataDescriptor.getNumberOfAttributes(); i++){
                if(i == dataDescriptor.getClassIndex()) continue;

                infoGain = getInfoGain(samples, i);
                if(infoGain > currentLargestInfoGain){
                    currentLargestInfoGain = infoGain;
                    largestInfoGain = i;
                }
            }

            attributeSplitIndex = largestInfoGain;
            ArrayList<ArrayList<DataElement>> valueSubsets = getSubSets(samples, attributeSplitIndex);

            for(int i = 0; i < valueSubsets.size(); i ++){
                subNodes.add(new Node(valueSubsets.get(i)));
            }
        }

        private double getInfoGain(ArrayList<DataElement> samples, int attributeIndex){
            int sampleSize = samples.size();
            double sampleEntropy = getEntropy(samples);

            //Create subsets of the values
            ArrayList<ArrayList<DataElement>> valueSubsets = getSubSets(samples, attributeIndex);

            double subSetEntropySum = 0;

            for(int i = 0; i < valueSubsets.size(); i ++){
                if(valueSubsets.get(i).size() == 0) continue;
                subSetEntropySum += ((double) valueSubsets.get(i).size() / (double)sampleSize) *  getEntropy(valueSubsets.get(i));
            }

            return sampleEntropy - subSetEntropySum;
        }

        private double getEntropy(ArrayList<DataElement> samples){
            int classIndex = dataDescriptor.getClassIndex();
            int sampleSize = samples.size();
            int[] classValueCounts = new int[dataDescriptor.getAttributeValues(classIndex).size()];

            //Count the number of data elements for each attribute value
            for(DataElement dataElement: samples){
                classValueCounts[dataElement.values.get(classIndex)] ++;
            }

            double ent = 0;
            for(int i = 0; i < classValueCounts.length; i++){
                double p_ = (double)classValueCounts[i] / (double)sampleSize;
                if(p_ == 0) continue;
                ent += -p_ * Math.log(p_);
            }

            return ent;
        }

        private ArrayList<ArrayList<DataElement>> getSubSets(ArrayList<DataElement> samples, int attributeIndex){
            ArrayList<ArrayList<DataElement>> valueSubsets = new ArrayList<>();
            for(int i = 0; i < dataDescriptor.getAttributeValues(attributeIndex).size(); i++){
                valueSubsets.add(new ArrayList<>());
            }

            //Populate the subSets
            for(DataElement dataElement: samples){
                valueSubsets.get(dataElement.values.get(attributeIndex)).add(dataElement);
            }

            return valueSubsets;
        }

        public int getNodeIndex(){
            return nodeIndex;
        }

        public int getSampleCount(){
            return sampleCount;
        }

        public int determineClass(DataElement dataElement){
            if(subNodes == null) return currentClassValue;
            return subNodes.get(dataElement.values.get(attributeSplitIndex)).determineClass(dataElement);
        }

        public String toString(){
            //0 [label="petal length (cm) <= 2.45\ngini = 0.6665\nsamples = 120\nvalue = [40, 41, 39]\nclass = versicolor", fillcolor="#11111103"] ;

            String s = "";
            s += nodeIndex + " [label=\"";
            s += "Split on: " + dataDescriptor.getAttributeHeaderValue(attributeSplitIndex) + "\\n";
            s += "Entropy = " + currentSampleEntropy + "\\n";
            s += "Samples: " + sampleCount + "\\n";
            s += "Class counts: [";
            for(int i = 0; i < classCounts.length; i ++){
                s += classCounts[i];
                if(i < classCounts.length - 1){
                    s += ",";
                }
            }
            s += "]\\n";
            s += "Class: " + dataDescriptor.getAttributeValues(dataDescriptor.getClassIndex()).get(currentClassValue) + "\"";
            s += ", fillcolor=\"#" + 11111103 + "\"];\n";
            if(subNodes != null){
                for (int i = 0; i < subNodes.size(); i++ ) {
                    if(subNodes.get(i).getSampleCount() == 0) continue;
                    s += subNodes.get(i).toString();
                    //0 -> 1 [headlabel="True"] ;
                    s += getNodeIndex() + " -> " + subNodes.get(i).getNodeIndex() + "[label=\"" + dataDescriptor.getAttributeValues(attributeSplitIndex).get(i) + "\"]" + ";";
                    s += "\n";
                }
            }

            return s;
        }
    }
}


/*
  ID3 (Examples, Target_Attribute, Attributes)
    Create a root node for the tree
    If all examples are positive, Return the single-node tree Root, with label = +.
    If all examples are negative, Return the single-node tree Root, with label = -.
    If number of predicting attributes is empty, then Return the single node tree Root,
    with label = most common value of the target attribute in the examples.
    Otherwise Begin
        A ← The Attribute that best classifies examples.
        Decision Tree attribute for Root = A.
        For each possible value, vi, of A,
            Add a new tree branch below Root, corresponding to the test A = vi.
            Let Examples(vi) be the subset of examples that have the value vi for A
            If Examples(vi) is empty
                Then below this new branch add a leaf node with label = most common target value in the examples
            Else below this new branch add the subtree ID3 (Examples(vi), Target_Attribute, Attributes – {A})
    End
    Return Root
    */

/*
*
* if(classCounts == null) return s;
            s += dataDescriptor.getAttributeValues(dataDescriptor.getClassIndex()).get(currentClassValue) + " | [";
            for(Integer count : classCounts){
                s+= count + ", ";
            }
            s += "] | Split attribute: " + dataDescriptor.getAttributeHeaderValue(attributeSplitIndex) + "\n";

            if(subNodes != null) {
                for (Node node : subNodes) {
                    s += "\t" + node.toString() + "\n";
                }
            }
*/
