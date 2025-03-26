package affichage;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import moteur.MoteurJeu;


public class Credits {
    public void start(Stage primaryStage, MoteurJeu jeu) {
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        VBox creditsBox = new VBox(30);
        creditsBox.setSpacing(20);
        creditsBox.setAlignment(Pos.CENTER);

        this.creerTache(creditsBox, "Développeurs", new String[]{"Célie Ponroy", "Maëlle Bitsindou", "Luc Dechezleprêtre", "Matias Amaglio"});
        this.creerTache(creditsBox, "Graphisme", new String[]{"Célie Ponroy", "Matias Amaglio"});
        this.creerTache(creditsBox, "Musique", new String[]{"Maëlle Bitsindou"});
        this.creerTache(creditsBox, "Conception", new String[]{"Célie Ponroy", "Maëlle Bitsindou", "Luc Dechezleprêtre", "Matias Amaglio"});
        this.creerTache(creditsBox, "Tests empiriques", new String[]{"Matias Amaglio","Maëlle Bitsindou","Célie Ponroy", "Luc Dechezleprêtre" });
        this.creerTache(creditsBox, "Création de page de crédits", new String[]{"Célie Ponroy"});
        this.creerTache(creditsBox, "Réseaux de neurones", new String[]{ "Matias Amaglio","Luc Dechezleprêtre"});
        this.creerTache(creditsBox, "Interface", new String[]{ "Maëlle Bitsindou","Célie Ponroy" });
        this.creerTache(creditsBox, "Arbre de décision", new String[]{ "Célie Ponroy"});
        this.creerTache(creditsBox, "Analyse", new String[]{"Maëlle Bitsindou"});
        this.creerTache(creditsBox, "Sauvegarde", new String[]{"Célie Ponroy"});
        this.creerTache(creditsBox,"Débat Houleux", new String[]{"Luc Dechezleprêtre","Matias Amaglio","Célie Ponroy","Maëlle Bitsindou" });
        this.creerTache(creditsBox,"Création du sujet", new String[]{"Célie Ponroy","Maëlle Bitsindou","Luc Dechezleprêtre","Matias Amaglio","Guenaël Cabanes"});
        this.creerTache(creditsBox,"Acteurs",new String[]{"gardien.png","prisonnier.png"});
        this.creerTache(creditsBox,"Decors",new String[]{"mur.png","sol.png","sortie.png", "camera.png","raccourciGardien.png"});
        this.creerTache(creditsBox, "En la mémoire de", new String[]{"Librairies Java abandonnées n°1 : Libraie de M. Boniface", "Librairies Java abandonnées n°2 : Neuroph","La patience de Matias"});
        this.creerTache(creditsBox, "Soutenances", new String[]{"Célie Ponroy", "Maëlle Bitsindou", "Luc Dechezleprêtre", "Matias Amaglio", "Guenaël Cabanes", "Isabelle Debled-Rennesson"});
        this.creerTache(creditsBox,"Avec la participation de", new String[]{"Matias Amaglio","Luc Dechezleprêtre","Maëlle Bitsindou","Célie Ponroy" , "Guenaël Cabanes"});
        this.creerTache(creditsBox, "Remerciements", new String[]{"Célie Ponroy", "Maëlle Bitsindou", "Luc Dechezleprêtre", "Matias Amaglio", "Guenaël Cabanes", "Isabelle Debled-Rennesson","Charlemiam","Les deux collègues de M. Cabanes","Nino Arcelin"});
        this.creerTache(creditsBox,"Ce projet à été réalisé dans la joie et la bonne humeur", new String[]{""});

        ScrollPane scrollPane = new ScrollPane(creditsBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, screenWidth, screenHeight);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Crédits défilants");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();

        creditsBox.setTranslateY(screenHeight);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(30), creditsBox);
        transition.setToY(-creditsBox.getBoundsInParent().getHeight()); // Monte jusqu'à disparaître
        transition.setCycleCount(1);
        transition.setInterpolator(javafx.animation.Interpolator.LINEAR);
        transition.setOnFinished(e -> {
            PageAccueil.lancerPageAcceuil( jeu);
            primaryStage.close();});
        transition.play();
    }

    private void creerTache( VBox credit, String tache, String[] noms){
        Text tacheText = new Text(tache);
        tacheText.setFont(new Font("Verdana", 24));
        tacheText.setStyle("-fx-fill: yellow; -fx-font-weight: bold; -fx-font-size: 24px;");
        credit.getChildren().add(tacheText);
        for (int i = 0; i < noms.length; i++) {
            Text nomText = new Text(noms[i]);
            nomText.setFont(new Font("Verdana", 24));
            nomText.setStyle("-fx-fill: yellow; -fx-font-size: 24px;");
            credit.getChildren().add(nomText);
        }

    }
}
