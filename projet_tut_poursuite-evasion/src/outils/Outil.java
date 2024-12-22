package outils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Outil {
    /**
     * Methode qui permet d'enregistrer un object dans un fichier
     *
     * @param nomFichier nom du fichier où l'object sera sauvegarde
     * @param obj        object a sauvegarder
     */
    public static void sauve(String nomFichier, Object obj) {
        try {
            // cree un flux de sortie ( fichier puis flux d ’ objet )
            FileOutputStream os = new FileOutputStream(nomFichier);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            // ecrit l ’ objet
            oos.writeObject(obj);
            // referme le flux
            oos.close();
        } catch (IOException e) {
            System.out.println(" erreur d ’E / S ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" erreur hors E / S ");
            e.printStackTrace();
        }
    }

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
}
