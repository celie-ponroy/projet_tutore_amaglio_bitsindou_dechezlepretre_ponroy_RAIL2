package affichage;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import moteur.Jeu;
import simulation.CaseEnum;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;
import simulation.tuto.SimulationTutoriel;

/**
 * Affiche un tutoriel du mode interactif.
 */
public class VueTutoriel extends VueSimulation implements DessinJeu {
    private int CENTRE = 0;
    VBox instructions;
    private Rectangle[][] filtreVision; // Filtre pour cacher les cases non visibles


    public VueTutoriel(SimulationTutoriel simulation, double width, double height) {
        this.simulation = simulation;
        if (width < height) {
            TAILLE_CELLULE = (int) (width / (Simulation.CARTE[0].length) * 0.75);
        } else {
            TAILLE_CELLULE = (int) (height / (Simulation.CARTE.length) * 0.75);
        }
        CENTRE = (int) width / 2;
        this.init();

    }

    /**
     * initialise la vue
     */
    private void init() {
        //zone a gauche avec les instructions
        instructions = new VBox(15);
        instructions.setAlignment(javafx.geometry.Pos.CENTER);
        int hauteur = TAILLE_CELLULE * Simulation.CARTE.length;
        instructions.setPrefSize(CENTRE, hauteur);
        instructions.setStyle("-fx-background-color: #f0f0f0;-fx-border-color: black;-fx-border-width: 1px;-fx-border-radius: 5px;");
        setLayoutX(0);

        this.getChildren().add(instructions);
        var laby = this.initLabyrinthe(true);
        laby.setLayoutX(CENTRE);
        this.getChildren().add(laby);
        //ajout filtre de la vision
        filtreVision = new Rectangle[Simulation.CARTE[0].length][Simulation.CARTE.length];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.rgb(44, 88, 245));
                rectangle.setLayoutX(j * TAILLE_CELLULE + CENTRE);
                rectangle.setLayoutY(i * TAILLE_CELLULE);
                filtreVision[j][i] = rectangle;
                rectangle.setOpacity(0.5);
                this.getChildren().add(rectangle);
            }
        }
        updateInfos();
    }

    @Override
    protected void setOpacityPersonnage() {
        ImageView imageP2 = gardienView;

        //si le joueur choisit le personnage prsionnnier, on cache le gardien du champ de vision
        if (simulation.estVisible(simulation.getGardien(), false)) {
            imageP2.setOpacity(1);
        } else {
            imageP2.setOpacity(0);
        }
    }

    /**
     * Met à jour les positions des personnages.
     */
    @Override
    protected void updatePositions() {
        setPositions(this.simulation.getPrisonnier().getPosition(), this.prisonnierView);
        setPositions(this.simulation.getGardien().getPosition(), this.gardienView);
    }

    /**
     * Mets à jour la partie info selon l'état actuel de la simulationTuto
     */
    private void updateInfos() {
        //on clear les infos si besoin et on les remplace
        SimulationTutoriel tuto = (SimulationTutoriel) this.simulation;
        switch (tuto.etatActuel) {
            case DEBUT_Perso:
                //on affiche les infos du début
                filtrePrisonnier();
                setInfoDebutPerso();
                break;
            case DEPLACEMENT:
            case DEPLACEMENT_GARDIEN:
                //on affiche les infos du déplacement
                setInfoDeplacement();
                break;
            case GARDIEN:
                //on affiche les infos du gardien
                filtreGardien();
                setInfoGardien();
                break;
            case FIN:
                //on affiche les infos de fin
                setInfoFin();
                break;
        }
    }

    /**
     * Informations lorsque l'état est DEBUT_Perso
     * affiche les informations relatives au prisonnier
     */
    private void setInfoDebutPerso() {
        clearInfos();
        //explications du prisonnier et de son but
        //image du perso avec son but

        ImageView image = new ImageView("file:images/prisonnier.png");
        image.setFitWidth(100);
        image.setPreserveRatio(true);
        instructions.getChildren().add(image);
        instructions.getChildren().add(new Label("Vous êtes le prisonnier"));
        instructions.getChildren().add(new Label("Vous devez sortir du labyrinthe"));
        //infos case sortie
        ImageView imageSortie = new ImageView("file:images/sortie.png");
        imageSortie.setFitWidth(100);
        imageSortie.setPreserveRatio(true);
        Label label = new Label("Ceci est la sortie");
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        hBox.getChildren().add(imageSortie);
        hBox.getChildren().add(label);
        instructions.getChildren().add(hBox);
        instructions.getChildren().add(new Label("Appyer sur espace pour continuer"));


    }


    /**
     * Informations sur les touches de déplacement
     */
    private void setInfoDeplacement() {
        clearInfos();
        Label title = new Label("Vous pouvez vous déplacer avec les touches suivantes :");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        // Ajout des touches et flèches dans la grille
        ajouterTouche(grid, "Z ou 8", "haut.png", 0, 1);
        ajouterTouche(grid, "Q ou 4", "gauche.png", 1, 0);
        ajouterTouche(grid, "S ou 5", "cercle.png", 1, 1);
        ajouterTouche(grid, "D ou 6", "droite.png", 1, 2);
        ajouterTouche(grid, "X ou 2", "bas.png", 2, 1);
        ajouterTouche(grid, "A ou 7", "haut_gauche.png", 0, 0);
        ajouterTouche(grid, "E ou 9", "haut_droite.png", 0, 2);
        ajouterTouche(grid, "W ou 1", "bas_gauche.png", 2, 0);
        ajouterTouche(grid, "C ou 3", "bas_droite.png", 2, 2);
        instructions.getChildren().add(title);
        instructions.getChildren().add(grid);
    }

    /**
     * Méthode qui permet d'ajouter une touche avec son image à son emplacement dans la grip
     * @param grid où on ajoute
     * @param texte la touche corespondant à l'image
     * @param imagePath l'image de la flèche
     * @param row ligne
     * @param col colone
     */
    private void ajouterTouche(GridPane grid, String texte, String imagePath, int row, int col) {
        ImageView icon = new ImageView(new Image("file:images/fleches/" + imagePath));
        icon.setFitHeight(40);
        icon.setFitWidth(40);
        //on ajoute un contour noir

        Label label = new Label(texte);
        label.setStyle("-fx-font-size: 14px;");

        VBox box = new VBox(5, icon, label);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(100, 100);

        grid.add(box, col, row);
    }
    /**
     * Informations lorsque l'état est GARDIEN
     * affiche les informations relatives au gardien
     */
    private void setInfoGardien() {
        clearInfos();

        //on affiche les infos du gardien
        instructions.getChildren().add(new Label("Oh vous avez vu le gardien , si il vous attrape c'est perdu"));
        ImageView image = new ImageView("file:images/gardien.png");
        image.setFitWidth(100);
        image.setPreserveRatio(true);
        instructions.getChildren().add(image);
        instructions.getChildren().add(new Label("Ceci est le gardien"));
        ImageView imageCase = new ImageView("file:images/raccourciGardien.png");
        imageCase.setFitWidth(100);
        imageCase.setPreserveRatio(true);
        instructions.getChildren().add(imageCase);
        instructions.getChildren().add(new Label("Le gardien peut traverser ses grilles mais pas le prisonnier attention !"));
        ImageView imageCamera = new ImageView("file:images/cameraicon.png");
        instructions.getChildren().add(imageCamera);
        instructions.getChildren().add(new Label("Le gardien peut voir à distance grâce à des caméra"));

        instructions.getChildren().add(new Label("Appyer sur espace pour continuer"));

    }

    /**
     * Affiche le fin du tutoriel.
     */
    private void setInfoFin() {
        this.getChildren().clear();
        //on affiche les infos de fin
        Label label = new Label("Bravo vous avez fini le tutoriel :)");
        label.setAlignment(Pos.CENTER);
        label.setLayoutX(CENTRE - 100);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        this.getChildren().add(label);
        Button retourMenuBtn = new Button("Commercer");
        retourMenuBtn.getStyleClass().add("important");
        retourMenuBtn.setPrefSize(350, 75);
        retourMenuBtn.setOnAction(e -> {
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retourMenuBtn.getScene().getWindow();
            stage.close();
            //retour au menu principal
            VueMenus vm = new VueMenus();
            vm.afficherMenuPrincipal();
        });
        retourMenuBtn.setLayoutX(CENTRE - 175);
        retourMenuBtn.setLayoutY(200);
        this.getChildren().add(retourMenuBtn);
    }

    /**
     * Efface les informations affichées.
     */
    private void clearInfos() {
        //on clear les infos
        instructions.getChildren().clear();
    }

    @Override
    public void update(Jeu jeu) {
        this.updatePositions();
        setOpacityPersonnage();
        //on mets a jour selon l'état actuel
        updateFiltreVision();
        updateInfos();
    }

    /**
     * Met à jour le filtre de vision. (etat deplacement)
     */
    public void updateFiltreVision() {
        FiltreVision.updateFiltre(filtreVision, (Joueur) simulation.getJoueur(), avec_camera);
    }

    /**
     * Filtre pour mettre en avant les cases du gardien. (etat gardien)
     */
    public void filtreGardien() {
        //on mets en avant les cases du gardien et de ses cases
        Position posGardien = simulation.getGardien().getPosition();
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = filtreVision[j][i];
                if (posGardien.equals(new Position(j, i)) || Simulation.CARTE[i][j] == CaseEnum.RACCOURCI_GARDIEN.ordinal()) {
                    rectangle.setOpacity(0);
                } else {
                    rectangle.setOpacity(0.5);
                }
            }
        }
    }

    /**
     * Filtre pour mettre en avant les cases du gardien. (etat debut perso)
     */
    public void filtrePrisonnier() {
        //on mets en avant les cases du gardien et de ses cases
        Position posPri = simulation.getPrisonnier().getPosition();
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = filtreVision[j][i];
                if (posPri.equals(new Position(j, i)) || Simulation.getPosSortie().equals(new Position(j, i))) {
                    rectangle.setOpacity(0);
                } else {
                    rectangle.setOpacity(0.5);
                }
            }
        }
    }

}
