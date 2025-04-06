package affichage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class InformationsIa {
    static final int WIDTH = 700;
    static final int HEIGHT = 600;

    /**
     * Retourne une VBox contenant les informations sur les comportements des prisonniers
     *
     * @return
     */
    static VBox getInfosPrisonnier() {
        VBox vboxMain = new VBox();
        vboxMain.getStylesheets().add("style.css");
        var image = new ImageView("file:images/prisonnier.png");
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        vboxMain.getChildren().add(image);

        //on fait une liste de label pour les informations
        initLabel("Comportement aléatoire",
                "L'IA se déplace aléatoirement sur la carte.", vboxMain);
        initLabel("Arbre de décision déterministe 1.0",
                "L'IA se déplace en suivant des probabilités de présence du gardien" + "\n" +
                        " dites bayésiennes dans le but d'aller vers la sortie en l'évitant."
                , vboxMain);
        initLabel("Arbre de décision déterministe 2.0",
                "Cette IA fonctionne comme la version 1.0 mais une fois face au gardien, " + "\n" +
                        "le prisonnier cherche à fuir vers la sortie et non à l'opposé de l'adversaire.", vboxMain);

        initLabel("Réseau de neurones Renforcement",
                "L'IA se déplace en suivant un réseau de neurones ayant appris par QLearning,  "+ "\n"  +
                        "il ne tient pas compte de l'inférence bayesienne.", vboxMain);

        return vboxMain;
    }

    /**
     * Retourne une VBox contenant les informations sur les comportements des gardiens
     *
     * @return
     */
    public static VBox getInfosGardien() {
        VBox vboxMain = new VBox();
        vboxMain.getStylesheets().add("style.css");
        var image = new ImageView("file:images/gardien.png");
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        vboxMain.getChildren().add(image);

        //on fait une liste de label pour les informations
        initLabel("Comportement aléatoire", "L'IA se déplace aléatoirement sur la carte.", vboxMain);
        initLabel("Arbre de décision déterministe 1.0",
                "L'IA se déplace en suivant des probabilités de présence du prisonnier " + "\n" +
                        " dites bayésiennes dans le but d'attraper le prisonnier et l'empêcher " + "\n" +
                        "de sortir.", vboxMain);
        initLabel("Arbre de décision aléatoire",
                "Cette IA fonctionne comme la version 1.0 mais il y ajoute de " + "\n" +
                        "l'aléatoire en ayant une chance d'aller vers une direction égale à " + "\n" +
                        "la probabilité de présence de l'adversaire.", vboxMain);
        initLabel("Réseau de neurones (MLP)",
                "L'IA se déplace en suivant un réseau de neurones ayant appris le comportement" + "\n"  +
                        " de l'arbre de décision déterministe 1.0.", vboxMain);
        initLabel("Réseau de neurones (CNN)",
                "L'IA se déplace en suivant un réseau de neurones avec une couche de" + "\n"
                        +" convolution ayant appris le comportement de l'arbre de décision déterministe 1.0.", vboxMain);

        return vboxMain;
    }

    /**
     * Lance une fenetre contenant les informations sur les comportements des prisonniers
     *
     * @return
     */
    public static void popUpPrisonnier() {
        HBox hBox = new HBox();
        var gardien = getInfosPrisonnier();
        gardien.getStyleClass().add("popup");
        gardien.setPrefSize(WIDTH, HEIGHT);

        hBox.getChildren().add(gardien);
        hBox.getStyleClass().add("popup");
        Scene secondScene = new Scene(hBox, WIDTH, HEIGHT);

        Stage newWindow = new Stage();
        newWindow.setTitle("Informations sur les comportements du prisonnier");
        newWindow.setScene(secondScene);

        newWindow.setX(100);
        newWindow.setY(100);
        newWindow.setResizable(false);
        newWindow.setMaxWidth(WIDTH);
        newWindow.setMaxHeight(HEIGHT);

        newWindow.alwaysOnTopProperty();

        newWindow.show();
    }

    /**
     * Lance une fenetre contenant les informations sur les comportements des gardiens
     *
     * @return
     */
    public static void popUpGardien() {
        HBox hBox = new HBox();
        var gardien = getInfosGardien();
        gardien.getStyleClass().add("popup");
        gardien.setPrefSize(WIDTH, HEIGHT);

        hBox.getChildren().add(gardien);
        hBox.getStyleClass().add("popup");
        Scene secondScene = new Scene(hBox, WIDTH, HEIGHT);

        Stage newWindow = new Stage();
        newWindow.setTitle("Informations sur les comportements du gardien");
        newWindow.setScene(secondScene);

        newWindow.setX(100);
        newWindow.setY(100);
        newWindow.setResizable(false);
        newWindow.setMaxWidth(WIDTH);
        newWindow.setMaxHeight(HEIGHT);

        newWindow.alwaysOnTopProperty();

        newWindow.show();
    }

    /**
     * Lance une fenetre contenant les informations sur les comportements des gardiens et des prisonniers
     */
    public static void popUpNonInteractif() {
        HBox hBox = new HBox();
        var gardien = getInfosGardien();
        gardien.getStyleClass().add("popup");
        gardien.setPrefSize(WIDTH, HEIGHT);
        var prisonnier = getInfosPrisonnier();
        prisonnier.setPrefSize(WIDTH, HEIGHT);
        prisonnier.getStyleClass().add("popup");
        hBox.getChildren().addAll(gardien, prisonnier);
        hBox.getStyleClass().add("popup");

        Scene secondScene = new Scene(hBox, WIDTH * 2, HEIGHT);

        Stage newWindow = new Stage();
        newWindow.setTitle("Informations sur les comportements du gardien et du prisonnier");
        newWindow.setScene(secondScene);

        newWindow.setX(100);
        newWindow.setY(100);
        newWindow.setResizable(false);
        newWindow.setMaxWidth(WIDTH * 2);
        newWindow.setMaxHeight(HEIGHT);
        newWindow.alwaysOnTopProperty();

        newWindow.show();

    }

    /**
     * Initialise un label avec un titre et une description et l'ajoute à la VBox
     *
     * @param titre
     * @param description
     * @param vboxMain
     */
    private static void initLabel(String titre, String description, VBox vboxMain) {
        Label label = new Label(titre);
        label.getStyleClass().add("infos");
        Text textDescription = new Text();
        textDescription.setTextAlignment(TextAlignment.CENTER);
        textDescription.setText(description);
        textDescription.getStyleClass().add("cache");
        label.setOnMouseClicked(e -> {
            toggleLabel(textDescription);
        });
        vboxMain.getChildren().addAll(label, textDescription);
    }

    /**
     * Cache ou affiche un text
     *
     * @param description
     */
    private static void toggleLabel(Text description) {
        if (description.getStyleClass().contains("petit")) {
            description.getStyleClass().remove("petit");
            description.getStyleClass().add("cache");
        } else {
            description.getStyleClass().remove("cache");
            description.getStyleClass().add("petit");
        }
    }

    /**
     * Retourne un bouton d'information
     *
     * @return
     */
    public static Button getButtonInfo() {
        Button info = new Button();
        Image img = new Image("file:images/info.png");
        ImageView view = new ImageView(img);
        info.setPrefSize(40, 40);
        view.setFitHeight(40);
        view.setPreserveRatio(true);

        info.setStyle("-fx-background-color: transparent;");
        info.setGraphic(view);
        return info;
    }
}
