package simulation.comportement;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.NeuralNetwork;
import outils.Outil;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;

import java.io.Serializable;


public class ReseauDeNeurones implements Comportement, Serializable {

    private NeuralNetwork neuralNetwork;
    private Simulation simulation;
    private Personnage personnage;

    /**
     * Constructeur du reseau de neurones servant de lien entre Neuroph et Comportement
     *
     * @param nomFichier nom du fichier où le reseau est sauvegardee
     */
    public ReseauDeNeurones(String nomFichier, Simulation simulation, Personnage p) {
        neuralNetwork = NeuralNetwork.load(nomFichier);
        this.simulation = simulation;
        this.personnage = p;
    }

    @Override
    public Deplacement prendreDecision() {
        //input
        double[] bayesPlat = Outil.applatissement(simulation.getCarteBayesienne(personnage));
        double[] dsr = ArrayUtils.addAll(bayesPlat, personnage.getPosition().getY(), personnage.getPosition().getX());

        double max = -100;
        neuralNetwork.setInput(dsr);
        neuralNetwork.calculate();

        double[] output = neuralNetwork.getOutput();
        int index = -1;
        for (int i = 0; i < output.length; i++) {
            if (output[i] > max) {
                max = output[i];
                index = i;
            }
        }
        return Deplacement.values()[index];
    }
    @Override
    public Comportements getType() {
        return Comportements.ReseauArbreAleatoire;
    }
}