package lancercalculs;

import outils.DataCollector;
import outils.Outil;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.comportement.ArbreDecisionGardien;
import simulation.personnages.Position;

import java.util.List;

public class LancerCalculsDataSet {
    public static void main(String[] args) {
        DataCollector.etiquettage("\"map\",\"dep\"");
        Simulation sim = new Simulation(Comportements.ArbreAleatoire, Comportements.ArbreDeterministev2);
        List<double[][]> bayesien;
        List<Deplacement> deplacements;
        List<Position> positions;
        for(int i = 0; i < 500; i++){
            sim.deplacerAgents();
            //on récupére les déplacements
            deplacements = sim.historiqueDeplacement.get(sim.getGardien());
            //on récupère les cartes bayesiennes
            bayesien = sim.historiqueBayesien.get(sim.getGardien());
            //on récupère les positions
            positions = sim.historiquePosition.get(sim.getGardien());
            System.out.println(bayesien.size()+" "+deplacements.size()+" "+positions.size());
            for(int j = 0; j < deplacements.size(); j++){
                //
                double x = (double) (positions.get(j).getX()) /sim.CARTE[0].length;
                double y = (double) (positions.get(j).getY()) /sim.CARTE.length;
                DataCollector.saveData(Outil.applatissement(bayesien.get(j)), x, y, deplacements.get(j).ordinal());
            }
            sim = new Simulation(Comportements.ArbreAleatoire, Comportements.ArbreDeterministev2);
        }
    }
}
