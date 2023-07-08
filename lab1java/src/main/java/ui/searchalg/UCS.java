package ui.searchalg;

import ui.node.Node;
import ui.utils.SearchAlgUtils;

import java.nio.file.Path;
import java.util.*;

public class UCS implements SearchAlgorithm{

    private final Queue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getCost).thenComparing(Node::getValue));

    private int statesVisited = 0;

    @Override
    public void search(Path stateSpacePath, Path heuristicPath) {
        final HashMap<String,Node> checkedNodes = new HashMap<>();

        Map<String, String[]> stateMap = SearchAlgUtils.getStateMap(stateSpacePath);

        if(stateMap == null) return;

        open.add(new Node(null,0,stateMap.get("initialState")[0]));

        List<String> finalStates = Arrays.asList(stateMap.get("finalState"));

        while(!open.isEmpty()){
            Node currentNode = open.poll();

            if(finalStates.contains(currentNode.getValue())){
                SearchAlgUtils.printResult(currentNode,this.statesVisited, "UCS");
                return;
            }

            checkedNodes.put(currentNode.getValue(), currentNode);

            statesVisited++;

            Set<Node> childrenNodes = SearchAlgUtils.getNodes(stateMap.get(currentNode.getValue()),currentNode,checkedNodes);

            if(!Objects.isNull(childrenNodes))
                open.addAll(childrenNodes);

        }
        printStream.println("[FOUND_SOLUTION]:");

    }
}
