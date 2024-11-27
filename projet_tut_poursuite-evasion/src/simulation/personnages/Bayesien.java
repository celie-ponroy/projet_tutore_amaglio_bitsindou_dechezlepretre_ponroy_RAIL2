package simulation.personnages;

import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

public class Bayesien {
    double[][] carteBayesienne;
    List<Integer[]> casesValides;

    /**
     * Constructeur de l'inférence Bayesienne, mets les proba de présence a 1/nbCaseValide sur la carte
     */
    public Bayesien() {
        int[][] carte = Simulation.carte;
        casesValides = new ArrayList<Integer[]>();
        carteBayesienne = new double[carte.length][carte[0].length];
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

    /**
     * Méthode permetant d'actualiser la carte de probabilité de présence en fonction de la vue d'un agent
     *
     * @param ancienneCarteProba carte de probabilité de présence du tour précédent
     * @param casesVues          les cases vues par l'agents
     * @return carte de probabilité de présence actualisé
     */
    public double[][] calculerProbaPresence(double[][] ancienneCarteProba, List<Integer[]> casesVues) {
        double probaTransition = 0;
        int nombreCasesVoisineValide = 0;

        for (int i = 0; i < ancienneCarteProba.length; i++) {
            for (int j = 0; j < ancienneCarteProba[0].length; j++) {
                //On regarde si la case est une case valide
                if (casesValides.contains(new Integer[]{i, j})) {

                    List<Integer[]> caseVoisineValide = getCasesVoisineValide(j, i);
                    for (Integer[] caseVoisine : caseVoisineValide) {
                        //On calcule la probabilité de transition d'une case a une autre de chaque case voisine
                        probaTransition += 1.0 / getCasesVoisineValide(caseVoisine[1], caseVoisine[0]).size();
                        nombreCasesVoisineValide++;
                    }
                    carteBayesienne[i][j] = probaTransition / nombreCasesVoisineValide;
                    probaTransition = 0;
                    nombreCasesVoisineValide = 0;
                }
            }
        }

        //On actualise nos probabilité en fonction des classes vues
        for (Integer[] caseVue : casesVues) {
            if (caseVue[2] == 0) {
                for (Integer[] caseValide : casesValides) {
                    // On divise la proba actuelle par la somme des proba sans la case vue
                    double probaTotalSansCaseVue = 1 - carteBayesienne[caseVue[0]][caseVue[1]];
                    double probaCaseModifier = carteBayesienne[caseValide[0]][caseValide[1]];
                    carteBayesienne[caseValide[0]][caseValide[1]] = probaCaseModifier / probaTotalSansCaseVue;
                }
                carteBayesienne[caseVue[0]][caseVue[1]] = 0;
            } else {
                for (Integer[] caseValide : casesValides) {
                    carteBayesienne[caseValide[0]][caseValide[1]] = 0;
                }
                carteBayesienne[caseVue[0]][caseVue[1]] = 1;
            }
        }
        return carteBayesienne;
    }

    /**
     * Méthode renvoyant les cases voisines d'une case de coordonée x,y
     *
     * @param x coordoné en abscisse de la case donnée
     * @param y coordoné en ordonée de la case donnée
     * @return list de case coisine n'étant pas des murs
     */
    public List<Integer[]> getCasesVoisineValide(int x, int y) {
        ArrayList<Integer[]> casesVoisinesValides = new ArrayList<>();
        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                int i = y + k, j = x + l;
                if (x >= 0 && x < Simulation.carte.length && y >= 0 && y < Simulation.carte[0].length) {
                    //On regarde si la case exploré appartient au case valide
                    if (casesValides.contains(new Integer[]{i, j})) {
                        casesVoisinesValides.add(new Integer[]{i, j});
                    }
                }
            }
        }
        return casesVoisinesValides;
    }

    public double[][] getCarteBayesienne() {
        return carteBayesienne;
    }
}
