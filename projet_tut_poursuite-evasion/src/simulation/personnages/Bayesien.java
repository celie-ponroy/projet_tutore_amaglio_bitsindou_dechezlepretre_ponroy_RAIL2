package simulation.personnages;

import simulation.Simulation;
import java.util.ArrayList;
import java.util.List;

public class Bayesien {
    Double[][] carteBayesienne;
    List<Integer[]> casesValides;

    /**
     * Constructeur de l'inférence Bayesienne, mets les proba de présence a 1/nbCaseValide sur la carte
     */
    public Bayesien() {
        int[][] carte = Simulation.carte;
        casesValides = new ArrayList<Integer[]>();
        carteBayesienne = new Double[carte.length][carte[0].length];
        for (int i = 0; i < carte.length; i++) {
            for (int j = 0; j < carte[0].length; j++) {
                //Initialisation du tableau a -1
                carteBayesienne[i][j] = -1.0;
                if (carte[i][j] == 0) {
                    casesValides.add(new Integer[]{i, j});
                }
            }
        }
        //On remplace les cases valides par la proba de présence initiale
        for (Integer[] cases : casesValides) {
            carteBayesienne[cases[0]][cases[1]] = 1.0 / casesValides.size();
        }
    }

    public Double[][] getCarteBayesienne() {
        return carteBayesienne;
    }
}
