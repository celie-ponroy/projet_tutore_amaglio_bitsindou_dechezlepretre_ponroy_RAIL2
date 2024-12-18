package simulation;

import affichage.DessinJeu;
import moteur.Jeu;
import simulation.comportement.ArbreDecisionGardien;
import simulation.comportement.ArbreDecisionPrisonnier;
import simulation.comportement.Comportement;

import simulation.personnages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Simulation implements Jeu {
    private List<DessinJeu> observateurs;
    private int nbTours;
    private Personnage gardien;
    private Personnage prisonnier;
    private Comportement comportementGardien;
    private Comportement comportementPrisonnier;
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
    public static final HashMap<Position, ArrayList<Position>> VISION = CalculVision.recupererVision();
    public static final HashMap<List<Position>, Stack> CHEMINS = CalculChemins.recupererChemin();


    private boolean estFini;
    private HashMap<Personnage, double[][]> carteBayesiennes;
    private HashMap<Personnage,Bayesien> bayesiens;

    /**
     * Constructeur par deéfaut (mode non interactif)
     */
    public Simulation(){
        this.observateurs = new ArrayList<>();
        this.nbTours = 0;
        this.estFini = false;

        //les 2 personnages sont des agents
        this.prisonnier = new Agent(4, 10);
        this.gardien = new Agent(5, 4);

        this.comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
        this.comportementGardien = new ArbreDecisionGardien(this, this.gardien);

        //this.comportementGardien = new ReseauDeNeurones();
        //Initialisation des carte bayesiennes pour les deux agents
        bayesiens = new HashMap<>();
        bayesiens.put(this.gardien,new Bayesien());
        bayesiens.put(this.prisonnier,new Bayesien());
        carteBayesiennes = new HashMap<>();
        carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
        carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());

    }

    /**
     * Constructeur secondaire pour le mode interactif
     * @param perso true si le personnage est le prisonnier et false sinon
     */
    public Simulation(boolean perso){
        this.observateurs = new ArrayList<>();
        this.nbTours = 0;
        this.estFini = false;

        //Initialisation des carte bayesiennes pour les deux agents
        bayesiens = new HashMap<>();
        this.carteBayesiennes = new HashMap<>();


        if (perso) {
            this.prisonnier = new Joueur(4, 10);
            this.gardien = new Agent(5, 4);
            comportementGardien = new ArbreDecisionGardien(this, this.gardien);
            bayesiens.put(this.gardien,new Bayesien());
            carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
        }else{
            this.gardien = new Joueur(5, 4);
            this.prisonnier = new Agent(4, 10);
            comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
            bayesiens.put(this.prisonnier,new Bayesien());
            carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());

        }
    }


    /**
     * Methode permettant d'ajouter un observateur
     * @param dj
     */
    public void ajouterObservateur(DessinJeu dj){
        this.observateurs.add(dj);
    }

    /**
     * Methode permettant de notifier les observateurs
     */
    public void notifierObservateurs(){
        for(DessinJeu dj : this.observateurs){
            dj.update(this);
        }
    }

    /**
     * Methode de deplacement non interactif
     */
    public void deplacerAgents(){

        deplacerPersonnage(this.prisonnier, this.comportementPrisonnier.prendreDecision());
        deplacerPersonnage(this.gardien, this.comportementGardien.prendreDecision());

        this.nbTours++;
        //gestion des interactions et de la fin du jeu
        miseAJourFinJeu();
        this.notifierObservateurs();
        actualisationBayesienne(this.gardien,this.prisonnier);
        actualisationBayesienne(this.prisonnier,this.gardien);

    }

    /**
     * Permet de déplacer le joueur (prisonnier ou gardien) en fonction du déplacement
     * @param d déplacement souhaité
     */
    public void deplacementJoueur(Deplacement d){

        Deplacement deplacementAgent;

        Joueur joueur = (Joueur) this.getJoueur();
        Personnage agent;
        if(joueur.equals(this.prisonnier)){
            agent = this.gardien;
            deplacementAgent = this.comportementGardien.prendreDecision();

        }else{
            agent = this.prisonnier;
            deplacementAgent = this.comportementPrisonnier.prendreDecision();

        }
        actualisationBayesienne(agent,joueur);

        //initialisation du déplacement du joueur
        boolean deplacement = deplacerPersonnage(joueur, d);
        if (!deplacement) {
            return;
        }
        this.nbTours++;

        deplacerPersonnage(joueur, d);
        deplacerPersonnage(agent, deplacementAgent);

        //gestion des interactions et de la fin du jeu
        miseAJourFinJeu();
        this.notifierObservateurs();
        //actualisation des proba de présence

    }

    /**
     * Mise à jour fin du jeu
     */
    public void miseAJourFinJeu(){
        if(this.prisonnier.getPosition().equals(this.gardien.getPosition())){
            this.estFini = true;
        }
        if(Simulation.CARTE[this.prisonnier.getPosition().getY()][this.prisonnier.getPosition().getX()] == Simulation.SORTIE){
            this.estFini = true;
        }
    }

    /**
     * Méthode permetant de mettre a jour la carte bayesienne d'un personnage
     * @param p1 le mis a jour
     * @param p2 le deuxieme personnage
     */
    public void actualisationBayesienne(Personnage p1, Personnage p2){
        ArrayList<Position> positionsCasesVue = p1.getVision();

        ArrayList<Integer[]> casesVue = new ArrayList<>();
        for (Position position : positionsCasesVue) {
            Integer present = 0;
            if (p2.getPosition().equals(position)) {
                present = 1;
            }
            casesVue.add(new Integer[]{position.getY(), position.getX(),present});
        }
        carteBayesiennes.replace(p1, bayesiens.get(p1).calculerProbaPresence( carteBayesiennes.get(p1),casesVue));
    }

    /**
     * Methode permettant d'initialiser le jeu
     */
    @Override
    public void init() {}

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
     * Methode permettant de savoir si un personnage est visible par un autre
     * @param p1 le personnage qui est observé
     * @param role true si le personnage est le prisonnier, false sinon
     * @return
     */
    public boolean estVisible(Personnage p1,boolean role){
        Position pos1 = p1.getPosition();
        Position pos2;
        if(role){
            pos2 = this.gardien.getPosition();
        }else{
            pos2 = this.prisonnier.getPosition();
        }
        if(pos1.equals(pos2)){
            return true;
        }
        ArrayList<Position> casesVisibles = VISION.get(pos2);

        for(Position pos : casesVisibles){
            if(pos.equals(pos1)){
                return true;
            }
        }
        return false;
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

    /**
     * Methode permettant de recuperer la carte bayesienne d'un personnage
     * @param p
     * @return
     */
    public double[][] getCarteBayesienne(Personnage p) {
        return carteBayesiennes.get(p);
    }

    /**
     * Methode permettant de recuperer le nombre de tours actuels
     * @return
     */
    public int getNbTours() {
        return this.nbTours;
    }

<<<<<<< HEAD

    /**
     * Méthode permettant de renvoyer le personnage que l'on veut jouer
     */
    public Personnage getJoueur() {
        if (this.prisonnier instanceof Joueur) {
            return this.prisonnier;
        } else {
            return this.gardien;
        }
    }
=======
>>>>>>> d3ad33b (Début analyse)

    public static Position getPosSortie(){
        return new Position(7,1);
    }


    /**
     * Methode permettant les bayesiens
     * @return
     */
    public HashMap<Personnage, Bayesien> getBayesiens() {
        return bayesiens;
    }
}
