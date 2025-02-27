package affichage;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import moteur.Clavier;
import moteur.ClavierTuto;
import moteur.Jeu;
import moteur.MoteurJeu;
import sauvegarde.Sauvegarde;
import simulation.Comportements;
import simulation.Simulation;
import simulation.tuto.SimulationTutoriel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.image.Image;
import javafx.scene.Cursor;

public class VueMenus extends VueSimulation {

    private static double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
    private static double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
    private static MoteurJeu jeu;

    protected Stage primaryStage; //scene
    private String choixPersonnage;

    /**
     * constructeur avec paramètre jeu
     *
     * @param j le moteur de jeu
     */
    public VueMenus(MoteurJeu j) {
        this.jeu = j;
        this.primaryStage = new Stage();
        this.primaryStage.setFullScreen(true);
        this.choixPersonnage = "";
    }

    /**
     * constructeur sans paramètre
     */
    public VueMenus() {
        this.primaryStage = new Stage();
        this.primaryStage.setFullScreen(true);
        this.choixPersonnage = "";
    }

    /**
     * constructeur pour accepter un stage
     */
    public VueMenus(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.choixPersonnage = "";
    }

    /**
     * Permet d'initialiser la taille de la fenêtre à la taille de l'écran
     */
//    private void initPrimaryStage() {
//        this.primaryStage = new Stage();
//        this.primaryStage.setFullScreen(true);
//    }

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
        title.getStyleClass().add("titre");

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        //Bouton du mode interactif
        Button modeInteractif = new Button("Mode interactif");
        modeInteractif.setPrefSize(200, 100);
        modeInteractif.setOnAction(e ->{//Affichage du menu de choix du personnage
            afficherMenuPersonnage();// Appel le menu des choix des personnages
        });


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

        //Boutton parties sauvegardées
        Button modePartiesSauvegardes = new Button("Parties sauvegardées");
        modePartiesSauvegardes.setPrefSize(200,100);
        modePartiesSauvegardes.setOnAction(e -> {
            afficherMenuSauvegarde(this.primaryStage, root, scene);
        });

        //Tuto
        Button modeTuto = new Button();
        javafx.scene.image.Image img = new Image("file:images/tuto.png");
        ImageView view = new ImageView(img);
        modeTuto.setPrefSize(80, 80);
        view.setFitHeight(80);
        view.setPreserveRatio(true);

        modeTuto.setStyle("-fx-background-color: transparent;");
        modeTuto.setGraphic(view);
        modeTuto.setOnAction(e -> {
            SimulationTutoriel simulationTutoriel = new SimulationTutoriel();
            MoteurJeu.jeu = simulationTutoriel;
            afficherTuto(simulationTutoriel, scene, root);
        });

