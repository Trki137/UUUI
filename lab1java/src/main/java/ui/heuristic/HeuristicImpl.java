package ui.heuristic;

import ui.node.Node;
import ui.utils.SearchAlgUtils;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class HeuristicImpl implements IHeuristic {

    private static final PrintStream printStream = new PrintStream(System.out,true, StandardCharsets.UTF_8);

    @Override
    public boolean isHeuristicOptimistic(Path stateSpacePath, Path heuristicPath) {
        boolean isOptimistic = true;

        Map<String, String[]> stateMap = SearchAlgUtils.getStateMap(stateSpacePath);
        Map<String, Double> heuristicMap = SearchAlgUtils.getHeuristicMap(heuristicPath);

        if (Objects.isNull(stateMap) || Objects.isNull(heuristicMap))
            throw new IllegalArgumentException("Couldn't open file for state space or heuristic path");

        Set<String> allNodes = new TreeSet<>(Comparator.naturalOrder());
        allNodes.addAll(stateMap.keySet());
        allNodes.remove("finalState");
        allNodes.remove("initialState");

        for (String node : allNodes) {
            Double hStartValue = getHStar(node, stateMap);
            Double hValue = heuristicMap.get(node);

            boolean conditionSatisfied = hValue <= hStartValue;

            if (!conditionSatisfied) isOptimistic = false;

            printStream.printf("[CONDITION]: [%s] h(%s) <= h*: %.1f <= %.1f\n", conditionSatisfied ? "OK" : "ERR", node, hValue, hStartValue);
        }

        return isOptimistic;
    }

    private Double getHStar(String node, Map<String, String[]> stateMap) {
        final Queue<Node> open = new PriorityQueue<>(Comparator.comparing(Node::getCost).thenComparing(Node::getValue));
        final Map<String, Node> close = new HashMap<>();

        List<String> finalStates = Arrays.asList(stateMap.get("finalState"));

        Node startingNode = new Node(null, 0, node);
        open.add(startingNode);

        while (!open.isEmpty()) {

            Node currentNode = open.poll();

            if (finalStates.contains(currentNode.getValue())) {
                return currentNode.getCost();
            }

            close.put(currentNode.getValue(), currentNode);

            Set<Node> childrenNodes = SearchAlgUtils.getNodes(stateMap.get(currentNode.getValue()),currentNode,close);

            if(!Objects.isNull(childrenNodes))
                open.addAll(childrenNodes);
        }

        return null;
    }

    @Override
    public boolean isHeuristicConsistent(Path stateSpacePath, Path heuristicPath) {
        boolean isConsistent = true;

        Map<String, String[]> stateMap = SearchAlgUtils.getStateMap(stateSpacePath);
        Map<String, Double> heuristicMap = SearchAlgUtils.getHeuristicMap(heuristicPath);

        if (Objects.isNull(stateMap) || Objects.isNull(heuristicMap))
            throw new IllegalArgumentException("Couldn't open file for state space or heuristic path");

        Set<String> allNodes = new TreeSet<>(Comparator.naturalOrder());
        allNodes.addAll(stateMap.keySet());
        allNodes.remove("finalState");
        allNodes.remove("initialState");

        for(String node: allNodes){
            if(Objects.isNull(stateMap.get(node))) continue;

            Double parentHeuristicValue = heuristicMap.get(node);

            Set<String> childNodes = new TreeSet<>(Comparator.naturalOrder());
            childNodes.addAll(Arrays.asList(stateMap.get(node)));

            for(String childNode: childNodes){
                String[] childNodeSplit = childNode.split(",");
                String childName = childNodeSplit[0];
                Double distance = Double.parseDouble(childNodeSplit[1]);
                Double childHeuristicValue = heuristicMap.get(childName);

                boolean conditionSatisfied = parentHeuristicValue <= (childHeuristicValue + distance);

                if(!conditionSatisfied) isConsistent = false;

                printStream.printf("[CONDITION]: [%s] h(%s) <= h(%s) + c: %.1f <= %.1f + %.1f\n",conditionSatisfied ? "OK":"ERR",node,childName,parentHeuristicValue,childHeuristicValue,distance);

            }
        }

        return isConsistent;
    }
}
