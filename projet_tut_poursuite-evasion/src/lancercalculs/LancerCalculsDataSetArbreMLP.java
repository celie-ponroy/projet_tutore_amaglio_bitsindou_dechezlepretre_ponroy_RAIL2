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
import java.util.stream.IntStream;

public class LancerCalculsDataSetArbreMLP {
    public static void launch() throws IOException {
        File file = new File("donnees/game_data.csv");
        if (file.exists()) {
            file.delete();
        }
        file = new File("donnees/game_data_validation.csv");
        if (file.exists()) {
            file.delete();
        }

        DataCollector.etiquettage("\"input\",\"dep\"", "donnees/game_data_validation.csv");
        DataCollector.etiquettage("\"input\",\"dep\"", "donnees/game_data.csv");

        Simulation sim = new Simulation(Comportements.ArbreDeterministeGard, Comportements.ArbreDeterministev2);
        List<double[][]> bayesien;
        List<Deplacement> deplacements;
        List<Position> positions;

        int[] deplacementInt = new int[Deplacement.values().length];
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
                double[] carteBaye = Outil.applatissement(bayesien.get(j + 1));
                double[][] cartePos2D = new double[sim.CARTE.length][sim.CARTE[0].length];
                cartePos2D[positions.get(j).getY()][positions.get(j).getX()] = 1;
                double[] cartePos = Outil.applatissement(cartePos2D);
                double[] carte = Outil.applatissement(sim.getCarteMursSortie());

                int[] deps = IntStream.range(0, Deplacement.values().length).map(x -> 0).toArray();
                deps[deplacements.get(j).ordinal()] = 1;
                DataCollector.saveDataMLP(Outil.concatener_tab(carte, Outil.concatener_tab(carteBaye, cartePos)), deplacements.get(j).ordinal(), nomFichier);
            }
            //LancerCalculs.initSansDS();
            sim = new Simulation(Comportements.ArbreAleatoire, Comportements.ArbreDeterministev2);
            System.out.println("iteration : "+i);
        }
    }
}
