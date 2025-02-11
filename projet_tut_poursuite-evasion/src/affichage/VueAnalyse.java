package affichage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import lancercalculs.LancerAnalyse;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Comportements;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.List;

public class VueAnalyse extends VueSimulation implements DessinJeu {

    //Attribuits
    private PieChart camembert = new PieChart();
    private LineChart<String, Number> courbes;
    private LancerAnalyse lancerAnalyse;
    private int nbIterationsInt;
    private ObservableList<PieChart.Data> pieChartData;

    //constructeur
    public VueAnalyse() {
        super();
        this.lancerAnalyse = new LancerAnalyse();
        this.courbes = new LineChart<>(new CategoryAxis(), new NumberAxis());
        this.pieChartData = FXCollections.observableArrayList();
        this.camembert = new PieChart(pieChartData);
        this.courbes = new LineChart<>(new CategoryAxis(), new NumberAxis());
    }

    /**
     * Méthode qui crée la vue analyse
     */
    public VBox createAnalyseView(Stage primaryStage){
        // VBox principale
        VBox root = new VBox(30);

        // GridPane légende
        GridPane gridLegende;
        gridLegende = initLegende();

        // Nombre d'itérations
        Label nbIteration = new Label("Nombre de partie(s) : 57/100");
        nbIteration.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // ComboBox prisonnier
        ComboBox<String> comboBoxPrisonnier = new ComboBox<>();
        comboBoxPrisonnier.getItems().addAll(
                "Arbre de décision déterministe 1.0",
                "Arbre de décision déterministe 2.0",
                "Comportement aléatoire",
                "Réseau de neurones 1.0"
        );


        // ComboBox gardien
        ComboBox<String> comboBoxGardien = new ComboBox<>();
        comboBoxGardien.getItems().addAll(
                "Arbre de décision déterministe",
                "Arbre de décision aléatoire",
                "Comportement aléatoire",
                "Réseau de neurones 1.0"
        );

        //Intput pour le nombre d'itérations (uniquement des chiffres)
        TextField nbIterations = new TextField();
        nbIterations.setPromptText("Nombre de parties");
        nbIterations.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nbIterations.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        //Bouton pour revenir au menu principal
        Button retourMenu = new Button("Retour au menu principal");
        retourMenu.setPrefSize(250, 50);
        VueMenus vm = new VueMenus();
        retourMenu.setOnAction(e -> vm.afficherMenuPrincipal());

        // Bouton lancer simulation
        Button lancerBtn = new Button("Lancer");

        // Conteneur pour les ComboBox et le bouton
        HBox choixContainer = new HBox(20);
        choixContainer.setAlignment(Pos.CENTER);
        choixContainer.getChildren().addAll(comboBoxGardien, comboBoxPrisonnier, nbIterations,lancerBtn);

        //Création du container top
        VBox topContainer = initTopContainer(nbIteration, choixContainer, retourMenu);

        // HBox pour aligner la légende à gauche et le topContainer au centre
        HBox headerContainer = new HBox(20);
        headerContainer.setAlignment(Pos.TOP_CENTER);
        headerContainer.setPadding(new Insets(20, 20, 20, 20));

        // Permet à topContainer de s'étendre et de centrer son contenu
        HBox.setHgrow(topContainer, Priority.ALWAYS);

        headerContainer.getChildren().addAll(topContainer);

        // HBox pour les graphiques et le labyrinthe
        HBox graphiques = new HBox(20);
        graphiques.setAlignment(Pos.CENTER);
        graphiques.getChildren().addAll(camembert, initLabyrinthe(false), courbes);

        // Conteneur pour centrer les graphiques verticalement et horizontalement
        VBox graphiquesWrapper = new VBox(graphiques);
        graphiquesWrapper.setAlignment(Pos.CENTER);
        VBox.setVgrow(graphiquesWrapper, Priority.ALWAYS);

//        root.getStylesheets().add("style.css");

        // Bouton lancer simulation

        lancerBtn.setPrefSize(150, 50);
        lancerBtn.setOnAction(e -> {
            if (comboBoxGardien.getValue() == null || comboBoxPrisonnier.getValue() == null ||
                    (comboBoxPrisonnier.getValue() == null && comboBoxGardien.getValue() == null ||
                            nbIterations.getText().isEmpty())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Attention !");
                alert.setContentText("Veuillez remplir tous les champs");
                alert.showAndWait();
            } else {
                // Réinitialisation des données avant de lancer une nouvelle série
                lancerAnalyse.reinitialiser();

                // Réinitialisation des graphiques
                courbes.getData().clear();
                camembert.getData().clear();

                nbIterationsInt = Integer.parseInt(nbIterations.getText());
                Comportements[] tabChoix = choixComboBox(comboBoxPrisonnier, comboBoxGardien);

                MoteurJeu.jeu = lancerAnalyse;
                lancerAnalyse.ajouterObservateur(this);
                lancerAnalyse.lancerAnalyse(nbIterationsInt, tabChoix[0], tabChoix[1]);

                this.update(lancerAnalyse);
            }
        });
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.getChildren().addAll(headerContainer, graphiquesWrapper, gridLegende);
        root.setAlignment(Pos.BOTTOM_RIGHT);

        // Définition de la scène
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Analyse des données");
        primaryStage.show();

        return root;
    }

