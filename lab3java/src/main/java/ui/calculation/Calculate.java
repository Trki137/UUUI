package ui.calculation;

import ui.table.Table;

import java.util.List;

public class Calculate {
    public static double calculateEntropy(Table data){
        String finalFeature = data.getFinalFeature();

        double originalSize = data.numOfRows();
        double result = 0.0;

        for(String finalFeatureValue :data.finalFeatureValues()){
            Table subTable = data.getSubTable(finalFeature,finalFeatureValue);

            int size = subTable.numOfRows();
            if(size == 0) continue;

            result +=  (-1*size/originalSize) * log2(size/originalSize);
        }
        return result;
    }
    public static double calculateIG(Table data, String attributeName, List<String> attributeValues, double entropy){
        double originalSize = data.numOfRows();
        double IG = entropy;

        for(String value : attributeValues){
            Table subTable = data.getSubTable(attributeName,value);
            int size = subTable.numOfRows();

            double E = calculateEntropy(subTable);

            IG -= (size/originalSize) * E;
        }

        return IG;
    }

    public static double log2(double N){
        return Math.log(N) / Math.log(2);
    }
}
