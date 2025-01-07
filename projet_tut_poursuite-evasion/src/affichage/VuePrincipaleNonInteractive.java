package affichage;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import moteur.Jeu;
import simulation.Simulation;
import simulation.personnages.Position;

public class VuePrincipaleNonInteractive extends VueSimulation implements DessinJeu {
    private Simulation simulation;

    private Label iterationLabel; // Label pour afficher le nombre d'itération
    private int tour;
    private Rectangle[][] caseBayesienneHisto;
    private static final int TAILLE_CELLULE = 30; // Taille des cases du labyrinthe
    private VueBayesienne vB1,vB2;


    //constructeur
    public VuePrincipaleNonInteractive(){
        super();
        this.tour = 0;
    }


    /**
     * Initialise le labyrinthe et les personnages
     */
    private void init() {
        initLabyrinthe();

        //Ajout d'une vbox pour afficher le nombre d'itération sous le labyrinthe
        VBox vbox = new VBox();
        vbox.setLayoutX(10);
        vbox.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+100);
        this.iterationLabel = new Label("Nombre d'itération: " + simulation.getNbTours());
        vbox.getChildren().add(this.iterationLabel);
        this.getChildren().add(vbox);

        //Ajout d'un encadré pour afficher le nombre d'itération
        Rectangle rectangle = new Rectangle(150, 20);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.DARKGREY);
        rectangle.setLayoutX(10);
        rectangle.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+100);
        this.getChildren().add(rectangle);

        //vues d'en bas bayesien
        vB1 = new VueBayesienne(this.simulation,simulation.getPrisonnier());
        vB2 = new VueBayesienne(this.simulation,simulation.getGardien());
        HBox hBox = new HBox(vB1,vB2);
        hBox.setSpacing(20);
        hBox.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+200);
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(vB1, Priority.ALWAYS);
        HBox.setHgrow(vB2, Priority.ALWAYS);
        this.getChildren().add(hBox);
    }

    @Override
    protected void setOpacityPersonnage() {
        prisonnierView.setOpacity(1);
        gardienView.setOpacity(1);
    }

    /**
     * Met à jour uniquement les positions des personnages
     */
    protected void updatePositions() {
        // Met à jour la position du prisonnier
        Position p = simulation.historiquePosition.get(simulation.getPrisonnier()).get(tour);
        Position g = simulation.historiquePosition.get(simulation.getGardien()).get(tour);
        setPositions(p, prisonnierView);
        setPositions(g, gardienView);
    }

    /**
     * Méthode principale de l'interface DessinJeu
     */
    @Override
    public void update(Jeu jeu) {
        // Récuperation de la simulation
        this.simulation = (Simulation)jeu;
        if(simulation.etreFini()){
            init();
            javafx.scene.control.Button precedent = new Button("Précédent");
            precedent.setPrefSize(200, 75);
            precedent.setOnAction(e -> {
                if (tour > 0) {
                    tour -= 1;
                    updatePositions();
                    updateIteration();
                    vB1.update(tour);
                    vB2.update(tour);
                }
            });

            javafx.scene.control.Button suivant = new Button("Suivant");
            suivant.setPrefSize(200, 75);
            suivant.setOnAction(e -> {
                if (tour < simulation.getNbTours() - 1) {
                    tour += 1;
                    updatePositions();
                    updateIteration();
                    vB1.update(tour);
                    vB2.update(tour);
                }
            });

            javafx.scene.control.Button retourMenuBtn = new Button("Revenir au menu principal");
            retourMenuBtn.setPrefSize(200, 75);
            retourMenuBtn.setOnAction(e -> {
                //Ferme la fenetre actuelle
                Stage stage = (Stage) retourMenuBtn.getScene().getWindow();
                stage.close();
                //retour au menu principal
                VueMenus vm = new VueMenus();
                vm.afficherMenuPrincipal();
            });

            //ajout des boutons
            HBox hboxBouttons = new HBox();
            hboxBouttons.setLayoutX(10);
            hboxBouttons.setLayoutY(TAILLE_CELLULE*simulation.CARTE.length);
            hboxBouttons.getChildren().add(precedent);
            hboxBouttons.getChildren().add(suivant);
            hboxBouttons.getChildren().add(retourMenuBtn);
            this.getChildren().add(hboxBouttons);
        }
    }

    /**
     * Méthode pour récupérer afficher le nombre d'itération
     */
    public void updateIteration() {
        // Mise à jour du texte du label
        this.iterationLabel.setText("Tour: " + tour);
    }

    /**
     * Methode set positions imagewiew
     */
    public void setPositions(Position p, ImageView im) {
        im.setX(p.getX() * TAILLE_CELLULE);
        im.setY(p.getY() * TAILLE_CELLULE);
    }

}
