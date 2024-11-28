package simulation.personnages;

public interface Personnage {

    public void deplacer(int x, int y);
    public void deplacer(Position p);

    Position getPosition();
}
