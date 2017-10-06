package com.riaanvo;

import java.util.ArrayList;

/**
 * Java object for storing a data elements values.
 */
public class DataElement {

    private static DataDescriptor dataDescriptor;
    private final ArrayList<Integer> values;

    /**
     * Constructor for creating a data element object. Stores all the values as indexes for a single row in
     * the data set.
     *
     * @param values List of value indexes for this data element
     */
    public DataElement(int[] values) {

        //Create the values list and store all the value indexes
        this.values = new ArrayList<>();
        for (int value : values) {

            this.values.add(value);
        }
    }

    /**
     * Sets the global Data Descriptor for all Data elements
     *
     * @param newDataDescriptor Data descriptor that is used for all data elements
     */
    public static void setDataDescriptor(DataDescriptor newDataDescriptor) {

        dataDescriptor = newDataDescriptor;
    }

    /**
     * This returns the data descriptor used for all data elements
     * @return The data descriptor used for data elements
     */
    public static DataDescriptor getDataDescriptor() {

        return dataDescriptor;
    }

    /**
     * Gets the index value for the desired attribute index
     * @param attributeIndex Attribute index of the desired value
     * @return The index value for that attribute
     */
    public int getValue(int attributeIndex){

        return values.get(attributeIndex);
    }

    /**
     * Returns a string list of all the value indexes stored in this data element.
     *
     * @return String of data value indexes
     */
    public String toStringInts() {

        StringBuilder s = new StringBuilder();

        // Determine the number of values and loop through them
        int numValues = values.size();
        for (int i = 0; i < numValues; i++) {

            s.append(values.get(i));

            // Add a comma if it is not the last value
            if (i != numValues - 1) {
                s.append(", ");
            }
        }

        return s.toString();
    }

    /**
     * Returns the data elements values as a string of attribute values separated by commas
     *
     * @return A data element as a string of values
     */
    public String toString() {

        StringBuilder s = new StringBuilder();

        // Determine the number of values and loop through them
        int numValues = values.size();
        for (int i = 0; i < numValues; i++) {

            s.append(dataDescriptor.indexToValue(i, values.get(i)));

            // Add a comma if it is not the last value
            if (i != numValues - 1) {
                s.append(", ");
            }
        }

        return s.toString();
    }

}
