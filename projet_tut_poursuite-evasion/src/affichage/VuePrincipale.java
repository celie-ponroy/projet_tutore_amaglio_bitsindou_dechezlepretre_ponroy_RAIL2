package affichage;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import javafx.scene.control.Label;
import org.w3c.dom.Text;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.awt.*;
import java.util.Arrays;

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
    private int tour;
    private Rectangle[][] caseBayesienneHisto;



    private static final int TAILLE_CELLULE = 50; // Taille des cases du labyrinthe
    private boolean afficherVision;

    //constructeur
    public VuePrincipale(boolean afficherVision){
        this.afficherVision = afficherVision;
    }
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
        //prisonnier
        prisonnierView = new ImageView(imagePrisonnier);
        prisonnierView.setFitWidth(TAILLE_CELLULE); // Taille de l'image
        prisonnierView.setFitHeight(TAILLE_CELLULE); // Taille de l'image

        //gardien
        gardienView = new ImageView(imageGardien);
        gardienView.setFitWidth(TAILLE_CELLULE); // Taille de l'image
        gardienView.setFitHeight(TAILLE_CELLULE); // Taille de l'image

        if (afficherVision){
            //variable pour savoir si le joueur a choisi le personnage prisonnier ou gardien
            Personnage p1 = simulation.getGardien();
            Personnage p2 = simulation.getPrisonnier();
            ImageView imageP2 = prisonnierView;

            if(simulation.getJoueur().equals(simulation.getPrisonnier())) {//si le joueur choisit le personnage prisonnier
                p1 = simulation.getPrisonnier();
                p2 = simulation.getGardien();
                imageP2 = gardienView;
            }

            //si le joueur choisit le personnage prsionnnier, on cache le gardien du champ de vision

            //si le gardient est sur des cases non visibles par le prisonnier on ne l'affiche pas sinon on l'affiche
            if (p1.getVision().contains(p2.getPosition())) {
                imageP2.setOpacity(1);
            } else {
                imageP2.setOpacity(0);
            }
        }else {
            gardienView.setOpacity(1);
            prisonnierView.setOpacity(1);
        }
        this.getChildren().addAll(prisonnierView, gardienView);


        // Placement initial des personnages
        updatePositions();

    }

    /**
     * Met à jour uniquement les positions des personnages
     */
    private void updatePositions() {
        // Met à jour la position du prisonnier
        setPositions(simulation.getPrisonnier().getPosition(), prisonnierView);
        setPositions(simulation.getGardien().getPosition(), gardienView);
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
            if (this.afficherVision){
                initFiltreVision();
            }
        } else {
            // Sinon, il met juste a jour les positions des personnages
            updatePositions();
            updateIteration();
            if (this.afficherVision){
                setFiltreVision();
                //variable pour savoir si le joueur a choisi le personnage prisonnier ou gardien
                Personnage p1 = simulation.getGardien();
                Personnage p2 = simulation.getPrisonnier();
                ImageView imageP2 = prisonnierView;

                if(simulation.getJoueur().equals(simulation.getPrisonnier())) {//si le joueur choisit le personnage prisonnier
                    p1 = simulation.getPrisonnier();
                    p2 = simulation.getGardien();
                    imageP2 = gardienView;
                }

                //si le joueur choisit le personnage prsionnnier, on cache le gardien du champ de vision

                //si le gardient est sur des cases non visibles par le prisonnier on ne l'affiche pas sinon on l'affiche
                if (p1.getVision().contains(p2.getPosition())) {
                    imageP2.setOpacity(1);
                } else {
                    imageP2.setOpacity(0);
                }
            }else{
                gardienView.setOpacity(1);
                prisonnierView.setOpacity(1);
            }

        }
        if(simulation.etreFini()&& this.afficherVision){
            historique();
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
     * Méthode pour initialiser un filtre sur les cases non visibles
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
    /**
     * Methode pour historique à la fin du jeu en mode interactif
     */
    public void historique(){
        tour=0;
        //initaliser la map (enlever la vision)
        Arrays.stream(filtreVision).forEach(rectangles -> Arrays.stream(rectangles).forEach(rectangle -> rectangle.setOpacity(0)));
        //on mets la première carte bayesienne
        caseBayesienneHisto = FiltreBayesien.initFiltre(simulation.historiqueBayesien.get(0),TAILLE_CELLULE);
        for (Rectangle[] rect : caseBayesienneHisto) {
            for (Rectangle sousrect : rect) {
                this.getChildren().add(sousrect);
            }
        }
        //on mets les perso à l'emplacement ini
        Position pPrisonnier = simulation.historiquePosition.get(0).get(0);
        Position pGardien= simulation.historiquePosition.get(0).get(1);
        setPositions(pPrisonnier,prisonnierView);
        prisonnierView.setOpacity(1);
        setPositions(pGardien,gardienView);
        gardienView.setOpacity(1);


        //ajout boutons pour precedent et suivant
        javafx.scene.control.Button precedent = new Button("Précédent");
        precedent.setPrefSize(200, 75);
        precedent.setOnAction(e -> {
            tour-=1;
            updateHistorique();
            });
        javafx.scene.control.Button suivant = new Button("Suivant");
        suivant.setPrefSize(200, 75);
        suivant.setOnAction(e -> {
            tour+=1;
            updateHistorique();
        });
        //ajout des boutons
        HBox hboxBouttons = new HBox();
        hboxBouttons.setLayoutX(10);
        hboxBouttons.setLayoutY(620);
        hboxBouttons.getChildren().add(precedent);
        hboxBouttons.getChildren().add(suivant);
        this.getChildren().add(hboxBouttons);



    }
    /**
     * Update de l'historique (par rapport à un tour)
     */
    public void updateHistorique(){
        //on mets a jour position et bayes
        if(tour<0){
            tour=0;
        }
        if(tour>=simulation.historiquePosition.size()){
            tour=simulation.historiquePosition.size()-1;
        }
        //on mets a jour la carte bayesienne
        FiltreBayesien.updateBayes(caseBayesienneHisto,simulation.historiqueBayesien.get(tour));

        setPositions(simulation.historiquePosition.get(tour).get(0),prisonnierView);
        setPositions(simulation.historiquePosition.get(tour).get(1),gardienView);

        //si negatif ou out of bound
    }
    /**
     * Methode set positions imagewiew
     */
    public void setPositions(Position p, ImageView im) {
        im.setX(p.getX() * TAILLE_CELLULE);
        im.setY(p.getY() * TAILLE_CELLULE);
    }

}
