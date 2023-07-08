package ui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Row {
    private final List<Double> values = new ArrayList<>();

    public Row(String line){
        Arrays.stream(line.split(",")).forEach(data -> values.add(Double.parseDouble(data)));
    }
    public static String extractData(List<Row> testDataRows, Path path) {
        List<String> lines;
        try{
            lines = Files.readAllLines(path);
        }catch (IOException e){
            throw new InvalidPathException(path.toString(),"Couldn't open file.");
        }

        String header = lines.remove(0);

        for(String line : lines)
            testDataRows.add(new Row(line));

        return header;
    }

    public Double getTargetValue(){
        return values.get(values.size() - 1);
    }

    public List<Double> getInput(){
        List<Double> input = new ArrayList<>(values.size() - 1);
        for(int i = 0; i < values.size() - 1; i++)
            input.add(values.get(i));

        return input;
    }
}
