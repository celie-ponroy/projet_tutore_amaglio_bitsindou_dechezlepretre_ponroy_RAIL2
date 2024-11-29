package moteur;

//https://github.com/zarandok/megabounce/blob/master/MainCanvas.java

import affichage.VueBayesienne;
import affichage.VuePrincipale;
import javafx.application.Application;
import javafx.scene.Scene;
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
        // Initialisation du Pane et du conteneur principal
        VuePrincipale vp = new VuePrincipale();
        VueBayesienne vb = new VueBayesienne();
        vp.update(MoteurJeu.jeu);
        vb.update(MoteurJeu.jeu);
        MoteurJeu.jeu.ajouterObservateur(vp);
        MoteurJeu.jeu.ajouterObservateur(vb);
        final BorderPane root = new BorderPane();
        root.setCenter(vb);

        //Création du controleur
        Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);

        // Création de la scène
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();
    }
}