package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import simulation.CaseEnum;
import simulation.Simulation;
import simulation.personnages.Position;

public abstract class VueSimulation extends Pane {
    protected Image imageMur;
    protected Image imageSol;
    protected Image imageSortie;
    protected Image imagePrisonnier;
    protected Image imageGardien;// Pane principal pour afficher le jeu
    protected Image imageRaccourciGardien;
    protected ImageView prisonnierView; // Vue pour le prisonnier
    protected ImageView gardienView; // Vue pour le gardien
    protected int TAILLE_CELLULE = 25;
    private Pane labyrinthePane;// Taille des cases du labyrinthe


    VueSimulation() {
        this.imageMur = new Image("file:images/murs.png");
        this.imageSol = new Image("file:images/sol.png");
        this.imageSortie = new Image("file:images/sortie.png");
        this.imagePrisonnier = new Image("file:images/prisonnier.png");
        this.imageGardien = new Image("file:images/gardien.png");
        this.imageRaccourciGardien = new Image("file:images/raccourciGardien.png");
    }

    /**
     * Initialise le labyrinthe et les personnages
     */

    protected Pane initLabyrinthe(boolean withPerso) {
        // Création d'un conteneur pour le labyrinthe
        labyrinthePane = new Pane();
        labyrinthePane.setPrefSize(TAILLE_CELLULE * Simulation.CARTE[0].length,
                TAILLE_CELLULE * Simulation.CARTE.length);
        // Création du labyrinthe à partir de la carte
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                StackPane stackPane = new StackPane();
                stackPane.setLayoutX(j * TAILLE_CELLULE);
                stackPane.setLayoutY(i * TAILLE_CELLULE);

                // Sélection de l'image en fonction de la case
                Image image = null;
                if (Simulation.CARTE[i][j] == CaseEnum.MUR.ordinal()) {
                    image = this.imageMur;
                } else if (Simulation.CARTE[i][j] == CaseEnum.SOL.ordinal() ||
                        Simulation.CARTE[i][j] == CaseEnum.SPAWN_GARDIEN.ordinal() ||
                        Simulation.CARTE[i][j] == CaseEnum.SPAWN_PRISONNIER.ordinal()) {
                    image = this.imageSol;
                } else if (Simulation.CARTE[i][j] == CaseEnum.SORTIE.ordinal()) {
                    image = this.imageSortie;
                } else if (Simulation.CARTE[i][j] == CaseEnum.RACCOURCI_GARDIEN.ordinal()) {
                    image = this.imageRaccourciGardien;

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

                labyrinthePane.getChildren().add(stackPane); // Ajout au conteneur labyrinthe
            }
        }

        if (withPerso) {
            // Initialisation des personnages
            prisonnierView = new ImageView(imagePrisonnier);
            prisonnierView.setFitWidth(TAILLE_CELLULE);
            prisonnierView.setFitHeight(TAILLE_CELLULE);

            gardienView = new ImageView(imageGardien);
            gardienView.setFitWidth(TAILLE_CELLULE);
            gardienView.setFitHeight(TAILLE_CELLULE);

            setOpacityPersonnage();

            // Ajouter les personnages au conteneur labyrinthe
            labyrinthePane.getChildren().addAll(prisonnierView, gardienView);

            // Placement initial des personnages
            updatePositions();
        }

        return labyrinthePane;
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
     *
     * @param p
     * @param im
     */
    protected void setPositions(Position p, ImageView im) {
        im.setX(p.getX() * TAILLE_CELLULE);
        im.setY(p.getY() * TAILLE_CELLULE);
    }
}
