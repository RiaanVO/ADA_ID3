package com.riaanvo;

import java.util.ArrayList;

/**
 * This class contains the ID3 model and allows the testing and printing of the model.
 */
public class ID3 {

    //Used for determining the decision tree structure
    private int currentNodeIndex = 0;
    private final int maxNodeDepth;

    private final DataDescriptor dataDescriptor;
    private Node rootNode;
    private final String decFormat = "%.3f";

    /**
     * Constructor for the ID3 model. It takes in the data descriptor for the data set as well as the training data,
     * it then creates the model and stores the structure.
     * @param trainingData Data set to build the model with
     * @param maxNodeDepth The max node depth of the tree
     */
    public ID3(ArrayList<DataElement> trainingData, int maxNodeDepth) {

        this.dataDescriptor = DataElement.getDataDescriptor();
        this.maxNodeDepth = maxNodeDepth;
        buildModel(trainingData);
    }

    /**
     * This method builds the ID3 model using the provided training data.
     *
     * @param trainingData List of data elements in the training data set
     */
    private void buildModel(ArrayList<DataElement> trainingData) {

        // Store the starting time of model construction
        long previousTime = System.currentTimeMillis();
        System.out.print("Building ID3 Tree:");

        // Reset node indexing if a new model is built
        currentNodeIndex = 0;

        // Create a string to contain indicators of which attributes have been used
        StringBuilder attributesLeft = new StringBuilder();
        for (int i = 0; i < dataDescriptor.getNumberOfAttributes(); i++) {

            // If the index is the class attribute set it to one
            if (dataDescriptor.getClassAttributeIndex() == i) {
                attributesLeft.append("1");
            } else {
                attributesLeft.append("0");
            }
        }

        // Create and store the root node of the model. This will recursively construct the decision tree
        rootNode = new Node(trainingData, attributesLeft.toString(), 0);

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms\n");
    }

    /**
     * Creates a string containing the script used to display a decision tree model using graphviz.
     *
     * @return The ID3 model as a script
     */
    public String createTreeDiagramScript(boolean showEmptyLeaves) {

        String s = "";
        s += "digraph Tree {\nnode [shape=box, style=\"filled\", color=\"black\"];\n";
        s += rootNode.toString(showEmptyLeaves);
        s += "}\n";
        return s;
    }

    /**
     * Tests the model with the provided data set and returns a string containing the confusion matrix and
     * accuracy statistics based on the test data set.
     *
     * @param testDataSet List of data elements
     * @return A string containing the test results
     */
    public String testModel(ArrayList<DataElement> testDataSet) {

        // Store the start time of testing
        long previousTime = System.currentTimeMillis();
        System.out.print("Testing model:");

        int numberOfClasses = dataDescriptor.getNumberOfClasses();
        int classIndex = dataDescriptor.getClassAttributeIndex();

        // Loop through all the data elements and increment counters in the confusion matrix
        int[][] confusionMatrix = new int[numberOfClasses][numberOfClasses];
        for (DataElement dataElement : testDataSet) {
            confusionMatrix[dataElement.getValue(classIndex)][rootNode.determineClass(dataElement)]++;
        }

        // Display the time taken to test the data set
        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");

        StringBuilder s = new StringBuilder();
        s.append("Number of samples: ").append(testDataSet.size()).append("\n");

        // Display the confusion matrix
        s.append("True Class \\ Predicted Class\n \\\t");

        // Display hypothesis cases
        for (int h = 0; h < confusionMatrix.length; h++) {
            s.append("H").append(h).append("\t");
        }
        s.append("\n");

        // Display the confusion matrix values
        for (int r = 0; r < confusionMatrix.length; r++) {
            s.append("H").append(r).append("\t");

            for (int c = 0; c < confusionMatrix[0].length; c++) {
                s.append(confusionMatrix[r][c]).append("\t");
            }

            if (r < confusionMatrix.length - 1) {
                s.append("\n");
            }
        }

        // If there are only two classes display the accuracy statistics
        if (dataDescriptor.getNumberOfClasses() == 2) {
            s.append("\n\nAccuracy Statistics:");

            double accuracy = (double) (confusionMatrix[0][0] + confusionMatrix[1][1]) / (double) testDataSet.size() * 100;
            s.append("\nAccuracy: ").append(String.format(decFormat, accuracy)).append("%");

            double errorRate = (double) (confusionMatrix[0][1] + confusionMatrix[1][0]) / (double) testDataSet.size() * 100;
            s.append("\nError Rate: ").append(String.format(decFormat, errorRate)).append("%");

            double FAR = (double) (confusionMatrix[0][1]) / (double) (confusionMatrix[0][1] + confusionMatrix[0][0]) * 100;
            s.append("\nFalse Alarm Rate: ").append(String.format(decFormat, FAR)).append("%");

            double DR = (double) (confusionMatrix[1][1]) / (double) (confusionMatrix[1][1] + confusionMatrix[1][0]) * 100;
            s.append("\nDetection Rate: ").append(String.format(decFormat, DR)).append("%");

            double precision = (double) (confusionMatrix[1][1]) / (double) (confusionMatrix[0][1] + confusionMatrix[1][1]) * 100;
            s.append("\nPrecision: ").append(String.format(decFormat, precision)).append("%");

            double recall = DR;
            s.append("\nRecall: ").append(String.format(decFormat, recall)).append("%");

            double F1 = 2 * precision * recall / (precision + recall);
            s.append("\nF1 score: ").append(String.format(decFormat, F1)).append("%");
        }

        return s.toString();
    }

