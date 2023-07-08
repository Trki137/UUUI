package ui.cross.impl;

import ui.cross.Cross;
import ui.neural.network.NeuralNet;
import ui.neural.network.Neuron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageCross implements Cross {
    @Override
    public NeuralNet cross(List<NeuralNet> twoNeuralNet) {
        NeuralNet firstNN = twoNeuralNet.get(0);
        NeuralNet secondNN = twoNeuralNet.get(1);

        Map<Integer, List<Neuron>> newLayers = new HashMap<>();
        Map<Integer, List<Neuron>> firstNNLayers = firstNN.getLayers();
        Map<Integer, List<Neuron>> secondNNLayers = secondNN.getLayers();


        int i = 0;
        while (firstNNLayers.containsKey(i)) {
            List<Neuron> firstNNNeurons = firstNNLayers.get(i);
            List<Neuron> secondNNNeurons = secondNNLayers.get(i);
            List<Neuron> newNNNeurons = new ArrayList<>();

            for (int j = 0; j < firstNNNeurons.size(); j++) {
                Neuron firstNeuron = firstNNNeurons.get(j);
                Neuron secondNeuron = secondNNNeurons.get(j);

                List<Double> newWeights = new ArrayList<>();
                List<Double> firstNeuronWeights = firstNeuron.getWeights();
                List<Double> secondNeuronWeights = secondNeuron.getWeights();

                for (int k = 0; k < firstNeuronWeights.size(); k++) {
                    //System.out.printf("w%d%d = %.6f, w%d%d = %.6f, w-new = %.6f\n",k,j,firstNeuronWeights.get(k),k,j,secondNeuronWeights.get(k),(firstNeuronWeights.get(k) + secondNeuronWeights.get(k)) / 2);
                    newWeights.add(k, (firstNeuronWeights.get(k) + secondNeuronWeights.get(k)) / 2);
                }

                double freeFactor = (firstNeuron.getFreeFactor() + secondNeuron.getFreeFactor()) / 2;

                newNNNeurons.add(new Neuron(newWeights, freeFactor, firstNeuron.getIsFinal(), firstNeuron.getActivationFunction()));
            }
            newLayers.put(i++, newNNNeurons);
        }

        return new NeuralNet(newLayers, firstNN.getActivationFunction());
    }
}
