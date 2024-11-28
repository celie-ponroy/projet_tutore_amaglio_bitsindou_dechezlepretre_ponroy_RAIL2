package simulation;

import moteur.Clavier;
import moteur.Jeu;
import simulation.personnages.Agent;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

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
    private boolean estFini;

    public Simulation(){
        this.nbTours = 0;
        this.prisonnier = new Joueur(4,10);
        this.gardien = new Agent(5,4);
    }

    /**
     * Methode permettant de mettre à jour l'etat du jeu en fonction du clavier et du temps ecoule
     * @param secondes temps ecoule depuis la derniere mise a jour
     * @param clavier  objet contenant l'état du clavier'
     */
    @Override
    public void update(double secondes, Clavier clavier) {
    // TODO
        //gestion des déplacements
        //deplacer le personnage en fonction du clavier si cela est possible
        int[] actionJ = deplacementJoueur(clavier);
        // pour l'instant le joueur est forcément le prisonnier
        boolean deplacement = deplacerPersonnage(this.prisonnier,actionJ);
        if (!deplacement){
            return;
        }
        this.nbTours++;
        //deplacer le gardien

        //gestion des interactions et de la fin du jeu
        if(this.prisonnier.getPosition().equals(this.gardien.getPosition())){
            this.estFini = true;
        }
        if(Simulation.CARTE[this.prisonnier.getPosition().getY()][this.prisonnier.getPosition().getX()] == Simulation.SORTIE){
            this.estFini = true;
        }

    }

    /**
     * Methode permettant d'initialiser le jeu
     */
    @Override
    public void init() {
        // TODO
        this.nbTours = 0;
        this.estFini = false;
        //le prisonier est un joueur et le gardien un agent
        this.prisonnier = new Joueur(4,10);
        this.gardien = new Agent(5,4);
    }

    /**
     * Methode permettant de verifier si le jeu est fini
     * @return
     */
    @Override
    public boolean etreFini() {
        return this.estFini;
    }

    /**
     * Methode permettant de deplacer le joueur en fonction des touches appuyees
     * @param clavier
     * @return un tableau d'entiers contenant les coordonnees du deplacement
     */
    public int[] deplacementJoueur(Clavier clavier){
        int[] deplacmentpos = {0,0};

        if(clavier.diagHG){
            deplacmentpos[0]=-1;
            deplacmentpos[1]=-1;
        }
        if(clavier.haut){
            deplacmentpos[1]=-1;
        }
        if(clavier.diagHD){
            deplacmentpos[0]=1;
            deplacmentpos[1]=-1;
        }
        if(clavier.droite){
            deplacmentpos[0]=1;
        }
        if(clavier.gauche){
            deplacmentpos[0]=-1;
        }
        if(clavier.diagBG){
            deplacmentpos[0]=-1;
            deplacmentpos[1]=1;
        }
        if(clavier.bas){
            deplacmentpos[1]=1;
        }
        if(clavier.diagBD){
            deplacmentpos[0]=1;
            deplacmentpos[1]=1;
        }
        return deplacmentpos;
    }
    public boolean murPresent(int x , int y){
        return Simulation.CARTE[y][x] == Simulation.MUR;
    }

    /**
     * Methode permettant de deplacer un personnage en fonction de l'action et de la carte
     * @param p
     * @param action
     */

    public boolean deplacerPersonnage(Personnage p, int[] action){
        Position pos = p.getPosition();
        //calcul des positions après déplacement
        int[] deplacmentpos = {pos.getX()+action[0],pos.getY()+action[1]};
        //verifier si le deplacement est possible
        if(murPresent(deplacmentpos[0],deplacmentpos[1])){
            return false;
        }
        //verification des diagonales
        if(action[0] != 0 && action[1] != 0){
            if(murPresent(deplacmentpos[0],pos.getY()) || murPresent(pos.getX(),deplacmentpos[1])){
                return true;
            }
        }
        //si oui deplacer le personnage
        p.deplacer(action[0],action[1]);
        return true;
    }

    /**
     * Methode permettant de recuperer le priosnnier
     * @return le prisonnier
     */
    public Personnage getPrisonnier() {
        return prisonnier;
    }

    /**
     * Methode permettant de recuperer le gardien
     * @return le gardien
     */
    public Personnage getGardien() {
        return gardien;
    }
}
