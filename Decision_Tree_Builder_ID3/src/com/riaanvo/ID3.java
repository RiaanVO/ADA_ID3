package com.riaanvo;

import java.util.ArrayList;

public class ID3 {
    private static int currentNodeIndex = 0;
    private final DataDescriptor dataDescriptor;

    private Node rootNode;

    public ID3(DataDescriptor dataDescriptor, ArrayList<DataElement> trainingData){
        this.dataDescriptor = dataDescriptor;
        buildModel(trainingData);
    }

    private void buildModel(ArrayList<DataElement> trainingData){
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
        int classIndex = dataDescriptor.getClassAttributeIndex();

        int[][] confusionMatrix = new int[numberOfClasses][numberOfClasses];

        for(DataElement dataElement: testingData){
            confusionMatrix[dataElement.getValue(classIndex)][rootNode.determineClass(dataElement)] ++;
        }

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms\n");
        StringBuilder s = new StringBuilder();
        s.append("Number of samples: ").append(testingData.size()).append("\n");

        s.append("Tr \\ Pr\n");
        for(int r = 0; r < confusionMatrix.length; r++){
            for(int c = 0; c < confusionMatrix[0].length; c++){
                s.append(confusionMatrix[r][c]).append("\t");
            }

            if(r < confusionMatrix.length - 1) {
                s.append("\n");
            }
        }

        if(confusionMatrix.length == 2 && confusionMatrix[0].length == 2){
            double accuracy = (double) (confusionMatrix[0][0] + confusionMatrix[1][1]) / (double) testingData.size() * 100;
            s.append("\nAccuracy: ").append(accuracy).append("%");
            double errorRate = (double) (confusionMatrix[0][1] + confusionMatrix[1][0]) / (double) testingData.size() * 100;
            s.append("\nError Rate: ").append(errorRate).append("%");
            double FAR = (double) (confusionMatrix[0][1]) / (double) (confusionMatrix[0][1] + confusionMatrix[0][0]) * 100;
            s.append("\nFalse Alarm Rate: ").append(FAR).append("%");
            double DR = (double) (confusionMatrix[1][1]) / (double) (confusionMatrix[1][1] + confusionMatrix[1][0]) * 100;
            s.append("\nDetection Rate: ").append(DR).append("%");
            double precision = (double) (confusionMatrix[1][1]) / (double) (confusionMatrix[0][1] + confusionMatrix[1][1]) * 100;
            s.append("\nPrecision: ").append(precision).append("%");
            double recall = DR;
            s.append("\nRecall: ").append(recall).append("%");
            double F1 = 2 * precision * recall / (precision + recall);
            s.append("\nF1 score: ").append(F1).append("%");
        }

        return s.toString();
    }

    private class Node{
        private final int nodeIndex;
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
                classCounts[dataElement.getValue(dataDescriptor.getClassAttributeIndex())] ++;
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
            for (int classCount : classCounts) {
                if (classCount != 0) {
                    if (!isSingleClass) {
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
            subNodes = new ArrayList<>();

            int largestInfoGain = 0;
            double currentLargestInfoGain = Double.MIN_VALUE;
            double infoGain;

            for(int i = 0; i < dataDescriptor.getNumberOfAttributes(); i++){
                if(i == dataDescriptor.getClassAttributeIndex()) continue;

                infoGain = getInfoGain(samples, i);
                if(infoGain > currentLargestInfoGain){
                    currentLargestInfoGain = infoGain;
                    largestInfoGain = i;
                }
            }

            attributeSplitIndex = largestInfoGain;
            ArrayList<ArrayList<DataElement>> valueSubsets = getSubSets(samples, attributeSplitIndex);

            for (ArrayList<DataElement> valueSubset : valueSubsets) {
                subNodes.add(new Node(valueSubset));
            }
        }

        private double getInfoGain(ArrayList<DataElement> samples, int attributeIndex){
            int sampleSize = samples.size();
            double sampleEntropy = getEntropy(samples);

            //Create subsets of the values
            ArrayList<ArrayList<DataElement>> valueSubsets = getSubSets(samples, attributeIndex);

            double subSetEntropySum = 0;

            for (ArrayList<DataElement> valueSubset : valueSubsets) {
                if (valueSubset.size() == 0) continue;
                subSetEntropySum += ((double) valueSubset.size() / (double) sampleSize) * getEntropy(valueSubset);
            }

            return sampleEntropy - subSetEntropySum;
        }

        private double getEntropy(ArrayList<DataElement> samples){
            int classIndex = dataDescriptor.getClassAttributeIndex();
            int sampleSize = samples.size();
            int[] classValueCounts = new int[dataDescriptor.getUniqueAttributeValues(classIndex).size()];

            //Count the number of data elements for each attribute value
            for(DataElement dataElement: samples){
                classValueCounts[dataElement.getValue(classIndex)] ++;
            }

            double ent = 0;
            for (int classValueCount : classValueCounts) {
                double p_ = (double) classValueCount / (double) sampleSize;
                if (p_ == 0) continue;
                ent += -p_ * Math.log(p_);
            }

            return ent;
        }

        private ArrayList<ArrayList<DataElement>> getSubSets(ArrayList<DataElement> samples, int attributeIndex){
            ArrayList<ArrayList<DataElement>> valueSubsets = new ArrayList<>();
            for(int i = 0; i < dataDescriptor.getUniqueAttributeValues(attributeIndex).size(); i++){
                valueSubsets.add(new ArrayList<>());
            }

            //Populate the subSets
            for(DataElement dataElement: samples){
                valueSubsets.get(dataElement.getValue(attributeIndex)).add(dataElement);
            }

            return valueSubsets;
        }

        private int getNodeIndex(){
            return nodeIndex;
        }

        private int getSampleCount(){
            return sampleCount;
        }

        private int determineClass(DataElement dataElement){
            if(subNodes == null) return currentClassValue;
            return subNodes.get(dataElement.getValue(attributeSplitIndex)).determineClass(dataElement);
        }

        public String toString(){
            StringBuilder s = new StringBuilder();
            s.append(nodeIndex).append(" [label=\"");
            s.append("Split on: ").append(dataDescriptor.getAttribute(attributeSplitIndex)).append("\\n");
            s.append("Entropy = ").append(currentSampleEntropy).append("\\n");
            s.append("Samples: ").append(sampleCount).append("\\n");
            s.append("Class counts: [");
            for(int i = 0; i < classCounts.length; i ++){
                s.append(classCounts[i]);
                if(i < classCounts.length - 1){
                    s.append(",");
                }
            }
            s.append("]\\n");
            s.append("Class: ").append(dataDescriptor.getUniqueAttributeValues(dataDescriptor.getClassAttributeIndex()).get(currentClassValue)).append("\"");
            s.append(", fillcolor=\"#" + 11111103 + "\"];\n");
            if(subNodes != null){
                for (int i = 0; i < subNodes.size(); i++ ) {
                    if(subNodes.get(i).getSampleCount() == 0) continue;
                    s.append(subNodes.get(i).toString());
                    s.append(getNodeIndex()).append(" -> ").append(subNodes.get(i).getNodeIndex());
                    s.append("[label=\"").append(dataDescriptor.getUniqueAttributeValues(attributeSplitIndex).get(i)).append("\"]").append(";");
                    s.append("\n");
                }
            }

            return s.toString();
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