    /**
     * Takes in a data set and predicts the class for each data value
     *
     * @param dataSet List of data elements
     * @return A string containing all the predicted values
     */
    public String predictClasses(ArrayList<DataElement> dataSet) {
        StringBuilder s = new StringBuilder();
        s.append("Predicted Classes:\n");

        long previousTime = System.currentTimeMillis();
        System.out.print("Predicting Classes:");

        for (DataElement dataElement : dataSet) {
            String value = dataDescriptor.indexToValue(dataDescriptor.getClassAttributeIndex(), rootNode.determineClass(dataElement));
            s.append(value).append("\n");
        }

        // Display the time taken to predict the data set
        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");

        return s.toString();
    }


    /**
     * The java object that defines the nodes of a decision tree. When a node is created it will try to recursively add
     * sub nodes to build a decision tree until there are no more samples left undefined or there are no more attributes
     * to break the data up into.
     * <p>
     * This class provides the implementation of the following algorithm
     * ID3 (Examples, Target_Attribute, Attributes)
     * Create a root node for the tree
     * If all examples are positive, Return the single-node tree Root, with label = +.
     * If all examples are negative, Return the single-node tree Root, with label = -.
     * If number of predicting attributes is empty, then Return the single node tree Root,
     * with label = most common value of the target attribute in the examples.
     * Otherwise Begin
     * A ← The Attribute that best classifies examples.
     * Decision Tree attribute for Root = A.
     * For each possible value, vi, of A,
     * Add a new tree branch below Root, corresponding to the test A = vi.
     * Let Examples(vi) be the subset of examples that have the value vi for A
     * If Examples(vi) is empty
     * Then below this new branch add a leaf node with label = most common target value in the examples
     * Else below this new branch add the subtree ID3 (Examples(vi), Target_Attribute, Attributes – {A})
     * End
     * Return Root
     */
    private class Node {

        private final int nodeIndex;
        private int attributeSplitIndex = -1;
        private double informationGain = 0;
        private int mostCommonClass;

        private double currentEntropy = 0;

        private int sampleCount = 0;
        private int[] classCounts;
        private ArrayList<Node> subNodes;

        /**
         * Constructor for creating a node in a decision tree. Functions recursively and will create sub nodes until all
         * data is split or there are no more attributes to split on.
         *
         * @param samples        List of data elements
         * @param attributesLeft String defining which attributes can be used to split the data
         */
        public Node(ArrayList<DataElement> samples, String attributesLeft, int nodeDepth) {

            // Self assign a node index and increment the value
            nodeIndex = currentNodeIndex++;

            // Get sample count and stop if there are no samples
            sampleCount = samples.size();
            if (sampleCount == 0) return;

            // Count the number of data elements for each class value
            classCounts = extractClassCounts(samples);

            // Determine the class with the most number of samples
            mostCommonClass = determineMostCommonClass();

            // Determine the current sample entropy for this node
            currentEntropy = calculateEntropy(samples);

            // Check if this node is a single class and stop recursion
            if (isSingleClass()) return;

            // Stop if the max node depth is reached
            if(nodeDepth >= maxNodeDepth && maxNodeDepth != -1) return;

            constructSubNodes(samples, attributesLeft, nodeDepth);
        }

