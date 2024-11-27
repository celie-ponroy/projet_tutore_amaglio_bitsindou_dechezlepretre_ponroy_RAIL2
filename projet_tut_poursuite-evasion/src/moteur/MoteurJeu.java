package moteur;

//https://github.com/zarandok/megabounce/blob/master/MainCanvas.java

import affichage.DessinJeu;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;


// copied from: https://gist.github.com/james-d/8327842
// and modified to use canvas drawing instead of shapes

public class MoteurJeu extends Application {

    /**
     * gestion du temps : nombre de frame par secondes et temps par iteration
     */
    private static double FPS = 100;
    private static double dureeFPS = 1000 / (FPS + 1);

    /**
     * taille par defaut
     */
    private static double WIDTH = 1000;
    private static double HEIGHT = 600;

    /**
     * statistiques sur les frames
     */
    private final FrameStats frameStats = new FrameStats();

    /**
     * jeu en Cours et renderer du jeu
     */
    private static Jeu jeu = null;
    private static DessinJeu dessin = null;

    /**
     * touches appuyee entre deux frame
     */
    Clavier controle = new Clavier();


    /**
     * lancement d'un jeu
     *
     * @param jeu    jeu a lancer
     * @param dessin dessin du jeu
     */
    public static void launch(Jeu jeu, DessinJeu dessin) {
        // le jeu en cours et son afficheur
        MoteurJeu.jeu = jeu;
        MoteurJeu.dessin = dessin;

        // si le jeu existe, on lance le moteur de jeu
        if (jeu != null)
            launch();
    }

    /**
     * frame par secondes
     *
     * @param FPSSouhaitees nombre de frames par secondes souhaitees
     */
    public static void setFPS(int FPSSouhaitees) {
        FPS = FPSSouhaitees;
        dureeFPS = 1000 / (FPS + 1);
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
        final Pane pane = new Pane();
        final BorderPane root = new BorderPane();
        root.setCenter(pane);

        // Affichage des statistiques
        final Label stats = new Label();
        stats.textProperty().bind(frameStats.textProperty());
        root.setBottom(stats);

        // Création de la scène
        final Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Moteur de Jeu");
        primaryStage.show();

        // Gestion du clavier
        scene.setOnKeyPressed(event -> controle.appuyerTouche(event));
        scene.setOnKeyReleased(event -> controle.relacherTouche(event));

        // Gestion de la souris (réinitialisation du jeu au double-clic)
        pane.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                jeu.init();
            }
        });

        // Lancer la boucle de jeu
        startAnimation(pane);
    }

    private void startAnimation(final Pane pane) {
        final LongProperty lastUpdateTime = new SimpleLongProperty(0);

        final AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (jeu.etreFini()) {
                    this.stop();
                    return;
                }

                if (lastUpdateTime.get() == 0) {
                    lastUpdateTime.set(timestamp);
                }

                long elapsedTime = timestamp - lastUpdateTime.get();
                double dureeEnMilliSecondes = elapsedTime / 1_000_000.0;

                if (dureeEnMilliSecondes > dureeFPS) {
                    jeu.update(dureeEnMilliSecondes / 1_000., controle);
                    dessin.dessinerJeu(jeu, pane);
                    frameStats.addFrame(elapsedTime);
                    lastUpdateTime.set(timestamp);
                }
            }
        };

        timer.start();
    }


}