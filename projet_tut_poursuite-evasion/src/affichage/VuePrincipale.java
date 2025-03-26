package affichage;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import moteur.Jeu;
import musique.SoundManager;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Personnage;
import simulation.personnages.Position;
import java.util.Arrays;

import static musique.SoundManager.*;

/**
 * Classe pour la vue principale de la simulation
 */
public class VuePrincipale extends VueSimulation implements DessinJeu {

    /**
     * Attributs
     */
    private Label iterationLabel; // Label pour afficher le nombre d'itération
    private Rectangle[][] filtreVision; // Filtre pour cacher les cases non visibles
    private int tour;
    private Rectangle[][] caseBayesienneHisto;
    protected int DECALAGE;


    /**
     * Constructeur de la vue principale
     * @param width largeur de la fenêtre
     * @param height hauteur de la fenêtre
     */
    public VuePrincipale(double width, double height) {
        super();
        if(width<height){
            TAILLE_CELLULE = (int) (width/ (Simulation.CARTE[0].length)*0.75);
        }else{
            TAILLE_CELLULE = (int) (height/ (Simulation.CARTE.length)*0.75);
        }
        DECALAGE = (int)width/2 ;
    }

    /**
     * Initialise le labyrinthe et les personnages
     */
    private void init() {
        this.setAvec_camera(true);
        var laby = initLabyrinthe(true);
        //au centre
        laby.setLayoutX(DECALAGE);
        this.getChildren().add(laby);


        //affichage itération
        this.iterationLabel = new Label("Nombre d'itération: " + simulation.getNbTours());
        iterationLabel.setLayoutX(DECALAGE); //tout à droite du labyrinthe
        iterationLabel.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+10);
        iterationLabel.setStyle("-fx-font-size: 12px; -fx-border-color: black; -fx-padding: 10;");
        this.getChildren().add(iterationLabel);