    /**
     * Méthode qui permet de créer la legende du labyrinthe
     */

    public GridPane initLegende() {
        // GridPane légende (toujours en haut à gauche)
        GridPane gridLegende = new GridPane();
        gridLegende.setMaxWidth(Region.USE_PREF_SIZE);
        gridLegende.setMaxHeight(Region.USE_PREF_SIZE);
        gridLegende.setHgap(10);
        gridLegende.setPadding(new Insets(25, 25, 25, 25));

        ImageView prisonnierView = new ImageView(new Image("file:images/prisonnier.png"));
        ImageView gardienView = new ImageView(new Image("file:images/gardien.png"));
        prisonnierView.setFitWidth(80);
        prisonnierView.setFitHeight(80);
        gardienView.setFitWidth(80);
        gardienView.setFitHeight(80);

        gridLegende.add(prisonnierView, 0, 0);
        gridLegende.add(gardienView, 2, 0);

        Line lignePrisonnier = new Line(0, 0, 30, 0);
        lignePrisonnier.setStroke(Color.BLUE);
        lignePrisonnier.setStrokeWidth(3);

        Line ligneGardien = new Line(0, 0, 30, 0);
        ligneGardien.setStroke(Color.RED);
        ligneGardien.setStrokeWidth(3);

        gridLegende.add(lignePrisonnier, 1, 0);
        gridLegende.add(ligneGardien, 3, 0);

        gridLegende.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-padding: 10px;");

        return gridLegende;
    }
    /**
     * Méthode qui crée le container top
     */
    public VBox initTopContainer(Label nbIteration, HBox choixContainer, Button retourMenu) {
        // VBox pour centrer nbIteration et choixContainer en haut
        VBox topContainer = new VBox(10);
        topContainer.setAlignment(Pos.TOP_CENTER); // Garde l'alignement en haut mais centré en largeur

        // Centrer nbIteration dans la VBox
        nbIteration.setAlignment(Pos.CENTER);
        VBox.setMargin(nbIteration, new Insets(0, 0, 0, 0)); // Évite les décalages

        topContainer.getChildren().addAll(nbIteration, choixContainer, retourMenu);
        return topContainer;
    }

    /**
     * Méthode qui gère les choix des ComboBox
     * @return les comportements choisis
     */
    public Comportements[] choixComboBox(ComboBox<String> comboBoxPrisonnier, ComboBox<String> comboBoxGardien) {
        Comportements comportementP; //stocke le comportement du prisonnier choisi
        Comportements comportementG; //stocke le comportement du gardien choisi

        //Switch pour le choix de difficulté du gardien
        switch (comboBoxGardien.getValue()) {
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
                throw new IllegalStateException("Unexpected value: " + comboBoxGardien.getValue());
        }

        //Switch pour le choix de difficulté du prisonnier
        switch (comboBoxPrisonnier.getValue()) {
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
                throw new IllegalStateException("Unexpected value: " + comboBoxPrisonnier.getValue());

        }

        //Ajoute les comportements choisis dans un tableau
        Comportements[] comportements = {comportementG, comportementP};
        return comportements; //retourne le tableau des comporteemnts choisis

        //Création du nombre de simulation en fonction du nombre d'itérations entré par l'utilisateur
        //Simulation simulation = new Simulation(comportementG, comportementP);
        //MoteurJeu.jeu = simulation;
    }

