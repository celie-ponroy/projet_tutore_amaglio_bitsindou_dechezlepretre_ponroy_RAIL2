package simulation.personnages;

import simulation.Case;

import java.util.ArrayList;
import java.util.List;

public class Agent extends Personnage {

    public Agent(int x, int y){
        super(x,y);
    }

    @Override
    public void deplacer(int x, int y) {
        this.position.deplacer(x, y);
    }


}
