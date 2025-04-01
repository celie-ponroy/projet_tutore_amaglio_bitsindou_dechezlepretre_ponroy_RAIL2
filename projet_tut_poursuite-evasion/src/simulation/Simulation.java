package simulation;

import affichage.DessinJeu;
import calculs.CalculChemins;
import calculs.CalculVision;
import moteur.Jeu;
import outils.ChargementCarte;
import simulation.comportement.*;
import simulation.personnages.*;

import java.util.*;

public class Simulation implements Jeu {
    protected List<DessinJeu> observateurs;
    protected int nbTours;
    protected int nbDeplacementsPerso;
    protected Personnage gardien;
    protected Personnage prisonnier;
    protected boolean victoirePrisonnier;
    protected boolean victoireGardien;
    protected Comportement comportementGardien;
    protected Comportement comportementPrisonnier;
    public static int[][] CARTE = ChargementCarte.charger("donnees/petitLaby.txt");
    public static final HashMap<Position, ArrayList<Position>> VISION_G = CalculVision.recupererVision("G");
    public static final HashMap<Position, ArrayList<Position>> VISION_P = CalculVision.recupererVision("P");
    public static final HashMap<Position, ArrayList<Position>> VISION_CAMERAS = CalculVision.recupererVision("C");
    public static final HashMap<List<Position>, Stack> CHEMINS_G = CalculChemins.recupererCheminGardien();
    public static final HashMap<List<Position>, Stack> CHEMINS_P = CalculChemins.recupererCheminPrisonnier();

    protected HashMap<Personnage, List<Position>> historiquePosition;
    protected HashMap<Personnage, List<double[][]>> historiqueBayesien;
    protected HashMap<Personnage, List<Deplacement>> historiqueDeplacement;

    protected boolean estFini;
    protected HashMap<Personnage, double[][]> carteBayesiennes;
    protected HashMap<Personnage, Bayesien> bayesiens;

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
        this.nbDeplacementsPerso = 0;

        //les 2 personnages sont des agents
        this.prisonnier = new Agent(9, 18, VISION_P);
        this.gardien = new Agent(5, 4, VISION_G);
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

        //ajout des comportements
        setComportementsGardient(ComportementGardien);
        setComportementsPrisonier(ComportementPrisonier);
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
     * Constructeur secondaire pour le mode interactif
     *
     * @param perso true si le personnage est le prisonnier et false sinon
     */
    public Simulation(boolean perso, Comportements ComportementAdversaire) {
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
            this.prisonnier = new Joueur(9, 18, VISION_P);
            this.gardien = new Agent(5, 4, VISION_G);

            //Position aléatoire des agents
            this.positionnerAgentsSpawnAleatoire();

            setComportementsGardient(ComportementAdversaire);

            bayesiens.put(this.gardien, new Bayesien());
            carteBayesiennes.put(gardien, bayesiens.get(this.gardien).getCarteBayesienne());
            ArrayList<double[][]> list1 = new ArrayList<>();
            list1.add(carteBayesiennes.get(gardien).clone());
            historiqueBayesien.put(gardien, list1);

        } else {
            this.gardien = new Joueur(5, 4, VISION_G);
            this.prisonnier = new Agent(9, 18, VISION_G);

            this.positionnerAgentsSpawnAleatoire();

            setComportementsPrisonier(ComportementAdversaire);

            bayesiens.put(this.prisonnier, new Bayesien());
            carteBayesiennes.put(prisonnier, bayesiens.get(this.prisonnier).getCarteBayesienne());
            ArrayList<double[][]> list1 = new ArrayList<>();
            list1.add(carteBayesiennes.get(prisonnier).clone());
            historiqueBayesien.put(prisonnier, list1);

        }

        //historique
        historiquePosition = new HashMap<>();
        List<Position> list = new ArrayList<>();
        list.add(this.prisonnier.getPosition());
        historiquePosition.put(this.prisonnier, list);

        List<Position> list1 = new ArrayList<>();
        list1.add(this.gardien.getPosition());
        historiquePosition.put(this.gardien, list1);

