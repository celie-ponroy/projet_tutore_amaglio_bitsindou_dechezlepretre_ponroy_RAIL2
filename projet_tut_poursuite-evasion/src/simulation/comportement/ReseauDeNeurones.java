package simulation.comportement;

import org.neuroph.core.NeuralNetwork;
import simulation.Deplacement;


public class ReseauDeNeurones implements Comportement {

    private NeuralNetwork neuralNetwork;

    /**
     * Constructeur du reseau de neurones servant de lien entre Neuroph et Comportement
     *
     * @param nomFichier nom du fichier où le reseau est sauvegardee
     */
    public ReseauDeNeurones(String nomFichier) {
        neuralNetwork = NeuralNetwork.load(nomFichier);
    }

    @Override
    public Deplacement prendreDecision() {
        return null;
    }

    @Override
    public Deplacement prendreDecision(double[] entrees) {
        double max = -100;
        neuralNetwork.setInput(entrees);
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
}