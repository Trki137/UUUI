package ui;

import org.apache.commons.math3.analysis.function.Sigmoid;
import ui.activation.function.ActivationFunction;
import ui.cross.impl.AverageCross;
import ui.ga.GeneticAlgorithm;
import ui.mutation.impl.Gauss;
import ui.neural.network.NeuralNet;
import ui.selection.impl.Wheel;
import ui.util.Row;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private static final String TRAIN_DATA = "--train";
    private static final String TEST_DATA = "--test";
    private static final String NEURAL_NETWORK_ARCHITECTURE = "--nn";
    private static final String POPULATION_SIZE = "--popsize";
    private static final String ELITISM = "--elitism";
    private static final String MUTATION_PROBABILITY = "--p";
    private static final String STANDARD_DEVIATION = "--K";
    private static final String NUMBER_OF_ITERATION = "--iter";
    public static void main(String[] args) {
        if (args.length != 16)
            throw new IllegalArgumentException("Invalid arguments");

        String testDataPath = "", trainDataPath = "", neuralNetworkArch = "";
        double mutationProbability = 0.0, standardDeviation = 0.0;
        int populationSize = 0, elitism = 0, iterations = 0;

        String arg = "";
        for (int i = 0; i < args.length; i++) {
            if (i % 2 == 0) {
                arg = args[i];
                continue;
            }
            switch (arg) {
                case TEST_DATA -> testDataPath = args[i];
                case TRAIN_DATA -> trainDataPath = args[i];
                case NEURAL_NETWORK_ARCHITECTURE -> neuralNetworkArch = args[i];
                case MUTATION_PROBABILITY -> mutationProbability = Double.parseDouble(args[i]);
                case STANDARD_DEVIATION -> standardDeviation = Double.parseDouble(args[i]);
                case NUMBER_OF_ITERATION -> iterations = Integer.parseInt(args[i]);
                case ELITISM -> elitism = Integer.parseInt(args[i]);
                case POPULATION_SIZE -> populationSize = Integer.parseInt(args[i]);
                default -> throw new IllegalArgumentException("Invalid argument " + args[i]);
            }
        }

        if (neuralNetworkArch.isEmpty())
            throw new IllegalArgumentException("Neural network architecture isn't defined");

        Path testData = Path.of(testDataPath).toAbsolutePath().normalize();
        Path trainData = Path.of(trainDataPath).toAbsolutePath().normalize();

        if (!Files.exists(testData))
            throw new IllegalArgumentException("Invalid path to test data. Given path " + testData);

        if (!Files.exists(trainData))
            throw new IllegalArgumentException("Invalid path to test data. Given path " + trainData);

        List<Row> trainDataRows = new ArrayList<>();
        List<Row> testDataRows = new ArrayList<>();

        String trainDataHeader = Row.extractData(trainDataRows, trainData);
        Row.extractData(testDataRows, testData);

        List<NeuralNet> startingPopulation = new ArrayList<>(populationSize);

        ActivationFunction activationFunction = sumOfWeights -> 1/(1+Math.exp(-sumOfWeights));

        String[] archSplit = neuralNetworkArch.split("s");
        int[] dimensions = new int[archSplit.length + 1];

        int i = 0;
        for(String dim :archSplit)
            dimensions[i++] = Integer.parseInt(dim);

        dimensions[i] = 1;

        for (i = 0; i < populationSize; i++)
            startingPopulation.add(
                    new NeuralNet(dimensions, trainDataHeader.split(",").length - 1, activationFunction)
            );

        GeneticAlgorithm GA = new GeneticAlgorithm(startingPopulation, elitism, mutationProbability, standardDeviation, iterations, new AverageCross(),new Gauss(),new Wheel());

        GA.fit(trainDataRows);
        GA.test(testDataRows);
    }
}
