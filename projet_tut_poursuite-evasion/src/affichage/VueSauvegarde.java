package affichage;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import simulation.CaseEnum;
import simulation.Deplacement;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import static simulation.Simulation.*;

public class VueSauvegarde extends VueSimulation {
    private Label iterationLabel; // Label pour afficher le nombre d'itération
    private int tour;
    private VueBayesienne vB1, vB2;
    private Rectangle[][] filtreVision;
    private ImageView viewInteractifPerso, viewInteractifAdversaire;
    boolean interactive;


    //constructeur
    public VueSauvegarde(double width, double height, Simulation simulation) {
        super();
        this.simulation = simulation;
        TAILLE_CELLULE = (int) ((width - 6 * 10) / (CARTE[0].length) * 0.33);
        this.tour = 0;
        viewInteractifAdversaire = new ImageView();
        viewInteractifPerso = new ImageView();

    }

    /**
     * Initialise la vue avec les deux vues bayésiennes et le labyrinthe positionné entre elles.
     */
    private void init() {
        Pane labyPane = initLabyrinthe(true);
        var casesCameras = FiltreCamera.initFiltre(TAILLE_CELLULE, 0, 0);
        for (Rectangle[] rect : casesCameras) {
            for (Rectangle sousrect : rect) {
                labyPane.getChildren().add(sousrect);
            }
        }

        this.iterationLabel = new Label("Nombre d'itération: " + simulation.getNbTours());
        iterationLabel.setLayoutX(10);
        iterationLabel.setLayoutY(TAILLE_CELLULE * CARTE.length + TAILLE_CELLULE * 1);
        iterationLabel.setStyle("-fx-font-size: 11px;-fx-border-color: black; -fx-padding: 10;");


        // Vérifiez si `vbox` est déjà présent avant de l'ajouter
        if (!this.getChildren().contains(iterationLabel)) {
            this.getChildren().add(iterationLabel);
        }

        //Ajout de la vbox de l'iteration au Pane
        labyPane.getChildren().add(iterationLabel);

        // Création des deux vues bayésiennes dans des VBox distinctes

        //si la simulation est interactive
        interactive = simulation.getJoueur() != null;
        VBox vBoxGardien;
        VBox vBoxPrisonnier;
        if (interactive) {
            filtreVision = new Rectangle[simulation.CARTE[0].length][simulation.CARTE.length];

            if (simulation.getJoueur() == simulation.getGardien()) {
                vBoxGardien = creerVision("Vue joueur gardien"); //on ajoute le filtre vision
                vB1 = new VueBayesienne(this.simulation, simulation.getPrisonnier(), 0, 0, TAILLE_CELLULE);
                vBoxPrisonnier = createBayesienneView(simulation.getPrisonnier(), "Vue bayésienne du prisonnier", vB1);
            } else {
                vB2 = new VueBayesienne(this.simulation, simulation.getGardien(), 0, 0, TAILLE_CELLULE);
                vBoxGardien = createBayesienneView(simulation.getGardien(), "Vue bayésienne du gardien", vB2);
                vBoxPrisonnier = creerVision("Vue joueur prisonnier");
            }
        } else {
            vB1 = new VueBayesienne(this.simulation, simulation.getPrisonnier(), 0, 0, TAILLE_CELLULE);
            vB2 = new VueBayesienne(this.simulation, simulation.getGardien(), 0, 0, TAILLE_CELLULE);
            vBoxGardien = createBayesienneView(simulation.getGardien(), "Vue bayésienne du gardien", vB1);
            vBoxPrisonnier = createBayesienneView(simulation.getPrisonnier(), "Vue bayésienne du prisonnier", vB2);
        }

        // Mise en forme des VBox
        vBoxGardien.setAlignment(Pos.TOP_LEFT);
        vBoxPrisonnier.setAlignment(Pos.TOP_RIGHT);

        //Style
        vBoxGardien.setStyle("-fx-border-color: black; -fx-padding: 10;");
        vBoxPrisonnier.setStyle("-fx-border-color: black; -fx-padding: 10;");

        // HBox pour aligner les éléments (vue bayésienne 1, labyrinthe, vue bayésienne 2)
        HBox hbox = new HBox(20); // Espacement de 20px entre les éléments


        // Ajout des éléments à la HBox
        hbox.getChildren().add(vBoxGardien);
        hbox.getChildren().add(labyPane);
        hbox.getChildren().add(vBoxPrisonnier);
        hbox.setAlignment(Pos.CENTER);

        this.getChildren().clear();
        this.getChildren().addAll(hbox);

    }


    /**
     * Crée une vue Bayesienne associée à un personnage.
     */
    private VBox createBayesienneView(Personnage personnage, String titre, VueBayesienne vueBayesienne) {
        VBox vueBayesienneVBox = new VBox();
        vueBayesienneVBox.setSpacing(10);
        vueBayesienneVBox.setAlignment(Pos.TOP_CENTER);

        // Titre de la vue
        Label titleLabel = new Label(titre);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        vueBayesienneVBox.getChildren().addAll(titleLabel, vueBayesienne);

        return vueBayesienneVBox;
    }

