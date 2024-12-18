package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;

import simulation.personnages.Joueur;
import simulation.personnages.Personnage;

import java.util.Arrays;

public class VueBayesienne extends Pane implements DessinJeu {

    private Simulation simulation;
    private Image imageMur;
    private Image imageSol;
    private Image imageSortie;
    //private Image imagePrisonnier;
    //private Image imageGardien;
    private Image imagePerso;
    //private ImageView prisonnierView; // Vue pour le prisonnier
    private ImageView gardienView; // Vue pour le gardien
    private ImageView persoView; // Vue pour le gardien
    private Personnage perso; // Personnage à afficher
    private Rectangle[][] caseBayesienne;
    private static final int TAILLE_CELLULE = 45; // Taille des cases du labyrinthe

    /**
     * Constructeur dans le cas où l'on souhaiterait afficher qu'un seul personnage et son bayésien
     */
    public VueBayesienne(Simulation s, Personnage p) {
        if (s.getGardien() == p) {
            this.perso = s.getGardien();
        } else {
            this.perso = s.getPrisonnier();
        }
    }

    /**
     * Initialise les images
     */
    private void initImages() {
        this.imageMur = new Image("file:images/murs.png");
        this.imageSol = new Image("file:images/sol.png");
        this.imageSortie = new Image("file:images/sortie.png");
        //si le personnage est un prisonnier on affiche
        if (this.perso == simulation.getPrisonnier()) {
            this.imagePerso= new Image("file:images/prisonnier.png");
        } else {
            this.imagePerso= new Image("file:images/gardien.png"); //sinon on affiche le gardien
        }
    }

    /**
     * Initialise le labyrinthe et les personnages
     */
    private void initLabyrinthe() {
        // Création du labyrinthe à partir de la carte
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                StackPane stackPane = new StackPane();
                stackPane.setLayoutX(j * TAILLE_CELLULE);
                stackPane.setLayoutY(i * TAILLE_CELLULE);

                // Sélection de l'image en fonction de la case
                Image image = null;
                switch (simulation.CARTE[i][j]) {
                    case Simulation.MUR:
                        image = this.imageMur;
                        break;
                    case Simulation.SOL:
                        image = this.imageSol;
                        break;
                    case Simulation.SORTIE:
                        image = this.imageSortie;
                        break;
                }

                if (image != null) {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(TAILLE_CELLULE);
                    imageView.setFitHeight(TAILLE_CELLULE);
                    stackPane.getChildren().add(imageView);
                }

                // Ajout d'une zone de collision invisible (si nécessaire)
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.TRANSPARENT);
                stackPane.getChildren().add(rectangle);
                this.getChildren().add(stackPane); // Ajout au Pane principal
            }
        }

        //Création du personnage
        Personnage personnage;

        //Ajout filtre couleur bayes en fonction du personnage
        if (this.perso == simulation.getPrisonnier()){
            personnage = simulation.getPrisonnier();
        }else{
            personnage = simulation.getGardien();
        }

        // Initialisation de la carte bayesienne
        double[][] carteBayes = simulation.getCarteBayesienne(personnage);
        caseBayesienne = new Rectangle[carteBayes.length][carteBayes[0].length];

        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setX(j * TAILLE_CELLULE);
                rectangle.setY(i * TAILLE_CELLULE);
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    rectangle.setFill(Color.rgb(190, 35, 0, 1 * carteBayes[i][j]));
                }
                this.getChildren().add(rectangle);
                caseBayesienne[i][j] = rectangle;

            }
        }

        // Initialisation des personnages
        persoView = new ImageView(imagePerso);
        persoView.setFitWidth(TAILLE_CELLULE);
        persoView.setFitHeight(TAILLE_CELLULE);

        this.getChildren().add(persoView);

        // Placement initial des personnages
        updatePositions();
    }

    /**
     * Met à jour uniquement les positions des personnages
     */
    private void updatePositions() {

        if (this.perso == simulation.getPrisonnier()) {
            // Met à jour la position du prisonnier
            persoView.setX(simulation.getPrisonnier().getPosition().getX() * TAILLE_CELLULE);
            persoView.setY(simulation.getPrisonnier().getPosition().getY() * TAILLE_CELLULE);
        } else {
            // Met à jour la position du gardien
            persoView.setX(simulation.getGardien().getPosition().getX() * TAILLE_CELLULE);
            persoView.setY(simulation.getGardien().getPosition().getY() * TAILLE_CELLULE);
        }
    }

    /**
     * Met à jour les probabilités bayesiennes uniquement du joueur
     */
    private void updateBayes() {

        //Création du personnage
        Personnage personnage;
        // Mise à jour des probabilités bayesiennes en fonction du perosnnage
        if (this.perso == simulation.getPrisonnier()){
            personnage = simulation.getPrisonnier();
        }else{
            personnage = simulation.getGardien();
        }
        double[][] carteBayes = simulation.getCarteBayesienne(personnage);
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = caseBayesienne[i][j];
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    rectangle.setFill(Color.rgb(190, 35, 0, 1* carteBayes[i][j]));
                }
            }
        }
    }

    /**
     * Met à jour l'affichage du jeu
     * @param jeu jeu a afficher
     */
    @Override
    public void update(Jeu jeu) {
        // Récuperation de la simulation
        this.simulation = (Simulation) jeu;

        if (this.getChildren().isEmpty()) {
            // Si le labyrinthe n'est pas encore initialisé
            initImages();
            initLabyrinthe();
        } else {
            // Sinon, il met juste a jour les positions des personnages et des proba bayesienne
            updatePositions();
            updateBayes();
        }
    }

}
