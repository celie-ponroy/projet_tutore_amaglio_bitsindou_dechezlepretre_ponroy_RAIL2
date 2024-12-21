package simulation.apprentissage;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import outils.Outil;
import simulation.Simulation;
import simulation.comportement.reseau_neurones.ReseauDeNeurones;

import java.util.ArrayList;
import java.util.List;

public class ApprentissageDeArbre {
    public static void main(String[] args) {

        List<Integer> couches = new ArrayList<Integer>();
        couches.add(170);//entrées
        couches.add(100);//couche caché 1
        couches.add(50);//couche caché 2
        couches.add(10);//couche caché 3
        couches.add(9);//sortie
        NeuralNetwork<BackPropagation> rn = new MultiLayerPerceptron(couches, TransferFunctionType.SIGMOID);
        DataSet ds = new DataSet();

        Simulation sim = new Simulation(rn);
    }
}
