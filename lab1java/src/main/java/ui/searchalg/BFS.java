package ui.searchalg;

import ui.node.Node;
import ui.utils.SearchAlgUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BFS implements SearchAlgorithm{
    private final Queue<Node> open = new ArrayDeque<>();

    private int statesVisited = 0;
    @Override
    public void search(Path stateSpacePath, Path heuristicPath) {

        final HashMap<String, Node> visitedNodes = new HashMap<>();

        Map<String, String[]> stateMap = SearchAlgUtils.getStateMap(stateSpacePath);

        if(stateMap == null) return;

        open.add(new Node(null,0,stateMap.get("initialState")[0]));

        List<String> finalStates = Arrays.asList(stateMap.get("finalState"));

        while(!open.isEmpty()){
            Node currentNode = open.poll();

            if(finalStates.contains(currentNode.getValue())){
                SearchAlgUtils.printResult(currentNode,this.statesVisited, "BFS");
                return;
            }

            visitedNodes.put(currentNode.getValue(),currentNode);

            statesVisited++;

            Set<Node> childrenNodes = SearchAlgUtils.getNodes(stateMap.get(currentNode.getValue()),currentNode,visitedNodes);

            if(!Objects.isNull(childrenNodes))
                open.addAll(childrenNodes);

        }
        printStream.println("[FOUND_SOLUTION]:");
    }









}
