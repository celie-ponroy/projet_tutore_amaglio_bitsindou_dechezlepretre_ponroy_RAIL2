package simulation.apprentissage;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;

public class ApprentissageDeArbre {
    public static void main(String[] args) {
        //args[0] : personnage a faire apprendre (P ou G)
        //args[1] : arbre a faire apprendre (Deterministe ou Aleatoire)

        List<Integer> nbNeuronesCouches = new ArrayList<Integer>();
        nbNeuronesCouches.add(170);
        nbNeuronesCouches.add(50);
        nbNeuronesCouches.add(10);
        nbNeuronesCouches.add(9);
        NeuralNetwork<BackPropagation> neuralNetwork = new MultiLayerPerceptron(nbNeuronesCouches, TransferFunctionType.SIGMOID);

        DataSet ds = DataSet.load("donnees/sauvegardes_DataSet/" + args[0] + "-DataSet-" + args[1]);
        neuralNetwork.learn(ds);
        System.out.println("enregistrer les neurones");
        neuralNetwork.save("donnees/sauvegardes_NeuralNetwork/" + args[0] + "-RN-" + args[1]);
    }
}
