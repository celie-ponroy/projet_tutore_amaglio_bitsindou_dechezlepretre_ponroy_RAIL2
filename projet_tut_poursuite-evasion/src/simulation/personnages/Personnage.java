package simulation.personnages;

import simulation.Simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Personnage implements Serializable {
    protected Position position;
    protected HashMap<Position,ArrayList<Position>> vision;

    public Personnage(int x, int y,HashMap<Position,ArrayList<Position>> vision) {
        this.position = new Position(x, y);
        this.vision = vision;
    }

    public abstract void deplacer(Position p);

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public ArrayList<Position> getVision() {
        return vision.get(this.position);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
