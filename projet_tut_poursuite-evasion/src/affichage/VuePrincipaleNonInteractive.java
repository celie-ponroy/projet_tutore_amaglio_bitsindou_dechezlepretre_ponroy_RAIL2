package affichage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import moteur.Jeu;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

public class VuePrincipaleNonInteractive extends VueSimulation implements DessinJeu {

    private Label iterationLabel; // Label pour afficher le nombre d'itération
    private int tour;
    private VueBayesienne vB1,vB2;


    //constructeur
    public VuePrincipaleNonInteractive(double width, double height) {
        super();
        TAILLE_CELLULE = (int) ((width-6*10)/ (Simulation.CARTE[0].length)*0.33);
        this.tour = 0;
    }

    /**
     * Initialise la vue avec les deux vues bayésiennes et le labyrinthe positionné entre elles.
     */
    private void init() {
        Pane labyPane = initLabyrinthe();

        this.iterationLabel = new Label("Nombre d'itération: " + simulation.getNbTours());
        iterationLabel.setLayoutX(10);
        iterationLabel.setLayoutY(TAILLE_CELLULE*simulation.CARTE.length+TAILLE_CELLULE*1);
        iterationLabel.setStyle("-fx-font-size: 11px;-fx-border-color: black; -fx-padding: 10;");


        // Vérifiez si `vbox` est déjà présent avant de l'ajouter
        if (!this.getChildren().contains(iterationLabel)) {
            this.getChildren().add(iterationLabel);
        }

        //Ajout de la vbox de l'iteration au Pane
        labyPane.getChildren().add(iterationLabel);

        // Création des deux vues bayésiennes dans des VBox distinctes

        //Création des vues bayesiennes
        vB1 = new VueBayesienne(this.simulation,simulation.getPrisonnier(),0,0,TAILLE_CELLULE);
        vB2 = new VueBayesienne(this.simulation,simulation.getGardien(),0,0,TAILLE_CELLULE);

        VBox vBoxGardien = createBayesienneView(simulation.getGardien(), "Vue bayésienne du gardien", vB1);
        VBox vBoxPrisonnier = createBayesienneView(simulation.getPrisonnier(), "Vue bayésienne du prisonnier",vB2);
        // Mise en forme des VBox
        vBoxGardien.setAlignment(Pos.TOP_LEFT);
        vBoxPrisonnier.setAlignment(Pos.TOP_RIGHT);

        //Style
        vBoxGardien.setStyle("-fx-border-color: black; -fx-padding: 10;");
        vBoxPrisonnier.setStyle("-fx-border-color: black; -fx-padding: 10;");

        // HBox pour aligner les éléments (vue bayésienne 1, labyrinthe, vue bayésienne 2)
        HBox hbox = new HBox(20); // Espacement de 20px entre les éléments


        // Ajout des éléments à la HBox
        hbox.getChildren().add(vBoxGardien);
        hbox.getChildren().add(labyPane);
        hbox.getChildren().add(vBoxPrisonnier);
        hbox.setAlignment(Pos.CENTER);

        this.getChildren().clear();
        this.getChildren().addAll(hbox);

    }


    /**
     * Crée une vue Bayesienne associée à un personnage.
     */
    private VBox createBayesienneView(Personnage personnage, String titre, VueBayesienne vueBayesienne) {
        VBox vueBayesienneVBox = new VBox();
        vueBayesienneVBox.setSpacing(10);
        vueBayesienneVBox.setAlignment(Pos.TOP_CENTER);

        // Titre de la vue
        Label titleLabel = new Label(titre);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        vueBayesienneVBox.getChildren().addAll(titleLabel, vueBayesienne);

        return vueBayesienneVBox;
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
        updateDirections(tour);
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
                if (tour < simulation.getNbTours()) {
                    tour += 1;
                    updatePositions();
                    updateIteration();
                    vB1.update(tour);
                    vB2.update(tour);
                }
            });

            javafx.scene.control.Button retourMenuBtn = new Button("Revenir au menu principal");
            retourMenuBtn.setPrefSize(410, 75);
            retourMenuBtn.getStyleClass().add("important");
            retourMenuBtn.setOnAction(e -> {
                //Ferme la fenetre actuelle
                Stage stage = (Stage) retourMenuBtn.getScene().getWindow();
                stage.close();
                //retour au menu principal
                VueMenus vm = new VueMenus();
                vm.afficherMenuPrincipal();
            });
            retourMenuBtn.setLayoutX(TAILLE_CELLULE*simulation.CARTE[0].length+30+TAILLE_CELLULE*2);
            retourMenuBtn.setLayoutY(TAILLE_CELLULE*simulation.CARTE.length+TAILLE_CELLULE*7);

            //ajout des boutons
            HBox hboxBouttons = new HBox();
            hboxBouttons.setLayoutX(TAILLE_CELLULE*simulation.CARTE[0].length+30+TAILLE_CELLULE*2);
            hboxBouttons.setLayoutY(TAILLE_CELLULE*simulation.CARTE.length+TAILLE_CELLULE*3);
            hboxBouttons.setSpacing(10);
            hboxBouttons.getChildren().add(precedent);
            hboxBouttons.getChildren().add(suivant);
            this.getChildren().add(hboxBouttons);
            this.getChildren().add(retourMenuBtn);

        }
    }

    /**
     * Méthode pour récupérer afficher le nombre d'itération
     */
    public void updateIteration() {
        // Mise à jour du texte du label
        this.iterationLabel.setText("Tour: " + tour);
        if(tour == simulation.getNbTours() ){
            this.iterationLabel.setText("Fin");
        }
    }

    /**
     * Methode set positions imagewiew
     */
    public void setPositions(Position p, ImageView im) {
        im.setX(p.getX() * TAILLE_CELLULE);
        im.setY(p.getY() * TAILLE_CELLULE);
    }

}
