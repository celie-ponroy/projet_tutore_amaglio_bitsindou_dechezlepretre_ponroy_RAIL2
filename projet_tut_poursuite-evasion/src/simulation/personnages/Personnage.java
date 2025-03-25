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

    public double[][] getPositionCarte(){
        double[][] res = new double[Simulation.CARTE.length][Simulation.CARTE[0].length];
       // System.out.println(res.length+" "+res[0].length);
        for(int i = 0; i < res.length; i++){
            for(int j = 0; j < res[0].length; j++){
                //System.out.println(j+" "+i);
                if(i == this.position.getX() && j == this.position.getY()){
                    res[j][i] = 1;
                } else{
                    res[j][i] = 0;
                }
            }
        }
        return res;
    }
}
