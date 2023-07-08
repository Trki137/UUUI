package ui.searchalg;

import ui.node.Node;
import ui.utils.SearchAlgUtils;

import java.nio.file.Path;
import java.util.*;

public class AStar implements SearchAlgorithm{

    private final PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparing(Node::getHeuristicValue).thenComparing(Node::getValue));

    private int stateVisited = 0;

    @Override
    public void search(Path stateSpacePath, Path heuristicPath) {
        Objects.requireNonNull(stateSpacePath);
        Objects.requireNonNull(heuristicPath);

        Map<String, String[]> stateMap = SearchAlgUtils.getStateMap(stateSpacePath);
        Map<String, Double> heuristicMap = SearchAlgUtils.getHeuristicMap(heuristicPath);
        Map<String, Node> openHashMap = new HashMap<>();

        if(Objects.isNull(stateMap) || Objects.isNull(heuristicMap)) return;

        Node startingNode = new Node(null,0.0,stateMap.get("initialState")[0]);

        open.add(startingNode);
        openHashMap.put(startingNode.getValue(),startingNode);

        List<String> finalStates = Arrays.asList(stateMap.get("finalState"));

        HashMap<String, Node> closed = new HashMap<>();

        while(!open.isEmpty()){
            Node currentNode = open.poll();
            openHashMap.remove(currentNode.getValue());

            if(finalStates.contains(currentNode.getValue())){
                String firstLine = "A-STAR " + heuristicPath.getFileName();
                SearchAlgUtils.printResult(currentNode,stateVisited,firstLine);
                return;
            }

            closed.put(currentNode.getValue(),currentNode);

            stateVisited++;

            for(String childrenNode : stateMap.get(currentNode.getValue())){

                String[] values = childrenNode.split(",");
                String nodeName = values[0];
                int travelCost = Integer.parseInt(values[1]);

                Node childNode = new Node(currentNode,currentNode.getCost() + travelCost,nodeName, currentNode.getCost() + travelCost + heuristicMap.get(nodeName));
                if(openHashMap.containsKey(nodeName)){
                    if(openHashMap.get(nodeName).getCost() > childNode.getCost()){
                        openHashMap.put(nodeName,childNode);
                        open.remove(openHashMap.get(nodeName));
                        open.add(childNode);

                    }
                    continue;
                }

                if(closed.containsKey(nodeName)){
                    if(closed.get(nodeName).getCost() > childNode.getCost()){
                        closed.remove(nodeName);
                        openHashMap.put(nodeName,childNode);
                        open.add(childNode);
                    }
                    continue;
                }

                openHashMap.put(nodeName, childNode);
                open.add(childNode);



            }
        }

    }
}