        historiqueDeplacement = new HashMap<>();
        List<Deplacement> depP = new ArrayList<>();
        List<Deplacement> depG = new ArrayList<>();
        historiqueDeplacement.put(gardien, depG);
        historiqueDeplacement.put(prisonnier, depP);

    }

    /**
     * Constructeur par copie
     */
    public Simulation(Simulation simulation) {
        this.gardien = simulation.gardien;
        this.prisonnier = simulation.prisonnier;
        this.comportementGardien = simulation.comportementGardien;
        this.comportementPrisonnier = simulation.comportementPrisonnier;
        this.historiqueDeplacement = simulation.historiqueDeplacement;
        this.historiquePosition = simulation.historiquePosition;
        this.historiqueBayesien = simulation.historiqueBayesien;
        this.estFini = simulation.estFini;
        this.carteBayesiennes = simulation.carteBayesiennes;
        this.bayesiens = simulation.bayesiens;
        this.nbTours = simulation.nbTours;
        this.victoireGardien = simulation.victoireGardien;
        this.victoirePrisonnier = simulation.victoirePrisonnier;
        this.observateurs = new ArrayList<>();
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
     * Méthode qui positionne aléatoirement les personnages sur leurs spawns attitrees
     */
    protected void positionnerAgentsSpawnAleatoire() {
        List<Case> spawnsGardien = new ArrayList<>();
        List<Case> spawnsPrisonnier = new ArrayList<>();
        for (int i = 0; i < CARTE.length; i++) {
            for (int j = 0; j < CARTE[0].length; j++) {
                if (CARTE[i][j] == CaseEnum.SPAWN_GARDIEN.ordinal()) {
                    spawnsGardien.add(new Case(j, i));
                } else if (CARTE[i][j] == CaseEnum.SPAWN_PRISONNIER.ordinal()) {
                    spawnsPrisonnier.add(new Case(j, i));
                }
            }
        }
        Case spawnGardien = spawnsGardien.get((int) (Math.random() * spawnsGardien.size()));
        Case spawnPrisonnier = spawnsPrisonnier.get((int) (Math.random() * spawnsPrisonnier.size()));
        this.prisonnier.setPosition(new Position(spawnPrisonnier.getX(), spawnPrisonnier.getY()));
        this.gardien.setPosition(new Position(spawnGardien.getX(), spawnGardien.getY()));
    }

    /**
     * Méthode qui positionne aléatoirement les deux agents sur l'ensemble de la carte pour l'apprentissage
     */
    protected void positionnerAleatoirement() {
        List<Case> casesValides = new ArrayList<>();

        for (int i = 0; i < CARTE.length; i++) {
            for (int j = 0; j < CARTE[0].length; j++) {
                if (CARTE[i][j] == CaseEnum.SPAWN_GARDIEN.ordinal() || CARTE[i][j] == CaseEnum.SPAWN_PRISONNIER.ordinal() || CARTE[i][j] == CaseEnum.SOL.ordinal()) {
                    casesValides.add(new Case(j, i));
                }
            }
        }

        Case spawnGardien = casesValides.get((int) Math.round((Math.random() * (casesValides.size()-1))));
        //On retire la case pour eviter le spawn de deux agent sur la meme case
        casesValides.remove(spawnGardien);
        Case spawnPrisonnier = casesValides.get((int) Math.round((Math.random() * (casesValides.size()-1))));

        this.prisonnier.setPosition(new Position(spawnGardien.getX(),spawnGardien.getY()));
        this.gardien.setPosition(new Position(spawnPrisonnier.getX(), spawnPrisonnier.getY()));

        int casesHaut = 0;
        int casesBas = 0;

        for (Case c : casesValides) {
            if (c.getY() > 3) {
                casesBas++;
            } else if (c.getY() < 3) {
                casesHaut++;
            }
        }
    }

    /**
     * Methode de deplacement non interactif
     */
    public void deplacerAgents() {
        //tant que le jeu n'est pas fini
        while (!estFini) {
            actualisationBayesienne(this.gardien, this.prisonnier);
            actualisationBayesienne(this.prisonnier, this.gardien);

            Deplacement d1 = this.comportementPrisonnier.prendreDecision();
            Deplacement d2 = this.comportementGardien.prendreDecision();

            historiqueDeplacement.get(prisonnier).add(d1);
            historiqueDeplacement.get(gardien).add(d2);

            var cartebay = bayesiens.get(gardien).getCarteBayesienne().clone();
            var cartebay2 = bayesiens.get(prisonnier).getCarteBayesienne().clone();

            historiqueBayesien.get(gardien).add(cartebay);
            historiqueBayesien.get(prisonnier).add(cartebay2);

            deplacerPersonnage(this.prisonnier, d1);
            miseAJourFinJeu();
            deplacerPersonnage(this.gardien, d2);
            //on incremente le nombre de deplacements des personnages
            this.nbDeplacementsPerso++;

            historiquePosition.get(prisonnier).add(prisonnier.getPosition());
            historiquePosition.get(gardien).add(gardien.getPosition());

            this.nbTours++;

            miseAJourFinJeu();

        }

        historiquePosition.get(prisonnier).add(prisonnier.getPosition());
        historiquePosition.get(gardien).add(gardien.getPosition());

        this.notifierObservateurs();
    }

    /**
     * Permet de déplacer le joueur (prisonnier ou gardien) en fonction du déplacement
     *
     * @param d déplacement souhaité
     */
    public void deplacementJoueur(Deplacement d) {
        this.historiqueDeplacement.get(this.prisonnier).add(Deplacement.AUCUN);
        this.historiqueDeplacement.get(this.gardien).add(Deplacement.AUCUN);

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
        if (!verifierDeplacemnt(joueur, d)) {
            historiqueDeplacement.get(joueur).removeLast();
            historiqueDeplacement.get(agent).removeLast();
            return;
        }
        //on déplace d'abbord le joueur
        if (this.prisonnier.equals(getJoueur())) {
            deplacerPersonnage(joueur, d);
            this.historiqueDeplacement.get(joueur).removeLast();
            this.historiqueDeplacement.get(joueur).add(d);
            miseAJourFinJeu();
            if (!this.estFini) {
                deplacerPersonnage(agent, deplacementAgent);
                this.historiqueDeplacement.get(agent).removeLast();
                this.historiqueDeplacement.get(agent).add(deplacementAgent);
            }
        } else {
            deplacerPersonnage(agent, deplacementAgent);
            this.historiqueDeplacement.get(agent).removeLast();
            this.historiqueDeplacement.get(agent).add(deplacementAgent);
            miseAJourFinJeu();
            if (!this.estFini) {
                deplacerPersonnage(joueur, d);
                this.historiqueDeplacement.get(joueur).removeLast();
                this.historiqueDeplacement.get(joueur).add(d);
            }

        }

        this.nbTours++;

        actualisationBayesienne(agent, joueur);

        var cartebay = bayesiens.get(agent).getCarteBayesienne().clone();
        historiqueBayesien.get(agent).add(cartebay);


        //gestion des interactions et de la fin du jeu
        miseAJourFinJeu();
        historiquePosition.get(joueur).add(joueur.getPosition());
        historiquePosition.get(agent).add(agent.getPosition());

        this.notifierObservateurs();


    }

    /**
     * Mise à jour fin du jeu
     */
    public void miseAJourFinJeu() {
        if (nbTours >= 100)
            this.estFini = true;

        if (this.prisonnier.getPosition().equals(this.gardien.getPosition())) {
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
        carteBayesiennes.replace(p1, bayesiens.get(p1).calculerProbaPresence(carteBayesiennes.get(p1).clone(), casesVue));
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

    public static boolean murPresent(int x, int y) {
        return Simulation.CARTE[y][x] == CaseEnum.MUR.ordinal();
    }

    /**
     * Methode permettant de deplacer un personnage en fonction de l'action et de la carte
     *
     * @param p personnage a deplacer
     * @param d deplacement a effectuer
     */

    public boolean deplacerPersonnage(Personnage p, Deplacement d) {
        Position persoPos = p.getPosition();
        Position nvPos = new Position(persoPos.getX(), persoPos.getY());
        nvPos.deplacement(d);
        boolean valide = verifierDeplacemnt(p, d);
        //si oui deplacer le personnage
        if (valide)
            p.deplacer(nvPos);

        return valide;
    }

    /**
     * permets de savoir si le deplacement est valide
     *
     * @param p
     * @param d
     * @return
     */
    public boolean verifierDeplacemnt(Personnage p, Deplacement d) {
        Position persoPos = p.getPosition();
        Position nvPos = new Position(persoPos.getX(), persoPos.getY());
        nvPos.deplacement(d);

        if (p.equals(this.prisonnier) && Simulation.CARTE[nvPos.getY()][nvPos.getX()] == CaseEnum.RACCOURCI_GARDIEN.ordinal()) {
            return false;
        }
        if (p.equals(this.gardien) && nvPos.equals(getPosSortie())) {
            return false;
        }

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
     * Méthode permetant de set le comportement du gardient a partir de l'enum Comportement
     *
     * @param comportements enums représantant le comportement chosit
     */
    public void setComportementsGardient(Comportements comportements) {
        switch (comportements) {
            case Comportements.ArbreDeterministe:
                this.comportementGardien = new ArbreDecisionGardien(this, this.gardien);
                break;
            case Comportements.ArbreAleatoire:
                this.comportementGardien = new ArbreDecisionGardienAleatoire(this, this.gardien);
                break;
            case Comportements.Aleatoire:
                this.comportementGardien = new Aleatoire(this, this.gardien);
                break;
            case Comportements.ReseauArbreDeterministe:
                this.comportementGardien = new ReseauDeNeurones("mlp_q", this, this.gardien);
                break;
            case Comportements.ReseauArbreAleatoire:
                //this.comportementGardien = new ReseauDeNeurones("donnees/sauvegardes_NeuralNetwork/G-RN-ArbreAleatoire", this, this.gardien);
                break;
            default:
                break;
        }
    }

    /**
     * Méthode permetant de set le comportement du prisonier a partir de l'enum Comportement
     *
     * @param comportements enums représantant le comportement chosit
     */
    public void setComportementsPrisonier(Comportements comportements) {
        switch (comportements) {
            case Comportements.ArbreDeterministe:
                this.comportementPrisonnier = new ArbreDecisionPrisonnier(this, this.prisonnier);
                break;
            case Comportements.ArbreDeterministev2:
                this.comportementPrisonnier = new ArbreDecisionPrisonnier2(this, this.prisonnier);
                break;
            case Comportements.Aleatoire:
                this.comportementPrisonnier = new Aleatoire(this, this.prisonnier);
                break;
            case Comportements.ReseauArbreDeterministe:
                this.comportementPrisonnier = new ReseauDeNeurones("mlp_q", this, this.prisonnier);
                break;
            case Comportements.ReseauArbreAleatoire:
                //this.comportementPrisonnier = new ReseauDeNeurones("donnees/sauvegardes_NeuralNetwork/P-RN-ArbreAleatoire", this, this.prisonnier);
                break;
            default:
                break;
        }
    }

    /**
     * Methode permettant de savoir si un personnage est visible par un autre
     *
     * @param p1   le personnage qui est observé
     * @param role true si le personnage est le prisonnier, false sinon
     * @return true si le personnage est visible
     */
    public boolean estVisible(Personnage p1, boolean role) {
        Position pos1 = p1.getPosition();
        HashMap<Position, ArrayList<Position>> VISION;
        Position pos2;
        if (role) {
            pos2 = this.gardien.getPosition();
            VISION = VISION_G;
        } else {
            pos2 = this.prisonnier.getPosition();
            VISION = VISION_P;
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
     * @return le nolbre de tours
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
     * Méthode qui calcule la taille de la carte
     *
     * @return taille de la carte
     */
    public static int getTailleCarte() {
        return Simulation.CARTE.length * Simulation.CARTE[0].length;
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

    /**
     * Méthode qui récupère le nombre de déplacements des persos
     */
    public int getNbDeplacementsPerso() {
        return nbDeplacementsPerso;
    }

    public HashMap<Personnage, List<Deplacement>> getHistoriqueDeplacement() {
        return historiqueDeplacement;
    }

    public HashMap<Personnage, List<Position>> getHistoriquePosition() {
        return historiquePosition;
    }

    public HashMap<Personnage, List<double[][]>> getHistoriqueBayesien() {
        return historiqueBayesien;
    }

    public void supprimerObservateurs() {
        this.observateurs.clear();
    }

    public Comportement getComportementPrisonnier() {
        return comportementPrisonnier;
    }

    public Comportement getComportementGardien() {
        return comportementGardien;
    }

    public HashMap<Personnage, double[][]> getCarteBayesiennes() {
        return carteBayesiennes;
    }

    public void setComportementPrisonnier(Comportement comportementPrisonnier) {
        this.comportementPrisonnier = comportementPrisonnier;
    }

    public void setComportementGardien(Comportement comportementGardien) {
        this.comportementGardien = comportementGardien;
    }

    public void setCarteBayesiennes(HashMap<Personnage, double[][]> carteBayesiennes) {
        this.carteBayesiennes = carteBayesiennes;
    }

    public void setBayesiens(HashMap<Personnage, Bayesien> bayesiens) {
        this.bayesiens = bayesiens;
    }

    public void setGardien(Personnage gardien) {
        this.gardien = gardien;
    }

    public void setEstFini(boolean estFini) {
        this.estFini = estFini;
    }

    public void setHistoriqueBayesien(HashMap<Personnage, List<double[][]>> historiqueBayesien) {
        this.historiqueBayesien = historiqueBayesien;
    }

    public void setHistoriqueDeplacement(HashMap<Personnage, List<Deplacement>> historiqueDeplacement) {
        this.historiqueDeplacement = historiqueDeplacement;
    }

    public void setHistoriquePosition(HashMap<Personnage, List<Position>> historiquePosition) {
        this.historiquePosition = historiquePosition;
    }

    public void setNbTours(int nbTours) {
        this.nbTours = nbTours;
    }

    public void setObservateurs(List<DessinJeu> observateurs) {
        this.observateurs = observateurs;
    }

    public void setPrisonnier(Personnage prisonnier) {
        this.prisonnier = prisonnier;
    }

    public void setVictoireGardien(boolean victoireGardien) {
        this.victoireGardien = victoireGardien;
    }

    public void setVictoirePrisonnier(boolean victoirePrisonnier) {
        this.victoirePrisonnier = victoirePrisonnier;
    }

    public double[][] getCarteDouble(){
        double[][] carte = new double[Simulation.CARTE.length][Simulation.CARTE[0].length];
        for(int i = 0; i < carte.length; i++){
            for(int j = 0; j < carte[0].length; j++){
                carte[i][j] = (double) Simulation.CARTE[i][j];
            }
        }
        return carte;
    }

    public double[][] getCarteMursSortie() {
        double[][] carteMursSortie = new double[Simulation.CARTE.length][Simulation.CARTE[0].length];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[0].length; j++) {
                if (Simulation.CARTE[i][j] == CaseEnum.SORTIE.ordinal()) {
                    carteMursSortie[i][j] = 2.0;
                }else if (Simulation.CARTE[i][j] == CaseEnum.MUR.ordinal()) {
                    carteMursSortie[i][j] = 1.0;
                }else{
                    carteMursSortie[i][j] = 0.0;
                }
            }
        }
        return carteMursSortie;
    }

    @Override
    public String toString() {
        return "Simulation{" +
                ", nbTours=" + nbTours +
                ", gardien=" + gardien +
                ", prisonnier=" + prisonnier +
                ", victoirePrisonnier=" + victoirePrisonnier +
                ", victoireGardien=" + victoireGardien +
                ", comportementGardien=" + comportementGardien +
                ", comportementPrisonnier=" + comportementPrisonnier +
                ", historiquePosition=" + historiquePosition +
                ", historiqueBayesien=" + historiqueBayesien +
                ", historiqueDeplacement=" + historiqueDeplacement +
                ", estFini=" + estFini +
                ", carteBayesiennes=" + carteBayesiennes +
                ", bayesiens=" + bayesiens +
                '}';
    }
}
