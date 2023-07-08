package ui.cooking;

import ui.clause.Clause;
import ui.resolution.Resolution;
import ui.util.Util;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class CookBook {
    private Map<Integer, Clause> clausesMap;
    private final List<String> userCommands;

    private final PrintStream printStream = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    public CookBook(Path pathToClauses, Path pathToUserCommands) {
        this.clausesMap = Util.readClauses(pathToClauses);
        try {
            this.userCommands = Util.readUserCommands(pathToUserCommands);
        }catch (IOException e){
            throw new IllegalArgumentException("Invalid file.");
        }
    }

    public void cook(){
        for(String commandLine : userCommands){
            String clause = commandLine.substring(0, commandLine.length() - 1).toLowerCase().trim();
            String command = commandLine.substring(commandLine.length() - 1);
            Map<Integer, Clause> clauseCopyMap = new HashMap<>(this.clausesMap);
            Clause newClause = new Clause(clause, clauseCopyMap.size() + 1);
            switch (command){
                case "?" -> {
                    Map<Integer,Clause> SOSMap = Util.getSOSClauses(newClause, clauseCopyMap.size() + 1);
                    printStream.printf("User's command: %s\n", commandLine);
                    new Resolution(clauseCopyMap,SOSMap,newClause).refuteByResolution();
                }
                case "+" -> {
                    printStream.printf("User's command: %s\n", commandLine);
                    clausesMap.put(clausesMap.size() + 1, newClause);
                }
                case "-" -> {
                    printStream.printf("User's command: %s\n", commandLine);
                    Map<Integer,Clause> newClauseMap = new HashMap<>();
                    int num = 1;
                    for(var clauseItem : clausesMap.entrySet()){
                        Set<String> literals = clauseItem.getValue().getLiterals();
                        Set<String> literals2 = newClause.getLiterals();

                        if(literals2.size() == literals.size()){
                            if(new HashSet<>(literals).containsAll(literals2)) continue;
                        }

                        Clause clause1 = new Clause(clauseItem.getValue().getClause(),num);

                        newClauseMap.put(num++, clause1);
                    }

                    this.clausesMap = newClauseMap;
                }
                default -> throw new IllegalArgumentException("Invalid command");

            }
        }
    }
}
