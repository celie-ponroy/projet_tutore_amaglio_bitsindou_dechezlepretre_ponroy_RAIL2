package outils;

import simulation.comportement.ReseauDeNeurones;

import java.io.*;
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

    public static void afficher_tab(double[] tab){
        for(int i = 0; i < tab.length; i++){
            System.out.printf("%5f\t", tab[i]);
        }
        System.out.println();
    }

    public static ReseauDeNeurones chargerRn(String nomFichier) {
        ReseauDeNeurones rn = null;
        try {
            // creer le flux de lecture ( fichier puis flux d ’ objet )
            FileInputStream is = new FileInputStream("Sauvegardes_reseaux/" + nomFichier);
            ObjectInputStream ois = new ObjectInputStream(is);
            // lit l ’ objet et le cast en point
            rn = (ReseauDeNeurones) (ois.readObject());
            // referme le flux de lecture
            ois.close();
        } catch (IOException e) {
            System.out.println(" erreur d ’E / S ");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(" erreur hors E / S ");
            e.printStackTrace();
        }
        return rn;
    }
}
