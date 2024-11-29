package simulation.personnages;
import simulation.Simulation;
import java.util.ArrayList;

public class Joueur extends Personnage {
    private Position position;
    private Bayesien bayesien;
    private double[][] carteBayesien;
    public Joueur(int x, int y){
        super(x,y);
        bayesien = new Bayesien();
        carteBayesien = bayesien.getCarteBayesienne();
    }


    @Override
    public void deplacer(int x, int y) {
        this.position.deplacer(x, y);
    }


    @Override
    public void deplacer(Position p) {
        this.position = p;
        carteBayesien = this.bayesien.calculerProbaPresence(carteBayesien, new ArrayList<>());
    }

    public Position getPosition() {
        return position;
    }

    public double[][] getCarteBayesien() {
        return carteBayesien;
    }
}
