package ui.neural.network;

import ui.activation.function.ActivationFunction;
import ui.util.Row;

import java.util.*;

public class NeuralNet{
    private final Map<Integer, List<Neuron>> layers;
    private final ActivationFunction activationFunction;
    private double output;
    private double error;
    private double fitness;

    public NeuralNet(int[] dimensions, int inputSize, ActivationFunction activationFunction){
        this.layers = new HashMap<>();
        this.activationFunction = activationFunction;
        constructNN(dimensions,inputSize);
    }

    public NeuralNet(Map<Integer, List<Neuron>> layers, ActivationFunction activationFunction){
        this.layers = layers;
        this.activationFunction = activationFunction;
    }

    public double propagateForward(Row input){
        List<Double> inputValues = input.getInput();
        for(var entry : layers.entrySet()){
            List<Neuron> neurons = entry.getValue();

            for(Neuron neuron: neurons){
                neuron.calculate(inputValues);
            }

            inputValues = neurons.stream().map(Neuron::getY).toList();
        }

        output = inputValues.get(0);

        return output;
    }

    private void constructNN(int[] dimensions, int inputSize) {
        for(int i = 0; i < dimensions.length; i++){

            List<Neuron> layer = new ArrayList<>();
            for(int j = 0; j < dimensions[i]; j++){
                if(i == 0) layer.add(new Neuron(inputSize,false,activationFunction));
                else if(i == dimensions.length - 1) layer.add(new Neuron(dimensions[i-1],true,activationFunction));
                else layer.add(new Neuron(dimensions[i - 1],false,activationFunction));
            }
            layers.put(i,layer);
        }
    }

    public Map<Integer, List<Neuron>> getLayers() {
        return layers;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setError(double error) {
        this.error = error;
        setFitness(1/error);
    }

    public double getError() {
        return error;
    }

    public double getFitness() {
        return fitness;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }
}
