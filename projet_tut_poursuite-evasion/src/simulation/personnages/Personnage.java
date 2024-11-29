package simulation.personnages;

import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

public abstract class Personnage {
    protected Position position;

    public Personnage(int x, int y) {
        this.position = new Position(x, y);
    }

    public abstract void deplacer(int x, int y);

    public Position getPosition() {
        return this.position;
    }

}