        //Bouton pour revenir au menu principal
        Button retourMenuBtn = new Button("Revenir au menu principal");
        retourMenuBtn.getStyleClass().add("important");
        retourMenuBtn.setPrefSize(230, 30);
        //tout à gauche du labyrinthe
        retourMenuBtn.setLayoutX(DECALAGE + TAILLE_CELLULE * Simulation.CARTE[0].length - retourMenuBtn.getPrefWidth());
        retourMenuBtn.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+10);
        retourMenuBtn.setOnAction(e -> {
            SoundManager.stopAllMusic();
            SoundManager.playFondMusic();
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retourMenuBtn.getScene().getWindow();
            stage.close();
            //retour au menu principal
            VueMenus vm = new VueMenus();
            vm.afficherMenuPrincipal();
        });
        this.getChildren().add(retourMenuBtn);

    }

    /**
     * Méthode pour mettre à jour les chmaps de vision des personnages
     */
    @Override
    protected void setOpacityPersonnage() {

        Personnage p1 = simulation.getGardien();
        Personnage p2 = simulation.getPrisonnier();
        ImageView imageP2 = prisonnierView;

        if (simulation.getJoueur().equals(simulation.getPrisonnier())) {
            p1 = simulation.getPrisonnier();
            p2 = simulation.getGardien();
            imageP2 = gardienView;
        }

        //si le joueur choisit le personnage prsionnnier, on cache le gardien du champ de vision
        if (p1.getVision().contains(p2.getPosition())) {
            imageP2.setOpacity(1);
        } else {
            imageP2.setOpacity(0);
        }
    }

    /**
     * Méthode pour mettre à jour les positions des personnages
     */
    @Override
    protected void updatePositions() {
        setPositions(simulation.getPrisonnier().getPosition(), prisonnierView);
        setPositions(simulation.getGardien().getPosition(), gardienView);
    }

    /**
     * Méthode principale de l'interface DessinJeu
     */
    @Override
    public void update(Jeu jeu) {
        // Récuperation de la simulation
        this.simulation = (Simulation)jeu;

        //mise à jour du décalage en fonction de la carte
        if (this.getChildren().isEmpty()) {
            DECALAGE -= simulation.CARTE[0].length*TAILLE_CELLULE/2;
            // Si le labyrinthe n'est pas encore initialisé
            init();
            initFiltreVision();
            setOpacityPersonnage();
        } else {
            // Sinon, il met juste a jour les positions des personnages
            updateDirections(this.simulation.getNbTours()-1);
            updatePositions();
            updateIteration();
            setOpacityPersonnage();
            setFiltreVision();
        }

        //Pop up pour afficher la fin de la partie
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(simulation.etreFini()){
            SoundManager.stopAllMusic(); //on arrete toutes musiques
            if(!simulation.getVictoireGardien() && !simulation.getVictoirePrisonnier()){
                //on met le son de l'égalité
                playDrawMusic();
                alert.setHeaderText("Egalité !");
                alert.setContentText("Le nombre de coup est dépassé !\n" +
                        "Cliquez sur OK pour voir l'historique");
            }else if (simulation.getVictoireGardien() && simulation.getJoueur().equals(simulation.getGardien()) || (simulation.getVictoirePrisonnier() && simulation.getJoueur().equals(simulation.getPrisonnier()))) {
                //on met le son de la victoire
                playWinMusic();
                alert.setHeaderText("Félicitations !");
                alert.setContentText("Vous avez gagné la partie !\n" +
                        "Cliquez sur OK pour voir l'historique");
            } else if (!(simulation.getVictoirePrisonnier()) && simulation.getJoueur().equals(simulation.getPrisonnier()) || !(simulation.getVictoireGardien()) && simulation.getJoueur().equals(simulation.getGardien())) {
                //on met le son de la défaite
                playLooseMusic();
                alert.setHeaderText("Dommage...");
                alert.setContentText("L'IA a été plus rusée, vous avez perdu !\n" + "Cliquez sur OK pour voir l'historique");
            }
            //affiche l'alerte avec les messages pendant 2 secondes
            alert.showAndWait();
            //Affichage de l'historique
            historique();
        } else if (simulation.etreFini()) { //si on est en mode non interactif
            alert.setContentText("Fin de la partie !\n" +
                    "Cliquez sur OK pour quitter");
            alert.showAndWait();
            //Quand on clique sur le bouton ok, on quitte le jeu
            System.exit(0);

        }
    }

    /**
     * Méthode pour récupérer afficher le nombre d'itération
     */
    public void updateIteration() {
        // Mise à jour du texte du label
        this.iterationLabel.setText("Nombre d'itération: " + simulation.getNbTours());
    }

    /**
     * Méthode pour initialiser un filtre sur les cases non visibles
     */
    public void initFiltreVision() {
        this.filtreVision = FiltreVision.initFiltre(TAILLE_CELLULE, DECALAGE,0,(Joueur) simulation.getJoueur(),avec_camera);
        for (Rectangle[] rect : filtreVision) {
            for (Rectangle sousrect : rect) {
                this.getChildren().add(sousrect);
            }
        }
    }

    /**
     * Méthode pour lettre un filtre sur les cases non visibles
     */
    public void setFiltreVision() {
        FiltreVision.updateFiltre(filtreVision, (Joueur) simulation.getJoueur(),avec_camera);
    }

    /**
     * Methode pour historique à la fin du jeu en mode interactif
     */
    public void historique(){
        Button sauvegarder = new Button("Sauvegarder");
        sauvegarder.setPrefSize(200, 75);
        sauvegarder.setOnAction(e -> {
            lancerSauvegarde(sauvegarder);
        });
        tour=0;
        //initaliser la map (enlever la vision)
        Arrays.stream(filtreVision).forEach(rectangles -> Arrays.stream(rectangles).forEach(rectangle -> rectangle.setOpacity(0)));
        //on mets la première carte bayesienne
        Personnage agent = simulation.getPrisonnier();
        if (simulation.getJoueur() == simulation.getPrisonnier())
            agent = simulation.getGardien();

        caseBayesienneHisto = FiltreBayesien.initFiltre(simulation.getHistoriqueBayesien().get(agent).get(0),TAILLE_CELLULE,DECALAGE,0);
        for (Rectangle[] rect : caseBayesienneHisto) {
            for (Rectangle sousrect : rect) {
                this.getChildren().add(sousrect);
            }
        }
        //on met les perso à l'emplacement ini

        Position pPrisonnier = simulation.getHistoriquePosition().get(simulation.getPrisonnier()).get(0);
        Position pGardien= simulation.getHistoriquePosition().get(simulation.getGardien()).get(1);
        setPositions(pPrisonnier,prisonnierView);
        prisonnierView.setOpacity(1);
        setPositions(pGardien, gardienView);
        gardienView.setOpacity(1);

        //ajout boutons pour precedent, suivant et revenir au menu principal
        Button precedent = new Button("Précédent");
        precedent.setPrefSize(200, 75);
        precedent.setOnAction(e -> {
            tour -= 1;
            updateHistorique();
        });
        Button suivant = new Button("Suivant");
        suivant.setPrefSize(200, 75);
        suivant.setOnAction(e -> {
            tour += 1;
            updateHistorique();
        });

        Button retourMenuBtn = new Button("Revenir au menu principal");
        retourMenuBtn.getStyleClass().add("important");
        retourMenuBtn.setPrefSize(350, 75);
        retourMenuBtn.setOnAction(e -> {
            SoundManager.stopAllMusic();
            SoundManager.playFondMusic();
            //Ferme la fenetre actuelle
            Stage stage = (Stage) retourMenuBtn.getScene().getWindow();
            stage.close();
            //retour au menu principal
            VueMenus vm = new VueMenus();
            vm.afficherMenuPrincipal();
        });

        HBox hboxBouttons = new HBox();

        hboxBouttons.setLayoutX(DECALAGE);
        hboxBouttons.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+iterationLabel.getHeight()+15);
        hboxBouttons.getChildren().add(precedent);
        hboxBouttons.getChildren().add(suivant);
        hboxBouttons.getChildren().add(sauvegarder);
        hboxBouttons.getChildren().add(retourMenuBtn);
        hboxBouttons.setSpacing(30);
        this.getChildren().add(hboxBouttons);
    }

    /**
     * Update de l'historique (par rapport à un tour)
     */
    public void updateHistorique() {
        //on mets a jour position et bayes
        if (tour < 0) {
            tour = 0;
        }
        int taille = simulation.getHistoriquePosition().get(simulation.getJoueur()).size();
        this.iterationLabel.setText("Nombre d'itération: " + tour);
        if (tour >= taille - 1) {
            tour = taille - 1;
            this.iterationLabel.setText("Fin");
        }
        updateDirections(tour);
        //on met a jour la carte bayesienne
        Personnage agent = simulation.getPrisonnier();
        if (simulation.getJoueur() == simulation.getPrisonnier())
            agent = simulation.getGardien();

        FiltreBayesien.updateBayes(caseBayesienneHisto,simulation.getHistoriqueBayesien().get(agent).get(tour));

        setPositions(simulation.getHistoriquePosition().get(simulation.getPrisonnier()).get(tour),prisonnierView);
        setPositions(simulation.getHistoriquePosition().get(simulation.getGardien()).get(tour),gardienView);
    }

}
