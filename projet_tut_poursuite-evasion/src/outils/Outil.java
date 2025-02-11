package outils;

import simulation.comportement.ReseauDeNeurones;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Outil {
    /**
     * Méthode qui permet de transformer un tableau a double entree en tableau a 1 entree
     *
     * @param tab tableau a transformer
     * @return tableau a une entree
     */
    public static double[] applatissement(double[][] tab) {
        List<Double> list = new ArrayList<>();
        Arrays.stream(tab).forEach(array -> Arrays.stream(array).forEach(list::add));
        double[] flatCarte = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            flatCarte[i] = list.get(i);
        }
        return flatCarte;
    }

    public static void afficher_tab(double[] tab) {
        for (int i = 0; i < tab.length; i++) {
            System.out.printf("%5f\t", tab[i]);
        }
        System.out.println();
    }

    public static void afficher_doubleTab(int[][] tab) {
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                System.out.printf("%d\t", tab[i][j]);
            }
            System.out.println();
        }

    }
}
