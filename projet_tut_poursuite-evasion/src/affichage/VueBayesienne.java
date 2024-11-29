package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;

import simulation.personnages.Agent;
import simulation.personnages.Joueur;

import java.util.Arrays;

public class VueBayesienne extends Pane implements DessinJeu {

    private Simulation simulation;
    private Image imageMur;
    private Image imageSol;
    private Image imageSortie;
    private Image imagePrisonnier;
    private Image imageGardien;

    private ImageView prisonnierView; // Vue pour le prisonnier
    private ImageView gardienView; // Vue pour le gardien

    private Rectangle[][] caseBayesienne;


    private static final int TAILLE_CELLULE = 50; // Taille des cases du labyrinthe

    /**
     * Initialise les images
     */
    private void initImages() {
        this.imageMur = new Image("file:images/murs.png");
        this.imageSol = new Image("file:images/sol.png");
        this.imageSortie = new Image("file:images/sortie.png");
        this.imagePrisonnier = new Image("file:images/prisonnier.png");
        this.imageGardien = new Image("file:images/gardien.png");
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

        //Ajout filtre couleur bayes
        Joueur prisonnier = (Joueur) simulation.getPrisonnier();
        double[][] carteBayes = prisonnier.getCarteBayesien();
        caseBayesienne = new Rectangle[carteBayes.length][carteBayes[0].length];

        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setX(j * TAILLE_CELLULE);
                rectangle.setY(i * TAILLE_CELLULE);
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    System.out.println(Color.rgb(255, 45, 0, 1 * carteBayes[i][j]));
                    rectangle.setFill(Color.rgb(190, 35, 0, 15 * carteBayes[i][j]));
                }
                this.getChildren().add(rectangle);
                caseBayesienne[i][j] = rectangle;

            }
        }

        // Initialisation des personnages
        prisonnierView = new ImageView(imagePrisonnier);
        prisonnierView.setFitWidth(TAILLE_CELLULE);
        prisonnierView.setFitHeight(TAILLE_CELLULE);

        this.getChildren().add(prisonnierView);


        gardienView = new ImageView(imageGardien);
        gardienView.setFitWidth(TAILLE_CELLULE);
        gardienView.setFitHeight(TAILLE_CELLULE);

        this.getChildren().add(gardienView);


        // Placement initial des personnages
        updatePositions();
    }

    /**
     * Met à jour uniquement les positions des personnages
     */
    private void updatePositions() {

        // Met à jour la position du prisonnier
        prisonnierView.setX(simulation.getPrisonnier().getPosition().getX() * TAILLE_CELLULE);
        prisonnierView.setY(simulation.getPrisonnier().getPosition().getY() * TAILLE_CELLULE);

        // Met à jour la position du gardien
        gardienView.setX(simulation.getGardien().getPosition().getX() * TAILLE_CELLULE);
        gardienView.setY(simulation.getGardien().getPosition().getY() * TAILLE_CELLULE);
    }


    private void updateBayes() {

        Joueur prisonnier = (Joueur) simulation.getPrisonnier();
        double[][] carteBayes = prisonnier.getCarteBayesien();
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = caseBayesienne[i][j];
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    System.out.println(Color.rgb(255, 45, 0, 1 * carteBayes[i][j]));
                    rectangle.setFill(Color.rgb(190, 35, 0, 15 * carteBayes[i][j]));
                }
            }
        }
    }

    @Override
    public void update(Jeu jeu) {
        // Récuperation de la simulation
        this.simulation = (Simulation) jeu;

        if (this.getChildren().isEmpty()) {

            // Si le labyrinthe n'est pas encore initialisé
            initImages();
            initLabyrinthe();
        } else {
            // Sinon, il met juste a jour les positions des personnages
            updatePositions();

            updateBayes();
        }
    }

}
