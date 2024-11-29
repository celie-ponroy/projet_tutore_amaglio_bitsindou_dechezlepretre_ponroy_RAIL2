package simulation.personnages;

public class Agent implements Personnage {
    private Position position;

    public Agent(int x, int y) {this.position = new Position(x, y);}


    @Override
    public void deplacer(int x, int y) {
        this.position.deplacer(x, y);
    }

    @Override
    public void deplacer(Position p) {
        this.position = p;
    }


    public Position getPosition() {
        return position;
    }
}
