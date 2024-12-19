package affichage;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;
import simulation.personnages.Personnage;
import simulation.personnages.Position;

import java.util.Arrays;

public class VuePrincipale extends VueSimulation implements DessinJeu {
    private Simulation simulation;
    private Label iterationLabel; // Label pour afficher le nombre d'itération
    private Rectangle [][] filtreVision; // Filtre pour cacher les cases non visibles
    private int tour;
    private Rectangle[][] caseBayesienneHisto;



    //constructeur
    public VuePrincipale(){
        super();
        TAILLE_CELLULE = 50;
    }

    /**
     * Initialise le labyrinthe et les personnages
     */
    private void init() {
        initLabyrinthe();
        //affichage itération
        VBox vbox = new VBox();
        vbox.setLayoutX(10);
        vbox.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+100);
        this.iterationLabel = new Label("Nombre d'itération: " + simulation.getNbTours());
        vbox.getChildren().add(this.iterationLabel);
        this.getChildren().add(vbox);

        Rectangle rectangle = new Rectangle(150, 20);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.DARKGREY);
        rectangle.setLayoutX(10);
        rectangle.setLayoutY(TAILLE_CELLULE*Simulation.CARTE.length+100);
        this.getChildren().add(rectangle);
    }

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

        if (this.getChildren().isEmpty()) {
            // Si le labyrinthe n'est pas encore initialisé
            init();
            initFiltreVision();

        } else {
            // Sinon, il met juste a jour les positions des personnages
            updatePositions();
            updateIteration();


            setFiltreVision();
            //variable pour savoir si le joueur a choisi le personnage prisonnier ou gardien
            Personnage p1 = simulation.getGardien();
            Personnage p2 = simulation.getPrisonnier();
            ImageView imageP2 = prisonnierView;


            if(simulation.getJoueur().equals(simulation.getPrisonnier())) {//si le joueur choisit le personnage prisonnier
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

        //Pop up pour afficher la fin de la partie
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(simulation.etreFini()){

            if (simulation.getVictoireGardien() && simulation.getJoueur().equals(simulation.getGardien()) || (simulation.getVictoirePrisonnier() && simulation.getJoueur().equals(simulation.getPrisonnier()))) {
                alert.setHeaderText("Félicitations !");
                alert.setContentText("Vous avez gagné la partie !\n" +
                        "Cliquez sur OK pour voir l'historique");
            } else if (!(simulation.getVictoirePrisonnier()) && simulation.getJoueur().equals(simulation.getPrisonnier()) || !(simulation.getVictoireGardien()) && simulation.getJoueur().equals(simulation.getGardien())) {
                alert.setHeaderText("Dommage...");
                alert.setContentText("L'IA a été plus maligne, vous avez perdu !\n" + "Cliquez sur OK pour voir l'historique");
            }
            //affiche l'alerte avec les messages pendant 2 secondes
            alert.showAndWait();
            //Affichage de l'historique
            historique();
        }else if(simulation.etreFini()){ //si on est en mode non interactif
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
        this.filtreVision = new Rectangle[simulation.CARTE[0].length][simulation.CARTE.length];
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.rgb(44, 88, 245));
                rectangle.setLayoutX(j * TAILLE_CELLULE);
                rectangle.setLayoutY(i * TAILLE_CELLULE);
                this.filtreVision[j][i] = rectangle;

                if (!simulation.getJoueur().getVision().contains(new Position(j, i))) {
                    rectangle.setOpacity(0.5);
                }else{
                    rectangle.setOpacity(0);
                }
                this.getChildren().add(rectangle);
            }
        }
    }
    /**
     * Méthode pour lettre un filtre sur les cases non visibles
     */
    public void setFiltreVision() {
        // Parcours de toutes les cases du labyrinthe
        for (int i = 0; i < simulation.CARTE.length; i++) {
            for (int j = 0; j < simulation.CARTE[i].length; j++) {
                Rectangle rectangle = this.filtreVision[j][i];
                // Si la case n'est pas visible
                if (!simulation.getJoueur().getVision().contains(new Position(j, i))) {
                    // Création d'un filtre pour cacher la case
                    rectangle.setOpacity(0.5);
                }else{
                    rectangle.setOpacity(0);
                }
            }
        }
    }
    /**
     * Methode pour historique à la fin du jeu en mode interactif
     */
    public void historique(){
        tour=0;
        //initaliser la map (enlever la vision)
        Arrays.stream(filtreVision).forEach(rectangles -> Arrays.stream(rectangles).forEach(rectangle -> rectangle.setOpacity(0)));
        //on mets la première carte bayesienne
        Personnage agent = simulation.getPrisonnier();
        if(simulation.getJoueur()==simulation.getPrisonnier())
            agent = simulation.getGardien();

        caseBayesienneHisto = FiltreBayesien.initFiltre(simulation.historiqueBayesien.get(agent).get(0),TAILLE_CELLULE);
        for (Rectangle[] rect : caseBayesienneHisto) {
            for (Rectangle sousrect : rect) {
                this.getChildren().add(sousrect);
            }
        }
        //on mets les perso à l'emplacement ini
        Position pPrisonnier = simulation.historiquePosition.get(simulation.getPrisonnier()).get(0);
        Position pGardien= simulation.historiquePosition.get(simulation.getGardien()).get(1);
        setPositions(pPrisonnier,prisonnierView);
        prisonnierView.setOpacity(1);
        setPositions(pGardien,gardienView);
        gardienView.setOpacity(1);



        //ajout boutons pour precedent et suivant
        Button precedent = new Button("Précédent");
        precedent.setPrefSize(200, 75);
        precedent.setOnAction(e -> {
            tour-=1;
            updateHistorique();
            });
        Button suivant = new Button("Suivant");
        suivant.setPrefSize(200, 75);
        suivant.setOnAction(e -> {
            tour+=1;
            updateHistorique();
        });

        HBox hboxBouttons = new HBox();
        hboxBouttons.setLayoutX(10);
        hboxBouttons.setLayoutY(620);
        hboxBouttons.getChildren().add(precedent);
        hboxBouttons.getChildren().add(suivant);
        this.getChildren().add(hboxBouttons);



    }
    /**
     * Update de l'historique (par rapport à un tour)
     */
    public void updateHistorique(){
        //on mets a jour position et bayes
        if(tour<0){
            tour=0;
        }
        int taille =simulation.historiquePosition.get(simulation.getJoueur()).size();
        if(tour>=taille){
            tour=taille-1;
        }
        //on mets a jour la carte bayesienne
        Personnage agent = simulation.getPrisonnier();
        if(simulation.getJoueur()==simulation.getPrisonnier())
            agent = simulation.getGardien();

        FiltreBayesien.updateBayes(caseBayesienneHisto,simulation.historiqueBayesien.get(agent).get(tour));

        setPositions(simulation.historiquePosition.get(simulation.getPrisonnier()).get(tour),prisonnierView);
        setPositions(simulation.historiquePosition.get(simulation.getGardien()).get(tour),gardienView);
    }

    /**
     * Méthode pour afficher un pop up à la fin de la partie
     */
    private void afficherPopFinDePartie(boolean victoire) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partie");
        if (victoire) {
            alert.setHeaderText("Félicitations !");
            alert.setContentText("Vous avez gagné !");
        } else {
            alert.setHeaderText("Dommage...");
            alert.setContentText("Vous avez perdu !");
        }
        alert.showAndWait();
    }

}
