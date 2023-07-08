package ui.mutation;

import ui.neural.network.NeuralNet;

public interface Mutation {
    void mutateNT(NeuralNet neuralNet,double standardDeviation, double probability);
}
