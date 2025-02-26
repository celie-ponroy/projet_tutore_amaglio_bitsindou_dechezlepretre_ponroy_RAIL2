package simulation.tuto;

import javafx.scene.input.KeyEvent;
import simulation.Comportements;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

public class SimulationTutoriel extends Simulation {
    public EtatTuto etatActuel;

    public SimulationTutoriel(){
        super(true , Comportements.ArbreAleatoire);
        etatActuel = EtatTuto.DEBUT_Perso;
        Position gardien = getPosSortie();
        gardien.deplacement(Deplacement.BAS);
        this.gardien.setPosition(gardien);
    }
    /**
     * Permet de déplacer le joueur (prisonnier ou gardien) en fonction du déplacement
     *
     * @param d déplacement souhaité
     */
    public void deplacementJoueur(Deplacement d) { //ajout du chemin dédié
        if(etatActuel == EtatTuto.DEPLACEMENT && this.estVisible(gardien,false)){
            this.etatActuel = EtatTuto.GARDIEN;
        }
        Deplacement deplacementAgent;

        Joueur joueur = (Joueur) this.getJoueur();
        Personnage agent = this.gardien;

        if(this.nbTours<=4){
            deplacementAgent = Deplacement.AUCUN;
        }else {
            deplacementAgent = this.comportementGardien.prendreDecision();
        }
        //initialisation du déplacement du joueur
        if (!verifierDeplacemnt(joueur, d)) {
            return;
        }
        //on déplace d'abbord le joueur

        deplacerPersonnage(joueur, d);
        miseAJourFinJeu();
        if (!this.estFini){
            deplacerPersonnage(agent, deplacementAgent);
        }else {
            this.etatActuel = EtatTuto.FIN;
        }

        this.nbTours++;
        actualisationBayesienne(agent, joueur);
        var cartebay = bayesiens.get(agent).getCarteBayesienne().clone();
        historiqueBayesien.get(agent).add(cartebay);

        //gestion des interactions et de la fin du jeu
        miseAJourFinJeu();
        if(this.estFini){
            this.etatActuel = EtatTuto.FIN;
        }
        this.notifierObservateurs();
    }

    /**
     * update
     */
    public void update(KeyEvent keyEvent){
        switch (etatActuel){
            case DEBUT_Perso:
                //on affiche les informations du perso
                if(keyEvent.getCode().toString().equals("SPACE")){
                    etatActuel = EtatTuto.DEPLACEMENT;
                    notifierObservateurs();
                }
                break;
            case DEPLACEMENT:
                deplacementJoueur(getDeplacement(keyEvent));
                break;
            case GARDIEN:
                if(keyEvent.getCode().toString().equals("SPACE")){
                    etatActuel = EtatTuto.DEPLACEMENT_GARDIEN;
                    notifierObservateurs();
                }
                break;
            case DEPLACEMENT_GARDIEN:
                deplacementJoueur(getDeplacement(keyEvent));
                break;
            case FIN:
                //on fait rien c'est la fin
                break;
        }
    }
    private Deplacement getDeplacement(KeyEvent keyEvent){
        switch (keyEvent.getCode()) {
            case Z:
            case NUMPAD8:
                return Deplacement.HAUT;
            case Q:
            case NUMPAD4:
                return Deplacement.GAUCHE;
            case X:
            case NUMPAD2:
                return Deplacement.BAS;
            case S:
            case NUMPAD5:
                return Deplacement.AUCUN;
            case D:
            case NUMPAD6:
                return Deplacement.DROITE;
            case A:
            case NUMPAD7:
                return Deplacement.DIAG_HAUT_GAUCHE;
            case E:
            case NUMPAD9:
                return (Deplacement.DIAG_HAUT_DROITE);
            case W:
            case NUMPAD1:
                return (Deplacement.DIAG_BAS_GAUCHE);
            case C:
            case NUMPAD3:
                return Deplacement.DIAG_BAS_DROITE;
            default:
                return Deplacement.AUCUN;
        }
    }

}
