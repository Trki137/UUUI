package ui.neural.network;

import ui.activation.function.ActivationFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Neuron {
    private double y;
    private List<Double> weights;
    private double freeFactor;
    private final Random random = new Random();
    private final Double STANDARD_DEVIATION = 0.01;
    private final boolean isFinalNeuron;
    private final ActivationFunction activationFunction;

    public Neuron(int numOfWeights, boolean isFinalNeuron, ActivationFunction activationFunction){
        this.isFinalNeuron = isFinalNeuron;
        this.activationFunction = activationFunction;
        this.weights = new ArrayList<>();

        for(int i = 0; i < numOfWeights; i++)
            weights.add(random.nextGaussian(0,STANDARD_DEVIATION));

        this.freeFactor = random.nextGaussian(0,STANDARD_DEVIATION);
    }


    public Neuron(List<Double> weights, double freeFactor, boolean isFinalNeuron, ActivationFunction activationFunction){
        this.isFinalNeuron = isFinalNeuron;
        this.activationFunction = activationFunction;
        this.weights = weights;
        this.freeFactor = freeFactor;
    }

    public void calculate(List<Double> input){
        double result = 0.0;
        for(int i = 0; i < weights.size(); i++){
            result += weights.get(i) * input.get(i);
        }

        result += freeFactor;

        if(!isFinalNeuron){
            result = activationFunction.calc(result);
        }

        y = result;
    }


    public void mutateWeight(int index, double gaussNoise){
        if(index < 0 || index >= weights.size())
            throw new IllegalArgumentException("Invalid index "+index);

        double oldWeight = weights.get(index);
        double newWeight = oldWeight + gaussNoise;

        //System.out.printf("Before mutation at %d ...",index);
        //System.out.println(Arrays.toString(weights.toArray()));
        weights.remove(index);
        weights.add(index,newWeight);
        //System.out.print("After mutation...");
        //System.out.println(Arrays.toString(weights.toArray()));

    }

    public void mutateFreeFactor(double gaussNoise){
        this.freeFactor += gaussNoise;
    }

    public double getFreeFactor() {
        return freeFactor;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public boolean getIsFinal(){
        return isFinalNeuron;
    }

}
