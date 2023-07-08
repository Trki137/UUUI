package ui.mutation.impl;

import ui.mutation.Mutation;
import ui.neural.network.NeuralNet;
import ui.neural.network.Neuron;
import java.util.Random;

public class Gauss implements Mutation {

    private final Random random = new Random();
    @Override
    public void mutateNT(NeuralNet neuralNet, double standardDeviation, double probability) {
       for(var entry : neuralNet.getLayers().entrySet()){
            for(Neuron neuron : entry.getValue()){
                for(int i = 0;  i < neuron.getWeights().size(); i++){
                    if(random.nextDouble(0,100) >= probability*100) continue;
                    //System.out.println("Mutate weight");
                    neuron.mutateWeight(i,random.nextGaussian(0,standardDeviation));
                }
                if(random.nextDouble(0,100) >= probability*100) continue;

                neuron.mutateFreeFactor(random.nextGaussian(0,standardDeviation));
            }
        }
    }
}
