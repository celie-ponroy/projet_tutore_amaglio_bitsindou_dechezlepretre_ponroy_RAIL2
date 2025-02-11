package simulation.personnages;

public class Agent extends Personnage {

    public Agent(int x, int y) {
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
