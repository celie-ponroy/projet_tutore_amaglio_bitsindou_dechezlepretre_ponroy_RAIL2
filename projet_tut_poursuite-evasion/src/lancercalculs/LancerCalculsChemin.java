package lancercalculs;

import calculs.CalculChemins;
import calculs.CalculVision;

import java.io.IOException;

public class LancerCalculsChemin {
    public static void main(String[] args) throws IOException {
        System.out.println("Lancer les calculs de vision");
        CalculChemins.ecrireChemins("donnees/laby.txt");
    }
}
