package moteur;

import affichage.VueMenus;
import javafx.application.Application;
import javafx.stage.Stage;

public class MoteurJeu extends Application {

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