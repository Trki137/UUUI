package ui.selection.impl;

import ui.neural.network.NeuralNet;
import ui.selection.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wheel implements Selection {
    @Override
    public List<NeuralNet> select(List<NeuralNet> neuralNets) {
        Random random = new Random();
        List<NeuralNet> chosen = new ArrayList<>();
        List<NeuralNet> nn = neuralNets.stream().sorted((n1,n2) -> Double.compare(n2.getFitness(),n1.getFitness())).toList();
        double totalFitness = neuralNets.stream().mapToDouble(NeuralNet::getFitness).reduce(0.0, Double::sum);
        double lowerBound = 0.0;

        for (int i = 0; i < 2; i++) {

            while (true) {
                double r = random.nextDouble();
                NeuralNet chosenNN = null;
                int j = 0;
                for (NeuralNet neuralNet : nn) {
                    double upperBound = neuralNet.getFitness() / totalFitness + lowerBound;

                    if (r >= lowerBound && r < upperBound) {
                        lowerBound = 0.0;


                        chosenNN = neuralNet;
                        break;
                    }
                    j++;
                    lowerBound = upperBound;
                }
                if (chosenNN == null)
                    throw new IllegalStateException("Neural net cant be null");

                if (chosen.contains(chosenNN)) continue;

                chosen.add(chosenNN);
                break;
            }
        }

        return chosen;
    }
}
