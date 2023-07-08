package ui;

import ui.algorithm.ID3.*;
import ui.table.Table;
import ui.table.TableRow;

import java.util.*;

public class PrintResult {


    public void printResult(Node root, List<String> predictions, double correct, Table testData){
        printAllBranches(root);

        System.out.print("[PREDICTIONS]: ");
        for(int i = 0; i < predictions.size(); i++){
            if(i == predictions.size() -1) System.out.println(predictions.get(i));
            else System.out.print(predictions.get(i)+" ");
        }

        SortedSet<String> sortedFinalClasses = new TreeSet<>(testData.finalFeatureValues());
        List<String> actualPredictions = testData.getRows().stream().map(TableRow::getFinalFeatureValue).toList();

        System.out.printf("[ACCURACY]: %.5f\n", correct/testData.numOfRows());

        System.out.println("[CONFUSION_MATRIX]:");
        for(String myPrediction: sortedFinalClasses){
            StringBuilder sb = new StringBuilder();
            for(String correctPrediction: sortedFinalClasses){
                int count = 0;
                for(int i = 0; i < predictions.size(); i++){
                    if(actualPredictions.get(i).equals(myPrediction) && predictions.get(i).equals(correctPrediction))
                        count++;
                }
                sb.append(count).append(" ");
            }
            System.out.println(sb.substring(0,sb.toString().length()-1));
        }
    }


    public void printAllBranches(Node root) {
        SortedSet<String> outputPredictions = new TreeSet<>();
        Stack<Node> nodes = new Stack<>();
        nodes.add(root);

        while(!nodes.isEmpty()){
            Node node = nodes.pop();
            if(node.getNext().size() == 0){
                formatNode(node,outputPredictions);
                continue;
            }

            node.getNext().forEach(nodes::push);
        }

        System.out.println("[BRANCHES]:");
        outputPredictions.forEach(System.out::println);
    }

    private void formatNode(Node node, SortedSet<String> outputPredictions) {
        List<String> list = new ArrayList<>();
        while(node.getPrev() != null){
            if(node.getNext().size() == 0) list.add(node.getAttributeName());
            if(node.getAttributeValue().equals("all")) {
                node = node.getPrev();
                continue;
            }
            list.add(String.format("%d:%s=%s",node.getDepth(),node.getPrev().getAttributeName(),node.getAttributeValue()));
            node = node.getPrev();
        }

        StringBuilder sb = new StringBuilder();
        for(int i = list.size() - 1; i >= 0; i--){
            sb.append(list.get(i)).append(" ");
        }

        outputPredictions.add(sb.substring(0, sb.length() - 1));
    }
}
