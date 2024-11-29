package simulation.personnages;

<<<<<<< HEAD
public class Joueur extends Personnage {

    public Joueur(int x, int y){
        super(x,y);
=======
import java.util.ArrayList;

public class Joueur implements Personnage {
    private Position position;
    private Bayesien bayesien;
    private double[][] carteBayesien;

    public Joueur(int x, int y) {
        this.position = new Position(x, y);
        bayesien = new Bayesien();
        carteBayesien = bayesien.getCarteBayesienne();
        //System.out.println(bayesien.toString());
        //System.out.println(bayesien.getCasesVoisineValide(2,1));
>>>>>>> 1e61981 (maj vue + réctification bayesien)
    }


    @Override
    public void deplacer(int x, int y) {
        this.position.deplacer(x, y);
    }


    @Override
    public void deplacer(Position p) {
        this.position = p;

        //Bayesien tmp
        //ArrayList azfd = new ArrayList();
        //azfd.add(new Integer[]{10,4, 0});
        //azfd.add(new Integer[]{9,4, 0});
        carteBayesien = this.bayesien.calculerProbaPresence(carteBayesien, new ArrayList<>());
    }

    public Position getPosition() {
        return position;
    }

    public double[][] getCarteBayesien() {
        return carteBayesien;
    }
}
