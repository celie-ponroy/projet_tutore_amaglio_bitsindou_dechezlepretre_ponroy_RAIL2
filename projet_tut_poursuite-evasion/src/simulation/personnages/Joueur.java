package simulation.personnages;

import simulation.Simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Joueur extends Personnage implements Serializable {
    public Joueur(int x, int y, HashMap<Position, ArrayList<Position>> vision) {
        super(x, y, vision);
    }


    @Override
    public void deplacer(Position p) {
        this.position = p;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
