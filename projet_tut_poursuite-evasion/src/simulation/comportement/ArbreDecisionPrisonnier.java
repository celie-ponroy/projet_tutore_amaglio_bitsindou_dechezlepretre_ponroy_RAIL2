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
        Stack<Position> s = Simulation.CHEMIN.get(List.of(personnage.getPosition(),Simulation.getPosSortie()));
        if(s.empty())
            return direction(personnage.getPosition(),Simulation.getPosSortie());//toujours là :,(
        //si on voit le gardien
        if(simulation.estVisible(personnage, true)){
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }

        //sinon est ce qu'il est sur le chemin de la sortie ?
        if( s.contains(simulation.getGardien().getPosition())){
            //on fui le gardien
            return direction(personnage.getPosition(), s.getLast());// a changer
        }else{
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }
    }
}
