package simulation;

import affichage.DessinJeu;
import calculs.CalculChemins;
import calculs.CalculVision;
import moteur.Jeu;
import simulation.comportement.ArbreDecision;
import simulation.comportement.ArbreDecisionGardien;
import simulation.comportement.ArbreDecisionPrisonnier;
import outils.Outil;
import simulation.comportement.Comportement;

import simulation.comportement.reseau_neurones.ReseauDeNeurones;
import simulation.personnages.*;

import java.util.*;

public class Simulation implements Jeu {
    private List<DessinJeu> observateurs;
    private int nbTours;
    private Personnage gardien;
    private Personnage prisonnier;
    private boolean victoirePrisonnier;
    private boolean victoireGardien;
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

    public HashMap<Personnage,List<Position>> historiquePosition ;
    public HashMap<Personnage,List<double[][]>> historiqueBayesien;

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

        historiquePosition = new HashMap<>();
        List<Position> posP = new ArrayList<>();
        posP.add(this.prisonnier.getPosition());
        historiquePosition.put(this.prisonnier,posP );

        List<Position> posG = new ArrayList<>();
        posG.add(this.gardien.getPosition());
        historiquePosition.put(this.gardien, posG);

        this.comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
        this.comportementGardien = new ArbreDecisionGardien(this, this.gardien);

        //initialisation des victoires
        this.victoireGardien = false;
        this.victoirePrisonnier = false;


        //this.comportementGardien = new ReseauDeNeurones();
        //Initialisation des carte bayesiennes pour les deux agents
        bayesiens = new HashMap<>();
        bayesiens.put(this.gardien,new Bayesien());
        bayesiens.put(this.prisonnier,new Bayesien());

