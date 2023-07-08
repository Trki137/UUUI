package ui.resolution;

import ui.clause.Clause;
import ui.clause.ClausePair;
import ui.clause.Pair;
import ui.util.Util;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Resolution {
    private final Map<Integer, Clause> clausesMap;
    private final Map<Integer, Clause> SOSClauses;
    private final Clause endClause;

    public Resolution(Path pathToClauses){
        this.clausesMap = Util.readClauses(pathToClauses);

        if(Objects.isNull(clausesMap)){
            throw new IllegalArgumentException("Invalid file");
        }

        this.endClause = clausesMap.get(clausesMap.size());
        clausesMap.remove(clausesMap.size());
        SOSClauses = Util.getSOSClauses(endClause,clausesMap.size() + 1);
        Util.moveRedundant(clausesMap,SOSClauses,endClause);
    }
    public Resolution(Map<Integer,Clause> clausesMap, Map<Integer, Clause> SOSClauses, Clause endClause){
        this.clausesMap = clausesMap;
        this.SOSClauses = SOSClauses;
        Util.moveRedundant(this.clausesMap,SOSClauses,endClause);
        this.endClause = endClause;
    }

    PrintStream printStream = new PrintStream(System.out,true, StandardCharsets.UTF_8);

    public void refuteByResolution(){
        int longestString1 = Util.printMap(clausesMap,printStream);
        int longestString2 = Util.printMap(SOSClauses,printStream);

        String equalFormat = "=".repeat(Math.max(longestString1, longestString2) + 6) + "\n";
        printStream.printf(equalFormat);

        Map<Pair, ?> usedPairsMap = new HashMap<>();
        Map<Clause, ?> usedClausesMap = new HashMap<>();

        HashMap<Integer, Clause> clauseMapCopy = new HashMap<>(clausesMap);
        HashMap<Integer, Clause> SOSClauseMapCopy = new HashMap<>(SOSClauses);

        int number = new TreeMap<>(SOSClauses).lastKey() + 1;

        StringBuilder sb = new StringBuilder();

        while(true){

            ClausePair clausePair = get2clauses(clauseMapCopy,SOSClauseMapCopy,usedPairsMap);

            if(Objects.isNull(clausePair)){
                printStream.printf("[CONCLUSION]: %s is unknown\n\n", endClause.getClause());
                return;
            }

            Clause newClause = clausePair.resolve(number);

            if(Objects.isNull(newClause)){
                String output = Util.getOutput(clausePair);
                int firstNumber = clausePair.getFirstClause().getNumber();
                int secondNumber = clausePair.getSecondClause().getNumber();
                sb.append(output).append(String.format("NIL (%d, %d)", firstNumber,secondNumber));
                break;
            }

            if(usedClausesMap.containsKey(newClause) || newClause.isTautology()) continue;

            if(!Util.moveRedundant(clauseMapCopy,SOSClauseMapCopy,newClause,endClause)) continue;

            usedClausesMap.put(newClause,null);
            newClause.setCreatedBy(clausePair);
            SOSClauseMapCopy.put(number++,newClause);

        }

            printStream.printf(sb.append("\n").toString());
            printStream.printf(equalFormat);


        printStream.printf("[CONCLUSION]: %s is true\n\n", endClause.getClause());
    }



    private ClausePair get2clauses(Map<Integer,Clause> clauseMap, Map<Integer,Clause> SOSClauses, Map<Pair, ?> usedPairsMap){
        PriorityQueue<Clause> clausePriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Clause::getNumber));
        clausePriorityQueue.addAll(SOSClauses.values());

        for(var firstClause : clausePriorityQueue){
            for(var secondClause : clauseMap.values()){
                boolean compatible = ClausePair.isCompatible(firstClause,secondClause);
                if(!compatible) continue;

                Pair pair = new Pair(firstClause.getNumber(), secondClause.getNumber());

                if(usedPairsMap.containsKey(pair)) continue;

                usedPairsMap.put(pair, null);

                return new ClausePair(firstClause,secondClause);
            }
        }

        for(var firstClause : clausePriorityQueue){
            for(var secondClause : clausePriorityQueue){

                boolean compatible = ClausePair.isCompatible(firstClause,secondClause);
                if(!compatible) continue;

                Pair pair = new Pair(firstClause.getNumber(), secondClause.getNumber());

                if(usedPairsMap.containsKey(pair)) continue;

                usedPairsMap.put(pair,null);

                return new ClausePair(secondClause,firstClause);
            }
        }

        return null;
    }

}
