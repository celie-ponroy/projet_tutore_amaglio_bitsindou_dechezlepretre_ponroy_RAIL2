package affichage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

        // Événement lié au bouton de validation
        okButton.setOnAction(e -> {
            //si aucun choix de difficulté n'est fait pour le gardien
            if (gardienComboBox.getValue() == null || prisonnierComboBox.getValue() == null || (prisonnierComboBox.getValue() == null && gardienComboBox.getValue() == null)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Attention !");
                alert.setContentText("Veuillez choisir un niveau de difficulté ");
                alert.showAndWait();
            } else {
                //Création d'une simulation en fonction du choix de difficulté des 2 agents
                if (gardienComboBox.getValue() != null && prisonnierComboBox.getValue() != null) {
                    //Évenements lier au choix de difficulté
                    okButton.setOnAction(f -> {
                        Comportements comportementP; //stocke le comportement du prisonnier choisi
                        Comportements comportementG; //stocke le comportement du gardien choisi

                        //Switch pour le choix de difficulté du gardien
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

                        //Switch pour le choix de difficulté du prisonnier
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

                        //Création de la simulation
                        Simulation simulation = new Simulation(comportementG, comportementP);
                        MoteurJeu.jeu = simulation;
                        //Affichage du jeu
                        VuePrincipaleNonInteractive vp = new VuePrincipaleNonInteractive();
                        vp.update(MoteurJeu.jeu);
                        MoteurJeu.jeu.ajouterObservateur(vp);
                        root.getChildren().clear();
                        root.getChildren().add(vp);
                        primaryStage.setScene(scene);
                    });
                }
            }
        });

        // Ajout des éléments à la racine
        root.getChildren().addAll(title, container, okButton);

        // Création et affichage de la scène
        primaryStage.setScene(scene);
        primaryStage.setTitle("Choix de la difficulté de l'IA");
        primaryStage.show();
    }

}