    /**
     * Crée une vue Vision associée à un personnage.
     */
    private VBox creerVision(String titre) {
        var laby = creerLabyvision();
        //filtre vision
        initFiltreVision(laby);
        VBox vueVBox = new VBox();
        vueVBox.setSpacing(10);
        vueVBox.setAlignment(Pos.TOP_CENTER);

        // Titre de la vue
        Label titleLabel = new Label(titre);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        vueVBox.getChildren().addAll(titleLabel, laby);

        return vueVBox;
    }

    private Pane creerLabyvision() {
        // Création d'un conteneur pour le labyrinthe
        Pane labyrinthePane = new Pane();
        labyrinthePane.setPrefSize(TAILLE_CELLULE * Simulation.CARTE[0].length,
                TAILLE_CELLULE * Simulation.CARTE.length);
        // Création du labyrinthe à partir de la carte
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                StackPane stackPane = new StackPane();
                stackPane.setLayoutX(j * TAILLE_CELLULE);
                stackPane.setLayoutY(i * TAILLE_CELLULE);

                // Sélection de l'image en fonction de la case
                Image image = null;
                if (Simulation.CARTE[i][j] == CaseEnum.MUR.ordinal()) {
                    image = this.imageMur;
                } else if (Simulation.CARTE[i][j] == CaseEnum.SOL.ordinal() ||
                        Simulation.CARTE[i][j] == CaseEnum.SPAWN_GARDIEN.ordinal() ||
                        Simulation.CARTE[i][j] == CaseEnum.SPAWN_PRISONNIER.ordinal()) {
                    image = this.imageSol;
                } else if (Simulation.CARTE[i][j] == CaseEnum.SORTIE.ordinal()) {
                    image = this.imageSortie;
                } else if (Simulation.CARTE[i][j] == CaseEnum.RACCOURCI_GARDIEN.ordinal()) {
                    image = this.imageRaccourciGardien;
                } else if (Simulation.CARTE[i][j] == CaseEnum.CAMERA.ordinal()) {
                    if (avec_camera)
                        image = this.imageCamera;
                    else image = this.imageSol;
                }

                if (image != null) {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(TAILLE_CELLULE);
                    imageView.setFitHeight(TAILLE_CELLULE);
                    stackPane.getChildren().add(imageView);
                }

                // Ajout d'une zone de collision invisible
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.TRANSPARENT);
                stackPane.getChildren().add(rectangle);

