package simulation.personnages;

import simulation.Simulation;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Personnage implements Serializable {
    protected Position position;

    public Personnage(int x, int y) {
        this.position = new Position(x, y);
    }

    public abstract void deplacer(Position p);

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public ArrayList<Position> getVision() {
        return Simulation.VISION.get(this.position);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
