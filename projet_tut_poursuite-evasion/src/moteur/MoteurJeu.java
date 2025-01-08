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
//        setTaille((int) Screen.getPrimary().getBounds().getWidth(), (int) Screen.getPrimary().getBounds().getHeight());
//
//        final VBox root = new VBox();
//        final Scene scene = new Scene(root, WIDTH, HEIGHT);
//        scene.getStylesheets().add("test.css");
//        scene.setUserAgentStylesheet("test.css");

    }
}