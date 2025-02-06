package apprentissage;


import ai.djl.Application;
import simulation.Simulation;

public class ApprentissageArbre {
    public static void main(String[] args) {
        Application application = Application.Tabular.SOFTMAX_REGRESSION;
        long inputSize = 28*28;
        long outputSize = 10;
    }
}
