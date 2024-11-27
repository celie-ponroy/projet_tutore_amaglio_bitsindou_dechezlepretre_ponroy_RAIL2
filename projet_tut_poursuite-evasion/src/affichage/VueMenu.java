package affichage;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Classe qui affiche le menu principal
 */
public class VueMenu extends VBox{

    public VueMenu(){
        super();
    }

    /**
     * Affiche le menu principal de l'application
     * @param stage scene de l'application
     */
    public void afficherMenu(Stage stage){
        // Conteneur principal
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #d3d3d3;"); // Fond gris
        root.setSpacing(20); // Espacement entre les éléments
        root.setPrefSize(800, 600);
        root.setAlignment(Pos.CENTER); // Centre tous les éléments du VBox

        // Titre du menu
        Label title = new Label("Veuillez choisir un mode:");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Conteneur pour les boutons de mode
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20); // Espacement entre les boutons
        buttonBox.setAlignment(Pos.CENTER); // Centre les boutons horizontalement

        // Bouton "Mode interactif"
        Button modeInteractif = new Button("Mode interactif");
        modeInteractif.setPrefSize(200, 100);
        modeInteractif.setOnAction(e -> {
            // TODO: Afficher le jeu
        });

        // Bouton "Mode non interactif"
        Button modeNonInteractif = new Button("Mode non interactif");
        modeNonInteractif.setPrefSize(200, 100);
        modeNonInteractif.setOnAction(e -> {
            // TODO: Afficher le jeu
        });

        // Ajout des boutons dans la HBox
        buttonBox.getChildren().addAll(modeInteractif, modeNonInteractif);

        // Bouton "Quitter"
        Button quitter = new Button("Quitter");
        quitter.setPrefSize(150, 50);
        quitter.setOnAction(e -> stage.close());

        // Ajout des éléments au VBox principal
        root.getChildren().addAll(title, buttonBox, quitter);

        // Création et affichage de la scène
        Scene scene = new Scene(root);
        stage.setTitle("Menu principal");
        stage.setScene(scene);
        stage.show();

    }
}
