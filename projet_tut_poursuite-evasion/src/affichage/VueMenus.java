package affichage;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import moteur.Jeu;
import moteur.MoteurJeu;
import musique.SoundManager;
import sauvegarde.Sauvegarde;
import simulation.Simulation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.Cursor;

import static affichage.PageAccueil.isMuted;
import static affichage.PageAccueil.lancerPageAcceuil;
import static musique.SoundManager.playGameMusic;

/**
 * Classe pour gérer les menus du jeu interactif
 */
public class VueMenus {

    /**
     * Attributs
     */
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
    public VueMenus(MoteurJeu j,Stage primaryStage) {
        this.jeu = j;
        this.choixPersonnage = "";
        this.primaryStage = primaryStage;
    }


    /**
     * constructeur pour accepter un stage
     */
    public VueMenus(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.choixPersonnage = "";
    }

    /**
     * Méthode pour afficher la scene
     *
     * @param root pour modifier la scene
     * @param title le titre de la scene
     */
    private void setScene(Parent root, String title) {
        if(this.primaryStage.getScene() == null) {
            this.primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        }else{
            this.primaryStage.getScene().setRoot(root);
        }
        this.primaryStage.setTitle(title);
    }


    /**
     * Affiche le menu principal (mode interactif ou non interactif)
     */
    public void afficherMenuPrincipal() {

        final VBox root = new VBox();

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
        modeInteractif.setOnAction(e -> {//Affichage du menu de choix du personnage
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
        modePartiesSauvegardes.setPrefSize(200, 100);
        modePartiesSauvegardes.setOnAction(e -> {
            afficherMenuSauvegarde(this.primaryStage, root);
        });

        //Ajout des boutons au conteneur de boutons
        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif, modeAnalyse, modePartiesSauvegardes);

        //Bouton pour revenir à la page d'accueil
        Button retour = new Button("Retour");
        retour.getStyleClass().add("important");

        retour.setPrefSize(230, 50);
        retour.setOnAction(e -> lancerPageAcceuil(jeu, primaryStage));

        //Ajout des éléments à la scene principale
        root.getChildren().addAll(title, buttonBox, retour);

        //Affichage de la scene et changement du titre de la fenêtre
        setScene(root, "Menu principal");
    }


    /**
     * Affiche le menu de choix du personnage
     */
    public void afficherMenuPersonnage() {
        VBox root = new VBox();
        root.getStylesheets().add("style.css");
        root.setSpacing(20);
        root.setPrefSize(800, 600);
        root.setAlignment(Pos.CENTER);

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
        root.getChildren().addAll(title2, buttonBox2, retour);

        //Affichage de la scene et changement du titre de la fenêtre
        setScene(root, "Choix du personnage");
    }

    /**
     * Affiche le bouton de retour
     */
    public Button retourBtn() {
        //Bouton "Retour"
        Button retour = new Button("Retour au menu principal");
        retour.setPrefSize(230, 50);
        retour.getStyleClass().add("important"); //rend le bouton rouge
        retour.setOnAction(e -> {
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retour.getScene().getWindow();
            //retour au menu principal
            this.primaryStage = stage;
            this.afficherMenuPrincipal();

        });

        return retour;
    }

    /**
     * Permet d'afficher le jeu (mode interactif)
     */
    public void afficherJeu(Jeu j, Pane root) {
        //Affichage du jeu
        VuePrincipale vp = new VuePrincipale(WIDTH, HEIGHT);
        vp.update(j);
        j.ajouterObservateur(vp);
        root.getChildren().clear();
        root.getChildren().add(vp);
        Clavier clavier = new Clavier((Simulation) MoteurJeu.jeu);
        primaryStage.getScene().addEventHandler(KeyEvent.KEY_PRESSED, clavier);
    }

    /**
     * Affiche le menu de choix de la difficulté de l'IA
     */
    public void afficherMenuIA() {
        final VBox root = new VBox();

        root.getStylesheets().add("style.css");
        root.setSpacing(20);
        root.setPadding(new Insets(10)); // Ajout d'un padding
        root.setAlignment(Pos.CENTER);

        //on change le nom de la scene
        setScene(root, "Choix de la difficulté de l'IA adverse");

        Label title = new Label("Veuillez choisir un niveau de difficulté:");
        title.getStyleClass().add("titre");

        //on recupere le choix du personnage que l'utilisateur va joué
        choixPersonnage = getChoixPersonnage();

        //Création de la combobox pour le choix de la difficulté en fonction du personnage séléctionné
        ComboBox<String> comboBox = new ComboBox<>();
        //si le choix du personnage de l'utilisateur est le prisonnier, on adapte la combobox
        if (Objects.equals(choixPersonnage, "Prisonnier")) { //si l'utilisateur joue le prisonnier
            //Ajout de chaque choix possible
            comboBox = FabriqueComportement.creerComboBoxGardien();

        } else { //si l'utilisateur joue le gardien
            //Ajout de chaque choix possible
            comboBox = FabriqueComportement.creerComboBoxPrisonnier();
        }

        HBox buttonBox = new HBox(comboBox);
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        //Bouton "OK" pour valider le choix
        Button okButton = new Button("Valider");
        okButton.setPrefSize(150, 50);

        //Évenements lier au choix de difficulté
        ComboBox<String> finalComboBox = comboBox;
        okButton.setOnAction(e -> {
            //on stop la musique de fond et on met en place la musique de jeu
            SoundManager.stopFondMusic();

            //Chargement du bouton
            String originalText = chargementBouton(okButton);

            // On utilise Task pour exécuter le code de manière asynchrone
            Task<Simulation> task = new Task<>() {
                @Override
                protected Simulation call() throws Exception {
                    Simulation simulation = null;

                    //Switch pour le choix de la difficulté de l'agent en fonction du choix de l'utilisateur
                    return FabriqueComportement.creerSimulation(finalComboBox.getValue());
                }
            };

            // Gére la fin du task
            task.setOnSucceeded(event -> {
                Simulation simulation = task.getValue();
                if (simulation != null) {
                    //on change le nom de la scene
                    setScene(root, "Simulation interactive");
                    MoteurJeu.jeu = simulation;
                    if (isMuted == false){
                        playGameMusic();
                    }
                    //Affichage du jeu
                    afficherJeu(MoteurJeu.jeu, root);
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
            if (choixPersonnage == "Prisonnier") {
                InformationsIa.popUpGardien();

            } else {
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
        setScene(root, "Choix de la difficulté de l'IA adverse");
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
    public void afficherMenuSauvegarde(Stage primaryStage, VBox root) {
        AtomicReference<Simulation> simulation = new AtomicReference<>();
        //récuperration du nom de la sauvegarde
        List<String> choices = Sauvegarde.nomsSauvegardes();
        if (choices.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Attention !");
            alert.setHeaderText("Aucune sauvegarde n'a été faite.");
            alert.showAndWait();
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.getFirst(), choices);
        dialog.setTitle("Choix sauvegarde");
        dialog.setContentText("Sauvegarde à choisir :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent((letter) -> {
            try {
                System.out.println("Chargement de la sauvegarde");
                simulation.set(Sauvegarde.charger(letter));
                VueSauvegarde vs = new VueSauvegarde(WIDTH, HEIGHT, simulation.get());
                vs.update();
                root.getChildren().clear();
                root.getChildren().add(vs);
                primaryStage.getScene().setRoot(root);
                System.out.println("Partie chargée");

            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de la sauvegarde de :" + letter + ".ser");
            }
        });

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
