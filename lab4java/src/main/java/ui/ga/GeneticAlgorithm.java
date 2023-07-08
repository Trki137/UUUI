package ui.ga;

import ui.cross.Cross;
import ui.mutation.Mutation;
import ui.neural.network.NeuralNet;
import ui.selection.Selection;
import ui.util.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithm {
    private List<NeuralNet> neuralNets;
    private NeuralNet bestNeuralNet;
    private final int POP_SIZE;
    private final int ELITISM;
    private final double MUTATION_PROBABILITY;
    private final double STANDARD_DEVIATION;
    private final int ITERATIONS;
    private final Cross CROSS_ALG;
    private final Mutation MUTATION_ALG;
    private final Selection SELECTION_ALG;

    public GeneticAlgorithm(List<NeuralNet> startingPopulation, int elitism, double mutationProbability, double standardDeviation, int iterations, Cross cross, Mutation mutation, Selection selection) {
        this.neuralNets = startingPopulation;
        this.POP_SIZE = startingPopulation.size();
        this.ELITISM = elitism;
        this.MUTATION_PROBABILITY = mutationProbability;
        this.STANDARD_DEVIATION = standardDeviation;
        this.ITERATIONS = iterations;
        this.CROSS_ALG = cross;
        this.MUTATION_ALG = mutation;
        this.SELECTION_ALG = selection;

    }

    public void fit(List<Row> trainData) {
        evaluate(neuralNets, trainData);
        for (int i = 1; i <= ITERATIONS; i++) {
            //System.out.printf("ITERATION %d.\n",i);
            List<NeuralNet> sortedNN = neuralNets.stream().sorted((n1, n2) -> Double.compare(n2.getFitness(), n1.getFitness())).toList();
            List<NeuralNet> NNForCross = sortedNN.subList(ELITISM, sortedNN.size());
            List<NeuralNet> elites = sortedNN.subList(0, ELITISM);

            List<NeuralNet> newNN = populate(sortedNN,POP_SIZE - ELITISM);
            evaluate(newNN, trainData);
            newNN.addAll(elites);

            if(i % 2000 == 0) {
                sortedNN = neuralNets.stream().sorted((n1, n2) -> Double.compare(n2.getFitness(), n1.getFitness())).toList();
                bestNeuralNet = sortedNN.get(0);
                System.out.printf("[Train error @%d]: %.6f\n", i, bestNeuralNet.getError());
            }

            neuralNets = newNN;
        }


    }

    private List<NeuralNet> populate(List<NeuralNet> NNForCross, int numOfChildren) {
        List<NeuralNet> newNNs = new ArrayList<>();

        for (int i = 0; i < numOfChildren; i++) {
            List<NeuralNet> twoNeuralNets = SELECTION_ALG.select(NNForCross);
            NeuralNet newNN = CROSS_ALG.cross(twoNeuralNets);
            MUTATION_ALG.mutateNT(newNN, STANDARD_DEVIATION, MUTATION_PROBABILITY);
            newNNs.add(newNN);
        }

        return newNNs;
    }

    private void evaluate(List<NeuralNet> neuralNets, List<Row> data) {
        for (NeuralNet neuralNet : neuralNets) {
            List<Double> errors = new ArrayList<>();

            for (Row row : data) {
                double output = neuralNet.propagateForward(row);
                errors.add(Math.pow(row.getTargetValue() - output, 2));
            }

            double error = errors.stream().reduce(0.0, Double::sum) / data.size();
            neuralNet.setError(error);
        }


    }

    public void test(List<Row> testDataRows) {
        evaluate(List.of(bestNeuralNet),testDataRows);
        System.out.printf("[Test error]: %.6f", bestNeuralNet.getError());
    }
}