    /**
     * Méthode créant le graphique camembert en fonction des simulations effectuées
     */

    public void afficherCamembert() {
        int nbIte = lancerAnalyse.getNbIterationCourrante();
        if (nbIte == 0) {
            nbIte = 1;
        } else {
            System.out.println("Nombre d'itérations courante  : " + nbIte);
        }
        System.out.println("Nombre de parties : " + nbIte);
        System.out.println("Nombre de victoire gardien : " + lancerAnalyse.getNbVictoireGardien());
        System.out.println("Nombre de victoire prisonnier : " + lancerAnalyse.getNbVictoirePrisonnier());
        System.out.println("Nombre de match null : " + lancerAnalyse.getMatchNull());

        // Effacer les anciennes données
        pieChartData.clear();

        // Ajouter de nouvelles données
        if (lancerAnalyse.getNbVictoireGardien() > 0) {
            pieChartData.add(new PieChart.Data("Victoire Gardien", lancerAnalyse.getNbVictoireGardien()));
        }
        if (lancerAnalyse.getNbVictoirePrisonnier() > 0) {
            pieChartData.add(new PieChart.Data("Victoire Prisonnier", lancerAnalyse.getNbVictoirePrisonnier()));
        }
        if (lancerAnalyse.getMatchNull() > 0) {
            pieChartData.add(new PieChart.Data("Match Null", lancerAnalyse.getMatchNull()));
        }

        camembert.setTitle("Résultats des parties");

        // Ajout des tooltips pour afficher le pourcentage
        for (PieChart.Data data : camembert.getData()) {
            Tooltip tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.seconds(0.2)); // Petit délai pour éviter les clignotements

            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                double total = pieChartData.stream().mapToDouble(PieChart.Data::getPieValue).sum();
                double percentage = (data.getPieValue() / total) * 100;
                tooltip.setText(String.format("%.1f%%", percentage)); // Formate à 1 décimale
                Tooltip.install(data.getNode(), tooltip); // Associe le tooltip au nœud
            });
        }
    }


    /**
     * Méthode créant le graphique en courbes en fonction des simulations effectuées
     */
    public LineChart<String, Number> graphiqueCourbes() {
        courbes.getData().clear();

        // Configuration des axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Parties");
        yAxis.setLabel("Nombre de déplacements");

        // Rotation des labels de l'axe X pour éviter le chevauchement
        xAxis.setTickLabelRotation(45);

        // Configuration du titre
        courbes.setTitle("Nombres de déplacements pour chaque personnage par parties");

        // Création des séries de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Nommage des séries pour la légende
        series.setName("Nombre de déplacements");

        // Activation de la légende
        courbes.setLegendVisible(true);

        // Ajout des données pour chaque partie
        for (int i = 0; i < nbIterationsInt; i++) {
            String partie = "P" + (i + 1);  // Utilisation d'un format plus court pour les labels
            series.getData().add(new XYChart.Data<>(partie, lancerAnalyse.getNbDeplacementPerso(i)));
        }

        // Ajout des séries au graphique
        courbes.getData().addAll(series);

        // Configuration des points sur les courbes
        courbes.setCreateSymbols(true);

        // Style des courbes
        series.getNode().setStyle("-fx-stroke: blue; -fx-stroke-width: 2px;");

        // Ajustement de l'espacement
        courbes.setHorizontalGridLinesVisible(true);
        courbes.setVerticalGridLinesVisible(true);

        // Amélioration de la lisibilité
        courbes.setAnimated(false);  // Désactive les animations pour une meilleure performance

        return courbes;
    }

    /**
     * Met à jour la vue
     * @param jeu jeu a afficher
     */
    @Override
    public void update(Jeu jeu) {
        // Récuperation de la simulation
        this.lancerAnalyse = (LancerAnalyse) jeu;

//        System.out.println("TEEEEEEEST");
        // Mise à jour des graphiques
        afficherCamembert();

        // Mise à jour des courbes
        graphiqueCourbes();

    }

    @Override
    protected void setOpacityPersonnage() {
        prisonnierView.setOpacity(0);
        gardienView.setOpacity(0);
    }

    @Override
    protected void updatePositions() {

    }
}