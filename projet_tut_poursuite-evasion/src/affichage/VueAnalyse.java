package affichage;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lancercalculs.LancerAnalyse;
import moteur.Jeu;
import moteur.MoteurJeu;
import simulation.Comportements;
import javafx.scene.control.Tooltip;
import simulation.Simulation;
import simulation.personnages.Position;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VueAnalyse extends VueSimulation implements DessinJeu {

    //Attribuits
    private PieChart camembert = new PieChart();
    private LineChart<String, Number> courbes;
    private LancerAnalyse lancerAnalyse;
//    private TextField nbIterations;
    private int nbIterationsInt;
    private ObservableList<PieChart.Data> pieChartData;
    private final Rectangle[][] caseFiltreChaleur = new Rectangle[Simulation.CARTE.length][Simulation.CARTE[0].length];

    //constructeur
    public VueAnalyse() {
        super();
        this.lancerAnalyse = new LancerAnalyse();
        this.courbes = new LineChart<>(new CategoryAxis(), new NumberAxis());
        this.pieChartData = FXCollections.observableArrayList();
        this.camembert = new PieChart(pieChartData);
        camembert.setTitle("Résultats des parties");
        camembert.setLabelsVisible(true);
        this.courbes = new LineChart<>(new CategoryAxis(), new NumberAxis());
        courbes.setTitle("Nombre de déplacements par parties");
    }

    /**
     * Méthode qui crée la vue analysevoici
     */
    public VBox createAnalyseView(Stage primaryStage){
        // VBox principale
        VBox root = new VBox(30);
        root.getStylesheets().add("style.css");

        // Nombre d'itérations
        Label nbIteration = new Label();
        nbIteration.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        nbIteration.setText("Nombre de partie(s) : .../...");

        // ComboBox prisonnier
        ComboBox<String> comboBoxPrisonnier = new ComboBox<>();
        comboBoxPrisonnier.getItems().addAll(
                "Arbre de décision déterministe 1.0",
                "Arbre de décision déterministe 2.0",
                "Comportement aléatoire",
                "Réseau de neurones 1.0"
        );
        //Ajout d'un titre dans la comboBox
        comboBoxPrisonnier.setPromptText("Choix du prisonnier");

        comboBoxPrisonnier.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Choix du prisonnier"); // Placeholder
                    setStyle("-fx-text-fill: gray;"); // Texte gris clair
                } else {
                    setText(item);
                    setStyle(""); // Réinitialise le style normal
                }
            }
        });


        // ComboBox gardien
        ComboBox<String> comboBoxGardien = new ComboBox<>();
        comboBoxGardien.getItems().addAll(
                "Arbre de décision déterministe",
                "Arbre de décision aléatoire",
                "Comportement aléatoire",
                "Réseau de neurones 1.0"
        );
        //Ajout d'un titre dans la comboBox avec une couleur grise
        comboBoxGardien.setPromptText("Choix du gardien");

        comboBoxGardien.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Choix du gardien"); // Placeholder
                    setStyle("-fx-text-fill: gray;"); // Texte gris clair
                } else {
                    setText(item);
                    setStyle(""); // Réinitialise le style normal
                }
            }
        });



        //Intput pour le nombre d'itérations (uniquement des chiffres jusqu'à 400 itérations)
        TextField nbIterations = new TextField();
        nbIterations.setPromptText("Nombre de parties");

        //Vérification que l'utilisateur rentre bien un nombre inférieur ou égal à 400 sinon affiche un message de warning
        nbIterations.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nbIterations.setText(newValue.replaceAll("[^\\d]", ""));
            }
            //Vérification que l'utilisateur rentre bien un nombre pas trop élevé sinon messge de warning
            if (!newValue.isEmpty() && Integer.parseInt(newValue) > 400) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Attention !");
                alert.setHeaderText("Nombre d'itérations trop élevé");
                alert.setContentText("Veuillez entrer un nombre d'itérations inférieur ou égal à 400");
                alert.showAndWait();
                nbIterations.setText("");
            }
        });
        //Style pour le placeholder
        nbIterations.setStyle("-fx-prompt-text-fill: gray;");

        //Bouton pour revenir au menu principal
        Button retourMenu = new Button("Retour au menu principal");
        retourMenu.setPrefSize(250, 50);
        retourMenu.getStyleClass().add("important");
        VueMenus vm = new VueMenus(primaryStage);
        retourMenu.setOnAction(e -> vm.afficherMenuPrincipal());

        //Bouton pour pause la simulation
        Button pauseBtn = new Button("Pause");
        pauseBtn.setPrefSize(150, 50);

        // Bouton lancer simulation
        Button lancerBtn = new Button("Lancer");

        //Titres de chaque comboBox
        Label titrePrisonnier = new Label("Prisonnier");
        titrePrisonnier.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label titreGardien = new Label("Gardien");
        titreGardien.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label titreNbIterations = new Label("Nombre de parties");
        titreNbIterations.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Conteneur pour le titre et la comboBox du prisonnier
        VBox prisonnierContainer = new VBox(5);
        prisonnierContainer.setAlignment(Pos.CENTER);
        prisonnierContainer.getChildren().addAll(titrePrisonnier, comboBoxPrisonnier);

        // Conteneur pour le titre et la comboBox du gardien
        VBox gardienContainer = new VBox(5);
        gardienContainer.setAlignment(Pos.CENTER);
        gardienContainer.getChildren().addAll(titreGardien, comboBoxGardien);

        // Conteneur pour le titre et le champ de nombre d'itérations
        VBox iterationContainer = new VBox(5);
        iterationContainer.setAlignment(Pos.CENTER);
        iterationContainer.getChildren().addAll(titreNbIterations, nbIterations);

        // Conteneur pour les ComboBox et les boutons
        HBox choixContainer = new HBox(20);
        choixContainer.setAlignment(Pos.CENTER);
        choixContainer.getChildren().addAll(gardienContainer, prisonnierContainer, iterationContainer, lancerBtn, pauseBtn);

        // Conteneur pour le label du nombre d'itérations avec un espacement en bas
        VBox nbIterationContainer = new VBox(nbIteration);
        nbIterationContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(nbIterationContainer, new Insets(0, 0, 40, 0)); // Ajoute un espace sous le label

        //Création du container top
        VBox topContainer = initTopContainer(nbIterationContainer,choixContainer, retourMenu);

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
        var laby = initLabyrinthe(false);
        Rectangle[][] filtre = initFiltreChaleur();
        for (Rectangle[] rectangles: filtre) {
            for (Rectangle rectangle: rectangles) {
                laby.getChildren().add(rectangle);
            }
        }
        graphiques.getChildren().addAll(camembert,laby, courbes);

        // Conteneur pour centrer les graphiques verticalement et horizontalement
        VBox graphiquesWrapper = new VBox(graphiques);
        graphiquesWrapper.setAlignment(Pos.CENTER);
        VBox.setVgrow(graphiquesWrapper, Priority.ALWAYS);

        //Bouton pour mettre en pause la simulation et afficher le texte "Reprendre"
        pauseBtn.setPrefSize(150, 50);
        pauseBtn.setOnAction(e -> {
            if (lancerAnalyse.isPause()) {
                lancerAnalyse.setPause(false);
                pauseBtn.setText("Pause");
            } else {
                lancerAnalyse.setPause(true);
                pauseBtn.setText("Reprendre");
            }
        });

        // Bouton lancer simulation
        lancerBtn.setPrefSize(150, 50);
        // Dans le bouton lancerBtn
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
                lancerBtn.setDisable(true);

                // Réinitialisation des données
                Platform.runLater(() -> {
                    lancerAnalyse.reinitialiser();
                    courbes.getData().clear();
                    camembert.getData().clear();
                    caseFiltreChaleur[0][0].setOpacity(0);
                });

                nbIterationsInt = Integer.parseInt(nbIterations.getText());
                Comportements[] tabChoix = choixComboBox(comboBoxPrisonnier, comboBoxGardien);

                MoteurJeu.jeu = lancerAnalyse;
                lancerAnalyse.ajouterObservateur(this);
                lancerAnalyse.setNbIterationsTotal(nbIterationsInt);

                Thread analyseThread = new Thread(() -> {
                    for (int i = 1; i <= nbIterationsInt; i++) {
                        final int currentIteration = i;
                        // Exécution de l'analyse
                        lancerAnalyse.lancerAnalyse(i, tabChoix[0], tabChoix[1]);

                        // Mise à jour de l'interface
                        Platform.runLater(() -> {
                            nbIteration.setText("Nombre de partie(s) : " + currentIteration + "/" + nbIterationsInt);
                            update(lancerAnalyse);

                            if (currentIteration == nbIterationsInt) {
                                lancerBtn.setDisable(false);
                            }
                        });

                        try {
                            while (lancerAnalyse.isPause()) {
                                Thread.sleep(500); // Attente active en pause
                            }
                            Thread.sleep(1000); // Pause normale entre les itérations
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt(); // Bonne pratique pour gérer les interruptions
                        }

                    }
                });

                analyseThread.start();
            }
        });
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.getChildren().addAll(headerContainer, graphiquesWrapper);
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
     * Méthode qui crée le container top
     */
    public VBox initTopContainer(VBox nbIteration, HBox choixContainer, Button retourMenu) {
        // VBox pour centrer nbIteration et choixContainer en haut
        VBox topContainer = new VBox(10);
        topContainer.setAlignment(Pos.TOP_CENTER); // Garde l'alignement en haut mais centré en largeur

        // Centrer nbIteration dans la VBox
//        nbIteration.setAlignment(Pos.CENTER);
//        VBox.setMargin(nbIteration, new Insets(0, 0, 0, 0)); // Évite les décalages

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
    }

    /**
     * Méthode créant le graphique camembert en fonction des simulations effectuées
     */

    public void afficherCamembert() {
        Platform.runLater(() -> {
            int nbIte = lancerAnalyse.getNbIterationCourrante();
            if (nbIte == 0) {
                nbIte = 1;
            }

            // Effacer les anciennes données
            pieChartData.clear();

            // Enlever l'animation pour éviter les effets indésirables
            camembert.setAnimated(false);

            // Ajouter de nouvelles données avec leurs couleurs associées
            Map<String, String> couleurs = new HashMap<>();

            if (lancerAnalyse.getNbVictoireGardien() > 0) {
                PieChart.Data gardienData = new PieChart.Data("Victoire Gardien", lancerAnalyse.getNbVictoireGardien());
                pieChartData.add(gardienData);
                couleurs.put("Victoire Gardien", "#3B4466");
            }

            if (lancerAnalyse.getNbVictoirePrisonnier() > 0) {
                PieChart.Data prisonnierData = new PieChart.Data("Victoire Prisonnier", lancerAnalyse.getNbVictoirePrisonnier());
                pieChartData.add(prisonnierData);
                couleurs.put("Victoire Prisonnier", "#E26F1D");
            }

            if (lancerAnalyse.getMatchNull() > 0) {
                PieChart.Data nullData = new PieChart.Data("Match Null", lancerAnalyse.getMatchNull());
                pieChartData.add(nullData);
                couleurs.put("Match Null", "grey");
            }

            // Appliquer les styles après que le graphique soit rendu
            camembert.applyCss();
            camembert.layout();

            //Supprime les label
            camembert.setLabelsVisible(false);

            // Appliquer les couleurs aux sections du camembert
            for (PieChart.Data data : pieChartData) {
                String color = couleurs.getOrDefault(data.getName(), "black");

                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + color + ";");
                }
            }

            // Mettre à jour les couleurs de la légende
            Platform.runLater(() -> {
                Set<Node> legendItems = camembert.lookupAll(".chart-legend-item");
                for (Node legend : legendItems) {
                    if (legend instanceof Label) {
                        Label label = (Label) legend;
                        String text = label.getText();
                        if (couleurs.containsKey(text)) {
                            Node symbol = label.getGraphic();
                            if (symbol != null) {
                                symbol.setStyle("-fx-background-color: " + couleurs.get(text) + ";");
                            }
                        }
                    }
                }
            });

            // Ajouter des tooltips pour afficher les pourcentages
            for (PieChart.Data data : camembert.getData()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setShowDelay(Duration.seconds(0.2));

                final PieChart.Data finalData = data;
                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    double total = pieChartData.stream().mapToDouble(PieChart.Data::getPieValue).sum();
                    double percentage = (finalData.getPieValue() / total) * 100;
                    tooltip.setText(String.format("%.1f%%", percentage));
                    Tooltip.install(finalData.getNode(), tooltip);
                });
            }
        });
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

        //Augmenter la taille de l'axe des abscisses du graphique en fonction du nombre d'itérations
        xAxis.setCategories(FXCollections.observableArrayList(String.valueOf(nbIterationsInt)));


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
        courbes.setAnimated(false);

        //Ajout des tool tips sur les points
        for (XYChart.Series<String, Number> s : courbes.getData()) {
            for (XYChart.Data<String, Number> d : s.getData()) {
                Tooltip tooltip = new Tooltip();
                tooltip.setShowDelay(Duration.seconds(0.2)); // Petit délai pour éviter les clignotements

                d.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
                    tooltip.setText("Nombre de déplacements : " + d.getYValue());
                    Tooltip.install(d.getNode(), tooltip); // Associe le tooltip au nœud
                });
            }
        }

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

        // Mise à jour des graphiques
        afficherCamembert();

        // Mise à jour des courbes
        graphiqueCourbes();

        // Mise à jour du filtre de chaleur
        updateFiltreChaleur();

