package outils;

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


    /**
     * Méthode qui permet de transformer un tableau a double entree d'int en float
     *
     * @param tab tableau d'int a transformer
     * @return tableau de float
     */
    public static float[][] conversionIntFloat(int[][] tab) {
        float[][] realMapFloat = new float[tab.length][tab[0].length]; // Nouveau tableau de float
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                realMapFloat[i][j] = (float) tab[i][j]; // Conversion de int en float
            }
        }
        return realMapFloat;
    }

    /**
     * Méthode qui permet de transformer un tableau a double entree d'int en float
     *
     * @param tab tableau d'int a transformer
     * @return tableau de float
     */
    public static float[][] conversionDoubleFloat(double[][] tab) {
        float[][] realMapFloat = new float[tab.length][tab[0].length]; // Nouveau tableau de float
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                realMapFloat[i][j] = (float) tab[i][j]; // Conversion de int en float
            }
        }
        return realMapFloat;
    }

    /**
     * Méthode qui permet de transformer un tableau a double entree d'int en float
     *
     * @param tab tableau d'int a transformer
     * @return tableau de float
     */
    public static float[] conversionDoubleFloat(double[] tab) {
        float[] realMapFloat = new float[tab.length]; // Nouveau tableau de float
        for (int i = 0; i < tab.length; i++) {
            realMapFloat[i] = (float) tab[i]; // Conversion de int en float
        }
        return realMapFloat;
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