        //Ajout des boutons au conteneur de boutons
        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif, modeAnalyse, modePartiesSauvegardes);


        //Bouton pour quitter l'application
        Button quitter = new Button("Quitter");
        quitter.getStyleClass().add("important");

        quitter.setPrefSize(230, 50);
        quitter.setOnAction(e -> this.primaryStage.close());

        //Ajout des éléments à la scene principale
        root.getChildren().addAll(title, buttonBox, quitter,modeTuto);

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
        title2.getStyleClass().add("titre");

        HBox buttonBox2 = new HBox();
        buttonBox2.setSpacing(50);
        buttonBox2.setAlignment(Pos.CENTER);

        //Création d'une VBox cliquable
        VBox vboxPris = new VBox();
        vboxPris.setAlignment(Pos.CENTER);
        vboxPris.setSpacing(10);
        vboxPris.getStyleClass().add("bordered-box");
        vboxPris.setCursor(Cursor.HAND); // Change le curseur en main au survol
        //Taille de la VBox
        vboxPris.setPrefSize(200, 200);

        //Fond pour la VBox
        vboxPris.setStyle("-fx-background-color: #8F9CBF;");

        //Label pour le prisonnier
        Label labelPriso = new Label("Prisonnier");
        labelPriso.setStyle("-fx-font-size: 20px;");

        //Image du prisonnier (sans le bouton)
        Image imgPrisonnier = new Image("file:images/prisonnier.png");
        ImageView imgViewPrisonnier = new ImageView(imgPrisonnier);
        imgViewPrisonnier.setFitHeight(80);
        imgViewPrisonnier.setPreserveRatio(true);

        //Ajout d'un tooltip sur la VBox entière
        Tooltip tooltipPris = new Tooltip("Prisonnier");
        Tooltip.install(vboxPris, tooltipPris);

        //Ajout des éléments à la VBox
        vboxPris.getChildren().addAll(imgViewPrisonnier, labelPriso);

        //Gestion du clic sur la VBox entière
        vboxPris.setOnMouseClicked(event -> {
            setChoixPersonnage("Prisonnier");
            afficherMenuIA(); // Menu de difficulté
        });

        //Ajouter des effets visuels pour simuler un bouton
        vboxPris.setOnMousePressed(event -> {
            vboxPris.setTranslateY(2); // Effet d'enfoncement
            vboxPris.getStyleClass().add("pressed-box");
        });

        vboxPris.setOnMouseReleased(event -> {
            vboxPris.setTranslateY(0); // Retour à la position normale
            vboxPris.getStyleClass().remove("pressed-box");
        });

        //Création d'une VBox cliquable
        VBox vboxGard = new VBox();
        vboxGard.setAlignment(Pos.CENTER);
        vboxGard.setSpacing(10);
        vboxGard.getStyleClass().add("bordered-box");
        vboxGard.setCursor(Cursor.HAND); // Change le curseur en main au survol
        //Taille de la VBox
        vboxGard.setPrefSize(200, 200);
        //Fond pour la VBox
        vboxGard.setStyle("-fx-background-color: #8F9CBF;");

        //Label pour le prisonnier
        Label labelGard = new Label("Gardien");
        labelGard.setStyle("-fx-font-size: 20px;");

        //Image du prisonnier (sans le bouton)
        Image imgGardien = new Image("file:images/gardien.png");
        ImageView imgViewGardien = new ImageView(imgGardien);
        imgViewGardien.setFitHeight(80);
        imgViewGardien.setPreserveRatio(true);

        //Ajout d'un tooltip sur la VBox entière
        Tooltip tooltipGard = new Tooltip("Gardien");
        Tooltip.install(vboxGard, tooltipGard);

        //Ajout des éléments à la VBox
        vboxGard.getChildren().addAll(imgViewGardien, labelGard);

        //Gestion du clic sur la VBox entière
        vboxGard.setOnMouseClicked(event -> {
            setChoixPersonnage("Gardien");
            afficherMenuIA(); // Menu de difficulté
        });

        //Ajouter des effets visuels pour simuler un bouton
        vboxGard.setOnMousePressed(event -> {
            vboxGard.setTranslateY(2); // Effet d'enfoncement
            vboxGard.getStyleClass().add("pressed-box");
        });

        vboxGard.setOnMouseReleased(event -> {
            vboxGard.setTranslateY(0); // Retour à la position normale
            vboxGard.getStyleClass().remove("pressed-box");
        });

        //Bouton "Retour"
        Button retour = retourBtn();

        //Ajout des boutons dans la HBox des boutons
        buttonBox2.getChildren().addAll(vboxPris, vboxGard);

        //Ajout des éléments au VBox principal
        root2.getChildren().addAll(title2, buttonBox2,retour);

        //Affichage de la scene et changement du titre de la fenêtre

        setScene(scene2, "Choix du personnage");
    }

    /**
     * Affiche le bouton de retour
     */
    public Button retourBtn(){
        //Bouton "Retour"
        Button retour = new Button ("Retour au menu principal");
        retour.setPrefSize(230, 50);
        retour.getStyleClass().add("important"); //rend le bouton rouge
        retour.setOnAction(e -> {
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retour.getScene().getWindow();
            stage.close();
            //retour au menu principal
            VueMenus vm = new VueMenus();
            vm.afficherMenuPrincipal();
        });

        return retour;
    }

    /**
     * Permet d'afficher le jeu (mode interactif)
     */
    public void afficherJeu(Jeu j, Pane root, Scene scene) {
        //Affichage du jeu
        VuePrincipale vp = new VuePrincipale(WIDTH, HEIGHT);
        vp.update(j);
        j.ajouterObservateur(vp);
        root.getChildren().clear();
        root.getChildren().add(vp);
        Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, clavier);
    }

    /**
     * Permet d'afficher le tutoriel
     * @param j
     * @param scene
     * @param root
     */
    public void afficherTuto(Jeu j ,Scene scene, Pane root){
        VueTutoriel vueTutoriel = new VueTutoriel((SimulationTutoriel) j, WIDTH, HEIGHT);
        j.ajouterObservateur(vueTutoriel);
        root.getChildren().clear();
        root.getChildren().add(vueTutoriel);
        ClavierTuto clavier = new ClavierTuto((SimulationTutoriel) MoteurJeu.jeu);
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
        title.getStyleClass().add("titre");

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
            //Chargement du bouton
            String originalText = chargementBouton(okButton);

            // On utilise Task pour exécuter le code de manière asynchrone
            Task<Simulation> task = new Task<>() {
                @Override
                protected Simulation call() throws Exception {
                    Simulation simulation;

                    //Switch pour le choix de la difficulté de l'agent en fonction du choix de l'utilisateur
                    switch (comboBox.getValue()) {
                        case "Arbre de décision déterministe 1.0":
                            if (choixPersonnage == "Prisonnier") {
                                simulation = new Simulation(true, Comportements.ArbreDeterministe);
                            } else {
                                simulation = new Simulation(false, Comportements.ArbreDeterministe);
                            }
                            break;
                        case "Arbre de décision déterministe 2.0":
                            simulation = new Simulation(false, Comportements.ArbreDeterministev2);
                            break;
                        case "Arbre de décision aléatoire":
                            simulation = new Simulation(true, Comportements.ArbreAleatoire);
                            break;
                        case "Comportement aléatoire":
                            if (choixPersonnage == "Prisonnier") {
                                simulation = new Simulation(true, Comportements.Aleatoire);
                            } else {
                                simulation = new Simulation(false, Comportements.Aleatoire);
                            }
                            break;
                        case "Réseau de neurones 1.0":
                            if (choixPersonnage == "Prisonnier") {
                                simulation = new Simulation(true, Comportements.ReseauArbreDeterministe);
                            } else {
                                simulation = new Simulation(false, Comportements.ReseauArbreDeterministe);
                            }
                            break;
                        case null:
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Information");
                                alert.setHeaderText("Attention !");
                                alert.setContentText("Veuillez choisir un niveau de difficulté");
                                alert.showAndWait();
                            });
                            return null;
                        default:
                            throw new IllegalStateException("Unexpected value: " + comboBox.getValue());
                    }
                    return simulation;
                }
            };

            // Gére la fin du task
            task.setOnSucceeded(event -> {
                Simulation simulation = task.getValue();
                if (simulation != null) {
                    //on change le nom de la scene
                    setScene(scene, "Simulation interactive");
                    MoteurJeu.jeu = simulation;
                    //Affichage du jeu
                    afficherJeu(MoteurJeu.jeu, root, scene);
                }

                // Réinitialise le bouton avec le texte original
                okButton.setGraphic(null);
                okButton.setText(originalText);
                okButton.setDisable(false);
            });

            // Gére l'erreur
            task.setOnFailed(event -> {
                // Réinitialiser le bouton
                okButton.setGraphic(null);
                okButton.setText(originalText);
                okButton.setDisable(false);

                // Afficher l'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Une erreur est survenue");
                alert.setContentText(task.getException().getMessage());
                alert.showAndWait();
            });

            // Démarre le task dans un nouveau thread
            new Thread(task).start();
        });
        
        //ajout bouton informatif
        Button info = InformationsIa.getButtonInfo();

        info.setOnAction(e -> {
            //on lance un popup
            if(choixPersonnage == "Prisonnier") {
                InformationsIa.popUpGardien();

            }else {
                InformationsIa.popUpPrisonnier();
            }
        });
        HBox buttonBox2 = new HBox();
        buttonBox2.setSpacing(20);
        buttonBox2.setAlignment(Pos.CENTER);
        buttonBox2.getChildren().addAll(okButton, info);
        //Ajout bouton retour
        Button retour = retourBtn();

        //Ajout des éléments à la scene principale
        root.getChildren().addAll(title, buttonBox, buttonBox2, retour);

        //Affichage de la scene et changement du titre de la fenêtre
        setScene(scene, "Choix de la difficulté de l'IA adverse");
    }

    /**
     * Affiche le bouton de chargement
     */
    public String chargementBouton(Button okButton) {
        // Désactive le bouton pendant le chargement
        okButton.setDisable(true);

        // Crée un ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(20, 20);

        // Sauvegarde le texte original du bouton
        String originalText = okButton.getText();

        // Créer un HBox pour contenir l'indicateur et le texte
        HBox loadingContent = new HBox(5); // 5 pixels d'espacement
        loadingContent.setAlignment(Pos.CENTER);
        loadingContent.getChildren().addAll(progressIndicator, new Text("Chargement..."));

        // Remplace le contenu du bouton original avec l'indicateur de chargement
        okButton.setGraphic(loadingContent);
        okButton.setText("");

        return originalText;
    }

    /**
     * Affiche le menu de sauvegarde
     */
    public void afficherMenuSauvegarde(Stage primaryStage, VBox root, Scene scene) {
        AtomicReference<Simulation> simulation = new AtomicReference<>();
        //récuperration du nom de la sauvegarde
        List<String> choices =  Sauvegarde.nomsSauvegardes();
        ChoiceDialog<String> dialog = new ChoiceDialog<>("simu.ser", choices);
        dialog.setTitle("Choix sauvegarde");
        dialog.setContentText("Sauvegarde à choisir :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent((letter) -> {
            try{
                System.out.println("Chargement de la sauvegarde");
                simulation.set(Sauvegarde.charger(letter));
                System.out.println("Partie chargée");

            }catch (Exception e){
                System.out.println("Erreur lors du chargement de la sauvegarde de :"+letter+".ser");
            }

        });
        VueSauvegarde vs = new VueSauvegarde(WIDTH, HEIGHT, simulation.get());
        vs.update();
        root.getChildren().clear();
        root.getChildren().add(vs);
        primaryStage.setScene(scene);
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
