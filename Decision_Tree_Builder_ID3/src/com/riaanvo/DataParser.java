package com.riaanvo;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataParser {
    private Category classifications;
    private ArrayList<Category> dataStructure = new ArrayList<Category>();
    private ArrayList<DataElement> tainingData;

    public DataParser(String trainDataFilePath){
        //Extract training data file into a string
        String trainingCSVText = extractFileContents(trainDataFilePath);
        String[] rows = trainingCSVText.split("\n");
        //Define the structure of the csv file
        buildDataStructure(rows[0]);




    }

    private void buildDataStructure(String categoriesRow){
        //Check if the row is the header row of the csv file
        if(categoriesRow.contains("#")){
            //categoriesRow = categoriesRow.replaceFirst("# ", "");

            //Split the fields into columns
            String[] categories = categoriesRow.split(",");

            //Set up the classification values structure;
            classifications = new Category("class", -1);

            //Assuming that the first column is the classification, start from one over and build the data structure.
            for (int i = 1; i < categories.length; i++) {
                dataStructure.add(new Category(categories[i], i - 1));
            }
        } else {
            System.out.println("First row does not contain file headers!!!");
        }
    }

    /**
     * Code from https://www.mkyong.com/java/how-to-read-file-from-java-bufferedreader-example/
     * @param filepath
     * @return
     */
    private String extractFileContents(String filepath){
        String output = "";
        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);

            String nextLine;

            while ((nextLine = br.readLine()) != null) {
                output += nextLine + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return output;
    }


    private class Category{
        private String fieldName;
        private int categoryIndex;
        private ArrayList<String> values;

        public Category(String fieldName, int categoryIndex){
            this.fieldName = fieldName;
            this.categoryIndex = categoryIndex;
            values = new ArrayList<String>();
        }

        public void tryAddValue(String value){
            for(int i = 0; i < value.length(); i ++){
                if(values.get(i).equals(value)){
                    return;
                }
            }
            values.add(value);
        }

        public int getValueIndex(String value){
            for(int i = 0; i < value.length(); i ++){
                if(values.get(i).equals(value)){
                    return i;
                }
            }
            return -1;
        }

        public int numDistinctValues(){
            return values.size();
        }

        public String getFieldName(){
            return fieldName;
        }

        public int getCategoryIndex(){
            return categoryIndex;
        }
    }

    private class DataElement{
        private int classification;
        private int[] columnValues;

        public DataElement(int classification, int[] columnValues){
            this.classification = classification;
            this.columnValues = columnValues;
        }
    }
}
