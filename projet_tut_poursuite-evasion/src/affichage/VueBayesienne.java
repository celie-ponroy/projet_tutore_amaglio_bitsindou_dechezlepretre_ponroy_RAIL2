package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

public class VueBayesienne extends VueSimulation {

    private Simulation simulation;
    private ImageView persoView; // Vue pour le gardien
    private Personnage perso; // Personnage à afficher
    private Image imagePerso;
    private Rectangle[][] caseBayesienne;
    private int tour = 0;

    /**
     * Constructeur dans le cas où l'on souhaiterait afficher qu'un seul personnage et son bayésien
     */
    public VueBayesienne(Simulation s, Personnage p) {
        super();
        this.simulation = s;
        if (s.getGardien() == p) {
            this.perso = s.getGardien();
        } else {
            this.perso = s.getPrisonnier();
        }
        if (this.perso == simulation.getPrisonnier()) {
            this.imagePerso= new Image("file:images/prisonnier.png");

        } else {
            this.imagePerso= new Image("file:images/gardien.png");
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

        if (this.perso == simulation.getPrisonnier()){
            personnage = simulation.getPrisonnier();
        }else{
            personnage = simulation.getGardien();
        }


        this.getChildren().add(initLabyrinthe());
        // Initialisation de la carte bayesienne
        double[][] carteBayes = simulation.getCarteBayesienne(personnage);
        caseBayesienne = FiltreBayesien.initFiltre(carteBayes, TAILLE_CELLULE);
        for (Rectangle[] rect : caseBayesienne) {
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
            Position p = simulation.historiquePosition.get(simulation.getPrisonnier()).get(tour);
            setPositions(p, persoView);
        } else {
            // Met à jour la position du gardien
            Position g = simulation.historiquePosition.get(simulation.getGardien()).get(tour);
            setPositions(g, persoView);
        }
    }

    /**
     * Met à jour les probabilités bayesiennes uniquement du joueur
     */
    private void updateBayes() {
        var carte = simulation.historiqueBayesien.get(this.perso).get(tour);
        FiltreBayesien.updateBayes(caseBayesienne,carte);
    }

    /**
     * Permets de mettre à jour selon le tour
     * @param tour
     */
    public void update(int tour) {
        this.tour = tour;
        updatePositions();
        updateBayes();
    }

}
