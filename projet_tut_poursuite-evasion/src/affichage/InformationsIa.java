package affichage;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class InformationsIa {
    static VBox getInfosPrisonnier() {
        VBox vboxMain = new VBox();
        vboxMain.getStylesheets().add("style.css");
        var image = new ImageView("file:images/prisonnier.png");
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        vboxMain.getChildren().add(image);

        //on fait une liste de label pour les informations
        Label comportementAleatoire = new Label("Comportement aléatoire");
        comportementAleatoire.getStyleClass().add("infos");
        comportementAleatoire.setOnMouseClicked(e -> {
            System.out.println("Comportement aléatoire");
        });
        Label arbre1 = new Label("Arbre de décision déterministe 1.0");
        arbre1.getStyleClass().add("infos");
        arbre1.setOnMouseClicked(e -> {
            System.out.println("Arbre de décision déterministe 1.0");
        });
        Label arbre2 = new Label("Arbre de décision déterministe 2.0");
        arbre2.getStyleClass().add("infos");
        arbre2.setOnMouseClicked(e -> {
            System.out.println("Arbre de décision déterministe 2.0");
        });
        Label reseauNeurones = new Label("Réseau de neurones 1.0");
        reseauNeurones.getStyleClass().add("infos");
        reseauNeurones.setOnMouseClicked(e -> {
            System.out.println("Réseau de neurones 1.0");
        });
        vboxMain.getChildren().addAll(comportementAleatoire, arbre1, arbre2, reseauNeurones);

        return vboxMain;
    }

}
