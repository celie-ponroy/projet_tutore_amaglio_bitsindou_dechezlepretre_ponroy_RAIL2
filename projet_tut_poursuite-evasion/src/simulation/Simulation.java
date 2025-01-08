package simulation;

import affichage.DessinJeu;
import calculs.CalculChemins;
import calculs.CalculVision;
import moteur.Jeu;
import outils.ChargementCarte;
import simulation.comportement.*;
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
    private boolean victoirePrisonnier;
    private boolean victoireGardien;
    private Comportement comportementGardien;
    private Comportement comportementPrisonnier;
    public static final int[][] CARTE = ChargementCarte.charger("donnees/laby.txt");
    public static final HashMap<Position, ArrayList<Position>> VISION = CalculVision.recupererVision();
    public static final HashMap<List<Position>, Stack> CHEMINS = CalculChemins.recupererChemin();

    public HashMap<Personnage, List<Position>> historiquePosition;
    public HashMap<Personnage, List<double[][]>> historiqueBayesien;
    public HashMap<Personnage, List<Deplacement>> historiqueDeplacement;

    private boolean estFini;
    private HashMap<Personnage, double[][]> carteBayesiennes;
    private HashMap<Personnage, Bayesien> bayesiens;

    /**
     * Constructeur mode non interactif
     *
     * @param ComportementGardien   Comportement du gardien
     * @param ComportementPrisonier Comportement du prisonier
     */
    public Simulation(Comportements ComportementGardien, Comportements ComportementPrisonier) {
        this.observateurs = new ArrayList<>();
        this.nbTours = 0;
        this.estFini = false;

        //les 2 personnages sont des agents
        this.prisonnier = new Agent(9, 18);
        this.gardien = new Agent(5, 4);
        this.positionnerAgentsSpawnAleatoire();
        historiqueDeplacement = new HashMap<>();
        List<Deplacement> depP = new ArrayList<>();
        List<Deplacement> depG = new ArrayList<>();
        historiqueDeplacement.put(gardien, depG);
        historiqueDeplacement.put(prisonnier, depP);

        historiquePosition = new HashMap<>();
        List<Position> posP = new ArrayList<>();
        posP.add(this.prisonnier.getPosition());
        historiquePosition.put(this.prisonnier, posP);

        List<Position> posG = new ArrayList<>();
        posG.add(this.gardien.getPosition());
        historiquePosition.put(this.gardien, posG);

        switch (ComportementGardien) {
            case Comportements.Aleatoire:
                this.comportementGardien = new Aleatoire(this, this.gardien);
                break;
            case Comportements.ArbreDeterministe:
                this.comportementGardien = new ArbreDecisionGardien(this, this.gardien);
                break;
            case Comportements.ArbreAleatoire:
                this.comportementGardien = new ArbreDecisionGardienAleatoire(this, this.gardien);
                break;
            case Comportements.ReseauArbreDeterministe:
                this.comportementGardien = new ReseauDeNeurones("donnees/sauvegardes_NeuralNetwork/G-RN-ArbreDeterministe", this, this.gardien);
                break;
            case Comportements.ReseauArbreAleatoire:
                this.comportementGardien = new ReseauDeNeurones("donnees/sauvegardes_NeuralNetwork/G-RN-ArbreAleatoire", this, this.gardien);
                break;
            default:
                break;
        }

        switch (ComportementPrisonier) {
            case Comportements.Aleatoire:
                this.comportementPrisonnier = new Aleatoire(this, this.prisonnier);
                break;
            case Comportements.ArbreDeterministe:
                this.comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
                break;
            case Comportements.ArbreAleatoire:
                //TODO: ajouter arbre aléatoire
                break;
            case Comportements.ReseauArbreDeterministe:
                this.comportementPrisonnier = new ReseauDeNeurones("donnees/sauvegardes_NeuralNetwork/P-RN-ArbreDeterministe", this, this.prisonnier);
                break;
            case Comportements.ReseauArbreAleatoire:
                this.comportementPrisonnier = new ReseauDeNeurones("donnees/sauvegardes_NeuralNetwork/P-RN-ArbreAleatoire", this, this.prisonnier);
                break;
            default:
                break;
        }

        //initialisation des victoires
        this.victoireGardien = false;
        this.victoirePrisonnier = false;

        //Initialisation des carte bayesiennes pour les deux agents
        bayesiens = new HashMap<>();
        bayesiens.put(this.gardien, new Bayesien());
        bayesiens.put(this.prisonnier, new Bayesien());

        carteBayesiennes = new HashMap<>();
        carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
        carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());

        historiqueBayesien = new HashMap<>();
        ArrayList<double[][]> list1 = new ArrayList<>();
        list1.add(carteBayesiennes.get(gardien));
        historiqueBayesien.put(gardien, list1);


        ArrayList<double[][]> list2 = new ArrayList<>();
        list2.add(carteBayesiennes.get(prisonnier).clone());
        historiqueBayesien.put(prisonnier, list2);

        deplacerAgents();
    }

    /**
     * Constructeur utiliser pour mode non interactif reseau
     *
     * @param rn                      Reseaux de neurones selectione pour l'apprentissage
     * @param apprentissagePrisonnier Vrai si on fais apprendre au prisonnier, faux sinon.
     */
    public Simulation(ReseauDeNeurones rn, boolean apprentissagePrisonnier) {
        this.observateurs = new ArrayList<>();
        this.prisonnier = new Agent(4, 10);
        this.gardien = new Agent(5, 4);
        this.nbTours = 0;
        this.estFini = false;
        if (apprentissagePrisonnier) {
            this.comportementPrisonnier = rn;
            this.comportementGardien = new ArbreDecisionGardienAleatoire(this, this.gardien);
        } else {
            this.comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
            this.comportementGardien = rn;
        }
        //Initialisation des carte bayesiennes pour les deux agents
        bayesiens = new HashMap<>();
        bayesiens.put(this.gardien, new Bayesien());
        bayesiens.put(this.prisonnier, new Bayesien());
        carteBayesiennes = new HashMap<>();
        carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
        carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());
    }

    /**
     * Constructeur secondaire pour le mode interactif
     *
     * @param prisonnier true si le personnage est le prisonnier et false sinon
     */
    public Simulation(boolean prisonnier) {
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


        if (prisonnier) {
            this.prisonnier = new Joueur(4, 10);
            this.gardien = new Agent(5, 4);
            this.positionnerAgentsSpawnAleatoire();
            comportementGardien = new ArbreDecisionGardien(this, this.gardien);
            bayesiens.put(this.gardien, new Bayesien());
            carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
            ArrayList<double[][]> list1 = new ArrayList<>();
            list1.add(carteBayesiennes.get(gardien).clone());
            historiqueBayesien.put(gardien, list1);

        } else {
            this.gardien = new Joueur(5, 4);
            this.prisonnier = new Agent(4, 10);
            this.positionnerAgentsSpawnAleatoire();
            comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
            bayesiens.put(this.prisonnier, new Bayesien());
            carteBayesiennes.put(this.prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());
            ArrayList<double[][]> list1 = new ArrayList<>();
            list1.add(carteBayesiennes.get(this.prisonnier).clone());
            historiqueBayesien.put(this.prisonnier, list1);

        }
        //historique
        historiquePosition = new HashMap<>();
        List<Position> list = new ArrayList<>();
        list.add(this.prisonnier.getPosition());
        historiquePosition.put(this.prisonnier, list);

        List<Position> list1 = new ArrayList<>();
        list1.add(this.gardien.getPosition());
        historiquePosition.put(this.gardien, list1);
    }

    /**
     * Methode permettant d'ajouter un observateur
     *
     * @param dj
     */
    public void ajouterObservateur(DessinJeu dj) {
        this.observateurs.add(dj);
    }

    /**
     * Methode permettant de notifier les observateurs
     */
    public void notifierObservateurs() {
        for (DessinJeu dj : this.observateurs) {
            dj.update(this);
        }
    }
    /**
     * Méthode qui positionne de manière aléatoire les deux perso sur un spawn
     */
    private void positionnerAgentsSpawnAleatoire(){
        List<Case> spawnsGardien = new ArrayList<>();
        List<Case> spawnsPrisonnier = new ArrayList<>();
        for(int i = 0; i < CARTE.length; i++){
            for(int j = 0; j < CARTE[0].length; j++){
                if(CARTE[i][j] == CaseEnum.SPAWN_GARDIEN.ordinal()){
                    spawnsGardien.add(new Case(j, i, 0));
                } else if (CARTE[i][j] == CaseEnum.SPAWN_PRISONNIER.ordinal()) {
                    spawnsPrisonnier.add(new Case(j, i, 0));
                }
            }
        }
        Case spawnGardien = spawnsGardien.get((int)(Math.random()*spawnsGardien.size()));
        Case spawnPrisonnier = spawnsPrisonnier.get((int)(Math.random()*spawnsPrisonnier.size()));
        this.prisonnier.setPosition(new Position(spawnPrisonnier.getX(), spawnPrisonnier.getY()));
        this.gardien.setPosition(new Position(spawnGardien.getX(), spawnGardien.getY()));
    }
    /**
     * Méthode qui positionne de manière aléatoire les deux agents pour l'apprentissage
     */
    private void positionnerAleatoirement(){
        List<Case> casesValides = new ArrayList<>();

        for(int i = 0; i < CARTE.length; i++){
            for(int j = 0; j < CARTE[0].length; j++){
                if(CARTE[i][j] == CaseEnum.SPAWN_GARDIEN.ordinal() || CARTE[i][j] == CaseEnum.SPAWN_PRISONNIER.ordinal() || CARTE[i][j] == CaseEnum.SOL.ordinal()) {
                    casesValides.add(new Case(j, i, 0));
                }
            }
        }
        Case spawnGardien = casesValides.get((int)(Math.random()*casesValides.size()));
        Case spawnPrisonnier = casesValides.get((int)(Math.random()*casesValides.size()));
        this.prisonnier.setPosition(new Position(spawnPrisonnier.getX(), spawnPrisonnier.getY()));
        this.gardien.setPosition(new Position(spawnGardien.getX(), spawnGardien.getY()));
    }

    /**
     * Methode de deplacement non interactif
     */
    public void deplacerAgents() {
        while (!estFini) {
            Deplacement d1 = this.comportementPrisonnier.prendreDecision();
            Deplacement d2 = this.comportementGardien.prendreDecision();

            deplacerPersonnage(this.prisonnier, d1);
            miseAJourFinJeu();
            deplacerPersonnage(this.gardien, d2);

            historiqueDeplacement.get(prisonnier).add(d1);
            historiqueDeplacement.get(gardien).add(d2);

            historiquePosition.get(prisonnier).add(prisonnier.getPosition());
            historiquePosition.get(gardien).add(gardien.getPosition());

            this.nbTours++;
            //gestion des interactions et de la fin du jeu
            miseAJourFinJeu();
            actualisationBayesienne(this.gardien, this.prisonnier);
            actualisationBayesienne(this.prisonnier, this.gardien);
            var cartebay = bayesiens.get(gardien).getCarteBayesienne().clone();
            historiqueBayesien.get(gardien).add(cartebay);
            var cartebay2 = bayesiens.get(prisonnier).getCarteBayesienne().clone();
            historiqueBayesien.get(prisonnier).add(cartebay2);
            this.notifierObservateurs();
        }
        this.notifierObservateurs();
    }

    /**
     * Permet de déplacer le joueur (prisonnier ou gardien) en fonction du déplacement
     *
     * @param d déplacement souhaité
     */
    public void deplacementJoueur(Deplacement d) {

        Deplacement deplacementAgent;

        Joueur joueur = (Joueur) this.getJoueur();
        Personnage agent;
        if (joueur.equals(this.prisonnier)) {
            agent = this.gardien;
            deplacementAgent = this.comportementGardien.prendreDecision();

        } else {
            agent = this.prisonnier;
            deplacementAgent = this.comportementPrisonnier.prendreDecision();
        }

        //initialisation du déplacement du joueur
        if(!verifierDeplacemnt(joueur,d))
            return;
        //on déplace d'abbord le joueur
        if(this.prisonnier.equals(getJoueur())){
            deplacerPersonnage(joueur, d);
            miseAJourFinJeu();
            if(!this.estFini)
            deplacerPersonnage(agent, deplacementAgent);
        }else {
            deplacerPersonnage(agent, deplacementAgent);
            miseAJourFinJeu();
            if(!this.estFini)
            deplacerPersonnage(joueur, d);
        }

        this.nbTours++;

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
        if(nbTours>=100)
            this.estFini=true;

        if(this.prisonnier.getPosition().equals(this.gardien.getPosition())){
            this.estFini = true;
            this.victoireGardien = true;
        }
        if (Simulation.CARTE[this.prisonnier.getPosition().getY()][this.prisonnier.getPosition().getX()] == CaseEnum.SORTIE.ordinal()) {
            this.estFini = true;
            this.victoirePrisonnier = true;
        }
    }

    /**
     * Méthode permetant de mettre a jour la carte bayesienne d'un personnage
     *
     * @param p1 le mis a jour
     * @param p2 le deuxieme personnage
     */
    public void actualisationBayesienne(Personnage p1, Personnage p2) {
        ArrayList<Position> positionsCasesVue = p1.getVision();

        ArrayList<Integer[]> casesVue = new ArrayList<>();
        for (Position position : positionsCasesVue) {
            Integer present = 0;
            if (p2.getPosition().equals(position)) {
                present = 1;
            }
            casesVue.add(new Integer[]{position.getY(), position.getX(), present});
        }
        carteBayesiennes.replace(p1, bayesiens.get(p1).calculerProbaPresence(carteBayesiennes.get(p1), casesVue));
    }

    /**
     * Methode permettant d'initialiser le jeu
     */
    @Override
    public void init() {
    }

    /**
     * Methode permettant de verifier si le jeu est fini
     *
     * @return
     */
    @Override
    public boolean etreFini() {
        return this.estFini;
    }

    public boolean murPresent(int x, int y) {
        return Simulation.CARTE[y][x] == CaseEnum.MUR.ordinal();
    }

    /**
     * Methode permettant de deplacer un personnage en fonction de l'action et de la carte
     *
     * @param p
     * @param d
     */

    public boolean deplacerPersonnage(Personnage p, Deplacement d) {
        Position persoPos = p.getPosition();
        Position nvPos = new Position(persoPos.getX(), persoPos.getY());
        nvPos.deplacement(d);
        boolean valide = verifierDeplacemnt(p,d);
        //si oui deplacer le personnage
        if(valide)
            p.deplacer(nvPos);

        return valide;
    }

    /**
     * permets de savoir si le deplacement est valide
     * @param p
     * @param d
     * @return
     */
    public boolean verifierDeplacemnt(Personnage p, Deplacement d){
        Position persoPos = p.getPosition();
        Position nvPos = new Position(persoPos.getX(), persoPos.getY());
        nvPos.deplacement(d);

        //verifier si le deplacement est possible
        if (murPresent(nvPos.getX(), nvPos.getY())) {
            return false;
        }
        //verification des diagonales
        if (!(d.equals(Deplacement.AUCUN))) {
            switch (d) {
                case DIAG_BAS_DROITE:
                    if (murPresent(persoPos.getX() + 1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY() + 1)) {
                        return false;
                    }
                    break;
                case DIAG_BAS_GAUCHE:
                    if (murPresent(persoPos.getX() - 1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY() + 1)) {
                        return false;
                    }
                    break;
                case DIAG_HAUT_DROITE:
                    if (murPresent(persoPos.getX() + 1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY() - 1)) {
                        return false;
                    }
                    break;
                case DIAG_HAUT_GAUCHE:
                    if (murPresent(persoPos.getX() - 1, persoPos.getY()) || murPresent(persoPos.getX(), persoPos.getY() - 1)) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * Methode permettant de savoir si un personnage est visible par un autre
     *
     * @param p1   le personnage qui est observé
     * @param role true si le personnage est le prisonnier, false sinon
     * @return
     */
    public boolean estVisible(Personnage p1, boolean role) {
        Position pos1 = p1.getPosition();
        Position pos2;
        if (role) {
            pos2 = this.gardien.getPosition();
        } else {
            pos2 = this.prisonnier.getPosition();
        }
        if (pos1.equals(pos2)) {
            return true;
        }
        ArrayList<Position> casesVisibles = VISION.get(pos2);

        for (Position pos : casesVisibles) {
            if (pos.equals(pos1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Methode permettant de recuperer le priosnnier
     *
     * @return le prisonnier
     */
    public Personnage getPrisonnier() {
        return prisonnier;
    }

    /**
     * Methode permettant de recuperer le gardien
     *
     * @return le gardien
     */
    public Personnage getGardien() {
        return gardien;
    }

    /**
     * Methode permettant de recuperer la carte bayesienne d'un personnage
     *
     * @param p
     * @return
     */
    public double[][] getCarteBayesienne(Personnage p) {
        return carteBayesiennes.get(p);
    }

    /**
     * Methode permettant de recuperer le nombre de tours actuels
     *
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
        }
        if (this.gardien instanceof Joueur) {
            return this.gardien;
        }
        return null;
    }


    public static Position getPosSortie() {
        for (int i = 0; i < CARTE.length; i++) {
            for (int j = 0; j < CARTE[0].length; j++) {
                if (CARTE[i][j] == CaseEnum.SORTIE.ordinal()) {
                    return new Position(j, i);
                }
            }
        }
        return new Position(0, 0);
    }

    /**
     * Methode permettant les bayesiens
     *
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
