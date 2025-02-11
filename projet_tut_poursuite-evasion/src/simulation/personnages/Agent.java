package simulation.personnages;

import java.io.Serializable;

public class Agent extends Personnage implements Serializable {

    public Agent(int x, int y){
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
