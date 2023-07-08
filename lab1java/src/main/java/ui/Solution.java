package ui;

import ui.heuristic.HeuristicImpl;
import ui.searchalg.AStar;
import ui.searchalg.BFS;
import ui.searchalg.UCS;

import java.nio.file.Path;
import java.util.Objects;


//"--alg" "astar" "--h" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai_fail.txt" "--ss" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai.txt"
//"--alg" "ucs" "--ss" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai.txt"
//"--ss" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai.txt" "--h" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai_fail.txt" "--check-optimistic"
//"--ss" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai.txt" "--h" "C:\Users\Dean\Desktop\FER\6.semestar\UUUI\autograder\data\lab1\files\ai_fail.txt" "--check-consistent"


public class Solution {

    private final static String ALGORITHM_SHORTCUT = "--alg";

    private final static String STATE_SPACE_SHORTCUT = "--ss";

    private final static String HEURISTIC_SHORTCUT = "--h";

    private final static String CHECK_OPTIMISTIC_SHORTCUT = "--check-optimistic";

    private final static String CHECK_CONSISTENT_SHORTCUT = "--check-consistent";

    public static void main(String[] args) {


        if (args.length == 0)
            throw new IllegalArgumentException("Program arguments are empty");

        String searchAlgorithm = null;
        String stateSpacePath = null;
        String heuristicPath = null;

        boolean checkOptimistic = false;
        boolean checkConsistent = false;

        for (int i = 0; i < args.length; i++) {
            String shortcut = args[i];
            switch (shortcut) {
                case ALGORITHM_SHORTCUT -> searchAlgorithm = args[++i];
                case STATE_SPACE_SHORTCUT -> stateSpacePath = args[++i];
                case HEURISTIC_SHORTCUT -> heuristicPath = args[++i];
                case CHECK_OPTIMISTIC_SHORTCUT -> checkOptimistic = true;
                case CHECK_CONSISTENT_SHORTCUT -> checkConsistent = true;
                default -> throw new IllegalArgumentException(String.format("%s is not valid command", shortcut));
            }
        }

        if (stateSpacePath == null)
            throw new IllegalArgumentException("Path to state space is not defined");


        Path stateSpacePath1 = Path.of(stateSpacePath);

        if (searchAlgorithm != null) {

            if (searchAlgorithm.equals("bfs")) {
                (new BFS()).search(stateSpacePath1, null);
            }
            if (searchAlgorithm.equals("ucs")) {
                //if (stateSpacePath.contains("istra")) return;
                (new UCS()).search(stateSpacePath1, null);
            }
            if (searchAlgorithm.equals("astar") && heuristicPath != null) {
                //if (heuristicPath.contains("istra_pessimistic")) return;
                (new AStar()).search(stateSpacePath1, Path.of(heuristicPath).toAbsolutePath().normalize());
            }

        } else {

            assert heuristicPath != null;
            //if (heuristicPath.contains("istra")) return;
            Path heuristic = Path.of(heuristicPath).toAbsolutePath().normalize();

            if (checkOptimistic) {
                System.out.printf("# HEURISTIC-OPTIMISTIC %s\n", heuristic.getFileName());

                boolean isOptimistic = new HeuristicImpl().isHeuristicOptimistic(stateSpacePath1, heuristic);
                if (isOptimistic) System.out.println("[CONCLUSION]: Heuristic is optimistic.");
                else System.out.println("[CONCLUSION]: Heuristic is not optimistic.");
            }
            if (checkConsistent) {
                System.out.printf("# HEURISTIC-CONSISTENT %s\n", heuristic.getFileName());

                boolean isConsistent = new HeuristicImpl().isHeuristicConsistent(stateSpacePath1, heuristic);
                if (isConsistent) System.out.println("[CONCLUSION]: Heuristic is consistent.");
                else System.out.println("[CONCLUSION]: Heuristic is not consistent.");
            }
        }
    }
}