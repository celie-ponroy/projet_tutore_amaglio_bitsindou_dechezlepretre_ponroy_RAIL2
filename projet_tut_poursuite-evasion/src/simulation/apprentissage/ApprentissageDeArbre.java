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
<<<<<<< HEAD
        //Args  0 : nb couches cachées
        //Args 1 : nb parties jouées
        int nbCouches = Integer.parseInt(args[0])+2;
        int[] couches = new int[nbCouches];
        couches[0] = 14*12+2;
        couches[couches.length-1] = 9;
<<<<<<< HEAD
        couches[1] = 10;
        couches[2] = 5;
=======
>>>>>>> a6f6818 (ajout calcul dataset)

        List<Integer> couches = new ArrayList<Integer>();
        couches.add(170);//entrées
        couches.add(100);//couche caché 1
        couches.add(50);//couche caché 2
        couches.add(10);//couche caché 3
        couches.add(9);//sortie
        NeuralNetwork<BackPropagation> rn = new MultiLayerPerceptron(couches, TransferFunctionType.SIGMOID);
        DataSet ds = new DataSet();

<<<<<<< HEAD
        ReseauDeNeurones rn = new ReseauDeNeurones(couches, 0.03);
=======
        couches[1] = 100;
        couches[2] = 75;
        couches[3]= 50;
        couches[4]= 25;
        couches[5] = 10;
        //Args 1 : nb neurones par couches cachées
        //Args 2: nb parties jouées
        //100 50 25 10
        ReseauDeNeurones rn = new ReseauDeNeurones(couches, 0.3);
>>>>>>> f6f0de2 (résolution conflit)
        int compteur = 0;
        while(compteur < Integer.parseInt(args[1])) {
            Simulation simulation = new Simulation(rn, false);
            simulation.apprentissage(500);
            /*System.out.println("Nombre de tours : " + simulation.getNbTours() + "\nSimulation finie : " + simulation.etreFini() + "\n");
            System.out.println("Gardien : " + simulation.getGardien());
            System.out.println("Prisonnier : " + simulation.getPrisonnier());*/
            System.out.println(compteur);
            compteur++;
        }
        Outil.sauve("Sauvegardes_reseaux/rnArbre"+args[1]+"-"+nbCouches+"-"+args[0]+".save", rn);
=======
        Simulation sim = new Simulation(rn);
>>>>>>> a6f6818 (ajout calcul dataset)
    }
}
