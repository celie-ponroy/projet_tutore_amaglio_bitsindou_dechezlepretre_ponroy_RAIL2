package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

public class VueBayesienne extends VueSimulation {

    private ImageView persoView; // Vue pour le gardien
    private Personnage perso; // Personnage à afficher
    private Image imagePerso;
    private Rectangle[][] caseBayesienne;
    private Rectangle[][] casesCameras;
    private int tour;
    private int decalageX = 0;
    private int decalageY = 0;

    /**
     * Constructeur dans le cas où l'on souhaiterait afficher qu'un seul personnage et son bayésien
     */
    public VueBayesienne(Simulation s, Personnage p, int decalageX, int decalageY, int tailleCellule) {
        super();
        TAILLE_CELLULE = tailleCellule;
        this.simulation = s;
        if (s.getGardien() == p) {
            this.perso = s.getGardien();
        } else {
            this.perso = s.getPrisonnier();
        }
        if (this.perso == simulation.getPrisonnier()) {
            this.imagePerso = new Image("file:images/prisonnier.png");

        } else {
            this.imagePerso = new Image("file:images/gardien.png");
        }
        this.persoView = new ImageView(imagePerso);
        persoView.setFitWidth(TAILLE_CELLULE);
        persoView.setFitHeight(TAILLE_CELLULE);

        tour = 0;
        init();
    }


    /**
     * Initialise le labyrinthe et les personnages
     */
    private void init() {
        Personnage personnage;

        if (this.perso == simulation.getPrisonnier()) {
            personnage = simulation.getPrisonnier();
        } else {
            personnage = simulation.getGardien();
        }


        this.getChildren().add(initLabyrinthe(true));
        // Initialisation de la carte bayesienne
        double[][] carteBayes = simulation.getCarteBayesienne(personnage);
        caseBayesienne = FiltreBayesien.initFiltre(carteBayes, TAILLE_CELLULE, decalageX, decalageY);
        for (Rectangle[] rect : caseBayesienne) {
            for (Rectangle sousrect : rect) {
                this.getChildren().add(sousrect);
            }
        }
        casesCameras = FiltreCamera.initFiltre(TAILLE_CELLULE, decalageX, decalageY);
        for (Rectangle[] rect : casesCameras) {
            for (Rectangle sousrect : rect) {
                this.getChildren().add(sousrect);
            }
        }
        this.getChildren().add(persoView);


        // Placement initial des personnages
        updatePositions();
    }

    @Override
    public void setOpacityPersonnage() {
        prisonnierView.setOpacity(0);
        gardienView.setOpacity(0);
        persoView.setOpacity(1);
    }

    /**
     * Met à jour uniquement les positions des personnages
     */
    protected void updatePositions() {
        if (this.perso == simulation.getPrisonnier()) {
            // Met à jour la position du prisonnier
            Position p = simulation.getHistoriquePosition().get(simulation.getPrisonnier()).get(tour);
            setPositions(p, persoView);
        } else {
            // Met à jour la position du gardien
            Position g = simulation.getHistoriquePosition().get(simulation.getGardien()).get(tour);
            setPositions(g, persoView);
        }
    }

    /**
     * Met à jour les probabilités bayesiennes uniquement du joueur
     */
    private void updateBayes() {
        if (tour >= simulation.getNbTours()) {
            return;
        }
        var carte = simulation.getHistoriqueBayesien().get(this.perso).get(tour + 1);
        FiltreBayesien.updateBayes(caseBayesienne, carte);
    }

    /**
     * Permets de mettre à jour selon le tour
     *
     * @param tour
     */
    public void update(int tour) {
        this.tour = tour;
        updateDirections(tour);
        updatePositions();
        updateBayes();
    }

    @Override
    protected void updateDirections(int tour) {
        var historiqueDeplacement = this.simulation.getHistoriqueDeplacement().get(this.simulation.getPrisonnier());
        if (historiqueDeplacement.isEmpty()) {
            System.out.println("Historique de déplacement vide");
            return;
        }
        String nomPerso = "prisonnier";
        if (perso == this.simulation.getGardien()) {
            nomPerso = "gardien";
        }
        if (this.simulation.getHistoriqueDeplacement().get(this.simulation.getPrisonnier()).size() <= tour || tour == 0) {
            persoView.setImage(new Image("file:images/" + nomPerso + ".png"));
            return;
        }

        switch (this.simulation.getHistoriqueDeplacement().get(perso).get(tour)) {
            case HAUT:
                persoView.setImage(new Image("file:images/" + nomPerso + "_haut.png"));
                break;
            case BAS:
                persoView.setImage(new Image("file:images/" + nomPerso + "_bas.png"));
                break;
            case GAUCHE:
            case DIAG_BAS_GAUCHE:
            case DIAG_HAUT_GAUCHE:
                persoView.setImage(new Image("file:images/" + nomPerso + "_gauche.png"));
                break;
            case DROITE:
            case DIAG_BAS_DROITE:
            case DIAG_HAUT_DROITE:
                persoView.setImage(new Image("file:images/" + nomPerso + "_droite.png"));
                break;
            case AUCUN:
                prisonnierView.setImage(new Image("file:images/" + nomPerso + ".png"));
                break;
        }
    }
}
