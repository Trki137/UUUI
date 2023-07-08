package ui;

import ui.algorithm.ID3;
import ui.table.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Solution {
    private static final String BASE = "C:\\Users\\Dean\\OneDrive - fer.hr\\Desktop\\FER\\6.semestar\\UUUI\\autograder\\data\\lab3\\files\\";
    public static void main(String[] args) throws IOException {
        if(args.length < 2 || args.length > 3)
            throw new IllegalArgumentException("Invalid number of args");

        Path trainingPath = Path.of(BASE + args[0]);
        Path testPath = Path.of(BASE + args[1]);

        Integer depth = args.length == 2 ? null : Integer.parseInt(args[2]);

        if(!Files.exists(trainingPath) || !Files.exists(testPath))
            throw new IllegalArgumentException("Invalid file.");


        List<String> trainingData = Files.readAllLines(trainingPath);
        List<String> testData = Files.readAllLines(testPath);

        ID3 id3 = new ID3(depth);
        id3.fit(new Table(trainingData));
        id3.predict(new Table(testData));
    }
}
