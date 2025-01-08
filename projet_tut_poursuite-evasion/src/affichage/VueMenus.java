package affichage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import moteur.Clavier;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Simulation;

public class VueMenus {

    private static  MoteurJeu jeu;
    private static double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
    private static double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
    protected Stage primaryStage; //scene

    /**
     * Initialisation de la fenêtre
     */
    private void initPrimaryStage() {
        this.primaryStage = new Stage();
        this.primaryStage.setFullScreen(true);
    }

    /**
     * constructeur avec paramètre jeu
     */
    public VueMenus(MoteurJeu j) {
        this.jeu = j;
        //Initialisation de la fenêtre
        initPrimaryStage();
    }

    /**
     * constructeur sans paramètre
     */
    public VueMenus() {
        initPrimaryStage();
    }

    /**
     * permet de changer la scene
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

        root.setStyle("-fx-background-color: #d3d3d3;");
        root.setSpacing(20);
        root.setPadding(new Insets(10)); // Ajout d'un padding
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Veuillez choisir un mode:");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button modeInteractif = new Button("Mode interactif");
        modeInteractif.setPrefSize(200, 100);
        modeInteractif.setOnAction(e -> afficherMenuPersonnage()); // Appel le menu des choix des personnages

        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {
            //Affichage du menu de difficulté des IA grace a la classe VueMenusNonInteractive
            VueMenusNonInteractive vni = new VueMenusNonInteractive(jeu);
            vni.afficherMenuIA(this.primaryStage);
        });

        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif);

        Button quitter = new Button("Quitter");
        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> this.primaryStage.close());

        root.getChildren().addAll(title, buttonBox, quitter);

        setScene(scene, "Menu principal");
    }

    /**
     * Affiche le menu de choix du personnage
     */
    public void afficherMenuPersonnage() {
        VBox root2 = new VBox();
        final Scene scene2 = new Scene(root2, WIDTH, HEIGHT);

        root2.setStyle("-fx-background-color: #d3d3d3;");
        root2.setSpacing(20);
        root2.setPrefSize(800, 600);
        root2.setAlignment(Pos.CENTER);

        Label title2 = new Label("Veuillez choisir un personnage:");
        title2.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox buttonBox2 = new HBox();
        buttonBox2.setSpacing(20);
        buttonBox2.setAlignment(Pos.CENTER);

        Button persoPrisonnier = new Button("Prisonnier");
        persoPrisonnier.setPrefSize(200, 100);
        persoPrisonnier.setOnAction(f -> {
            afficherMenuIA(); // Menu de difficulté

            //on change le nom de la scene
            setScene(scene2, "Simulation interactive");
            Simulation simulation = new Simulation(true);
            MoteurJeu.jeu = simulation;

            VuePrincipale vp = new VuePrincipale();
            vp.update(MoteurJeu.jeu);
            MoteurJeu.jeu.ajouterObservateur(vp);
            root2.getChildren().clear();
            root2.getChildren().add(vp);

            Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
            scene2.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
        });

        Button persoGardien = new Button("Gardien");
        persoGardien.setPrefSize(200, 100);
        persoGardien.setOnAction(f -> {
            afficherMenuIA(); // Menu de difficulté

            //on change le nom de la scene
            setScene(scene2, "Simulation interactive");
            Jeu simulation = new Simulation(false);
            MoteurJeu.jeu = simulation;

            VuePrincipale vp = new VuePrincipale();
            vp.update(MoteurJeu.jeu);
            MoteurJeu.jeu.ajouterObservateur(vp);
            root2.getChildren().clear();
            root2.getChildren().add(vp);

            Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
            scene2.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
        });

        buttonBox2.getChildren().addAll(persoPrisonnier, persoGardien);
        root2.getChildren().addAll(title2, buttonBox2);

        setScene(scene2, "Choix du personnage");
    }

    /**
     * Affiche le menu de choix de la difficulté de l'IA
     */
    public void afficherMenuIA() {
        final VBox root = new VBox();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        root.setStyle("-fx-background-color: #d3d3d3;");
        root.setSpacing(20);
        root.setPadding(new Insets(10)); // Ajout d'un padding
        root.setAlignment(Pos.CENTER);

        //on change le nom de la scene
        setScene(scene, "Choix de la difficulté de l'IA adverse");

        Label title = new Label("Veuillez choisir un niveau de difficulté:");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        //Création de la combobox pour choisir le niveau de difficulté
        ComboBox<String> comboBox = new ComboBox<>();
        //Ajout de chaque choix possible
        comboBox.getItems().add("Arbre de décision déterministe");
        comboBox.getItems().add("Arbre de décision aléatoire");
        comboBox.getItems().add("Comportement aléatoire");
        comboBox.getItems().add("Réseau de neurones v1");

        HBox buttonBox = new HBox(comboBox);
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        //Bouton "OK" pour valider le choix
        Button okButton = new Button("Valider");
        okButton.setPrefSize(150, 50);

        //Évenements lier au choix de difficulté
        okButton.setOnAction(e -> {
            switch (comboBox.getValue()) {
                case "Arbre de décision déterministe":
                    //Va lancer une méthode qui la simulation avec l'IA arbre de décision
                    break;
                case "Arbre de décision aléatoire":
                    //Va lancer une méthode qui la simulation avec une IA avec un arbre de décision aléatoire
                    break;
                case "Comportement aléatoire":
                    //Va lancer une méthode qui la simulation avec une IA avec   un comportement aléatoire
                    break;
                case "Réseau de neurones v1":
                    //Va lancer une méthode qui la simulation avec une IA avec un réseau de neurones (version 1)
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

        root.getChildren().addAll(title, buttonBox, okButton);

        setScene(scene, "Choix de la difficulté de l'IA adverse");
    }
}
