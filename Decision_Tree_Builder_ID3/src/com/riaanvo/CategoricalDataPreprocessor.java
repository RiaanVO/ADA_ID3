package com.riaanvo;

import java.util.ArrayList;

public class CategoricalDataPreprocessor {

    private DataDescriptor dataDescriptor;
    private ArrayList<DataElement> dataSet;

    public CategoricalDataPreprocessor(ArrayList<DataElement> oldDataSet, DataDescriptor oldDataDescriptor, DataDescriptor newDataDescriptor){
        long previousTime = System.currentTimeMillis();


        if(newDataDescriptor == null){
            System.out.print("Defining new data descriptor:");

            dataDescriptor = new DataDescriptor(createNewHeaders(oldDataDescriptor));
            enterUniqueValues(oldDataDescriptor);

            System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");

        } else {
            dataDescriptor = newDataDescriptor;
        }

        previousTime = System.currentTimeMillis();
        System.out.print("Converting data set:");

        dataSet = convertToNewDataSet(oldDataDescriptor, oldDataSet);

        System.out.println("\t| Time Taken: " + (System.currentTimeMillis() - previousTime) + "ms");
    }

    private String[] createNewHeaders(DataDescriptor baseDataDescriptor){

        int totalNumAttributes = 0;
        for(int i = 0; i < baseDataDescriptor.getNumberOfAttributes(); i ++){
            if(i == baseDataDescriptor.getClassAttributeIndex()) {
                totalNumAttributes++;
                continue;
            }
            totalNumAttributes += baseDataDescriptor.getUniqueAttributeValues(i).size();
        }

        String[] newHeaders = new String[totalNumAttributes];
        int currentIndex = 0;
        for (int a = 0; a < baseDataDescriptor.getNumberOfAttributes(); a++) {
            if(a == baseDataDescriptor.getClassAttributeIndex()){
                newHeaders[currentIndex] = baseDataDescriptor.getAttribute(a);
                currentIndex ++;
            } else {
                for(int ua = 0; ua < baseDataDescriptor.getUniqueAttributeValues(a).size(); ua++){
                    newHeaders[currentIndex] = baseDataDescriptor.getAttribute(a) + " = " + baseDataDescriptor.getUniqueAttributeValues(a).get(ua);
                    currentIndex++;
                }
            }
        }

        return newHeaders;
    }

    private void enterUniqueValues(DataDescriptor baseDataDescriptor){
        for(int a = 0; a < dataDescriptor.getNumberOfAttributes(); a ++){

            if(dataDescriptor.getAttribute(a).equals(baseDataDescriptor.getAttribute(baseDataDescriptor.getClassAttributeIndex()))){
                for(String value: baseDataDescriptor.getUniqueAttributeValues(baseDataDescriptor.getClassAttributeIndex())){
                    dataDescriptor.tryAddUniqueValue(a, value);
                }
            } else {
                dataDescriptor.tryAddUniqueValue(a, "false");
                dataDescriptor.tryAddUniqueValue(a, "true");
            }
        }
    }

    private ArrayList<DataElement> convertToNewDataSet(DataDescriptor baseDataDescriptor, ArrayList<DataElement> oldDataElements){
        ArrayList<DataElement> newDataSet = new ArrayList<DataElement>();

        int[] attributeUniqueNumbers = new int[baseDataDescriptor.getNumberOfAttributes()];
        for(int i = 0; i < baseDataDescriptor.getNumberOfAttributes(); i ++){
            if(i == baseDataDescriptor.getClassAttributeIndex()) {
                attributeUniqueNumbers[i] = 1;
                continue;
            }
            attributeUniqueNumbers[i] = baseDataDescriptor.getUniqueAttributeValues(i).size();
        }

        int newNumberOfAttributes = dataDescriptor.getNumberOfAttributes();
        int oldNumberOfAttributes = baseDataDescriptor.getNumberOfAttributes();
        int oldClassIndex = baseDataDescriptor.getClassAttributeIndex();
        for(DataElement dataElement : oldDataElements){
            int[] newValues = new int[newNumberOfAttributes];

            int currentIndex = 0;
            for(int a = 0; a < oldNumberOfAttributes; a++){
                if(a == oldClassIndex){
                    newValues[currentIndex] = dataElement.getValue(a);
                    currentIndex++;
                } else {
                    newValues[currentIndex + dataElement.getValue(a)] = 1;
                    currentIndex += attributeUniqueNumbers[a];
                }
            }

            newDataSet.add(new DataElement(newValues));
        }

        DataElement.setDataDescriptor(dataDescriptor);

        return newDataSet;
    }

    /**
     * Displays the first desired number of data elements from the data set and the data descriptor if there is one.
     *
     * @param numberOfElementsToShow The number of data elements to display
     */
    public void displayDataSet(int numberOfElementsToShow) {

        StringBuilder s = new StringBuilder();

        //Check if the data descriptor is null and add the output if it is not
        if (dataDescriptor != null) {

            s.append(dataDescriptor.toString());
        }

        // Print out the desired number of rows of data
        s.append("\n\nData output\n");
        for (int i = 0; i < numberOfElementsToShow; i++) {

            s.append(dataSet.get(i).toString()).append("\n");
        }
        System.out.println(s.toString());
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
