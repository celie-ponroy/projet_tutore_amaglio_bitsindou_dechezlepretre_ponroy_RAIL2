package simulation.personnages;

import simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

public abstract class Personnage {
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
