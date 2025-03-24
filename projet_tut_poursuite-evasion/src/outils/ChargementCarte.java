package outils;

import simulation.CaseEnum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChargementCarte {
    public static int[][] charger(String nom) {
        int[][] carte;
        List<int[]> lignes = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(nom));
            String ligne = bf.readLine();
            while (ligne != null) {
                int[] nbLigne = new int[ligne.length()];
                int car;
                for (int i = 0; i < ligne.length(); i++) {
                    car = ligne.charAt(i) - '0';
                    if (car >= CaseEnum.MUR.ordinal() && car <= CaseEnum.CAMERA.ordinal()) {
                        nbLigne[i] = car;
                    } else {
                        nbLigne[i] = CaseEnum.SOL.ordinal();
                    }
                }
                lignes.add(nbLigne);
                ligne = bf.readLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur d'E/S");
        }
        return lignes.toArray(new int[0][]);
    }
}
