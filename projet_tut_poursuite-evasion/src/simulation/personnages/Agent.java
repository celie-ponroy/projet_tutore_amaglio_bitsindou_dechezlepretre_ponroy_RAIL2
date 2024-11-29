package simulation.personnages;

public class Agent extends Personnage {

    public Agent(int x, int y){
        super(x,y);
    }


    @Override
    public void deplacer(int x, int y) {
        this.position.deplacer(x, y);
    }

}
