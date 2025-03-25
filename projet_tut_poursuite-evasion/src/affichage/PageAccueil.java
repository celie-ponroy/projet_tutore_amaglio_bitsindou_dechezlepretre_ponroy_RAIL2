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
        root.setSpacing(20);
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Poursuite - Évasion IA");
        title.getStyleClass().add("grostitre");
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
        personnages.setSpacing(50);

        root.getChildren().add(personnages);

        Button lancerJeu = new Button("Commencer");
        lancerJeu.getStyleClass().add("gris");
        lancerJeu.setOnAction(e->{
            VueMenus vueMenus = new VueMenus(jeu);
            vueMenus.afficherMenuPrincipal();
            primaryStage.close();
            });

        Button tutoriel = new Button("Tutoriel");
        tutoriel.getStyleClass().add("valider");
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

        root.getChildren().addAll(lancerJeu, tutoriel, quitter);




        primaryStage.setScene(scene);
        primaryStage.setTitle("Page d'acceuil");
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
