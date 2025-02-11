package simulation.personnages;
import simulation.Simulation;

import java.io.Serializable;
import java.util.ArrayList;

public class Joueur extends Personnage implements Serializable {
    public Joueur(int x, int y){
        super(x,y);
    }


    @Override
    public void deplacer(Position p) {
        this.position = p;
    }

    public Position getPosition() {
        return position;
    }
}
