package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import simulation.Simulation;
import simulation.personnages.Position;

public abstract class VueSimulation extends Pane {
    protected Image imageMur;
    protected Image imageSol;
    protected Image imageSortie;
    protected Image imagePrisonnier;
    protected Image imageGardien;// Pane principal pour afficher le jeu
    protected ImageView prisonnierView; // Vue pour le prisonnier
    protected ImageView gardienView; // Vue pour le gardien
    protected int TAILLE_CELLULE = 30; // Taille des cases du labyrinthe


    VueSimulation(){
        this.imageMur = new Image("file:images/murs.png");
        this.imageSol = new Image("file:images/sol.png");
        this.imageSortie = new Image("file:images/sortie.png");
        this.imagePrisonnier = new Image("file:images/prisonnier.png");
        this.imageGardien = new Image("file:images/gardien.png");
    }
    protected void initLabyrinthe() {
        // Création du labyrinthe à partir de la carte
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                StackPane stackPane = new StackPane();
                stackPane.setLayoutX(j * TAILLE_CELLULE);
                stackPane.setLayoutY(i * TAILLE_CELLULE);

                // Sélection de l'image en fonction de la case
                Image image = null;
                switch (Simulation.CARTE[i][j]) {
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

                // Ajout d'une zone de collision invisible
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.TRANSPARENT);
                stackPane.getChildren().add(rectangle);

                this.getChildren().add(stackPane); // Ajout au Pane principal

            }
        }

        // Initialisation des personnages
        //prisonnier
        prisonnierView = new ImageView(imagePrisonnier);
        prisonnierView.setFitWidth(TAILLE_CELLULE); // Taille de l'image
        prisonnierView.setFitHeight(TAILLE_CELLULE); // Taille de l'image

        //gardien
        gardienView = new ImageView(imageGardien);
        gardienView.setFitWidth(TAILLE_CELLULE); // Taille de l'image
        gardienView.setFitHeight(TAILLE_CELLULE); // Taille de l'image

        setOpacityPersonnage();
        this.getChildren().addAll(prisonnierView, gardienView);


        // Placement initial des personnages
        updatePositions();

    }

    /**
     * Methode qui change l'opacité des personnages selon certains criteres
     */
    protected abstract void setOpacityPersonnage();
    /**
     * Met à jour uniquement les positions des personnages
     */
    protected abstract void updatePositions();

    /**
     * Met à jour l'image en fonction de la position
     * @param p
     * @param im
     */
    protected void setPositions(Position p, ImageView im) {
        im.setX(p.getX() * TAILLE_CELLULE);
        im.setY(p.getY() * TAILLE_CELLULE);
    }
}
