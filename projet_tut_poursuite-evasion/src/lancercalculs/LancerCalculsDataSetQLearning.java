package lancercalculs;

import outils.DataCollector;
import outils.Outil;
import simulation.*;
import simulation.comportement.ReseauDeNeurones;
import simulation.comportement.ReseauDeNeuronesQLearning;
import simulation.personnages.Agent;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LancerCalculsDataSetQLearning {
    public static void launch(ReseauDeNeuronesQLearning rnq) {
        File file = new File("donnees/game_data_Qlearning.csv");
        if (file.exists()) {
            file.delete();
        }
        file = new File("donnees/game_data_validation_Qlearning.csv");
        if (file.exists()) {
            file.delete();
        }
        DataCollector.etiquettage("\"input\",\"reward\"", "donnees/game_data_Qlearning.csv");
        List<Case> casesValides = new ArrayList<>();
        for (int y = 0; y < Simulation.CARTE.length; y++) {
            for (int x = 0; x < Simulation.CARTE[0].length; x++) {
                if (Simulation.CARTE[y][x] != CaseEnum.MUR.ordinal() && Simulation.CARTE[y][x] != CaseEnum.SORTIE.ordinal()) {
                    casesValides.add(new Case(x, y));
                }
            }
        }
        Position p;
        Simulation sim = new Simulation(Comportements.Aleatoire, Comportements.Aleatoire);
        double[] reward = new double[Deplacement.values().length];
        double[] carte = Outil.applatissement(sim.getCarteMursSortie());
        double[] cartePos;
        double gamma = 0.95;
        Personnage personnage = sim.getPrisonnier();
        rnq.setSim(sim);
        rnq.setPersonnage(personnage);
        int indiceDep = 0;
        float sortieRN;
        Arrays.fill(reward, 0);
        for (Case c : casesValides) {
            p = new Position(c.getX(), c.getY());
            personnage.setPosition(p);
            for (Deplacement d : Deplacement.values()) {
                personnage.setPosition(p);
                Position positionDep = p.getNouvellePosition(d);
                if (positionDep.getY() >= 0 && positionDep.getY() < Simulation.CARTE.length && positionDep.getX() >= 0 && positionDep.getX() < Simulation.CARTE[0].length) {
                    sortieRN = rnq.donnerSortie();
                    if (sim.verifierDeplacemnt(personnage, d)) {
                        personnage.setPosition(positionDep);
                        sortieRN = rnq.donnerSortie();
                        if (Simulation.CARTE[positionDep.getY()][positionDep.getX()] == CaseEnum.SORTIE.ordinal()) {
                            reward[indiceDep] = 1;
                        }
                    }
                    else{
                        reward[indiceDep] = -0.3;
                    }
                    reward[indiceDep] += gamma*sortieRN;
                }
                indiceDep++;
            }
            indiceDep = 0;
            cartePos = Outil.applatissement(personnage.getPositionCarte());
            DataCollector.saveDateQLearning(Outil.concatener_tab(carte, cartePos), reward, "donnees/game_data_Qlearning.csv");
            Arrays.fill(reward, 0);
        }
    }

}
