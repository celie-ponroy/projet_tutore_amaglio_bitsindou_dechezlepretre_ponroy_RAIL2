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
    public static final int SORTIE = 2;
    public static final int MUR = -1;
    public static final int SOL = 0;



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
        //deplacer le personnage en fonction du clavier si cela est possible
        deplacerJoueur(clavier);

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

    /**
     * Methode permettant de deplacer le joueur en fonction des touches appuyees
     * @param clavier
     */
    public void deplacerJoueur(Clavier clavier){
        if(clavier.diagHG){
            this.prisonnier.deplacer(Personnage.DIAGHG);
        }
        if(clavier.haut){
            this.prisonnier.deplacer(Personnage.HAUT);
        }
        if(clavier.diagHD){
            this.prisonnier.deplacer(Personnage.DIAGHD);
        }
        if(clavier.droite){
            this.prisonnier.deplacer(Personnage.DROITE);
        }
        if(clavier.neutre){
            this.prisonnier.deplacer(Personnage.NEUTRE);
        }
        if(clavier.gauche){
            this.prisonnier.deplacer(Personnage.GAUCHE);
        }
        if(clavier.diagBG){
            this.prisonnier.deplacer(Personnage.DIAGBG);
        }
        if(clavier.bas){
            this.prisonnier.deplacer(Personnage.BAS);
        }
        if(clavier.diagBD){
            this.prisonnier.deplacer(Personnage.DIAGBD);
        }
    }
}
