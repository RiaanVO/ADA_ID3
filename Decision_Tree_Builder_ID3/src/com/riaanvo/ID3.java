package com.riaanvo;

import java.util.ArrayList;

public class ID3 {
    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> trainingData;

    private Node rootNode;

    public ID3(DataDescriptor dataDescriptor, ArrayList<DataElement> trainingData){
        this.dataDescriptor = dataDescriptor;
        this.trainingData = trainingData;
        buildModel();
    }

    private void buildModel(){
        System.out.println("Starting tree construction");
        rootNode = new Node(trainingData);
        System.out.println("tree built");
        System.out.println(rootNode.toString());
    }

    private class Node{
        private int attributeSplitIndex = -1;
        //private double gini;
        //private ArrayList<DataElement> samples;
        private int[] classCounts;
        private int currentClassValue = 0;
        private ArrayList<Node> subNodes;

        public Node(ArrayList<DataElement> samples){
            //Store the samples parsed into it
            //this.samples = samples;

            if(samples.size() == 0) return;

            //Generate the current class counts based on the samples
            classCounts = new int[dataDescriptor.getNumberOfClasses()];
            for(DataElement dataElement : samples){
                classCounts[dataElement.values.get(dataDescriptor.getClassIndex())] ++;
            }

            //Determine the current class with the most values
            for(int i = 0; i < classCounts.length; i ++){
                if(classCounts[i] > classCounts[currentClassValue]){
                    currentClassValue = i;
                }
            }

            //Determine if the node needs sub Nodes based on class counts
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

            //Don't do recursion if there is no more
            if(isSingleClass) return;
            constructSubnodes(samples);
        }

        private void constructSubnodes(ArrayList<DataElement> samples){
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

        public String toString(){
            String s = "";
            if(classCounts == null) return s;
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