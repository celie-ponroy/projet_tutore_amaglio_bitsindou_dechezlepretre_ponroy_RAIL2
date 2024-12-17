package affichage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import javafx.scene.control.Label;
import org.w3c.dom.Text;
import simulation.Simulation;
import simulation.personnages.Position;

import java.awt.*;

public class VuePrincipale extends Pane implements DessinJeu {
    private Simulation simulation;
    private Image imageMur;
    private Image imageSol;
    private Image imageSortie;
    private Image imagePrisonnier;
    private Image imageGardien;// Pane principal pour afficher le jeu
    private ImageView prisonnierView; // Vue pour le prisonnier
    private ImageView gardienView; // Vue pour le gardien
    private Label iterationLabel; // Label pour afficher le nombre d'itération
    private Rectangle [][] filtreVision; // Filtre pour cacher les cases non visibles


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

        //Ajout d'une vbox pour afficher le nombre d'itération sous le labyrinthe
        VBox vbox = new VBox();
        vbox.setLayoutX(10);
        vbox.setLayoutY(620);
        this.iterationLabel = new Label("Nombre d'itération: " + simulation.getNbTours());
        vbox.getChildren().add(this.iterationLabel);
        this.getChildren().add(vbox);

        //Ajout d'un encadré pour afficher le nombre d'itération
        Rectangle rectangle = new Rectangle(150, 20);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.DARKGREY);
        rectangle.setLayoutX(10);
        rectangle.setLayoutY(620);
        this.getChildren().add(rectangle);

        // Initialisation des personnages
        prisonnierView = new ImageView(imagePrisonnier);
        prisonnierView.setFitWidth(TAILLE_CELLULE);
        prisonnierView.setFitHeight(TAILLE_CELLULE);
        this.getChildren().add(prisonnierView);
        gardienView = new ImageView(imageGardien);

        //si le joueur choisit le personnage prsionnnier, on cache le gardien du champ de vision
        if (simulation.getJoueur().getNom().equals("prisonnier")) {
            gardienView.setOpacity(0);
        }
        //si le gardien est sur des cases non visibles on ne l'affiche pas sinon on l'affiche
        if (simulation.getPrisonnier().getVision().contains(simulation.getGardien().getPosition())) {
            gardienView.setOpacity(1);
        } else {
            gardienView.setOpacity(0);
        }
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

    /**
     * Méthode principale de l'interface DessinJeu
     */
    @Override
    public void update(Jeu jeu) {
        // Récuperation de la simulation
        this.simulation = (Simulation)jeu;

        if (this.getChildren().isEmpty()) {
            // Si le labyrinthe n'est pas encore initialisé
            initImages();
            initLabyrinthe();
            initFiltreVision();
        } else {
            // Sinon, il met juste a jour les positions des personnages
            updatePositions();
            updateIteration();
            setFiltreVision();
            //si le gardien est sur des cases non visibles on ne l'affiche pas sinon on l'affiche
            if (simulation.getPrisonnier().getVision().contains(simulation.getGardien().getPosition())) {
                gardienView.setOpacity(1);
            } else {
                gardienView.setOpacity(0);
            }
        }

    }

    /**
     * Méthode pour récupérer afficher le nombre d'itération
     */
    public void updateIteration() {
        // Mise à jour du texte du label
        this.iterationLabel.setText("Nombre d'itération: " + simulation.getNbTours());
    }

    /**
     * Méthode pour récupérer afficher le nombre d'itération
     */
    public void initFiltreVision() {
        this.filtreVision = new Rectangle[simulation.CARTE[0].length][simulation.CARTE.length];
        // Parcours de toutes les cases du labyrinthe
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.rgb(44, 88, 245));
                rectangle.setLayoutX(j * TAILLE_CELLULE);
                rectangle.setLayoutY(i * TAILLE_CELLULE);
                this.filtreVision[j][i] = rectangle;
                // Si la case n'est pas visible
                if (!simulation.getJoueur().getVision().contains(new Position(j, i))) {
                    // Création d'un filtre pour cacher la case

                    rectangle.setOpacity(0.5);

                }else{
                    rectangle.setOpacity(0);
                }
                this.getChildren().add(rectangle);
            }
        }
    }
    /**
     * Méthode pour lettre un filtre sur les cases non visibles
     */
    public void setFiltreVision() {
        // Parcours de toutes les cases du labyrinthe
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = this.filtreVision[j][i];
                // Si la case n'est pas visible
                if (!simulation.getJoueur().getVision().contains(new Position(j, i))) {
                    // Création d'un filtre pour cacher la case

                    rectangle.setOpacity(0.5);
                }else{
                    rectangle.setOpacity(0);
                }
            }
        }
    }


}
