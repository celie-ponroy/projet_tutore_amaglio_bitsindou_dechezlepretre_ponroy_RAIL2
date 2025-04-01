package affichage;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import musique.SoundManager;
import simulation.tuto.SimulationTutoriel;

import java.awt.*;

import static musique.SoundManager.stopBtnMusic;
public class PageAccueil {
    private static double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
    private static double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();

    /**
     * Lance la page d'accueil
     * @param jeu le jeu qui sera lancé en continuant
     * @param primaryStage la fenetre où on affiche la page
     */
    public static void lancerPageAcceuil(MoteurJeu jeu, Stage primaryStage) {

        primaryStage.setFullScreen(true);

        final VBox root = new VBox();
        root.getStylesheets().add("style.css");
        root.setStyle("-fx-background-color: #778DA9;");
        root.setSpacing(100);
        root.setAlignment(Pos.CENTER);

        ImageView title = new ImageView("file:images/titre.png");
        title.setFitWidth(WIDTH - 300);
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
        VBox boutons =  ajoutBoutons(jeu, primaryStage,root);

        root.getChildren().add(boutons);

        Button credits = new Button("Crédits");
        credits.setStyle("-fx-background-color: transparent;");
        credits.setPrefSize(150, 50);
        credits.setAlignment(Pos.CENTER);
        credits.setOnAction(e -> {
            Credits c = new Credits();
            c.start(primaryStage, jeu);
        });


        // Bouton Son
        Button boutonSon = creerBoutonSon();
        HBox bas = new HBox();
        bas.setAlignment(Pos.CENTER);
        bas.setPrefWidth(WIDTH);
        bas.setSpacing(WIDTH - 300);
        bas.getChildren().addAll(credits, boutonSon);
        root.getChildren().add(bas);
        if(primaryStage.getScene()==null){
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            primaryStage.setScene(scene);
        }else{
            primaryStage.getScene().setRoot(root);
        }

        primaryStage.setTitle("Page d'accueil");
        primaryStage.show();


    }

    private static VBox ajoutBoutons(MoteurJeu jeu, Stage primaryStage,Pane root) {
        VBox boutons = new VBox();
        boutons.setAlignment(Pos.CENTER);
        boutons.setSpacing(20);
        Button lancerJeu = new Button("Commencer");
        lancerJeu.getStyleClass().add("gris");
        lancerJeu.setPrefSize(20+150+150, 100); //150=taillle petit boutons 20 = spacing
        lancerJeu.setOnAction(e -> {
            VueMenus vueMenus = new VueMenus(jeu,primaryStage);
            vueMenus.afficherMenuPrincipal();
        });

        Button tutoriel = new Button("Tutoriel");
        tutoriel.getStyleClass().add("valider");
        tutoriel.setPrefSize(150, 50);
        tutoriel.setOnAction(e -> {
            SimulationTutoriel simulationTutoriel = new SimulationTutoriel();
            MoteurJeu.jeu = simulationTutoriel;
            afficherTuto(simulationTutoriel, primaryStage, root);
        });

        //Bouton pour quitter l'application
        Button quitter = new Button("Quitter");
        quitter.getStyleClass().add("important");

        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> primaryStage.close());

        HBox boutonsMilieu = new HBox();
        boutonsMilieu.setAlignment(Pos.CENTER);
        boutonsMilieu.setSpacing(20);
        boutonsMilieu.getChildren().addAll( tutoriel, quitter);

        boutons.getChildren().addAll(lancerJeu, boutonsMilieu);
        return boutons;

    }

    /**
     * Creer un bouton pour activer/desactiver le son
     * @return
     */
    private static Button creerBoutonSon() {
        Button boutonSon = new Button();
        ImageView iconeSon = new ImageView("file:images/son.png"); // Image du son activé
        iconeSon.setFitWidth(50);
        iconeSon.setPreserveRatio(true);
        boutonSon.setGraphic(iconeSon);
        boutonSon.setAlignment(Pos.BOTTOM_RIGHT);
        boutonSon.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); // Bouton invisible
        iconeSon.setFitWidth(30);
        iconeSon.setPreserveRatio(true);

        // Variable pour suivre l'état du son
        final boolean[] isMuted = {false};

        // Action du bouton
        boutonSon.setOnAction(e -> {
            isMuted[0] = !isMuted[0]; // Inverse l'état du son
            if (isMuted[0]) {
                SoundManager.stopAllMusic(); // Coupe le son
                iconeSon.setImage(new ImageView("file:images/mute.png").getImage()); // Change l'icône
            } else {
                SoundManager.playFondMusic(); // Remet le son
                iconeSon.setImage(new ImageView("file:images/son.png").getImage());
            }
        });        return boutonSon;
    }

    /**
     * Permet d'afficher le tutoriel
     *
     * @param j
     * @param primaryStage
     * @param root
     */
    public static void afficherTuto(Jeu j,Stage primaryStage, Pane root) {
        VueTutoriel vueTutoriel = new VueTutoriel((SimulationTutoriel) j, WIDTH, HEIGHT);
        j.ajouterObservateur(vueTutoriel);
        root.getChildren().clear();
        root.getChildren().add(vueTutoriel);
        ClavierTuto clavier = new ClavierTuto((SimulationTutoriel) MoteurJeu.jeu);
        primaryStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, clavier);
    }

}
