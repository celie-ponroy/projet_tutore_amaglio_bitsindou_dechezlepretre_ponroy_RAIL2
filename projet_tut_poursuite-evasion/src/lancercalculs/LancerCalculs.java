package lancercalculs;

import ai.djl.translate.TranslateException;
import calculs.CalculChemins;
import calculs.CalculVision;
import outils.ChargementCarte;
import simulation.Simulation;

import java.io.IOException;

public class LancerCalculs {
    static String laby = "donnees/laby.txt";

    /**
     * Methode qui lance le calcule de la vision, des chemin et du dataset
     * @throws IOException
     * @throws TranslateException
     */
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
        LancerCalculsDataSetArbreCNN.launch();
    }

    /**
     * Methode qui lance le calcule de la vision et des chemins
     * @throws IOException
     */
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

    public static void main(String[] args) throws IOException, TranslateException {
        initSansDS();
        //init();
    }
}