//        //Mise à jour des points de départ
//        updateStartPos();

    }

    @Override
    protected void setOpacityPersonnage() {
        prisonnierView.setOpacity(0);
        gardienView.setOpacity(0);
    }

    @Override
    protected void updatePositions() {

    }

    /**
     * Méthode qui initialise le filtre de chaleur
     */
    public Rectangle[][] initFiltreChaleur() {
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE.length; j++) {
                Rectangle rect = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rect.setX(i * TAILLE_CELLULE);
                rect.setY(j * TAILLE_CELLULE);
                rect.setOpacity(0);
                caseFiltreChaleur[i][j] = rect;
            }
        }
        return caseFiltreChaleur;
    }

    /**
     * Méthode qui normalise le nombre de visites
     */
    public double normaliser(int nbVis, HashMap<Position, Integer> casesVisitees) {
        int sum = 0;
        double res = 0;
        // pour chaque valeur de getcasesVisitees on ajoute la valeur à sum
        for (Position i :casesVisitees.keySet()) {
            //on verifie que la somme ne soit pas nulle
            if (casesVisitees.get(i) != 0) {
                sum += casesVisitees.get(i);
            }
            res = (double) nbVis / sum;
        }
        return res;
    }

    /**
     * Méthode qui met à jour le filtre de chaleur
     */
    public void updateFiltreChaleur() {
        // Réinitialise les couleurs de toutes les cases
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE.length; j++) {
                Rectangle rect = caseFiltreChaleur[i][j];
                rect.setFill(Color.rgb(0, 255, 0, 0));
                rect.setOpacity(0);
                Tooltip.uninstall(rect, null); // Enlever les anciens tooltips
            }
        }

        // Met à jour pour les cases visitées et les cases de départ
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE.length; j++) {
                Rectangle rect = caseFiltreChaleur[i][j];
                Position currentPos = new Position(i, j);

                // Vérifie si c'est une position de départ
                boolean isPrisonnierStart = lancerAnalyse.getCasesDepartPris().containsKey(currentPos);
                boolean isGardienStart = lancerAnalyse.getCasesDepartGard().containsKey(currentPos);

                // Case visitée ordinaire
                if (lancerAnalyse.getCasesVisitees().containsKey(currentPos)) {
                    rect.setOpacity(1);
                    int nbVisites = (int) lancerAnalyse.getCasesVisitees().get(currentPos);

                    // Position de départ du prisonnier
                    if (isPrisonnierStart) {
                        rect.setFill(Color.rgb(255, 165, 0)); // Orange
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText("Départ prisonnier : " + lancerAnalyse.getCasesDepartPris().get(currentPos));
                        Tooltip.install(rect, tooltip);
                    }
                    // Position de départ du gardien
                    else if (isGardienStart) {
                        rect.setFill(Color.rgb(0, 0, 255)); // Bleu
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText("Départ gardien : " + lancerAnalyse.getCasesDepartGard().get(currentPos));
                        Tooltip.install(rect, tooltip);
                    }
                    // Case visitée normale
                    else {
                        double opacite = Math.pow(normaliser(nbVisites, lancerAnalyse.getCasesVisitees()), 0.5);
                        rect.setFill(Color.rgb(0, 0, 255, opacite)); // Bleu avec transparence

                        Tooltip tooltip = new Tooltip();
                        tooltip.setText("Visites : " + nbVisites);
                        Tooltip.install(rect, tooltip);
                    }
                }
            }
        }
    }
}
