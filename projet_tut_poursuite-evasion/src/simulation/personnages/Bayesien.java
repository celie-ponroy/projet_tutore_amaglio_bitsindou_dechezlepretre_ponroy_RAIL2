package simulation.personnages;

import simulation.Case;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bayesien {
    double[][] carteBayesienne;
    List<Case> casesValides;

    /**
     * Constructeur de l'inférence Bayesienne, mets les proba de présence a 1/nbCaseValide sur la carte
     */
    public Bayesien() {
        int[][] carte = Simulation.CARTE;
        casesValides = new ArrayList<>();
        carteBayesienne = new double[carte.length][carte[0].length];
        for (int i = 0; i < carte.length; i++) {
            for (int j = 0; j < carte[0].length; j++) {
                //Initialisation du tableau a -1
                carteBayesienne[i][j] = -1.0;
                if (carte[i][j] == 0) {
                    casesValides.add(new Case(j, i, 0));
                }
            }
        }
        //On remplace les cases valides par la proba de présence initiale
        for (Case cases : casesValides) {
            carteBayesienne[cases.getY()][cases.getX()] = 1.0 / casesValides.size();
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
        double sommeProba = 0;
        for (int i = 0; i < ancienneCarteProba.length; i++) {
            for (int j = 0; j < ancienneCarteProba[0].length; j++) {
                //On regarde si la case est une case valide
                if (casesValides.contains(new Case(j, i, 0))) {

                    List<Case> caseVoisineValide = getCasesVoisineValide(j, i);
                    for (Case caseVoisine : caseVoisineValide) {
                        //On calcule la probabilité de transition d'une case a une autre de chaque case voisine
                        double probaTransition = 1.0 / getCasesVoisineValide(caseVoisine.getX(), caseVoisine.getY()).size();
                        sommeProba += ancienneCarteProba[caseVoisine.getY()][caseVoisine.getX()] * probaTransition;
                    }
                    carteBayesienne[i][j] = sommeProba;
                    sommeProba = 0;
                }
            }
        }

        //On actualise nos probabilité en fonction des classes vues
        for (Integer[] caseVue : casesVues) {
            if (caseVue[2] == 0) {
                for (Case caseValide : casesValides) {
                    // On divise la proba actuelle par la somme des proba sans la case vue
                    double probaTotalSansCaseVue = 1 - carteBayesienne[caseVue[0]][caseVue[1]];
                    double probaCaseModifier = carteBayesienne[caseValide.getY()][caseValide.getX()];
                    carteBayesienne[caseValide.getY()][caseValide.getX()] = probaCaseModifier / probaTotalSansCaseVue;
                }
                carteBayesienne[caseVue[0]][caseVue[1]] = 0;
            } else {
                for (Case caseValide : casesValides) {
                    carteBayesienne[caseValide.getY()][caseValide.getX()] = 0;
                }
                carteBayesienne[caseVue[0]][caseVue[1]] = 1;
                break;
            }
        }

        //Porba total des cases valides (doit etre egale a 1)
        double total = 0.0;
        for (Case cas : casesValides) {
            total += carteBayesienne[cas.getY()][cas.getX()];
        }

        //verification + correction des problèmes d'arrondissement
        if (total != 1) {
            for (Case cas : casesValides) {
                carteBayesienne[cas.getY()][cas.getX()] = carteBayesienne[cas.getY()][cas.getX()] * 100 / (total * 100);
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
    public List<Case> getCasesVoisineValide(int x, int y) {
        ArrayList<Case> casesVoisinesValides = new ArrayList<>();
        for (int k = -1; k < 2; k++) {
            for (int l = -1; l < 2; l++) {
                int i = y + k, j = x + l;
                if (x >= 0 && x < Simulation.CARTE[0].length && y >= 0 && y < Simulation.CARTE.length) {
                    //On regarde si la case exploré appartient au case valide
                    if (casesValides.contains(new Case(j, i, 0))) {
                        if ((k == -1) && (l == -1)) {
                            if (!(Simulation.CARTE[i][j + 1] == -1 || Simulation.CARTE[i + 1][j] == -1)) {
                                casesVoisinesValides.add(new Case(j, i, 0));
                                ;
                            }
                        } else if ((k == 1) && (l == -1)) {
                            if (!(Simulation.CARTE[i - 1][j] == -1 || Simulation.CARTE[i][j + 1] == -1)) {
                                casesVoisinesValides.add(new Case(j, i, 0));
                                ;
                            }
                        } else if ((k == -1) && (l == 1)) {
                            if (!(Simulation.CARTE[i][j - 1] == -1 || Simulation.CARTE[i + 1][j] == -1)) {
                                casesVoisinesValides.add(new Case(j, i, 0));
                                ;
                            }
                        } else if ((k == 1) && (l == 1)) {
                            if (!(Simulation.CARTE[i - 1][j] == -1 || Simulation.CARTE[i][j - 1] == -1)) {
                                casesVoisinesValides.add(new Case(j, i, 0));
                                ;
                            }
                        } else {
                            casesVoisinesValides.add(new Case(j, i, 0));
                        }

                    }
                }
            }
        }
        return casesVoisinesValides;
    }

    public List<Case> getPlusGrandeProbas() {
        double max = casesValides.stream().mapToDouble(caseValide -> carteBayesienne[caseValide.getY()][caseValide.getX()]).filter(caseValide -> caseValide >= 0).max().orElse(0);
        return casesValides.stream().filter(c -> carteBayesienne[c.getY()][c.getX()] == max).toList();
    }


    public double[][] getCarteBayesienne() {
        double[][] d = new double[Simulation.CARTE.length][Simulation.CARTE[0].length];
        for (int i = 0; i < carteBayesienne.length; i++) {
            for (int j = 0; j < carteBayesienne[0].length; j++) {
                d[i][j] = carteBayesienne[i][j];
            }
        }
        return d;
    }

    @Override
    public String toString() {
        String s = "Bayesien{" +
                "carteBayesienne= [";
        for (int i = 0; i < carteBayesienne.length; i++) {
            s += '\n' + "[";
            for (int j = 0; j < carteBayesienne[0].length; j++) {
                s += carteBayesienne[i][j] + ",";
            }
            s += "]";
        }
        s += "]";
        return s;
    }
}
