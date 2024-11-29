package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;

public class VueBayesienne implements DessinJeu {
    private Simulation simulation;
    private Image imageMur;
    private Image imageSol;
    private Image imageSortie;
    private Image imagePrisonnier;
    private Image imageGardien;
    private Pane pane; // Pane principal pour afficher le jeu
    private ImageView prisonnierView; // Vue pour le prisonnier
    private ImageView gardienView; // Vue pour le gardien

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

                pane.getChildren().add(stackPane); // Ajout au Pane principal
            }
        }

        // Initialisation des personnages
        prisonnierView = new ImageView(imagePrisonnier);
        prisonnierView.setFitWidth(TAILLE_CELLULE);
        prisonnierView.setFitHeight(TAILLE_CELLULE);
        pane.getChildren().add(prisonnierView);

        gardienView = new ImageView(imageGardien);
        gardienView.setFitWidth(TAILLE_CELLULE);
        gardienView.setFitHeight(TAILLE_CELLULE);
        pane.getChildren().add(gardienView);




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

    /**
     * Méthode principale de l'interface DessinJeu
     */
    @Override
    public void dessinerJeu(Jeu jeu, Pane pane) {
        // Récuperation de la simulation
        this.simulation = (Simulation)jeu;
        this.pane = pane;

        if (pane.getChildren().isEmpty()) {
            // Si le labyrinthe n'est pas encore initialisé
            initImages();
            initLabyrinthe();
        } else {
            // Sinon, il met juste a jour les positions des personnages
            updatePositions();
        }
    }

}
