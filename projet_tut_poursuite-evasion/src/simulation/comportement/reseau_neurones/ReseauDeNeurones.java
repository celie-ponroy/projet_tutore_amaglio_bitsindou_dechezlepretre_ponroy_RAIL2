package simulation.comportement.reseau_neurones;

import simulation.Deplacement;
import simulation.comportement.Comportement;

<<<<<<< HEAD
import java.io.Serial;
import java.io.Serializable;
=======
>>>>>>> 1279a4b (rebasage arbre)
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

<<<<<<< HEAD
public class ReseauDeNeurones implements Comportement, Serializable {
=======
public class ReseauDeNeurones implements Comportement{
>>>>>>> 1279a4b (rebasage arbre)
    private double vitesseApprentissage = 0.6;
    private Couche[] couches;
    private FonctionActivation fonctionActivation;

<<<<<<< HEAD
    /**
     * @param Couches              Nb neurones par couches
     * @param vitesseApprentissage tx d'apprentissage
     */
    public ReseauDeNeurones(int[] Couches, double vitesseApprentissage) {
        this.vitesseApprentissage = vitesseApprentissage;
        fonctionActivation = new Sigmoide();

        this.couches = new Couche[Couches.length];
        for (int i = 0; i < Couches.length; i++) {
            if (i != 0) {
=======

    /**
     * @param Couches Nb neurones par couches
     * @param learningRate tx d'apprentissage
     */

    public ReseauDeNeurones(int[] Couches, double learningRate) {
        vitesseApprentissage = learningRate;
        fonctionActivation = new Sigmoide();

        this.couches = new Couche[Couches.length];
        for(int i = 0; i < Couches.length; i++) {
            if(i != 0) {
>>>>>>> 1279a4b (rebasage arbre)
                this.couches[i] = new Couche(Couches[i], Couches[i - 1]);
            } else {
                this.couches[i] = new Couche(Couches[i], 0);
            }
        }
    }

<<<<<<< HEAD
=======

>>>>>>> 1279a4b (rebasage arbre)
    /**
     * Réponse à une entrée
     *
     * @param input l'entrée testée
     * @return résultat de l'exécution
     */
    public double[] execute(double[] input) {
        int i, j, k;
        double new_value;

        double output[] = new double[couches[couches.length - 1].Length];

        // input en entrée du réseau
<<<<<<< HEAD
        for (i = 0; i < couches[0].Length; i++) {
=======
        for(i = 0; i < couches[0].Length; i++) {
>>>>>>> 1279a4b (rebasage arbre)
            couches[0].Neurons[i].Value = input[i];
        }

        // calculs couches cachées et sortie
<<<<<<< HEAD
        for (k = 1; k < couches.length; k++) {
            for (i = 0; i < couches[k].Length; i++) {
                new_value = 0.0;
                for (j = 0; j < couches[k - 1].Length; j++)
=======
        for(k = 1; k < couches.length; k++) {
            for(i = 0; i < couches[k].Length; i++) {
                new_value = 0.0;
                for(j = 0; j < couches[k-1].Length; j++)
>>>>>>> 1279a4b (rebasage arbre)
                    new_value += couches[k].Neurons[i].Weights[j] * couches[k - 1].Neurons[j].Value;

                new_value -= couches[k].Neurons[i].Bias;
                couches[k].Neurons[i].Value = fonctionActivation.evaluate(new_value);
            }
        }

        // Renvoyer sortie
<<<<<<< HEAD
        for (i = 0; i < couches[couches.length - 1].Length; i++) {
            output[i] = couches[couches.length - 1].Neurons[i].Value;
=======
        for(i = 0; i < couches[couches.length-1].Length; i++) {
            output[i] = couches[couches.length-1].Neurons[i].Value;
>>>>>>> 1279a4b (rebasage arbre)
        }
        return output;
    }

<<<<<<< HEAD
    /**
     * Rétropropagation
     *
     * @param entrees L'entrée courante
     * @param sortie  Sortie souhaitée (apprentissage supervisé !)
     * @return Error différence entre la sortie calculée et la sortie souhaitée
     */
    public double retroPropagation(double[] entrees, double[] sortie) {
=======



    /**
     * Rétropropagation
     * @param entrees    L'entrée courante
     * @param sortie   Sortie souhaitée (apprentissage supervisé !)
     * @return Error différence entre la sortie calculée et la sortie souhaitée
     */

    public double backPropagate(double[] entrees, double[] sortie) {
>>>>>>> 1279a4b (rebasage arbre)
        double nouvelle_sorties[] = execute(entrees);
        double error;
        int i, j, k;

<<<<<<< HEAD
        // Erreur de sortie
        for (i = 0; i < couches[couches.length - 1].Length; i++) {
            error = sortie[i] - nouvelle_sorties[i];
            couches[couches.length - 1].Neurons[i].Delta = error * fonctionActivation.evaluateDer(nouvelle_sorties[i]);
        }

        for (k = couches.length - 2; k >= 0; k--) {
            // Calcul de l'erreur courante pour les couches cachées
            // et mise à jour des Delta de chaque neurone
            for (i = 0; i < couches[k].Length; i++) {
                error = 0.0;
                for (j = 0; j < couches[k + 1].Length; j++)
                    error += couches[k + 1].Neurons[j].Delta * couches[k + 1].Neurons[j].Weights[i];
                couches[k].Neurons[i].Delta = error * fonctionActivation.evaluateDer(couches[k].Neurons[i].Value);
            }
            // Mise à jour des poids de la couche suivante
            for (i = 0; i < couches[k + 1].Length; i++) {
                for (j = 0; j < couches[k].Length; j++)
                    couches[k + 1].Neurons[i].Weights[j] += vitesseApprentissage * couches[k + 1].Neurons[i].Delta *
                            couches[k].Neurons[j].Value;
                couches[k + 1].Neurons[i].Bias -= vitesseApprentissage * couches[k + 1].Neurons[i].Delta;
=======

        // Erreur de sortie
        for(i = 0; i < couches[couches.length - 1].Length; i++) {
            error = sortie[i] - nouvelle_sorties[i];
            couches[couches.length-1].Neurons[i].Delta = error * fonctionActivation.evaluateDer(nouvelle_sorties[i]);
        }

        for(k = couches.length - 2; k >= 0; k--) {
            // Calcul de l'erreur courante pour les couches cachées
            // et mise à jour des Delta de chaque neurone
            for(i = 0; i < couches[k].Length; i++) {
                error = 0.0;
                for(j = 0; j < couches[k+1].Length; j++)
                    error += couches[k+1].Neurons[j].Delta * couches[k+1].Neurons[j].Weights[i];
                couches[k].Neurons[i].Delta = error * fonctionActivation.evaluateDer(couches[k].Neurons[i].Value);
            }
            // Mise à jour des poids de la couche suivante
            for(i = 0; i < couches[k+1].Length; i++) {
                for(j = 0; j < couches[k].Length; j++)
                    couches[k+1].Neurons[i].Weights[j] += vitesseApprentissage * couches[k+1].Neurons[i].Delta *
                            couches[k].Neurons[j].Value;
                couches[k+1].Neurons[i].Bias -= vitesseApprentissage * couches[k+1].Neurons[i].Delta;
>>>>>>> 1279a4b (rebasage arbre)
            }
        }

        // Calcul de l'erreur
        error = 0.0;
<<<<<<< HEAD
        for (i = 0; i < sortie.length; i++) {
=======
        for(i = 0; i < sortie.length; i++) {
>>>>>>> 1279a4b (rebasage arbre)
            error += Math.abs(nouvelle_sorties[i] - sortie[i]);
        }
        error = error / sortie.length;
        return error;
    }

    /**
     * @return VitesseApprentissage
     */
    public double getVitesseApprentissage() {
        return vitesseApprentissage;
    }

    /**
     * maj VitesseApprentissage
     *
     * @param rate nouveau VitesseApprentissage
     */
<<<<<<< HEAD
    public void setVitesseApprentissage(double rate) {
=======
    public void	setVitesseApprentissage(double rate) {
>>>>>>> 1279a4b (rebasage arbre)
        vitesseApprentissage = rate;
    }

    /**
     * maj fonction d'activation
     *
     * @param fun nouvelle fonction d'activation
     */
    public void setFonctionActivation(FonctionActivation fun) {
        fonctionActivation = fun;
    }

    /**
     * @return Taille couche d'entrée
     */
    public int getTailleCoucheEntrees() {
        return couches[0].Length;
    }


    /**
     * @return Taille couche de sortie
     */
    public int getTailleCoucheSorties() {
        return couches[couches.length - 1].Length;
    }

    @Override
    public Deplacement prendreDecision() {
        return null;
    }

    @Override
<<<<<<< HEAD
    public Deplacement prendreDecision(double[] entrees) {
        double[] sortie = this.execute(entrees);
        int indiceMax = -1;
        double max = -2;
        for (int i = 0; i < sortie.length; i++) {
            if (sortie[i] > max) {
=======
    public Deplacement prendreDecision(double[][] carteBayesienne) {
        List<Double> list = new ArrayList<>();
        Arrays.stream(carteBayesienne).forEach(array -> Arrays.stream(array).forEach(list::add));
        double[] flatCarte = new double[list.size()];
        for(int i = 0; i < list.size(); i++){
            flatCarte[i] = list.get(i);
        }
        double[] sortie = this.execute(flatCarte);
        int indiceMax = -1;
        double max = -2;
        for(int i = 0; i < sortie.length; i++){
            if(sortie[i] > max){
>>>>>>> 1279a4b (rebasage arbre)
                max = sortie[i];
                indiceMax = i;
            }
        }
        return Deplacement.values()[indiceMax];
    }
<<<<<<< HEAD

=======
>>>>>>> 1279a4b (rebasage arbre)
}