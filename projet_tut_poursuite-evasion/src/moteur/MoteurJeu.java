package moteur;

//https://github.com/zarandok/megabounce/blob/master/MainCanvas.java

import affichage.VueMenu;
import affichage.VueBayesienne;
import affichage.VuePrincipale;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import simulation.Simulation;



// copied from: https://gist.github.com/james-d/8327842
// and modified to use canvas drawing instead of shapes

public class MoteurJeu extends Application {

    /**
     * taille par defaut
     */
    private static double WIDTH = 1000;
    private static double HEIGHT = 600;

    /**
     * jeu en Cours et renderer du jeu
     */
    private static Jeu jeu = null;


    /**
     * lancement d'un jeu
     *
     */
    public static void launch() {
        // le jeu en cours et son afficheur
        //MoteurJeu.jeu = jeu;

        // si le jeu existe, on lance le moteur de jeu
        if (jeu != null)
            launch();
    }

    public static void setTaille(double width, double height) {
        WIDTH = width;
        HEIGHT = height;
    }


    /**
     * creation de l'application avec juste un canvas et des statistiques
     */
    public void start(Stage primaryStage) {
        // Initialisation du Pane et du conteneur principal
        final VBox root = new VBox();

        // Création de la scène
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        //VBox root = new VBox();
        root.setStyle("-fx-background-color: #d3d3d3;"); // Fond gris
        root.setSpacing(20); // Espacement entre les éléments
        //root.setPrefSize(800, 600);
        root.setAlignment(Pos.CENTER); // Centre tous les éléments du VBox

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
            Jeu simulation = new Simulation(true);
            MoteurJeu.jeu = simulation;
            VuePrincipale vp = new VuePrincipale();
            vp.update(MoteurJeu.jeu);
            MoteurJeu.jeu.ajouterObservateur(vp);
            root.getChildren().clear();
            root.getChildren().add(vp);
            Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);

        });

        // Bouton "Mode non interactif"
        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {
            Jeu simulation = new Simulation(false);
            MoteurJeu.jeu = simulation;
            VueBayesienne vb = new VueBayesienne();
            vb.update(MoteurJeu.jeu);
            MoteurJeu.jeu.ajouterObservateur(vb);
            root.getChildren().clear();
            root.getChildren().add(vb);
            MoteurJeu.jeu.ajouterObservateur(vb);
            ClavierNonInteractif clavier = new ClavierNonInteractif((Simulation) MoteurJeu.jeu);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);

        });

        // Ajout des boutons dans la HBox
        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif);

        // Bouton "Quitter"
        Button quitter = new Button("Quitter");
        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> primaryStage.close());

        // Ajout des éléments au VBox principal
        root.getChildren().addAll(title, buttonBox, quitter);

        //root.setCenter(vp);

        //Création du controleur

        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();
    }
}