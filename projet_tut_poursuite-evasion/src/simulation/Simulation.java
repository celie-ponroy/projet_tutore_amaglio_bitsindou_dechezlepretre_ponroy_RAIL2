package simulation;

import moteur.Clavier;
import moteur.Jeu;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;

public class Simulation implements Jeu {
    private int nbTours;
    private Personnage gardien;
    private Personnage prisonnier;
    public static final int[][] CARTE = new int[][]{
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
            {-1, 0, 0, 0, 0, 0,-1, 2,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,-1},
            {-1, 0, 0, 0, 0, 0,-1,-1,-1,-1,-1,-1, 0,-1},
            {-1, 0,-1,-1, 0, 0,-1,-1,-1,-1,-1,-1, 0,-1},
            {-1, 0,-1,-1, 0, 0, 0, 0, 0, 0,-1,-1, 0,-1},
            {-1, 0, 0, 0, 0,-1,-1,-1,-1, 0, 0, 0, 0,-1},
            {-1,-1, 0, 0, 0, 0, 0, 0,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1, 0,-1,-1,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1, 0, 0, 0,-1,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1, 0, 0, 0,-1,-1,-1,-1,-1,-1,-1,-1},
            {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}};
    static final int SORTIE = 2;
    static final int MUR = -1;
    static final int SOL = 0;



    public Simulation(){
        this.nbTours = 0;
    }

    /**
     * Methode permettant de mettre a jour l'etat du jeu en fonction du clavier et du temps ecoule
     * @param secondes temps ecoule depuis la derniere mise a jour
     * @param clavier  objet contenant l'état du clavier'
     */
    @Override
    public void update(double secondes, Clavier clavier) {
    // TODO
        this.nbTours++;
        //gestion des déplacements
        //gestion des colisions
        //gestion des interactions et de la fin du jeu

    }

    /**
     * Methode permettant d'initialiser le jeu
     */
    @Override
    public void init() {
    // TODO
        //un personnage prisonnier
        this.prisonnier = new Joueur(5,4);
        // (11,3)
    }

    /**
     * Methode permettant de verifier si le jeu est fini
     * @return
     */
    @Override
    public boolean etreFini() {
        return false;
        // TODO
    }
}
