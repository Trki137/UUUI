package ui.searchalg;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public interface SearchAlgorithm {
    PrintStream printStream = new PrintStream(System.out,true, StandardCharsets.UTF_8);
    void search(Path stateSpacePath, Path heuristicPath);

}
