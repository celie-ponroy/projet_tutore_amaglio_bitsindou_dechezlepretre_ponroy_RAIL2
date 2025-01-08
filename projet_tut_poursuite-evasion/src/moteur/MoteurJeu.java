package moteur;

import affichage.VueMenus;
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
import simulation.Comportements;
import simulation.Simulation;


public class MoteurJeu extends Application {

    /**
     * Taille par defaut de la fenetre
     */
    private static double WIDTH = 1540;
    private static double HEIGHT = 1200;

    /**
     * Jeu en Cours et renderer du jeu
     */
    public static Jeu jeu = null;


    /**
     * Creation de l'application de jeu
     */
    public void start(Stage primaryStage) {
        VueMenus vueMenus = new VueMenus((MoteurJeu) jeu);
        vueMenus.afficherMenuPrincipal();
    }
}