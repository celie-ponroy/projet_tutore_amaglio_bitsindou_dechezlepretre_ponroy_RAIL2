package affichage;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import moteur.Clavier;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Comportements;
import simulation.Simulation;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;

public class VueMenus extends VueSimulation {

    private static double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
    private static double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
    private static MoteurJeu jeu;

    protected Stage primaryStage; //scene
    private String choixPersonnage;

    /**
     * Permet d'initialiser la taille de la fenêtre à la taille de l'écran
     */
    private void initPrimaryStage() {
        this.primaryStage = new Stage();
        this.primaryStage.setFullScreen(true);
    }

    /**
     * constructeur avec paramètre jeu
     *
     * @param j le moteur de jeu
     */
    public VueMenus(MoteurJeu j) {
        this.jeu = j;
        //Initialisation de la fenêtre
        initPrimaryStage();
        this.choixPersonnage = "";
    }

    /**
     * constructeur sans paramètre
     */
    public VueMenus() {
        initPrimaryStage();
        this.choixPersonnage = "";
    }

    @Override
    protected void setOpacityPersonnage() {
        this.gardienView.setOpacity(0);
        this.prisonnierView.setOpacity(0);
    }

    @Override
    protected void updatePositions() {

    }

    /**
     * permet de changer la scene et son nom
     *
     * @param scene la scene
     * @param title le titre de la scene
     */
    private void setScene(Scene scene, String title) {
        this.primaryStage.setScene(scene);
        this.primaryStage.setTitle(title);
        this.primaryStage.show();
    }


    /**
     * Affiche le menu principal (mode interactif ou non interactif)
     */
    public void afficherMenuPrincipal() {

        final VBox root = new VBox();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        root.getStylesheets().add("style.css");
        root.setSpacing(20);
        root.setPadding(new Insets(10)); // Ajout d'un padding
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Veuillez choisir un mode:");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        //Bouton du mode interactif
        Button modeInteractif = new Button("Mode interactif");
        modeInteractif.setPrefSize(200, 100);
        modeInteractif.setOnAction(e -> afficherMenuPersonnage()); // Appel le menu des choix des personnages


        //Bouton du mode non interactif
        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {
            //Affichage du menu de difficulté des IA grace a la classe VueMenusNonInteractive
            VueMenusNonInteractive vni = new VueMenusNonInteractive(jeu);
            vni.afficherMenuIA(this.primaryStage);
        });

        //Bouton pour le mode analyse
        Button modeAnalyse = new Button("Mode analyse");
        modeAnalyse.setPrefSize(200, 100);
        modeAnalyse.setOnAction(e -> {
            //Affichage de la vue d'analyse
            afficherAnalyse();

        });

