package ui.algorithm;

import ui.PrintResult;
import ui.calculation.Calculate;
import ui.table.Table;
import ui.table.TableRow;

import java.text.DecimalFormat;
import java.util.*;

public class ID3 {
    private Node root;
    private final Integer depth;
    private final DecimalFormat df = new DecimalFormat("#.####");
    private Table learningData;

    public ID3(Integer depth){
        this.depth = depth;
    }

    public void fit(Table data) {
        this.learningData = data;
        setRootNode(data);

        Stack<Node> nodes = new Stack<>();
        nodes.push(root);

        while(!nodes.isEmpty()){
            Node node = nodes.pop();

            List<String> prevUsedAttributes = getUsedAttributesForNode(node);
            List<String> usedAttributesValues = getUsedAttributeValuesForNode(node);
            List<String> remainingAttr = data.getHeader().stream().filter(attr -> !prevUsedAttributes.contains(attr)).toList();

            if(depth != null && node.depth + 1 > depth){
                if(depth != 0) prevUsedAttributes.remove(0);

                Table subTable = depth == 0 ? data : data.getSubTable(prevUsedAttributes,usedAttributesValues);
                Set<String> finalFeatureValues = subTable.finalFeatureValues();

                String result = resolveForSubtree(node,finalFeatureValues);

                Node leafNode = new Node(result,node.depth + 1,"all");
                leafNode.prev = node;
                node.next.add(leafNode);

                continue;
            }


            for(String attr : data.getAttributeValues(node.attributeName)){
                List<String> path = new ArrayList<>(usedAttributesValues);
                path.add(0,attr);

                Table subTable = data.getSubTable(prevUsedAttributes,path);
                Node nextNode = getNextNode(subTable,remainingAttr,node,attr);

                if(nextNode == null) continue;

                nextNode.prev = node;
                node.next.add(nextNode);
                nodes.push(nextNode);
            }
        }

    }

    private Node getNextNode(Table subTable, List<String> remainingAttr,Node prevNode, String attr) {
        double startEntropy = Calculate.calculateEntropy(subTable);

        if(startEntropy == 0) {
            setLeafs(subTable,prevNode);
            return null;
        }

        double maxIG = 0;
        String headerMax = null;

        for(String header : subTable.getHeader()){
            if(!remainingAttr.contains(header)) continue;

            double IG = Calculate.calculateIG(subTable,header,subTable.getAttributeValues(header),startEntropy);

            if(headerMax == null){
                maxIG = IG;
                headerMax = header;
                continue;
            }

            double IGRounded = Double.parseDouble(df.format(IG));
            double IGMarRounded = Double.parseDouble(df.format(maxIG));

            if(IGRounded >= IGMarRounded){
                if (IGRounded == IGMarRounded && headerMax.compareTo(header) > 0){
                    headerMax = header;
                }else if (IGRounded > IGMarRounded) {
                    maxIG = IG;
                    headerMax = header;
                }
            }
        }
        return new Node(headerMax,prevNode.depth + 1,attr);
    }

    private void setLeafs(Table subTable, Node prevNode) {
        HashMap<String, String> map = new HashMap<>();
        String header = prevNode.attributeName;
        int indexOfHeader = subTable.getHeader().indexOf(header);
        for (TableRow row : subTable.getRows()){
            map.put(row.getValue(indexOfHeader),row.getFinalFeatureValue());
        }

        for(var entrySet : map.entrySet()){
            Node leafNode = new Node(entrySet.getValue(),prevNode.depth + 1,entrySet.getKey());
            leafNode.prev = prevNode;
            prevNode.next.add(leafNode);
        }
    }

    public void predict(Table testData){
        List<String> predictions = new ArrayList<>();
        double correct = 0;


        for(TableRow row : testData.getRows()){
            String prediction = walkTree(row, testData);
            if(prediction.equals(row.getFinalFeatureValue())) correct++;
            predictions.add(prediction);
        }

        new PrintResult().printResult(root,predictions,correct,testData);

    }
    private String walkTree(TableRow row,Table testData) {
        List<String> header = testData.getHeader();
        Set<String> finalFeatureValues = testData.finalFeatureValues();
        Node node = root;

        while(true){
            String nodeAttribute = node.attributeName;
            if(finalFeatureValues.contains(nodeAttribute)){
                return nodeAttribute;
            }

            int index = header.indexOf(nodeAttribute);
            if(index < 0) throw new IllegalArgumentException("Invalid index.");

            String value = row.getValue(index);

            if(node.next.get(0).attributeValue.equals("all")){
                return node.next.get(0).attributeName;
            }

            List<Node> nextNodePossible = node.next.stream().filter(n -> n.attributeValue.equals(value)).toList();
            if(nextNodePossible.size() == 0){
                return resolveForSubtree(node,finalFeatureValues);
            }

            node = nextNodePossible.get(0);
        }
    }

    private String resolveForSubtree( Node node, Set<String> finalAttributesValues) {
        List<String> allUsedAttr = getUsedAttributesForNode(node);
        List<String> path = getUsedAttributeValuesForNode(node);

        allUsedAttr.remove(0);

        Table subTable = learningData.getSubTable(allUsedAttr,path);

        List<String> allPredictions = subTable.getRows().stream().map(TableRow::getFinalFeatureValue).toList();

        String mostCommonPrediction = null;
        int max = 0;

        for(String value : finalAttributesValues){
            int count = 0;
            for(String prediction : allPredictions){
                if(prediction.equals(value)) count++;
            }

            if(mostCommonPrediction == null || count > max) {
                max = count;
                mostCommonPrediction = value;
            }
            if(count == max && value.compareTo(mostCommonPrediction) < 0)
                mostCommonPrediction = value;
        }

        return mostCommonPrediction;
    }
    private List<String> getUsedAttributesForNode(Node node){
        List<String> prevAttributes = new ArrayList<>();
        while(node != null){
            prevAttributes.add(node.attributeName);
            node = node.prev;
        }

        return prevAttributes;
    }
    private List<String> getUsedAttributeValuesForNode(Node node){
        List<String> path = new ArrayList<>();
        while(node.prev != null){
            path.add(node.attributeValue);
            node = node.prev;
        }

        return path;
    }
    private void setRootNode(Table data){
        double startEntropy = Calculate.calculateEntropy(data);
        double maxIG = 0;

        String headerMax = null;

        for(String header : data.getHeader()){
            double IG = Calculate.calculateIG(data,header,data.getAttributeValues(header),startEntropy);

            if(headerMax == null){
                maxIG = IG;
                headerMax = header;
                continue;
            }

            if(IG >= maxIG){
                if (IG == maxIG && headerMax.compareTo(header) > 0){
                    headerMax = header;
                }else if (IG > maxIG) {
                    maxIG = IG;
                    headerMax = header;
                }
            }
        }

        root = new Node(headerMax,0,null);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class Node{
        private final String attributeName;
        private final String attributeValue;
        private final List<Node> next;
        private Node prev;
        private final int depth;

        public Node(String attributeName, int depth, String attributeValue){
            this.attributeName = attributeName;
            this.depth = depth;
            this.attributeValue = attributeValue;
            this.next = new ArrayList<>();
        }


        public List<Node> getNext() {
            return next;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public String getAttributeValue() {
            return attributeValue;
        }

        public Node getPrev() {
            return prev;
        }

        public int getDepth() {
            return depth;
        }

        @Override
        public String toString() {
            return String.format("%d. %s", depth,attributeName);
        }
    }
}