        /**
         * Extracts the current number of each class that is present in the given sample.
         *
         * @param samples A list of data elements
         * @return A array of integers containing class value counts
         */
        private int[] extractClassCounts(ArrayList<DataElement> samples) {

            int classAttributeIndex = dataDescriptor.getClassAttributeIndex();

            // Count the number of data elements for each class value
            int[] counts = new int[dataDescriptor.getNumberOfClasses()];
            for (DataElement dataElement : samples) {

                counts[dataElement.getValue(classAttributeIndex)]++;
            }

            return counts;
        }

        /**
         * Returns the class value with the largest number of data samples in it.
         *
         * @return The value index for the most common class
         */
        private int determineMostCommonClass() {

            //Determine the current class with the most values
            int currentClassIndex = 0;
            for (int i = 0; i < classCounts.length; i++) {

                //Check if this class has a higher count
                if (classCounts[i] > classCounts[currentClassIndex]) {

                    currentClassIndex = i;
                }
            }

            return currentClassIndex;
        }

        /**
         * Determines if all the sample data elements are of a single class.
         *
         * @return If the samples are all a single class
         */
        private boolean isSingleClass() {

            boolean isSingleClass = false;
            for (int classCount : classCounts) {

                // Check if the number of data elements in this class is not zero
                if (classCount != 0) {

                    // Check if there is already another class with more than one sample
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

        /**
         * Creates the sub nodes for this node in the tree. Takes in the current list of samples and the attributes that
         * can be used to split the data and determines which attribute has the highest information gain.
         *
         * @param samples        List of data elements to be split
         * @param attributesLeft A string describing the attributes that can be used to split the data
         */
        private void constructSubNodes(ArrayList<DataElement> samples, String attributesLeft, int currentNodeDepth) {

            int largestInfoGainAttributeIndex = 0;
            double currentLargestInfoGain = Double.MIN_VALUE;

            // If there are no more attributes to split on, stop
            if (!attributesLeft.contains("0")) return;

            for (int i = 0; i < dataDescriptor.getNumberOfAttributes(); i++) {

                // Don't attempt to check information gain on a previously used attribute
                if (attributesLeft.charAt(i) == '1') continue;

                // Calculate information gain for this attribute index
                double infoGain = calculateInformationGain(samples, i);

                // Check if the new information gain is larger than before
                if (infoGain > currentLargestInfoGain) {
                    currentLargestInfoGain = infoGain;
                    largestInfoGainAttributeIndex = i;
                }
            }

            // Store the information gain for this attribute split
            informationGain = currentLargestInfoGain;

            // Use the attribute which gives the largest information gain to split the data
            attributeSplitIndex = largestInfoGainAttributeIndex;

            // Mark the split attribute as used
            StringBuilder newAttributesLeft = new StringBuilder(attributesLeft);
            newAttributesLeft.setCharAt(attributeSplitIndex, '1');

            // Break up the samples based on that attribute
            ArrayList<ArrayList<DataElement>> valueSubsets = getSubSets(samples, attributeSplitIndex);

            // For each subset of the samples create a new node
            subNodes = new ArrayList<>();
            for (ArrayList<DataElement> valueSubset : valueSubsets) {
                Node node = new Node(valueSubset, newAttributesLeft.toString(), currentNodeDepth + 1);

                // If there are no samples in the subset use the parents most common class value
                if (valueSubset.size() == 0) {
                    node.setMostCommonClass(mostCommonClass);
                }

                subNodes.add(node);
            }
        }

        /**
         * This function calculates the information gain for splitting the samples using a specific attribute.
         *
         * @param samples        List of data elements
         * @param attributeIndex Attribute to split and calculate information gain
         * @return The information gain of this split
         */
        private double calculateInformationGain(ArrayList<DataElement> samples, int attributeIndex) {

            // Create subsets of the values based of the attribute to split on
            ArrayList<ArrayList<DataElement>> valueSubsets = getSubSets(samples, attributeIndex);

            // Sum up the weighted entropy for all the sub sets
            double subSetEntropySum = 0;
            for (ArrayList<DataElement> valueSubset : valueSubsets) {

                // If the sub set does not have any samples skip it
                if (valueSubset.size() == 0) continue;

                // Add the weighted entropy of the subset to the overall split entropy
                subSetEntropySum += ((double) valueSubset.size() / (double) sampleCount) * calculateEntropy(valueSubset);
            }

            return currentEntropy - subSetEntropySum;
        }

        /**
         * Calculates the current entropy of the provided data set based on the class attribute.
         *
         * @param samples List of data elements
         * @return The entropy of the data set
         */
        private double calculateEntropy(ArrayList<DataElement> samples) {

            // Determine the number of samples in this data set
            int sampleSize = samples.size();

            // Create an array to store the class counts for this data set
            int[] classValueCounts = extractClassCounts(samples);

            // Sum up the entropy for this sample
            double ent = 0;
            for (int classValueCount : classValueCounts) {

                // Calculate the probability of this class
                double p_ = (double) classValueCount / (double) sampleSize;

                // If the probability is zero skip this value to prevent math errors
                if (p_ == 0) continue;

                // Calculate the entropy to be added
                ent += -p_ * Math.log(p_);
            }

            return ent;
        }

        /**
         * Returns a list of split data sets based on the provided attribute.
         *
         * @param samples        List of data samples to split
         * @param attributeIndex Attribute to split the data set on
         * @return A split list of data samples
         */
        private ArrayList<ArrayList<DataElement>> getSubSets(ArrayList<DataElement> samples, int attributeIndex) {

            // Create a list to hold all the sub lists
            ArrayList<ArrayList<DataElement>> valueSubsets = new ArrayList<>();
            for (int i = 0; i < dataDescriptor.getUniqueAttributeValues(attributeIndex).size(); i++) {
                valueSubsets.add(new ArrayList<>());
            }

            // Populate the sub lists with the data elements
            for (DataElement dataElement : samples) {
                valueSubsets.get(dataElement.getValue(attributeIndex)).add(dataElement);
            }

            return valueSubsets;
        }

        /**
         * Determines the class value for the provided data element based on the tree structure. Functions recursively
         * and will go through the tree until there are no more sub nodes.
         *
         * @param dataElement The data element to be classified
         * @return The class classification for this data element
         */
        private int determineClass(DataElement dataElement) {

            // If this is a leaf node return the most common class value
            if (subNodes == null) return mostCommonClass;

            // Use the sub nodes to determine the most common class value
            return subNodes.get(dataElement.getValue(attributeSplitIndex)).determineClass(dataElement);
        }

        /**
         * Sets the most common class value. Used for sub nodes with no samples to determine themselves.
         *
         * @param mostCommonClass The most common class value
         */
        private void setMostCommonClass(int mostCommonClass) {
            this.mostCommonClass = mostCommonClass;
        }

        /**
         * Gets the node index used for defining the tree structure.
         *
         * @return Node index
         */
        private int getNodeIndex() {

            return nodeIndex;
        }

        /**
         * Gets the sample count at this node.
         *
         * @return Sample count for this node
         */
        private int getSampleCount() {

            return sampleCount;
        }

        /**
         * Returns the string describing the node and its connections to the sub nodes.
         *
         * @return A string structure of the node and sub nodes
         */
        public String toString(boolean showEmptyLeaves) {

            StringBuilder s = new StringBuilder();

            // Add all the information describing this node to the string
            s.append(nodeIndex).append(" [label=\"");

            // If this node is a leaf node don't display split and information gain
            if (attributeSplitIndex != -1) {

                s.append("Split on: ").append(dataDescriptor.getAttribute(attributeSplitIndex)).append("\\n");
                s.append("Information gain = ").append(String.format(decFormat, informationGain)).append("\\n");
            }
            s.append("Current Entropy = ").append(String.format(decFormat, currentEntropy)).append("\\n");
            s.append("Samples: ").append(sampleCount).append("\\n");
            s.append("Class counts: [");

            if(classCounts != null) {
                for (int i = 0; i < classCounts.length; i++) {

                    s.append(classCounts[i]);

                    // If this is not the last class count separate it by a ','
                    if (i < classCounts.length - 1) {
                        s.append(", ");
                    }
                }
            }
            s.append("]\\n");
            s.append("Class: ").append(dataDescriptor.getUniqueAttributeValues(dataDescriptor.getClassAttributeIndex()).get(mostCommonClass)).append("\"");
            s.append(", fillcolor=\"#" + 11111103 + "\"];\n");

            // Add all sub nodes to the string including their node connections
            if (subNodes != null) {
                for (int i = 0; i < subNodes.size(); i++) {

                    // If the sub node sample count is 0 do not include it in the print out
                    if (subNodes.get(i).getSampleCount() == 0 && !showEmptyLeaves) continue;

                    // Add the node linking description
                    s.append(getNodeIndex()).append(" -> ").append(subNodes.get(i).getNodeIndex());
                    s.append("[label=\"").append(dataDescriptor.getUniqueAttributeValues(attributeSplitIndex).get(i)).append("\"]").append(";");
                    s.append("\n");

                    // Add the sub nodes text
                    s.append(subNodes.get(i).toString());
                }
            }

            return s.toString();
        }
    }
}
