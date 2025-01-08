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
import simulation.Comportements;

import simulation.Simulation;

public class VueMenus {

    private static  MoteurJeu jeu;
    private static double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
    private static double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
    protected Stage primaryStage; //scene
    private String choixPersonnage;


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
        this.choixPersonnage = "";

    }

    /**
     * constructeur sans paramètre
     */
    public VueMenus() {
        initPrimaryStage();
        this.choixPersonnage = "";
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
            setChoixPersonnage("Prisonnier");
            afficherMenuIA(); // Menu de difficulté

        });

        Button persoGardien = new Button("Gardien");
        persoGardien.setPrefSize(200, 100);
        persoGardien.setOnAction(f -> {
            setChoixPersonnage("Gardien");
            afficherMenuIA(); // Menu de difficulté
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

        //on recupere le choix du personnage que l'utilisateur a joue
        choixPersonnage = getChoixPersonnage();

        ComboBox<String> comboBox = new ComboBox<>();
        //si le choix du personnage est le prisonnier, on adapte la combobox
        if (choixPersonnage == "Prisonnier"){
            //Ajout de chaque choix possible
            comboBox.getItems().add("Arbre de décision déterministe");
            comboBox.getItems().add("Arbre de décision aléatoire");
            comboBox.getItems().add("Comportement aléatoire");
            comboBox.getItems().add("Réseau de neurones 1.0");
        }else{
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

            //Déclaration de la vue principale
            VuePrincipale vp;

            //Déclaration de la classe Clavier
            Clavier clavier;

            switch (comboBox.getValue()) {
                case "Arbre de décision déterministe":
                    if (choixPersonnage == "Prisonnier") {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.ArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    } else {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(false, Comportements.ArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    }
                    break;
                case "Arbre de décision déterministe 2.0":
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.ArbreDeterministev2);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    break;
                case "Arbre de décision aléatoire":
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.ArbreAleatoire);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    break;
                case "Comportement aléatoire":
                    if (choixPersonnage == "Prisonnier") {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.Aleatoire);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    } else {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(false, Comportements.Aleatoire);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    }
                    break;
                case "Réseau de neurones 1.0":
                    if (choixPersonnage == "Prisonnier") {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(true, Comportements.ReseauArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
                    } else {
                        //on change le nom de la scene
                        setScene(scene, "Simulation interactive");
                        simulation = new Simulation(false, Comportements.ReseauArbreDeterministe);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        vp = new VuePrincipale();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        clavier = new Clavier((Simulation) MoteurJeu.jeu);
                        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
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

        root.getChildren().addAll(title, buttonBox, okButton);

        setScene(scene, "Choix de la difficulté de l'IA adverse");
    }

    /**
     * Permet de récupérer le choix du personnage
     * @return le choix du personnage
     */
    public String getChoixPersonnage(){
        return this.choixPersonnage;
    }

    /**
     * Permet de changer le choix du personnage
     * @param choixPersonnage le choix du personnage
     */
     public void setChoixPersonnage(String choixPersonnage) {
         this.choixPersonnage = choixPersonnage;
     }
}
