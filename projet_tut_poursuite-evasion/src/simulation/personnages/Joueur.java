package simulation.personnages;

public class Joueur extends Personnage {

    public Joueur(int x, int y){
        super(x,y);
    }


    @Override
    public void deplacer(int x, int y) {
        this.position.deplacer(x, y);
    }

}
