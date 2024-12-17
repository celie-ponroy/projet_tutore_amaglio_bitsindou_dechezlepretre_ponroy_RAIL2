package moteur;

//https://github.com/zarandok/megabounce/blob/master/MainCanvas.java

import affichage.*;
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
import simulation.personnages.Agent;
import simulation.personnages.Personnage;


// copied from: https://gist.github.com/james-d/8327842
// and modified to use canvas drawing instead of shapes

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
     * lancement d'un jeu
     *
     * @param jeu    jeu a lancer
     */
    public static void launch(Jeu jeu) {
        // le jeu en cours et son afficheur
        MoteurJeu.jeu = jeu;

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
            VuePrincipale vp = new VuePrincipale(true);
            vp.update(MoteurJeu.jeu);
            MoteurJeu.jeu.ajouterObservateur(vp);

            root.getChildren().clear();
            VBox.setVgrow(vp, Priority.ALWAYS); // Ajustement automatique de la vue
            root.getChildren().add(vp);

            Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
        });

        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {
            //Personnage prisonnier = new Agent(4, 10);
            //Personnage gardien = new Agent(5, 4);
            VuePrincipale vb = new VuePrincipale(false);
            VueAnalyse va1 = new VueAnalyse((Simulation) jeu,((Simulation) jeu).getPrisonnier());
            VueAnalyse va2 = new VueAnalyse((Simulation) jeu,((Simulation) jeu).getGardien());

            vb.update(MoteurJeu.jeu);
            va1.update(MoteurJeu.jeu);
            va2.update(MoteurJeu.jeu);

            MoteurJeu.jeu.ajouterObservateur(vb);
            MoteurJeu.jeu.ajouterObservateur(va1);
            MoteurJeu.jeu.ajouterObservateur(va2);

            HBox hboxBayesienne = new HBox(vb);
            hboxBayesienne.setAlignment(Pos.CENTER);
            HBox.setHgrow(vb, Priority.ALWAYS);

            HBox hboxAnalyse = new HBox(va1, va2);
            hboxAnalyse.setSpacing(20); // Réduction de l'espacement excessif
            hboxAnalyse.setAlignment(Pos.CENTER);
            HBox.setHgrow(va1, Priority.ALWAYS);
            HBox.setHgrow(va2, Priority.ALWAYS);

            VBox vbox = new VBox(hboxBayesienne, hboxAnalyse);
            vbox.setSpacing(20);
            VBox.setVgrow(hboxBayesienne, Priority.ALWAYS);
            VBox.setVgrow(hboxAnalyse, Priority.ALWAYS);

            rootAnalyse.getChildren().clear();
            rootAnalyse.getChildren().add(vbox);

            Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
            sceneAnalyse.addEventHandler(KeyEvent.KEY_PRESSED, clavier);

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