        //Ajout des boutons au conteneur de boutons
        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif, modeAnalyse);

        //Bouton pour quitter l'application
        Button quitter = new Button("Quitter");
        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> this.primaryStage.close());

        //Ajout des éléments à la scene principale
        root.getChildren().addAll(title, buttonBox, quitter);

        //Affichage de la scene et changement du titre de la fenêtre
        setScene(scene, "Menu principal");
    }

    /**
     * Affiche le menu de choix du personnage
     */
    public void afficherMenuPersonnage() {
        VBox root2 = new VBox();
        final Scene scene2 = new Scene(root2, WIDTH, HEIGHT);

        root2.getStylesheets().add("style.css");
        root2.setSpacing(20);
        root2.setPrefSize(800, 600);
        root2.setAlignment(Pos.CENTER);

        Label title2 = new Label("Veuillez choisir un personnage:");

        HBox buttonBox2 = new HBox();
        buttonBox2.setSpacing(20);
        buttonBox2.setAlignment(Pos.CENTER);


        //Bouton "Prisonnier"
        Button persoPrisonnier = new Button("Prisonnier");
        persoPrisonnier.setPrefSize(200, 100);
        persoPrisonnier.setOnAction(f -> {
            setChoixPersonnage("Prisonnier");
            afficherMenuIA(); // Menu de difficulté
        });

        //Bouton "Gardien"
        Button persoGardien = new Button("Gardien");
        persoGardien.setPrefSize(200, 100);
        persoGardien.setOnAction(f -> {
            setChoixPersonnage("Gardien");
            afficherMenuIA(); // Menu de difficulté
        });

        //Ajout des boutons dans la HBox des boutons
        buttonBox2.getChildren().addAll(persoPrisonnier, persoGardien);

        //Ajout des éléments au VBox principal
        root2.getChildren().addAll(title2, buttonBox2);

        //Affichage de la scene et changement du titre de la fenêtre
        setScene(scene2, "Choix du personnage");
    }


    /**
     * Permet d'afficher le jeu (mode interactif)
     */
    public void afficherJeu(Jeu j, Pane root, Scene scene) {
        //Affichage du jeu
        VuePrincipale vp = new VuePrincipale();
        vp.update(j);
        j.ajouterObservateur(vp);
        root.getChildren().clear();
        root.getChildren().add(vp);
        Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
    }

    /**
     * Affiche le menu de choix de la difficulté de l'IA
     */
    public void afficherMenuIA() {
        final VBox root = new VBox();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        root.getStylesheets().add("style.css");
        root.setSpacing(20);
        root.setPadding(new Insets(10)); // Ajout d'un padding
        root.setAlignment(Pos.CENTER);

        //on change le nom de la scene
        setScene(scene, "Choix de la difficulté de l'IA adverse");

        Label title = new Label("Veuillez choisir un niveau de difficulté:");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        //on recupere le choix du personnage que l'utilisateur va joué
        choixPersonnage = getChoixPersonnage();

        //Création de la combobox pour le choix de la difficulté en fonction du personnage séléctionné
        ComboBox<String> comboBox = new ComboBox<>();
        //si le choix du personnage de l'utilisateur est le prisonnier, on adapte la combobox
        if (choixPersonnage == "Prisonnier") { //si l'utilisateur joue le prisonnier
            //Ajout de chaque choix possible
            comboBox.getItems().add("Arbre de décision déterministe 1.0");
            comboBox.getItems().add("Arbre de décision aléatoire");
            comboBox.getItems().add("Comportement aléatoire");
            comboBox.getItems().add("Réseau de neurones 1.0");
        } else { //si l'utilisateur joue le gardien
            //Ajout de chaque choix possible
            comboBox.getItems().add("Arbre de décision déterministe 1.0");
            comboBox.getItems().add("Arbre de décision déterministe 2.0");
            comboBox.getItems().add("Comportement aléatoire");
            comboBox.getItems().add("Réseau de neurones 1.0");
        }

        HBox buttonBox = new HBox(comboBox);
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        //Bouton "OK" pour valider le choix
        Button okButton = new Button("Valider");
        okButton.setPrefSize(150, 50);

        //Évenements lier au choix de difficulté
        okButton.setOnAction(e -> {
            //Déclaration de la simulation
            Simulation simulation;

            //Switch pour le choix de la difficulté de l'agent en focntion du choix de l'utilisateur
            switch (comboBox.getValue()) {
                case "Arbre de décision déterministe 1.0":
                    if (choixPersonnage == "Prisonnier") {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.ArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        afficherJeu(MoteurJeu.jeu, root, scene);
                    } else {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(false, Comportements.ArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        afficherJeu(MoteurJeu.jeu, root, scene);
                    }
                    break;
                case "Arbre de décision déterministe 2.0":
                    //on change le nom de la scene
                    setScene(scene, "Simulation interactive");
                    simulation = new Simulation(false, Comportements.ArbreDeterministev2);
                    MoteurJeu.jeu = simulation;
                    //Affichage du jeu
                    afficherJeu(MoteurJeu.jeu, root, scene);
                    break;
                case "Arbre de décision aléatoire":
                    //on change le nom de la scene
                    setScene(scene, "Simulation interactive");
                    simulation = new Simulation(true, Comportements.ArbreAleatoire);
                    MoteurJeu.jeu = simulation;
                    //Affichage du jeu
                    afficherJeu(MoteurJeu.jeu, root, scene);
                    break;
                case "Comportement aléatoire":
                    if (choixPersonnage == "Prisonnier") {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.Aleatoire);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        afficherJeu(MoteurJeu.jeu, root, scene);
                    } else {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(false, Comportements.Aleatoire);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        afficherJeu(MoteurJeu.jeu, root, scene);
                    }
                    break;
                case "Réseau de neurones 1.0":
                    if (choixPersonnage == "Prisonnier") {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.ReseauArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        afficherJeu(MoteurJeu.jeu, root, scene);
                    } else {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(false, Comportements.ReseauArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        afficherJeu(MoteurJeu.jeu, root, scene);
                    }
                    break;
                case null:
                    //Pop up pour afficher un message d'alerte si aucun choix n'est fait
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Attention !");
                    alert.setContentText("Veuillez choisir un niveau de difficulté");
                    alert.showAndWait();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + comboBox.getValue());
            }
        });

        //Ajout des éléments à la scene principale
        root.getChildren().addAll(title, buttonBox, okButton);

        //Affichage de la scene et changement du titre de la fenêtre
        setScene(scene, "Choix de la difficulté de l'IA adverse");
    }

    /**
     * Permet de récupérer le choix de personnage de l'utilisateur
     *
     * @return le choix du personnage
     */
    public String getChoixPersonnage() {
        return this.choixPersonnage;
    }

    /**
     * Permet de changer le choix de personnage de l'utilisateur
     *
     * @param choixPersonnage le choix du personnage
     */
    public void setChoixPersonnage(String choixPersonnage) {
        this.choixPersonnage = choixPersonnage;
    }

    /**
     * Méthode permettant de lancer la vue analyse
     */
    public void afficherAnalyse() {
        VueAnalyse va = new VueAnalyse();
        va.createAnalyseView(primaryStage);


    }



}
