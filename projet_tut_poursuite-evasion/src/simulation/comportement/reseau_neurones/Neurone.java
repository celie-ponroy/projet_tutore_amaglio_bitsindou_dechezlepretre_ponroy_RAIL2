package simulation.comportement.reseau_neurones;


import java.io.Serializable;

class Neurone implements Serializable {
    public double    Value;
    public double[]  Weights;
    public double    Bias;
    public double    Delta;

    public Neurone(int prevLayerSize) {
        Weights = new double[prevLayerSize];
        Bias = Math.random();
        Delta = Math.random() / 10;
        Value = Math.random() / 10;

        for(int i = 0; i < Weights.length; i++)
            Weights[i] = Math.random()/ Weights.length;
    }
}