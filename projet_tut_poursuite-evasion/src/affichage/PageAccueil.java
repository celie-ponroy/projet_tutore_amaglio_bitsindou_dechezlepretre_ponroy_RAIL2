package affichage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import moteur.ClavierTuto;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.tuto.SimulationTutoriel;

import java.awt.*;

public class PageAccueil {
    private static double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
    private static double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();

    public static void lancerPageAcceuil(MoteurJeu jeu, Stage primaryStage){
//        var primaryStage = new Stage();
        primaryStage.setFullScreen(true);

        final VBox root = new VBox();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        root.getStylesheets().add("style.css");
        root.setStyle("-fx-background-color: #778DA9;");
        root.setSpacing(100);
        root.setAlignment(Pos.CENTER);

        ImageView title = new ImageView("file:images/titre.png");
        title.setFitWidth(WIDTH-300);
        root.getChildren().add(title);
        HBox personnages = new HBox();
        ImageView gardien = new ImageView("file:images/gardien.png");
        gardien.setFitWidth(100);
        gardien.setPreserveRatio(true);
        ImageView prisonnier = new ImageView("file:images/prisonnier.png");
        prisonnier.setFitWidth(100);
        prisonnier.setPreserveRatio(true);
        personnages.getChildren().addAll(gardien, prisonnier);
        personnages.setAlignment(Pos.CENTER);
        personnages.setSpacing(350);

        root.getChildren().add(personnages);
        VBox boutons = new VBox();
        boutons.setAlignment(Pos.CENTER);
        boutons.setSpacing(50);
        Button lancerJeu = new Button("Commencer");
        lancerJeu.getStyleClass().add("gris");
        lancerJeu.setPrefSize(250,100);
        lancerJeu.setOnAction(e->{
            VueMenus vueMenus = new VueMenus(jeu);
            vueMenus.afficherMenuPrincipal();
            primaryStage.close();
            });

        Button tutoriel = new Button("Tutoriel");
        tutoriel.getStyleClass().add("valider");
        tutoriel.setPrefSize(150,50);
        tutoriel.setOnAction(e->{
            SimulationTutoriel simulationTutoriel = new SimulationTutoriel();
            MoteurJeu.jeu = simulationTutoriel;
            afficherTuto(simulationTutoriel, scene, root);
        });

        //Bouton pour quitter l'application
        Button quitter = new Button("Quitter");
        quitter.getStyleClass().add("important");

        quitter.setPrefSize(100, 50);
        quitter.setOnAction(e -> primaryStage.close());

        boutons.getChildren().addAll(lancerJeu, tutoriel, quitter);
        root.getChildren().add(boutons);

        Button credits = new Button("Crédits");
        credits.setStyle("-fx-background-color: transparent;");
        credits.setPrefSize(150,50);
        credits.setOnAction(e->{
            Credits c = new Credits();
            c.start(primaryStage,jeu);
        });
        root.getChildren().add(credits);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Page d'accueil");
        primaryStage.show();


    }
    /**
     * Permet d'afficher le tutoriel
     * @param j
     * @param scene
     * @param root
     */
    public static void afficherTuto(Jeu j, Scene scene, Pane root){
        VueTutoriel vueTutoriel = new VueTutoriel((SimulationTutoriel) j, WIDTH, HEIGHT);
        j.ajouterObservateur(vueTutoriel);
        root.getChildren().clear();
        root.getChildren().add(vueTutoriel);
        ClavierTuto clavier = new ClavierTuto((SimulationTutoriel) MoteurJeu.jeu);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
    }

}
