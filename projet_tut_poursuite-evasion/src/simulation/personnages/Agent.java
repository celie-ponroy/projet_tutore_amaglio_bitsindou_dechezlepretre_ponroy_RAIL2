package simulation.personnages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Agent extends Personnage implements Serializable {

    public Agent(int x, int y, HashMap<Position, ArrayList<Position>> vision) {
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
