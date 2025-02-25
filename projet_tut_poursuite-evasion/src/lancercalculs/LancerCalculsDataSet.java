package lancercalculs;

import outils.DataCollector;
import outils.Outil;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Position;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LancerCalculsDataSet {
    public static void launch() throws IOException {
        File file = new File("donnees/game_data.csv");
        if (file.exists()) {
            file.delete();
        }
        file = new File("donnees/game_data_validation.csv");
        if (file.exists()) {
            file.delete();
        }

        DataCollector.etiquettage("\"map\",\"dep\"","donnees/game_data_validation.csv");
        DataCollector.etiquettage("\"map\",\"dep\"","donnees/game_data.csv");

        Simulation sim = new Simulation(Comportements.ArbreAleatoire, Comportements.ArbreDeterministev2);
        List<double[][]> bayesien;
        List<Deplacement> deplacements;
        List<Position> positions;

        int[] deplacementInt = new int[Deplacement.values().length];
        int nbIteration = 100;
        String nomFichier = "";
        for (int i = 0; i < nbIteration; i++) {
            //on récupére les déplacements
            deplacements = sim.getHistoriqueDeplacement().get(sim.getGardien());
            //on récupère les cartes bayesiennes
            bayesien = sim.getHistoriqueBayesien().get(sim.getGardien());
            //on récupère les positions
            positions = sim.getHistoriquePosition().get(sim.getGardien());
            //System.out.println(bayesien.size()+" "+deplacements.size()+" "+positions.size());

            //si it > 80% du nbIte choisit, on enregistre dans les donnée de validations
            nomFichier = (i >= nbIteration * 0.8) ? "donnees/game_data_validation.csv" : "donnees/game_data.csv";
            System.out.println(nomFichier);
            for (int j = 0; j < deplacements.size(); j++) {
                deplacementInt[deplacements.get(j).ordinal()] += 1;
                double x = (double) (positions.get(j).getX()) / sim.CARTE[0].length;
                double y = (double) (positions.get(j).getY()) / sim.CARTE.length;
                DataCollector.saveData(Outil.applatissement(bayesien.get(j + 1)), x, y, deplacements.get(j).ordinal(), nomFichier);
            }
            LancerCalculs.initSansDS();
            sim = new Simulation(Comportements.ArbreAleatoire, Comportements.ArbreDeterministev2);
            System.out.println(i);
        }

        for (int i = 0; i < deplacementInt.length; i++) {
            System.out.println(Deplacement.values()[i] + " " + deplacementInt[i]);
        }
    }
}
