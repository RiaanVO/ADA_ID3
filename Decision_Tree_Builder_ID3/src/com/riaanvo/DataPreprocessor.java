package com.riaanvo;

import java.util.ArrayList;

/**
 * This class is used to pre-process the data sets uses to build and test the model. The current functionality is to
 * convert categorical data into binary attributes for simpler decisions with only true and false values.
 */
public class DataPreprocessor {

    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> dataSet;

    public DataPreprocessor() {
    }

    /**
     * Takes in an old data set and data descriptor and creates a new binarised data descriptor and corresponding data
     * set. These new objects can be accessed using getters.
     *
     * @param oldDataSet        The original data set
     * @param oldDataDescriptor The original data descriptor
     */
    public void binariseDataSet(ArrayList<DataElement> oldDataSet, DataDescriptor oldDataDescriptor) {

        // Store the current time for duration calculations
        long previousTime = System.currentTimeMillis();

        // If the data descriptor does not exist, create and define a new binarised data descriptor
        if (dataDescriptor == null) {

            System.out.print("Defining new data descriptor:");

            dataDescriptor = new DataDescriptor(createNewHeaders(oldDataDescriptor));
            enterUniqueValues(oldDataDescriptor);

            System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");
        }

        previousTime = System.currentTimeMillis();
        System.out.print("Converting data set:");

        // Convert the old data set and store it
        dataSet = convertToNewDataSet(oldDataDescriptor, oldDataSet);

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms\n");
    }

    /**
     * Takes in a data descriptor and for every categorical attribute it creates binarised headers for every unique
     * value.
     *
     * @param baseDataDescriptor Original data descriptor
     * @return Attribute names for a new data descriptor
     */
    private String[] createNewHeaders(DataDescriptor baseDataDescriptor) {

        // Count the new number of attributes the data descriptor will have
        int totalNumAttributes = 0;
        for (int i = 0; i < baseDataDescriptor.getNumberOfAttributes(); i++) {

            // If the current attribute is the class, only at a single attribute to the headers
            if (i == baseDataDescriptor.getClassAttributeIndex()) {

                totalNumAttributes++;
                continue;
            }

            // Add the number of unique values as each will become an attribute itself
            totalNumAttributes += baseDataDescriptor.getUniqueAttributeValues(i).size();
        }

        String[] newHeaders = new String[totalNumAttributes];

        // Loop through each base attribute and create new headers for the new binarised attributes
        int currentIndex = 0;
        for (int a = 0; a < baseDataDescriptor.getNumberOfAttributes(); a++) {

            // If the attribute is the class, leave the same name, else add all values as new attributes
            if (a == baseDataDescriptor.getClassAttributeIndex()) {

                newHeaders[currentIndex] = baseDataDescriptor.getAttribute(a);
                currentIndex++;
            } else {

                // Loop through all the base attributes values and create new specific headers
                for (int ua = 0; ua < baseDataDescriptor.getUniqueAttributeValues(a).size(); ua++) {

                    newHeaders[currentIndex] = baseDataDescriptor.getAttribute(a) + " = " + baseDataDescriptor.getUniqueAttributeValues(a).get(ua);
                    currentIndex++;
                }
            }
        }

        return newHeaders;
    }

    /**
     * Sets up the unique data values for a binarised data set in the data descriptor
     *
     * @param oldDataDescriptor The previous data descriptor
     */
    private void enterUniqueValues(DataDescriptor oldDataDescriptor) {

        // Loop through all the attributes in the new data descriptor
        for (int a = 0; a < dataDescriptor.getNumberOfAttributes(); a++) {

            // If the attribute index is the same as the old data descriptors class index
            if (dataDescriptor.getAttribute(a).equals(oldDataDescriptor.getAttribute(oldDataDescriptor.getClassAttributeIndex()))) {

                // Add all the class values to the attribute
                for (String value : oldDataDescriptor.getUniqueAttributeValues(oldDataDescriptor.getClassAttributeIndex())) {

                    dataDescriptor.tryAddUniqueValue(a, value);
                }
            } else {

                // Add false and true as the unique values
                dataDescriptor.tryAddUniqueValue(a, "false");
                dataDescriptor.tryAddUniqueValue(a, "true");
            }
        }
    }

    /**
     * Takes in a data descriptor and the old data set and uses the new data descriptor to convert the data elements
     * into values for the new data descriptor.
     *
     * @param baseDataDescriptor The original data descriptor
     * @param oldDataElements    The original data set
     * @return The binarised data set
     */
    private ArrayList<DataElement> convertToNewDataSet(DataDescriptor baseDataDescriptor, ArrayList<DataElement> oldDataElements) {

        // Loop through the old data descriptor and count the number of values in each attribute
        int[] attributeUniqueNumbers = new int[baseDataDescriptor.getNumberOfAttributes()];
        for (int i = 0; i < baseDataDescriptor.getNumberOfAttributes(); i++) {

            // If it is the class attribute
            if (i == baseDataDescriptor.getClassAttributeIndex()) {

                attributeUniqueNumbers[i] = 1;
                continue;
            }
            attributeUniqueNumbers[i] = baseDataDescriptor.getUniqueAttributeValues(i).size();
        }

        // Create a list to hold the new data set
        ArrayList<DataElement> newDataSet = new ArrayList<DataElement>();

        // Store local values to reduce outer calls
        int newNumberOfAttributes = dataDescriptor.getNumberOfAttributes();
        int oldNumberOfAttributes = baseDataDescriptor.getNumberOfAttributes();
        int oldClassIndex = baseDataDescriptor.getClassAttributeIndex();

        // Loop through each data element and convert the values to the new data descriptor
        for (DataElement dataElement : oldDataElements) {

            // Create an array to store the new values of this data element
            int[] newValues = new int[newNumberOfAttributes];

            // Loop through the older data values and insert them into the new data values
            int currentIndex = 0;
            for (int a = 0; a < oldNumberOfAttributes; a++) {

                // If the current value is the class value, store that value
                if (a == oldClassIndex) {

                    newValues[currentIndex] = dataElement.getValue(a);
                } else {

                    // Set the value in the correct spot to 1 to signify true
                    newValues[currentIndex + dataElement.getValue(a)] = 1;
                }

                // Increment by the number of vales in the old attribute
                currentIndex += attributeUniqueNumbers[a];
            }

            // Create a new data element and store it in the new data set
            newDataSet.add(new DataElement(newValues));
        }

        // Set the data descriptor for the data elements
        DataElement.setDataDescriptor(dataDescriptor);

        return newDataSet;
    }

    /**
     * Returns the data descriptor used for this data parser
     *
     * @return The data descriptor
     */
    public DataDescriptor getDataDescriptor() {

        return dataDescriptor;
    }

    /**
     * Returns the list of data elements extracted from the CSV.
     *
     * @return Arraylist of data elements
     */
    public ArrayList<DataElement> getDataSet() {

        return dataSet;
    }
}
