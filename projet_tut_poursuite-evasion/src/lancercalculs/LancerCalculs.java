package lancercalculs;

import ai.djl.translate.TranslateException;
import calculs.CalculChemins;
import calculs.CalculVision;
import outils.ChargementCarte;
import simulation.Simulation;

import java.io.IOException;

public class LancerCalculs {
    static String laby = "donnees/labySmall.txt";

    public static void init() throws IOException, TranslateException {
        int[][] carte = ChargementCarte.charger(laby);
        System.out.println("Chargement chemin...");
        CalculChemins.CARTE = carte;
        CalculChemins.ecrireChemins(laby);
        System.out.println("Chargement vision...");
        CalculVision.CARTE = carte;
        CalculVision.ecrireVision();
        Simulation.CARTE = carte;
        System.out.println("Calcul dataset...");
        LancerCalculsDataSet.launch();
    }

    public static void initSansDS() throws IOException {
        int[][] carte = ChargementCarte.charger(laby);
        System.out.println("Chargement chemin...");
        CalculChemins.CARTE = carte;
        CalculChemins.ecrireChemins(laby);
        System.out.println("Chargement vision...");
        CalculVision.CARTE = carte;
        CalculVision.ecrireVision();
        Simulation.CARTE = carte;
    }
}
