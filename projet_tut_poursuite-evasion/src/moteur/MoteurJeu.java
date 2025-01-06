package moteur;

import affichage.VuePrincipale;
import affichage.VuePrincipaleNonInteractive;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import simulation.Simulation;



public class MoteurJeu extends Application {

    /**
     * taille par defaut de la fenetre
     */
    private static double WIDTH = 1540;
    private static double HEIGHT = 1200;

    /**
     * jeu en Cours et renderer du jeu
     */
    private static Jeu jeu = null;


    /**
     * creation de l'application avec juste un canvas et des statistiques
     */
    public void start(Stage primaryStage) {
        final VBox root = new VBox();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        final HBox rootAnalyse = new HBox();
        final Scene sceneAnalyse = new Scene(rootAnalyse, WIDTH, HEIGHT);

        root.setStyle("-fx-background-color: #d3d3d3;");
        root.setSpacing(20);
        root.setPadding(new Insets(10)); // Ajout d'un padding
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Veuillez choisir un mode:");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button modeInteractif = new Button("Mode interactif");
        modeInteractif.setPrefSize(200, 100);
        modeInteractif.setOnAction(e -> {
            VBox root2 = new VBox();
            // Création de la scène
            final Scene scene2 = new Scene(root2, WIDTH, HEIGHT);
            root2.setStyle("-fx-background-color: #d3d3d3;"); // Fond gris
            root2.setSpacing(20); // Espacement entre les éléments
            root2.setPrefSize(800, 600);
            root2.setAlignment(Pos.CENTER); // Centre tous les éléments du VBox

            // Titre du menu
            Label title2 = new Label("Veuillez choisir un personnage:");
            title2.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            // Conteneur pour les boutons de mode
            HBox buttonBox2 = new HBox();
            buttonBox2.setSpacing(20); // Espacement entre les boutons
            buttonBox2.setAlignment(Pos.CENTER); // Centre les boutons horizontalement

            // Bouton "Prisonnier"
            Button persoPrisonnier = new Button("Prisonnier");
            persoPrisonnier.setPrefSize(200, 100);
            persoPrisonnier.setOnAction(f -> {
                Simulation simulation = new Simulation(true);
                MoteurJeu.jeu = simulation;
                //Affichage du jeu
                VuePrincipale vp = new VuePrincipale();
                vp.update(MoteurJeu.jeu);
                MoteurJeu.jeu.ajouterObservateur(vp);
                root2.getChildren().clear();
                root2.getChildren().add(vp);
                Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
                scene2.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
            });

            // Bouton "Gardien"
            Button persoGardien = new Button("Gardien");
            persoGardien.setPrefSize(200, 100);
            persoGardien.setOnAction(f -> {
                Jeu simulation = new Simulation(false);
                MoteurJeu.jeu = simulation;
                //Affichage du jeu
                VuePrincipale vp = new VuePrincipale();
                vp.update(MoteurJeu.jeu);
                MoteurJeu.jeu.ajouterObservateur(vp);
                root2.getChildren().clear();
                root2.getChildren().add(vp);
                Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
                scene2.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
            });

            // Ajout des boutons dans la HBox
            buttonBox2.getChildren().addAll(persoPrisonnier, persoGardien);

            // Ajout des éléments au VBox principal
            root2.getChildren().addAll(title2, buttonBox2);

            // Création et affichage de la scène
            primaryStage.setScene(scene2);
            primaryStage.setTitle("Simulation");
            primaryStage.show();


        });

        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {

            Simulation simulation = new Simulation();
            MoteurJeu.jeu = simulation;
            // Création de la vue principale
            VuePrincipaleNonInteractive vb = new VuePrincipaleNonInteractive();
            vb.update(MoteurJeu.jeu);

            MoteurJeu.jeu.ajouterObservateur(vb);

            rootAnalyse.getChildren().clear();
            rootAnalyse.getChildren().add(vb);

            primaryStage.setScene(sceneAnalyse);
        });

        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif);

        Button quitter = new Button("Quitter");
        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(title, buttonBox, quitter);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();
    }
}