package ui.util;

import ui.clause.Clause;
import ui.clause.ClausePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Util {
    public static Map<Integer, Clause> readClauses(Path pathToClauses) {
        Map<Integer, Clause> clausesMap = new HashMap<>();
        int counter = 1;
        try(BufferedReader br = Files.newBufferedReader(pathToClauses)){

            String line = br.readLine();

            while(!Objects.isNull(line)){
                line = line.trim();
                if(line.startsWith("#")){
                    line = br.readLine();
                    continue;
                }

                Clause clause = new Clause(line,counter);

                if(!clause.isTautology()) clausesMap.put(counter++,clause);

                line = br.readLine();
            }

        }catch (IOException e){
            System.out.println("Couldn't open file "+ pathToClauses.getFileName().toString());
            return null;
        }

        return clausesMap;
    }

    public static Map<Integer, Clause> getSOSClauses(Clause endClause,int startNumber) {
        Map<Integer, Clause> SOSClauses = new HashMap<>();
        Set<String> literals = endClause.getLiterals();

        for(String literal : literals){
            if(literal.contains("~")) SOSClauses.put(startNumber,new Clause(literal.substring(1),startNumber++));
            else SOSClauses.put(startNumber, new Clause("~"+literal,startNumber++));
        }

        return SOSClauses;
    }

    public static int printMap(Map<Integer, Clause> clausesMap, PrintStream printStream){
        int longestString = 0;

        for(var clause: clausesMap.entrySet()){

            if(longestString < clause.getValue().getClause().length())
                longestString = clause.getValue().getClause().length();

            printStream.printf("%s\n",clause.getValue());
        }

        return longestString;
    }

    public static String getOutput(ClausePair clausePair) {
            List<Clause> usedClauses = new ArrayList<>();
            Queue<Clause> nextClauses = new ArrayDeque<>();
            if(clausePair.getFirstClause().getCreatedBy() != null){
                usedClauses.add(clausePair.getFirstClause());
                nextClauses.add(clausePair.getFirstClause());
            }

        if(clausePair.getSecondClause().getCreatedBy() != null){
            usedClauses.add(clausePair.getSecondClause());
            nextClauses.add(clausePair.getSecondClause());
        }
            while(!nextClauses.isEmpty()){
                Clause checkClause = nextClauses.poll();

                if(checkClause.getCreatedBy() != null){
                    Clause firstClause =checkClause.getCreatedBy().getFirstClause();
                    Clause secondClause =checkClause.getCreatedBy().getSecondClause();

                    if(firstClause != null && firstClause.getCreatedBy() != null) nextClauses.add(firstClause);
                    if(secondClause != null && secondClause.getCreatedBy() != null) nextClauses.add(secondClause);
                }

                usedClauses.add(checkClause.getCreatedBy().getFirstClause());
            }


            usedClauses.sort(Comparator.comparingInt(Clause::getNumber).reversed());

            StringBuilder sb = new StringBuilder();

            for(int i = usedClauses.size() - 2; i >= 0; i--){
                sb.append(usedClauses.get(i)).append("\n");
            }

            return sb.toString();

        }

    public static List<String> readUserCommands(Path pathToUserCommands) throws IOException {

        return Files.readAllLines(pathToUserCommands).stream().filter(line -> !line.startsWith("#")).toList();
    }

    public static void moveRedundant(Map<Integer, Clause> clauseMap, Map<Integer, Clause> SOSMap, Clause endClause){
        Map<Integer, Clause> allClauses = new HashMap<>(clauseMap);
        allClauses.putAll(SOSMap);

        allClauses.remove(endClause.getNumber());

        HashSet<Integer> removedClauses = new HashSet<>();

        for(Clause firstClause: allClauses.values()){
            for(Clause secondClause: allClauses.values()){
                if(firstClause.equals(secondClause))continue;

                if(removedClauses.contains(firstClause.getNumber()) || removedClauses.contains(secondClause.getNumber())) return;

                if (secondClause.getLiterals().containsAll(firstClause.getLiterals())) removedClauses.add(secondClause.getNumber());
            }
        }

        for(Integer number: removedClauses){
            Clause c = SOSMap.remove(number);
            if(Objects.isNull(c)) clauseMap.remove(number);
        }
    }

    public static boolean moveRedundant(Map<Integer, Clause> clauseMap, Map<Integer, Clause> SOSMap, Clause newClause, Clause endClause){
        Map<Integer, Clause> allClauses = new HashMap<>(clauseMap);
        allClauses.putAll(SOSMap);
        allClauses.remove(endClause.getNumber());

        HashSet<Integer> removedClauses = new HashSet<>();

        for (Clause clause: allClauses.values()){
            if(newClause.getLiterals().containsAll(clause.getLiterals())){
                return false;
            }
        }

        for (Clause clause: allClauses.values()){
            if(clause.getLiterals().containsAll(newClause.getLiterals())){
                removedClauses.add(clause.getNumber());
            }
        }

        for(Integer number : removedClauses){
            Clause c = SOSMap.remove(number);
            if(Objects.isNull(c)) clauseMap.remove(number);
        }

        return true;
    }
}
