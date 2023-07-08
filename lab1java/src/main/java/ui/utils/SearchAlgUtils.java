package ui.utils;

import ui.node.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SearchAlgUtils {

    private static final PrintStream printStream = new PrintStream(System.out,true, StandardCharsets.UTF_8);

    public static Set<Node> getNodes(String[] nodes, Node parent, Map<String,Node> visitedNode) {
        Set<Node> nodeList = new TreeSet<>(Comparator.comparing(Node::getValue));
        for(String childrenNode : nodes){
            String[] values = childrenNode.split(",");
            if(!visitedNode.containsKey(values[0]))
                nodeList.add(new Node(parent,Double.parseDouble(values[1]) + parent.getCost() ,values[0]));
        }

        return nodeList.size() != 0 ? nodeList : null;
    }


    public static Map<String, String[]> getStateMap(Path stateSpacePath) {
        List<String> stateSpaceFiltered;
        try{
            stateSpaceFiltered = Files.readAllLines(stateSpacePath.toAbsolutePath().normalize()).stream().filter(s -> !s.contains("#")).toList();
        }catch (IOException e){
            return null;
        }

        Map<String, String[]> stateMap = new HashMap<>();

        for(int i = 0; i < stateSpaceFiltered.size(); i++){
            String line = stateSpaceFiltered.get(i);
            if(!line.contains(":")){
                if(i == 0) stateMap.put("initialState", new String[]{line});
                if(i == 1) stateMap.put("finalState", line.split(" "));
                continue;

            }

            String nodeName = line.substring(0,line.indexOf(":"));
            String nodeChildrenLine = line.substring(line.indexOf(":"));
            String[] nodeChildren = null;


            if(nodeChildrenLine.length() > 1){
                nodeChildren = nodeChildrenLine.replace(":","").trim().split(" ");
            }

            stateMap.put(nodeName,nodeChildren);
        }

        return stateMap;
    }

    public static void printResult(Node currentNode, int statesVisited, String firstLine) {
        List<String> path = new LinkedList<>();

        int pathLength = 0;

        Node node = currentNode;

        while(!Objects.isNull(node)){
            pathLength++;
            path.add(node.getValue());
            node = node.getParent();
        }

        StringBuilder sb = new StringBuilder();

        for(int i = path.size() - 1; i >= 0; i--){
            if(i != 0)
                sb.append(path.get(i)).append(" => ");
            else
                sb.append(path.get(i));
        }

        printStream.printf("# %s\n", firstLine);
        printStream.print("[FOUND_SOLUTION]: yes\n");
        printStream.printf("[STATES_VISITED]: %d\n",statesVisited);
        printStream.printf("[PATH_LENGTH]: %d\n", pathLength);
        printStream.printf("[TOTAL_COST]: %.1f\n", currentNode.getCost());
        printStream.printf("[PATH]: %s\n", sb);

    }

    public static Map<String, Double> getHeuristicMap(Path heuristicPath) {
        Map<String, Double> heuristicMap = new HashMap<>();

        try(BufferedReader bf = Files.newBufferedReader(heuristicPath)){
            String line = bf.readLine();

            while(line != null){
                String nodeName = line.substring(0,line.indexOf(":"));
                Double heuristicValue = Double.parseDouble(line.substring(line.indexOf(":")+1).trim());

                heuristicMap.put(nodeName,heuristicValue);
                line = bf.readLine();
            }

        }catch (IOException e){
            printStream.println("Can't read file: "+heuristicPath.getFileName());
            return null;
        }

        return heuristicMap;
    }
}
