package com.riaanvo;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Data Descriptor defines the current structure of the inputted data set. It allows for the conversion of string values
 * into integer values for faster comparisons and smaller storage requirements.
 */
public class DataDescriptor {

    private final ArrayList<String> attributes;
    private final ArrayList<ArrayList<String>> uniqueAttributeValues;

    private int classAttributeIndex = 0;
    private int numberOfClasses = 0;

    /**
     * Constructor for creating a data descriptor object. The data descriptor will define the structure of the supplied
     * data set.
     *
     * @param attributes Data sets attributes
     */
    public DataDescriptor(String[] attributes) {

        // Create a list of all the attribute headers and store the passed attribute name values
        this.attributes = new ArrayList<>();
        Collections.addAll(this.attributes, attributes);

        // Create arrays to store the unique data values found in the data set
        uniqueAttributeValues = new ArrayList<>();
        for (int i = 0; i < attributes.length; i++) {

            uniqueAttributeValues.add(new ArrayList<>());

            // Assign the attribute index if it is denoted with a '#'
            if (attributes[i].contains("#")) {
                classAttributeIndex = i;
            }
        }
    }

    /**
     * Attempts to add the value to the respective attribute values list to build a unique list of attribute values.
     *
     * @param attributeIndex Column index of the attribute in the data set
     * @param value          Value for that attribute
     */
    public void tryAddUniqueValue(int attributeIndex, String value) {

        // Check if the unique attribute list contains the value and add if not
        if (!uniqueAttributeValues.get(attributeIndex).contains(value)) {
            uniqueAttributeValues.get(attributeIndex).add(value);
        }
    }

    /**
     * Converts the string values into a list of integer indexes for faster comparisons.
     *
     * @param attributeValues The list of attribute values for the data element
     * @return A integer list of attribute values based of value indexes
     */
    public int[] convertStringValuesToInt(String[] attributeValues) {

        // Create an integer array to store all the value indexes
        int[] values = new int[attributeValues.length];

        // Determine the index of the value based on the unique attribute values
        for (int i = 0; i < values.length; i++) {
            values[i] = uniqueAttributeValues.get(i).indexOf(attributeValues[i]);
        }

        return values;
    }

    /**
     * Converts an attribute index and attribute value index into the unique string value for that attribute.
     *
     * @param attributeIndex      The attribute index from the data set
     * @param attributeValueIndex The attribute value as an index
     * @return The attribute value as a string
     */
    public String indexToValue(int attributeIndex, int attributeValueIndex) {

        return uniqueAttributeValues.get(attributeIndex).get(attributeValueIndex);
    }

    /**
     * Returns the unique list of attribute values for the desired attribute
     *
     * @param attributeIndex The index of the attribute
     * @return The list of unique attribute values
     */
    public ArrayList<String> getUniqueAttributeValues(int attributeIndex) {

        return uniqueAttributeValues.get(attributeIndex);
    }

    /**
     * Used to get the string value of an attribute based on index. Returns a blank string if there is no
     * attribute index (Value '-1').
     *
     * @param attributeIndex The index of the desired attribute
     * @return The string name of the attribute
     */
    public String getAttribute(int attributeIndex) {

        // Check if the index is for no attribute and return a blank string if it is
        if (attributeIndex == -1) return "";
        return attributes.get(attributeIndex);
    }

    /**
     * Returns the number of attributes in the data set. Includes the class attribute.
     *
     * @return The number of attributes contained in the data set
     */
    public int getNumberOfAttributes() {

        return attributes.size();
    }

    /**
     * Returns the index of the data sets class attribute
     *
     * @return The index of the class attribute
     */
    public int getClassAttributeIndex() {

        return classAttributeIndex;
    }

    /**
     * Determines the number of classes in the data set and returns the value.
     *
     * @return The number of classes in the data set
     */
    public int getNumberOfClasses() {

        // Check if the number of classes has been defined and count the values if not
        if (numberOfClasses == 0) {

            numberOfClasses = uniqueAttributeValues.get(classAttributeIndex).size();
        }

        return numberOfClasses;
    }

    /**
     * Conversion of the data descriptor values to a string of information
     *
     * @return A string of the data descriptor values
     */
    public String toString() {

        StringBuilder s = new StringBuilder();
        for (int c = 0; c < attributes.size(); c++) {

            // Added that attribute name to the output
            s.append(attributes.get(c)).append(":\n");

            // Add each unique value for that attribute to the output
            ArrayList<String> uniqueValues = uniqueAttributeValues.get(c);
            for (int v = 0; v < uniqueValues.size(); v++) {

                s.append("\t").append(v).append(": ").append(uniqueValues.get(v)).append("\n");
            }
            s.append("\n");
        }
        return s.toString();
    }
}
