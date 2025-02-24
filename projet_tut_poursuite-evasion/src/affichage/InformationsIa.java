package affichage;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class InformationsIa {
    /**
     * Retourne une VBox contenant les informations sur les comportements des prisonniers
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
        initLabel("Comportement aléatoire","L'IA se déplace aléatoirement sur la carte",vboxMain);
        initLabel("Arbre de décision déterministe 1.0","L'IA se déplace en suivant un arbre de décision déterministe",vboxMain);
        initLabel("Arbre de décision déterministe 2.0","L'IA se déplace en suivant un arbre de décision déterministe",vboxMain);
        initLabel("Réseau de neurones 1.0","L'IA se déplace en suivant un réseau de neurones",vboxMain);

        return vboxMain;
    }

    /**
     * Retourne une VBox contenant les informations sur les comportements des gardiens
     * @return
     */
    public static VBox getInfosGardien(){
        VBox vboxMain = new VBox();
        vboxMain.getStylesheets().add("style.css");
        var image = new ImageView("file:images/gardien.png");
        image.setFitHeight(100);
        image.setPreserveRatio(true);
        vboxMain.getChildren().add(image);

        //on fait une liste de label pour les informations
        initLabel("Comportement aléatoire","L'IA se déplace aléatoirement sur la carte",vboxMain);
        initLabel("Arbre de décision déterministe 1.0","L'IA se déplace en suivant un arbre de décision déterministe",vboxMain);
        initLabel("Arbre de décision aléatoire","L'IA se déplace en suivant un arbre de décision déterministe",vboxMain);
        initLabel("Réseau de neurones 1.0","L'IA se déplace en suivant un réseau de neurones",vboxMain);

        return vboxMain;
    }

    /**
     * Retourne une alerte contenant les informations sur les comportements des prisonniers
     * @return
     */
    public static Alert getAlertPrisonnier(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations sur les comportements du prisonnier");
        alert.setHeaderText("Informations sur les comportements du prisonnier");
        VBox vboxMain = getInfosPrisonnier();
        vboxMain.getStyleClass().add("popup");
        alert.getDialogPane().setContent(vboxMain);
        alert.getDialogPane().getStyleClass().add("popup");

        return alert;
    }

    /**
     * Retourne une alerte contenant les informations sur les comportements des gardiens
     * @return
     */
    public static Alert getAlertGardien(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations sur les comportements du gardien");
        alert.setHeaderText("Informations sur les comportements du gardien");
        VBox vboxMain = getInfosGardien();
        vboxMain.getStyleClass().add("popup");
        alert.getDialogPane().setContent(vboxMain);
        alert.getDialogPane().getStyleClass().add("popup");

        return alert;
    }
    public static Alert getAlertNonInteractif(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informations sur les comportements du gardien et du prisonnier");
        alert.setHeaderText("Informations sur les comportements du gardien et du prisonnier");
        HBox hBox = new HBox();
        var gardien =  getInfosGardien();
        gardien.getStyleClass().add("popup");
        var prisonnier = getInfosPrisonnier();
        prisonnier.getStyleClass().add("popup");
        hBox.getChildren().addAll(gardien,prisonnier);
        hBox.getStyleClass().add("popup");
        alert.getDialogPane().setContent(hBox);
        alert.getDialogPane().getStyleClass().add("popup");

        return alert;
    }

    /**
     * Initialise un label avec un titre et une description et l'ajoute à la VBox
     * @param titre
     * @param description
     * @param vboxMain
     */
    private  static void initLabel(String titre, String description,VBox vboxMain){
        Label label = new Label(titre);
        label.getStyleClass().add("infos");
        Label labelDescription = new Label(description);
        labelDescription.getStyleClass().add("cache");
        label.setOnMouseClicked(e -> {
            toggleLabel(labelDescription);
            System.out.println(titre);
        });
        vboxMain.getChildren().addAll(label,labelDescription);
    }

    /**
     * Cache ou affiche un label
     * @param label
     */
    private static void toggleLabel(Label label){
        if(label.getStyleClass().contains("petit")) {
            label.getStyleClass().remove("petit");
            label.getStyleClass().add("cache");
        }else {
            label.getStyleClass().remove("cache");
            label.getStyleClass().add("petit");
        }
    }

}
