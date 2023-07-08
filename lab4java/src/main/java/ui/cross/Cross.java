package ui.cross;

import ui.neural.network.NeuralNet;

import java.util.List;

public interface Cross {
    NeuralNet cross(List<NeuralNet> twoNeuralNet);
}
