package simulation.personnages;

import simulation.Simulation;

import java.util.ArrayList;

public class Joueur extends Personnage {
    public Joueur(int x, int y) {
        super(x, y);
    }


    @Override
    public void deplacer(Position p) {
        this.position = p;
    }

    public Position getPosition() {
        return position;
    }
}
