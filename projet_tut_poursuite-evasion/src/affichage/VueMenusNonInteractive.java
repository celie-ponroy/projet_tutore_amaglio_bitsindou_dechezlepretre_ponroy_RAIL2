package affichage;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import moteur.MoteurJeu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import simulation.Comportements;
import simulation.Simulation;

/**
 * Classe qui affiche les menus non interactifs
 */
public class VueMenusNonInteractive {

    private static MoteurJeu jeu;
    private static double WIDTH = 1540;
    private static double HEIGHT = 1200;

    /**
     * constructeur
     */
    public VueMenusNonInteractive(MoteurJeu j) {
        this.jeu = j;
    }

    /**
     * Affiche le menu de choix de la difficulté de l'IA pour le gardien et le prisonnier
     */
    public void afficherMenuIA(Stage primaryStage) {
        final VBox root = new VBox();
        final Scene scene = new Scene(root, WIDTH, HEIGHT);

        root.getStylesheets().add("style.css");
        root.setSpacing(20);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Veuillez choisir un niveau de difficulté pour chaque personnage:");

        // Conteneur principal pour les deux colonnes
        HBox container = new HBox();
        container.setSpacing(50);
        container.setAlignment(Pos.CENTER);

        // Colonne pour le choix de difficulté du gardien
        VBox gardienBox = new VBox();
        gardienBox.setSpacing(10);
        gardienBox.setAlignment(Pos.CENTER);

        //Image du prisonnier au dessus du label
        Image imgGard = new Image("file:images/gardien.png");
        ImageView imgViewGard = new ImageView(imgGard);
        imgViewGard.setFitHeight(50);
        imgViewGard.setFitWidth(50);
        gardienBox.getChildren().add(imgViewGard);
        //Label du gardien
        Label gardienLabel = new Label("Gardien");

        //Combobox pour le choix de difficulté du gardien
        ComboBox<String> gardienComboBox = new ComboBox<>();
        gardienComboBox.getItems().add("Arbre de décision déterministe");
        gardienComboBox.getItems().add("Arbre de décision aléatoire");
        gardienComboBox.getItems().add("Comportement aléatoire");
        gardienComboBox.getItems().add("Réseau de neurones 1.0");

        gardienBox.getChildren().addAll(gardienLabel, gardienComboBox);

        // Colonne pour le choix de difficulté du prisonnier
        VBox prisonnierBox = new VBox();
        prisonnierBox.setSpacing(10);
        prisonnierBox.setAlignment(Pos.CENTER);

        //Image du prisonnier au dessus du label
        Image imgPri = new Image("file:images/prisonnier.png");
        ImageView imgViewPri = new ImageView(imgPri);
        imgViewPri.setFitHeight(50);
        imgViewPri.setFitWidth(50);
        prisonnierBox.getChildren().add(imgViewPri);
        //Label du prisonnier
        Label prisonnierLabel = new Label("Prisonnier");

        //Combobox pour le choix de difficulté du prisonnier
        ComboBox<String> prisonnierComboBox = new ComboBox<>();
        prisonnierComboBox.getItems().add("Arbre de décision déterministe 1.0");
        prisonnierComboBox.getItems().add("Arbre de décision déterministe 2.0");
        prisonnierComboBox.getItems().add("Comportement aléatoire");
        prisonnierComboBox.getItems().add("Réseau de neurones 1.0");

        prisonnierBox.getChildren().addAll(prisonnierLabel, prisonnierComboBox);

        // Ajout des deux colonnes au conteneur principal
        container.getChildren().addAll(gardienBox, prisonnierBox);

        // Bouton pour valider les choix
        Button okButton = new Button("Valider");
        okButton.setPrefSize(150, 50);

        //Bouton de retour au menu principal
        Button retour = new Button("Retour au menu principal");
        retour.setPrefSize(230, 50);
        retour.getStyleClass().add("important"); //rend le bouton rouge

        //Événement lié au bouton de retour
        retour.setOnAction(e -> {
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retour.getScene().getWindow();
            stage.close();
            //retour au menu principal
            VueMenus vm = new VueMenus();
            vm.afficherMenuPrincipal();
        });

        // Événement lié au bouton de validation
        okButton.setOnAction(e -> {
            // Chargement du bouton
            String originalText = chargementBouton(okButton);

            // Vérification des choix de difficulté
            if (gardienComboBox.getValue() == null || prisonnierComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Attention !");
                alert.setContentText("Veuillez choisir un niveau de difficulté ");
                alert.showAndWait();

                // Réinitialisation du bouton
                okButton.setGraphic(null);
                okButton.setText(originalText);
                okButton.setDisable(false);
            } else {
                // Création d'une tâche asynchrone
                Task<Simulation> task = new Task<>() {
                    @Override
                    protected Simulation call() throws Exception {
                        Comportements comportementP;
                        Comportements comportementG;

                        // Switch pour le choix de difficulté du gardien
                        switch (gardienComboBox.getValue()) {
                            case "Arbre de décision déterministe":
                                comportementG = Comportements.ArbreDeterministe;
                                break;
                            case "Arbre de décision aléatoire":
                                comportementG = Comportements.ArbreAleatoire;
                                break;
                            case "Comportement aléatoire":
                                comportementG = Comportements.Aleatoire;
                                break;
                            case "Réseau de neurones 1.0":
                                comportementG = Comportements.ReseauArbreDeterministe;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + gardienComboBox.getValue());
                        }

                        // Switch pour le choix de difficulté du prisonnier
                        switch (prisonnierComboBox.getValue()) {
                            case "Arbre de décision déterministe 1.0":
                                comportementP = Comportements.ArbreDeterministe;
                                break;
                            case "Arbre de décision déterministe 2.0":
                                comportementP = Comportements.ArbreDeterministev2;
                                break;
                            case "Comportement aléatoire":
                                comportementP = Comportements.Aleatoire;
                                break;
                            case "Réseau de neurones v1":
                                comportementP = Comportements.ReseauArbreDeterministe;
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + prisonnierComboBox.getValue());
                        }

                        // Création de la simulation
                        return new Simulation(comportementG, comportementP);
                    }
                };

                // Gére la fin de la tâche
                task.setOnSucceeded(event -> {
                    Simulation simulation = task.getValue();

                    //Si la simulation n'est pas null on lance la simulation
                    if (simulation != null) {
                        MoteurJeu.jeu = simulation;

                        // Affichage du jeu
                        VuePrincipaleNonInteractive vp = new VuePrincipaleNonInteractive(WIDTH, HEIGHT);
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        primaryStage.setScene(scene);
                    }

                    // Réinitialisation du bouton
                    okButton.setGraphic(null);
                    okButton.setText(originalText);
                    okButton.setDisable(false);
                });

                // Gére l'erreur
                task.setOnFailed(event -> {
                    // Réinitialisation du bouton
                    okButton.setGraphic(null);
                    okButton.setText(originalText);
                    okButton.setDisable(false);

                    // Affichage de l'erreur
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Une erreur est survenue");
                    alert.setContentText(task.getException().getMessage());
                    alert.showAndWait();
                });

                // Démarre la tâche dans un nouveau thread
                new Thread(task).start();
            }
        });


        //ajout bouton informatif
        Button info = InformationsIa.getButtonInfo();
        info.setOnAction(e -> {
            InformationsIa.popUpNonInteractif();
        });
        HBox hBoxButtons = new HBox();
        hBoxButtons.setSpacing(20);
        hBoxButtons.getChildren().addAll(okButton ,info);
        hBoxButtons.setAlignment(Pos.CENTER);
        // Ajout des éléments à la racine
        root.getChildren().addAll(title, container, hBoxButtons,retour);

        // Création et affichage de la scène
        primaryStage.setScene(scene);
        primaryStage.setTitle("Choix de la difficulté de l'IA");
        primaryStage.show();
    }

    /**
     * Affiche le bouton de chargement
     */
    public String chargementBouton(Button okButton) {
        // Désactive le bouton pendant le chargement
        okButton.setDisable(true);

        // Créer un indicateur de progression
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

}
