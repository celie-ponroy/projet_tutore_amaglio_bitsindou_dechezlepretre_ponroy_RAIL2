package simulation.comportement;

import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.List;
import java.util.Stack;

public class ArbreDecisionPrisonnier2 extends ArbreDecision implements Comportement {
    private Simulation simulation;
    private Personnage personnage;

    public ArbreDecisionPrisonnier2(Simulation simulation, Personnage personnage){
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
        //si on voit pas le gardien
        if(!simulation.estVisible(personnage, true)){
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }

        //sinon est ce qu'il est sur le chemin de la sortie ?
        if(s.contains(simulation.getGardien().getPosition())){
            //on fui le gardien
            //on cherche la meilleure solution des cases à coté de nous
            return fuir();

        }else{
            //on va vers la sortie
            return direction(personnage.getPosition(), s.getLast());
        }
    }


    /**
     * choisit le meilleur deplacement pour fuir
     * @return
     */
    private Deplacement fuir(){

        //on cherche la meilleure solution des cases à coté de nous
        Position position = this.personnage.getPosition();
        Position sortie = Simulation.getPosSortie();

        for (Position p: position.casesAdjacentes()){
            Stack<Position> s = Simulation.CHEMINS.get(List.of(p,sortie));

            if(!s.empty()&&!s.contains(simulation.getGardien().getPosition()))//cas si aucun chemin (dont murs)  //si le gardien bloque on cherche une autre case
                return direction(position, s.getLast());

        }
        return Deplacement.AUCUN;
    }
}