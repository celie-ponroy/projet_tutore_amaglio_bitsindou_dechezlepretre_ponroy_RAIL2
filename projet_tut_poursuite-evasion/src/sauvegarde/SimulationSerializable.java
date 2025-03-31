package sauvegarde;

import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Bayesien;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SimulationSerializable implements Serializable {
    private int nbTours;
    private Personnage gardien;
    private Personnage prisonnier;
    private boolean victoirePrisonnier;
    private boolean victoireGardien;
    private Comportements comportementGardien;
    private Comportements comportementPrisonnier;

    private HashMap<Personnage, List<Position>> historiquePosition;
    private HashMap<Personnage, List<double[][]>> historiqueBayesien;
    private HashMap<Personnage, List<Deplacement>> historiqueDeplacement;

    private boolean estFini;
    private HashMap<Personnage, double[][]> carteBayesiennes;
    private HashMap<Personnage, Bayesien> bayesiens;

    public SimulationSerializable(Simulation simulation) {
        this.nbTours = simulation.getNbTours();
        this.estFini = simulation.etreFini();
        this.gardien = simulation.getGardien();
        this.prisonnier = simulation.getPrisonnier();
        this.victoireGardien = simulation.getVictoireGardien();
        this.victoirePrisonnier = simulation.getVictoirePrisonnier();
        this.historiqueBayesien = simulation.getHistoriqueBayesien();
        this.historiqueDeplacement = simulation.getHistoriqueDeplacement();
        this.bayesiens = simulation.getBayesiens();
        this.carteBayesiennes = simulation.getCarteBayesiennes();
        if (simulation.getComportementGardien() != null) {
            this.comportementGardien = simulation.getComportementGardien().getType();
        }
        if (simulation.getComportementPrisonnier() != null) {
            this.comportementPrisonnier = simulation.getComportementPrisonnier().getType();
        }

        this.historiquePosition = simulation.getHistoriquePosition();
    }

    public Simulation creerSimulation() {
        Simulation simulation;
        if (comportementGardien != null && comportementPrisonnier != null) {
            simulation = new Simulation(comportementGardien, comportementPrisonnier);
        } else {
            boolean joueur = comportementPrisonnier == null;
            if (joueur) {
                simulation = new Simulation(joueur, comportementGardien);
            } else {
                simulation = new Simulation(joueur, comportementPrisonnier);
            }
        }
        simulation.setNbTours(nbTours);
        simulation.setGardien(gardien);
        simulation.setPrisonnier(prisonnier);
        simulation.setVictoirePrisonnier(victoirePrisonnier);
        simulation.setVictoireGardien(victoireGardien);
        simulation.setHistoriquePosition(historiquePosition);
        simulation.setHistoriqueBayesien(historiqueBayesien);
        simulation.setHistoriqueDeplacement(historiqueDeplacement);
        simulation.setEstFini(estFini);
        simulation.setCarteBayesiennes(carteBayesiennes);
        simulation.setBayesiens(bayesiens);
        return simulation;
    }

}
