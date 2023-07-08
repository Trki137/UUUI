package ui;

import ui.cooking.CookBook;
import ui.resolution.Resolution;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Solution {

    private final static String RESOLUTION = "resolution";
    private final static String COOKING= "cooking";

    public static void main(String[] args) {
        if(args.length == 0 || args.length > 3){
            System.out.println("Invalid arguments.");
            return;
        }

        String flag = args[0];

        if(!(flag.equals(RESOLUTION) || flag.equals(COOKING))){
            System.out.printf("Invalid flag. Expected %s or %s but got %s.",RESOLUTION,COOKING,flag);
            return;
        }

        Path pathToClauses = Path.of(args[1]).toAbsolutePath().normalize();
        Path pathToUserCommands = null;

        if(args.length > 2) pathToUserCommands = Path.of(args[2]).toAbsolutePath().normalize();

        if(Objects.isNull(pathToUserCommands))
            new Resolution(pathToClauses).refuteByResolution();

        else new CookBook(pathToClauses,pathToUserCommands).cook();


    }
}