package simulation.personnages;

import simulation.Case;
import simulation.CaseEnum;
import simulation.Simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                if (!(carte[i][j] == CaseEnum.MUR.ordinal())) {
                    casesValides.add(new Case(j, i));
                    carteBayesienne[i][j] = 0;
                }
            }
        }
        //On remplace les cases possible par la proba de présence initiale
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

        // Ajout des cases possibles pour le déplacement
        List<Case> casesValideVoisine = new ArrayList<>();
        for (Case caseVoisine : casesValides) {
            casesValideVoisine.addAll(getCasesVoisineValide(caseVoisine.getX(), caseVoisine.getY()));
        }

        // Suppression des doublons en utilisant un HashSet
        Set<Case> uniqueCases = new HashSet<>(casesValides);
        uniqueCases.addAll(casesValideVoisine);

        // Mise à jour de casesValides avec des cases uniques
        casesValides = new ArrayList<>(uniqueCases);

        for (Case ca : casesValides) {
            //On regarde si la case est une case valide
            List<Case> caseValideVoisine = getCasesVoisineValide(ca.getX(), ca.getY());
            for (Case caseVoisine : caseValideVoisine) {
                //On calcule la probabilité de transition d'une case a une autre de chaque case voisine
                double probaTransition = 1.0 / getCasesVoisineValide(caseVoisine.getX(), caseVoisine.getY()).size();
                sommeProba += ancienneCarteProba[caseVoisine.getY()][caseVoisine.getX()] * probaTransition;
            }
            carteBayesienne[ca.getY()][ca.getX()] = sommeProba;
            sommeProba = 0;

        }

        //On actualise nos probabilité en fonction des classes vues
        for (Integer[] caseVue : casesVues) {
            if (caseVue[2] == 0) {
                // La case vue est vide, on ajuste les probabilités
                for (Case caseValide : casesValides) {
                    // On divise la probabilité actuelle par la somme des probabilités sans la case vue
                    double probaTotalSansCaseVue = 1 - carteBayesienne[caseVue[0]][caseVue[1]];

                    double probaCaseModifier = carteBayesienne[caseValide.getY()][caseValide.getX()];
                    carteBayesienne[caseValide.getY()][caseValide.getX()] = probaCaseModifier / probaTotalSansCaseVue;

                }
                // On retire la case vue des cases valides
                casesValides.remove(new Case(caseVue[1], caseVue[0]));
                carteBayesienne[caseVue[0]][caseVue[1]] = 0;
            } else {
                // La case vue contient le personnage adverse
                for (int i = 0; i < carteBayesienne.length; i++) {
                    for (int j = 0; j < carteBayesienne[0].length; j++) {
                        // Toutes les cases sont mises à 0
                        if (Simulation.CARTE[i][j] != CaseEnum.MUR.ordinal()) {
                            carteBayesienne[i][j] = 0;
                        }
                    }
                }
                // Seule la case vue a une probabilité de 1
                carteBayesienne[caseVue[0]][caseVue[1]] = 1;
                // On retire toutes les cases valides sauf celles autour de la case vue
                casesValides.clear();
                casesValides.add(new Case(caseVue[1], caseVue[0]));
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
        // Créer une liste pour les cases voisines valides
        ArrayList<Case> casesVoisinesValides = new ArrayList<>();

        // Parcourir les voisins dans un rayon de 1 (y compris diagonales)
        for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
                int i = y + k;
                int j = x + l;

                // Vérifier que les indices sont dans les limites de la carte
                if (i >= 0 && i < Simulation.CARTE.length && j >= 0 && j < Simulation.CARTE[0].length) {
                    // Ne pas ajouter la case si c'est un mur ou si elle est déjà dans la liste
                    if (Simulation.CARTE[i][j] != CaseEnum.MUR.ordinal()) {
                        Case voisine = new Case(j, i);
                        if (!casesVoisinesValides.contains(voisine)) {
                            casesVoisinesValides.add(voisine);
                        }
                    }
                }
            }
        }

        // Retourner la liste des cases voisines valides (y compris la case d'origine)
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
