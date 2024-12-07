package simulation;

import affichage.DessinJeu;
import moteur.Clavier;
import moteur.Jeu;
import simulation.personnages.Agent;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Simulation implements Jeu {
    private List<DessinJeu> observateurs;
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
    public static final HashMap<Position, ArrayList<Position>> vision = CalculVision.recupererVision();
    private boolean estFini;
    private Deplacement derDeplacement;

    public Simulation(){
        this.nbTours = 0;
        this.prisonnier = new Joueur(4,10);
        this.gardien = new Agent(5,4);
        this.observateurs = new ArrayList<>();
    }

    public void ajouterObservateur(DessinJeu dj){
        this.observateurs.add(dj);
    }
    public void notifierObservateurs(){
        for(DessinJeu dj : this.observateurs){
            dj.update(this);
        }
    }

    public void deplacementJoueur(Deplacement d){
        // pour l'instant le joueur est forcément le prisonnier
        boolean deplacement = deplacerPersonnage(this.prisonnier, d);
        if (!deplacement){
            return;
        }
        this.nbTours++;
        this.notifierObservateurs();
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


    public boolean murPresent(int x , int y){
        return Simulation.CARTE[y][x] == Simulation.MUR;
    }

    /**
     * Methode permettant de deplacer un personnage en fonction de l'action et de la carte
     * @param p
     * @param d
     */

    public boolean deplacerPersonnage(Personnage p, Deplacement d){
        this.derDeplacement = d;
        Position persoPos = p.getPosition();
        Position nvPos = new Position(persoPos.getX(), persoPos.getY());
        nvPos.deplacement(d);

        //verifier si le deplacement est possible
        if(murPresent(nvPos.getX(), nvPos.getY())){
            return false;
        }
        //verification des diagonales
        if(!(d.equals(Deplacement.AUCUN))){
            switch (d){
                case DIAG_BAS_DROITE:
                    if(murPresent(persoPos.getX()+1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY()+1)){
                        return false;
                    }
                    break;
                case DIAG_BAS_GAUCHE:
                    if(murPresent(persoPos.getX()-1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY()+1)){
                        return false;
                    }
                    break;
                case DIAG_HAUT_DROITE:
                    if(murPresent(persoPos.getX()+1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY()-1)){
                        return false;
                    }
                    break;
                case DIAG_HAUT_GAUCHE:
                    if(murPresent(persoPos.getX()-1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY()-1)){
                        return false;
                    }
                    break;
            }
        }
        //si oui deplacer le personnage
        p.deplacer(nvPos);
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

    public int getNbTours() {
        return this.nbTours;
    }

    /**
     * Méthode permettant de récupérer le dernier déplacement effectué
     */
    public Deplacement getDerDeplacement() {
        return this.derDeplacement;
    }

}