                labyrinthePane.getChildren().add(stackPane); // Ajout au conteneur labyrinthe
            }
        }

        // Initialisation des personnages
        if (simulation.getJoueur() == simulation.getGardien()) {
            this.viewInteractifPerso.setImage(imageGardien);
            this.viewInteractifAdversaire.setImage(imagePrisonnier);

        } else {
            this.viewInteractifPerso.setImage(imagePrisonnier);
            this.viewInteractifAdversaire.setImage(imageGardien);
        }
        this.viewInteractifPerso.setPreserveRatio(true);
        this.viewInteractifPerso.setFitHeight(TAILLE_CELLULE);

        this.viewInteractifAdversaire.setPreserveRatio(true);
        this.viewInteractifAdversaire.setFitHeight(TAILLE_CELLULE);

        // Ajouter les personnages au conteneur labyrinthe
        labyrinthePane.getChildren().addAll(this.viewInteractifPerso, this.viewInteractifAdversaire);

        // Placement initial des personnages
        updatePositions();

        return labyrinthePane;
    }


    @Override
    protected void setOpacityPersonnage() {
        prisonnierView.setOpacity(1);
        gardienView.setOpacity(1);
        if (interactive) {
            Position p;
            Joueur tmp;
            Position ptmp = simulation.getHistoriquePosition().get(simulation.getJoueur()).get(tour);

            if (simulation.getJoueur() == simulation.getGardien()) {
                p = simulation.getHistoriquePosition().get(simulation.getPrisonnier()).get(tour);
                tmp = new Joueur(ptmp.getX(), ptmp.getY(), VISION_G);
            } else {
                p = simulation.getHistoriquePosition().get(simulation.getGardien()).get(tour);
                tmp = new Joueur(ptmp.getX(), ptmp.getY(), VISION_P);

            }
            if (tmp.getVision().contains(p)) {
                viewInteractifAdversaire.setOpacity(1);
            } else {
                viewInteractifAdversaire.setOpacity(0);
            }
        }

    }

    /**
     * Met à jour uniquement les positions des personnages
     */
    protected void updatePositions() {
        // Met à jour la position du prisonnier
        Position p = simulation.getHistoriquePosition().get(simulation.getPrisonnier()).get(tour);
        Position g = simulation.getHistoriquePosition().get(simulation.getGardien()).get(tour);
        setPositions(p, prisonnierView);
        setPositions(g, gardienView);
        setOpacityPersonnage();
        if (simulation.getJoueur() == simulation.getGardien()) {
            setPositions(g, viewInteractifPerso);
            setPositions(p, viewInteractifAdversaire);
            VueSimulation.updateDirectionPersonnage(tour, simulation.getHistoriqueDeplacement().get(simulation.getGardien()), "gardien", viewInteractifPerso);
            VueSimulation.updateDirectionPersonnage(tour, simulation.getHistoriqueDeplacement().get(simulation.getPrisonnier()), "prisonnier", viewInteractifAdversaire);

        } else {
            setPositions(p, viewInteractifPerso);
            setPositions(g, viewInteractifAdversaire);
            VueSimulation.updateDirectionPersonnage(tour, simulation.getHistoriqueDeplacement().get(simulation.getGardien()), "gardien", viewInteractifAdversaire);
            VueSimulation.updateDirectionPersonnage(tour, simulation.getHistoriqueDeplacement().get(simulation.getPrisonnier()), "prisonnier", viewInteractifPerso);

        }
        updateDirections(tour);
    }

    /**
     * Méthode principale de l'interface DessinJeu
     */
    public void update() {
        init();

        javafx.scene.control.Button precedent = new Button("Précédent");
        precedent.setPrefSize(200, 75);
        precedent.setOnAction(e -> {

            if (tour > 0) {
                tour -= 1;
                updatePositions();
                updateIteration();
                if (vB2 == null) {
                    vB1.update(tour);
                    updateFiltreVision();
                } else if (vB1 == null) {
                    vB2.update(tour);
                    updateFiltreVision();
                } else {
                    vB1.update(tour);
                    vB2.update(tour);
                }
            }
        });

        javafx.scene.control.Button suivant = new Button("Suivant");
        suivant.setPrefSize(200, 75);
        suivant.setOnAction(e -> {
            if (tour < simulation.getNbTours()) {
                tour += 1;
                updatePositions();
                updateIteration();
                if (vB2 == null) {
                    vB1.update(tour);
                    updateFiltreVision();
                } else if (vB1 == null) {
                    vB2.update(tour);
                    updateFiltreVision();
                } else {
                    vB1.update(tour);
                    vB2.update(tour);
                }
            }
        });

        javafx.scene.control.Button retourMenuBtn = new Button("Revenir au menu principal");
        retourMenuBtn.setPrefSize(410, 75);
        retourMenuBtn.getStyleClass().add("important");
        retourMenuBtn.setOnAction(e -> {
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retourMenuBtn.getScene().getWindow();
            //retour au menu principal
            VueMenus vm = new VueMenus(stage);
            vm.afficherMenuPrincipal();
        });
        retourMenuBtn.setLayoutX(TAILLE_CELLULE * CARTE[0].length + 30 + TAILLE_CELLULE * 2);
        retourMenuBtn.setLayoutY(TAILLE_CELLULE * CARTE.length + TAILLE_CELLULE * 7);

        //ajout des boutons
        HBox hboxBouttons = new HBox();
        hboxBouttons.setLayoutX(TAILLE_CELLULE * CARTE[0].length + 30 + TAILLE_CELLULE * 2);
        hboxBouttons.setLayoutY(TAILLE_CELLULE * CARTE.length + TAILLE_CELLULE * 3);
        hboxBouttons.setSpacing(10);
        hboxBouttons.getChildren().add(precedent);
        hboxBouttons.getChildren().add(suivant);
        this.getChildren().add(hboxBouttons);
        this.getChildren().add(retourMenuBtn);


    }

    /**
     * Méthode pour récupérer afficher le nombre d'itération
     */
    public void updateIteration() {
        // Mise à jour du texte du label
        this.iterationLabel.setText("Tour: " + tour);
        if (tour == simulation.getNbTours()) {
            this.iterationLabel.setText("Fin");
        }
    }

    /**
     * Methode set positions imagewiew
     */
    public void setPositions(Position p, ImageView im) {
        im.setX(p.getX() * TAILLE_CELLULE);
        im.setY(p.getY() * TAILLE_CELLULE);
    }

    public void initFiltreVision(Pane laby) {
        Position ptmp = simulation.getHistoriquePosition().get(simulation.getJoueur()).get(tour);
        Joueur tmp;
        if (simulation.getJoueur() == simulation.getPrisonnier()) {
            tmp = new Joueur(ptmp.getX(), ptmp.getY(), VISION_P);
        } else {
            tmp = new Joueur(ptmp.getX(), ptmp.getY(), VISION_G);
        }

        this.filtreVision = FiltreVision.initFiltre(TAILLE_CELLULE, 0, 0, tmp, avec_camera);
        for (Rectangle[] rect : filtreVision) {
            for (Rectangle sousrect : rect) {
                laby.getChildren().add(sousrect);
            }
        }
    }

    public void updateFiltreVision() {
        Position ptmp = simulation.getHistoriquePosition().get(simulation.getJoueur()).get(tour);
        Joueur tmp;
        if (simulation.getJoueur() == simulation.getPrisonnier()) {
            tmp = new Joueur(ptmp.getX(), ptmp.getY(), VISION_P);
        } else {
            tmp = new Joueur(ptmp.getX(), ptmp.getY(), VISION_G);
        }
        FiltreVision.updateFiltre(filtreVision, tmp, avec_camera);
    }
}
