package simulation.personnages;

public class Joueur implements Personnage {
    private Position position;

    public Joueur(int x, int y){
        this.position = new Position(x,y);
    }


    @Override
    public void deplacer() {

    }
}
