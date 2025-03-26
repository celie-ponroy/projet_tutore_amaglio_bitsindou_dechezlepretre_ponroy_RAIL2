package outils;

import simulation.Case;
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
        List<Case> sorties = new ArrayList<>();
        try{
            BufferedReader bf = new BufferedReader(new FileReader(nom));
            String ligne = bf.readLine();
            int y = 0;
            while (ligne != null){
                int[] nbLigne = new int[ligne.length()];
                int car;
                for (int i = 0; i < ligne.length(); i++) {
                    car = ligne.charAt(i) - '0';
                    if (car >= CaseEnum.MUR.ordinal() && car <= CaseEnum.CAMERA.ordinal()) {
                        if(car == CaseEnum.SORTIE.ordinal()){
                            sorties.add(new Case(i, y));
                        }
                        nbLigne[i] = car;
                    } else {
                        nbLigne[i] = CaseEnum.SOL.ordinal();
                    }
                }
                lignes.add(nbLigne);
                ligne = bf.readLine();
                y++;
            }
        } catch (IOException e) {
            System.out.println("Erreur d'E/S");
        }
        carte = lignes.toArray(new int[0][]);
        //On détermine aléatoirement la sortie
        Case sortie = sorties.get((int)(Math.random()*sorties.size()));
        carte[sortie.getY()][sortie.getX()] = CaseEnum.SORTIE.ordinal();
        return carte;
    }
}
