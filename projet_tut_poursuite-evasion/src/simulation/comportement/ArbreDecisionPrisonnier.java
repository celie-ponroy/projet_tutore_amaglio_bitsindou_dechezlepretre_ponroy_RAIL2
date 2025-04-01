package simulation.comportement;

import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

public class ArbreDecisionPrisonnier extends ArbreDecision implements Comportement, Serializable {
    private Simulation simulation;
    private Personnage personnage;

    public ArbreDecisionPrisonnier(Simulation simulation, Personnage personnage) {
        this.simulation = simulation;
        this.personnage = personnage;
    }

    /**
     * Renvoie le deplacement à prendre
     *
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        Deplacement deplacementRes = Deplacement.AUCUN;
        Stack<Position> s = Simulation.CHEMINS_P.get(List.of(personnage.getPosition(), Simulation.getPosSortie()));
        if (s.empty()) {
            deplacementRes = direction(personnage.getPosition(), Simulation.getPosSortie());
            //si on voit pas le gardien
        } else if (!simulation.estVisible(simulation.getGardien(), false)) {
            //on va vers la sortie
            deplacementRes = direction(personnage.getPosition(), s.getLast());

            //sinon est ce qu'il est sur le chemin de la sortie ?
        } else if (s.contains(simulation.getGardien().getPosition())) {
            //on fui le gardien
            deplacementRes = oppose(direction(personnage.getPosition(), s.getLast()));// a changer
        } else {
            //on va vers la sortie
            deplacementRes = direction(personnage.getPosition(), s.getLast());
        }
        return deplacementRes;

    }

    @Override
    public Comportements getType() {
        return Comportements.ArbreDeterministe;
    }

}
