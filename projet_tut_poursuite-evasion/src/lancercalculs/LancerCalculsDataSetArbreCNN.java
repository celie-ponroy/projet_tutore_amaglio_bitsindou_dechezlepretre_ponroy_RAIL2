package lancercalculs;

import outils.DataCollector;
import outils.Outil;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Position;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class LancerCalculsDataSetArbreCNN {
    public static void launch() throws IOException {
        File file = new File("donnees/game_data.csv");
        if (file.exists()) {
            file.delete();
        }
        file = new File("donnees/game_data_validation.csv");
        if (file.exists()) {
            file.delete();
        }

        DataCollector.etiquettage("\"map\",\"pos\",\"rmap\",\"dep\"", "donnees/game_data_validation.csv");
        DataCollector.etiquettage("\"map\",\"pos\",\"rmap\",\"dep\"", "donnees/game_data.csv");

        Simulation sim = new Simulation(Comportements.ArbreDeterministeGard, Comportements.ArbreDeterministev2);
        List<double[][]> bayesien;
        List<Deplacement> deplacements;
        List<Position> positions;

        int[] deplacementInt = new int[Deplacement.values().length];
        HashMap<Position, Integer> sortie = new HashMap();
        int nbIteration = 8000;
        String nomFichier = "";
        for (int i = 0; i < nbIteration; i++) {
            //on récupére les déplacements
            deplacements = sim.getHistoriqueDeplacement().get(sim.getGardien());
            //on récupère les cartes bayesiennes
            bayesien = sim.getHistoriqueBayesien().get(sim.getGardien());
            //on récupère les positions
            positions = sim.getHistoriquePosition().get(sim.getGardien());

            //si it > 80% du nbIte choisit, on enregistre dans les donnée de validations
            nomFichier = (i >= nbIteration * 0.8) ? "donnees/game_data_validation.csv" : "donnees/game_data.csv";
            for (int j = 0; j < deplacements.size(); j++) {

                deplacementInt[deplacements.get(j).ordinal()] += 1;

                int x = positions.get(j).getX();
                int y = positions.get(j).getY();
                DataCollector.saveDataCNN(Outil.applatissement(bayesien.get(j + 1)), x, y, Outil.applatissement(sim.getCarteMursSortie()), deplacements.get(j).ordinal(), nomFichier);
            }
            if (!sortie.containsKey(Simulation.getPosSortie())) {
                sortie.put(Simulation.getPosSortie(), 1);
            } else {
                sortie.replace(Simulation.getPosSortie(), sortie.get(Simulation.getPosSortie()) + 1);
            }
            //LancerCalculs.initSansDS();
            sim = new Simulation(Comportements.ArbreAleatoire, Comportements.ArbreDeterministev2);
            System.out.println("iterration : "+i);
        }
    }
}
