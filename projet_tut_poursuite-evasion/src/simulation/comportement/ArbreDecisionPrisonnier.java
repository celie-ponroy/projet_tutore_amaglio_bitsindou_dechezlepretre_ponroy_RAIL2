package simulation.comportement;

import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.List;
import java.util.Stack;

public class ArbreDecisionPrisonnier extends ArbreDecision implements Comportement {
    private Simulation simulation;
    private Personnage personnage;

    public ArbreDecisionPrisonnier(Simulation simulation, Personnage personnage){
        this.simulation = simulation;
        this.personnage = personnage;
    }

    /**
     * Renvoie le deplacement à prendre
     * @return
     */
    @Override
    public Deplacement prendreDecision() {
        Stack<Position> s = Simulation.CHEMINS.get(List.of(personnage.getPosition(),Simulation.getPosSortie()));
        if(s.empty())
            return direction(personnage.getPosition(),Simulation.getPosSortie());
        //si on voit le gardien
        if(!simulation.estVisible(personnage, true)){
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }

        //sinon est ce qu'il est sur le chemin de la sortie ?
        if(s.contains(simulation.getGardien().getPosition())){
            //on fui le gardien
            return oppose(direction(personnage.getPosition(), s.getLast()));// a changer
        }else{
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }
    }

    @Override
    public Deplacement prendreDecision(double[] entrees) {
        return null;
    }

    public Deplacement oppose(Deplacement d){
        switch (d){
            case HAUT:
                return Deplacement.BAS;
            case BAS:
                return Deplacement.HAUT;
            case GAUCHE:
                return Deplacement.DROITE;
            case DROITE:
                return Deplacement.GAUCHE;
            case DIAG_BAS_DROITE:
                return Deplacement.DIAG_HAUT_GAUCHE;
            case DIAG_BAS_GAUCHE:
                return Deplacement.DIAG_HAUT_DROITE;
            case DIAG_HAUT_DROITE:
                return Deplacement.DIAG_BAS_GAUCHE;
            case DIAG_HAUT_GAUCHE:
                return Deplacement.DIAG_BAS_DROITE;

        }
        return Deplacement.AUCUN;
    }
}
