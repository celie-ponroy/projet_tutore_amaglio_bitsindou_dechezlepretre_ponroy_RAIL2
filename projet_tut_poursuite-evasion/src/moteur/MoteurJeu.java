package moteur;

import affichage.PageAccueil;
import affichage.VueMenus;
import javafx.application.Application;
import javafx.stage.Stage;
import lancercalculs.LancerCalculs;

import java.io.IOException;
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

        try {
            LancerCalculs.initSansDS();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}