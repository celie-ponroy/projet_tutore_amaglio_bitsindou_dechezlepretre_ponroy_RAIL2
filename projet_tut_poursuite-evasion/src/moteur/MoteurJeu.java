package moteur;

import affichage.PageAccueil;
import affichage.VueMenus;
import javafx.application.Application;
import javafx.stage.Stage;
import musique.SoundManager;

import static musique.SoundManager.playFondMusic;

public class MoteurJeu extends Application {

    /**
     * Jeu en Cours et renderer du jeu
     */
    public static Jeu jeu = null;


    /**
     * Creation de l'application de jeu
     */
    public void start(Stage primaryStage) {
        PageAccueil.lancerPageAcceuil((MoteurJeu) jeu, primaryStage);
        SoundManager soundManager = new SoundManager();
        playFondMusic();

    }
}