        carteBayesiennes = new HashMap<>();
        carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
        carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());

        historiqueBayesien = new HashMap<>();
        ArrayList<double[][]> list1 = new ArrayList<>();
        list1.add(carteBayesiennes.get(gardien));
        historiqueBayesien.put(gardien,list1);


        ArrayList<double[][]> list2 = new ArrayList<>();
        list2.add(carteBayesiennes.get(prisonnier).clone());
        historiqueBayesien.put(prisonnier,list2);

        deplacerAgents();
    }

    /**
     * Constructeur utiliser pour l'apprentissage
     *
     * @param rn                      Reseaux de neurones selectione pour l'apprentissage
     * @param apprentissagePrisonnier Vrai si on fais apprendre au prisonnier, faux sinon.
     */
    public Simulation(ReseauDeNeurones rn, boolean apprentissagePrisonnier) {
        this.prisonnier = new Agent(4, 10);
        this.gardien = new Agent(5, 4);
        this.nbTours = 0;
        this.estFini = false;
        if (apprentissagePrisonnier) {
            this.comportementPrisonnier = rn;
            this.comportementGardien = new ArbreDecisionGardien(this, this.gardien);
        } else {
            this.comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
            this.comportementGardien = rn;
        }
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
        historiqueBayesien = new HashMap<>();

        //initialisation des victoires
        this.victoireGardien = false;
        this.victoirePrisonnier = false;

        if (perso) {
            this.prisonnier = new Joueur(4, 10);
            this.gardien = new Agent(5, 4);
            comportementGardien = new ArbreDecisionGardien(this, this.gardien);
            bayesiens.put(this.gardien,new Bayesien());
            carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
            ArrayList<double[][]> list1 = new ArrayList<>();
            list1.add(carteBayesiennes.get(gardien).clone());
            historiqueBayesien.put(gardien,list1);

        }else{
            this.gardien = new Joueur(5, 4);
            this.prisonnier = new Agent(4, 10);
            comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
            bayesiens.put(this.prisonnier,new Bayesien());
            carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());

            ArrayList<double[][]> list1 = new ArrayList<>();
            list1.add(carteBayesiennes.get(prisonnier).clone());
            historiqueBayesien.put(prisonnier,list1);

        }
        //historique
        historiquePosition = new HashMap<>();
        List<Position> list = new ArrayList<>();
        list.add(this.prisonnier.getPosition());
        historiquePosition.put(this.prisonnier,list );

        List<Position> list1 = new ArrayList<>();
        list1.add(this.gardien.getPosition());
        historiquePosition.put(this.gardien, list1);
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
     * Méthode permettant de faire apprendre l'arbre de descision au réseau de neurones selectionee
     *
     * @param nbIte Nombre d'itération
     */
    public void apprentissage(int nbIte) {
        //On differentie quelle personnage apprend et lequel n'apprend pas
        Personnage personnageApprenant;
        Personnage personnageNonApprenant;
        Comportement comportementApprenant;
        Comportement comportementNonApprenant;
        if (this.comportementGardien instanceof ReseauDeNeurones) {
            personnageApprenant = this.gardien;
            comportementApprenant = this.comportementGardien;
            personnageNonApprenant = this.prisonnier;
            comportementNonApprenant = this.comportementPrisonnier;
        } else {
            personnageApprenant = this.prisonnier;
            comportementApprenant = this.comportementPrisonnier;
            personnageNonApprenant = this.gardien;
            comportementNonApprenant = this.comportementGardien;
        }

        while (this.nbTours < nbIte && !this.estFini) {
            //creation de l'arbre de decision pour l'apprenant pour comparer avec choix rn
            Deplacement depArbre;
            if (this.comportementGardien instanceof ReseauDeNeurones) {
                ArbreDecisionGardien tmpArbre = new ArbreDecisionGardien(this,personnageApprenant);
                //Prise de descision de l'arbre pour comparaison avec reseaux
                depArbre = tmpArbre.prendreDecision();
            } else {
                ArbreDecisionPrisonnier tmpArbre = new ArbreDecisionPrisonnier(this, personnageApprenant);
                depArbre = tmpArbre.prendreDecision();
            }
            //Creation des entrees du reseaux
            double[][] carteBayesienne = this.carteBayesiennes.get(personnageApprenant);
            double[] carteApplatie = Outil.applatissement(carteBayesienne);
            double[] entrees = new double[carteApplatie.length + 2];
            for (int i = 0; i < carteApplatie.length; i++) {
                entrees[i] = carteApplatie[i];
            }
            //Ajout de la position dans les entrées du réseau de neurones
            entrees[entrees.length - 1] = (double) personnageApprenant.getPosition().getY() / CARTE.length;
            entrees[entrees.length - 2] = (double) personnageApprenant.getPosition().getX() / CARTE[0].length;

            //Prise de descision du reseaux
            Deplacement depRn = comportementApprenant.prendreDecision(entrees);
            
            //Creation sortie voulue (descision de l'abre)
            double[] sortieVoulues = new double[Deplacement.values().length];
            int i = 0;
            for (Deplacement dep : Deplacement.values()) {
                if (depArbre.equals(dep)) {
                    sortieVoulues[i] = 1.;
                } else {
                    sortieVoulues[i] = 0.;
                }
                i++;
            }
            deplacerPersonnage(personnageApprenant, depRn);
            deplacerPersonnage(personnageNonApprenant, comportementNonApprenant.prendreDecision());
            //On compare
            ((ReseauDeNeurones) comportementApprenant).retroPropagation(entrees, sortieVoulues);
            this.nbTours++;
            this.miseAJourFinJeu();
        }
    }
    /**
     * Methode de deplacement non interactif
     */
    public void deplacerAgents(){
        while (!estFini){
            Deplacement d1 = this.comportementPrisonnier.prendreDecision();
            Deplacement d2 = this.comportementGardien.prendreDecision();
            deplacerPersonnage(this.prisonnier, d1);
            deplacerPersonnage(this.gardien, d2);

            historiquePosition.get(prisonnier).add(prisonnier.getPosition());
            historiquePosition.get(gardien).add(gardien.getPosition());

            this.nbTours++;
            //gestion des interactions et de la fin du jeu
            miseAJourFinJeu();
            actualisationBayesienne(this.gardien,this.prisonnier);
            actualisationBayesienne(this.prisonnier,this.gardien);
            var cartebay = bayesiens.get(gardien).getCarteBayesienne().clone();
            historiqueBayesien.get(gardien).add(cartebay);
            var cartebay2 = bayesiens.get(prisonnier).getCarteBayesienne().clone();
            historiqueBayesien.get(prisonnier).add(cartebay2);
            this.notifierObservateurs();
        }
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

        //initialisation du déplacement du joueur
        boolean deplacement = deplacerPersonnage(joueur, d);
        if (!deplacement) {
            return;
        }
        this.nbTours++;

        deplacerPersonnage(agent, deplacementAgent);
        actualisationBayesienne(agent,joueur);

        var cartebay = bayesiens.get(agent).getCarteBayesienne().clone();
        historiqueBayesien.get(agent).add(cartebay);

        //gestion des interactions et de la fin du jeu
        miseAJourFinJeu();
        this.notifierObservateurs();
        historiquePosition.get(joueur).add(joueur.getPosition());
        historiquePosition.get(agent).add(agent.getPosition());

    }

    /**
     * Mise à jour fin du jeu
     */
    public void miseAJourFinJeu(){
        if(this.prisonnier.getPosition().equals(this.gardien.getPosition())){
            this.estFini = true;
            this.victoireGardien = true;
        }
        if(Simulation.CARTE[this.prisonnier.getPosition().getY()][this.prisonnier.getPosition().getX()] == Simulation.SORTIE){
            this.estFini = true;
            this.victoirePrisonnier = true;
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

    /**
     * Méthode qui récupère la victoire du prisonnier
     */
    public boolean getVictoirePrisonnier() {
        return this.victoirePrisonnier;
    }

    /**
     * Méthode qui récupère la victoire du gardien
     */
    public boolean getVictoireGardien() {
        return this.victoireGardien;
    }


}
