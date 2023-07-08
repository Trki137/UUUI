package ui.selection;

import ui.neural.network.NeuralNet;

import java.util.List;

public interface Selection {
    List<NeuralNet> select(List<NeuralNet> neuralNets);
}
