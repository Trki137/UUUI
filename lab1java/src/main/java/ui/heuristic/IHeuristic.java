package ui.heuristic;

import java.nio.file.Path;

public interface IHeuristic {

    boolean isHeuristicOptimistic(Path stateSpacePath, Path heuristicPath);

    boolean isHeuristicConsistent(Path stateSpacePath, Path heuristicPath);

}
