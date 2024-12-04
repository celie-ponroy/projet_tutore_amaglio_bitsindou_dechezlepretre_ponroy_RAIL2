package affichage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Simulation;

/**
 * Classe qui affiche le menu principal
 */
public class VueMenu extends VBox {

    public VueMenu(){
        super();
    }

    /**
     * Affiche le menu principal de l'application
     * @param stage scene de l'application
     */
    public void afficherMenu(){
        // Conteneur principal
        this.setStyle("-fx-background-color: #d3d3d3;"); // Fond gris
        this.setSpacing(20); // Espacement entre les éléments
        this.setPrefSize(800, 600);
        this.setAlignment(Pos.CENTER); // Centre tous les éléments du VBox

        // Titre du menu
        Label title = new Label("Veuillez choisir un mode:");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Conteneur pour les boutons de mode
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20); // Espacement entre les boutons
        buttonBox.setAlignment(Pos.CENTER); // Centre les boutons horizontalement

        // Bouton "Mode interactif"
        Button modeInteractif = new Button("Mode interactif");
        modeInteractif.setPrefSize(200, 100);
        modeInteractif.setOnAction(e -> {
            Platform.exit();
            // TODO: Afficher le jeu
            int width = 700;
            int height = 700;
            // creation des objets
            Jeu simulation = new Simulation();
            simulation.init();
            // parametrage du moteur de jeu
            MoteurJeu.setTaille(width, height);
            // lancement du jeu
            MoteurJeu.launch(simulation);
        });

        // Bouton "Mode non interactif"
        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {
            // TODO: Afficher le jeu
        });

        // Ajout des boutons dans la HBox
        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif);

        // Bouton "Quitter"
        Button quitter = new Button("Quitter");
        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> Platform.exit());

        // Ajout des éléments au VBox principal
        this.getChildren().addAll(title, buttonBox, quitter);

        // Création et affichage de la scène
//        Scene scene = new Scene(this);
//        stage.setTitle("Menu principal");
//        stage.setScene(scene);
//        stage.show();
    }
}
