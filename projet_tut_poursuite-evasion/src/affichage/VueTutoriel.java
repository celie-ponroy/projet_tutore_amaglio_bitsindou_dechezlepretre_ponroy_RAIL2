package affichage;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import moteur.Jeu;
import simulation.Simulation;
import simulation.tuto.SimulationTutoriel;

/**
 * Affiche un tutoriel du mode interactif.
 */
public class VueTutoriel extends VueSimulation implements DessinJeu {
    private int CENTRE = 0;
    VBox instructions;

    public VueTutoriel(SimulationTutoriel simulation,double width, double height) {
        this.simulation = simulation;
        if(width<height){
            TAILLE_CELLULE = (int) (width/ (Simulation.CARTE[0].length)*0.75);
        }else{
            TAILLE_CELLULE = (int) (height/ (Simulation.CARTE.length)*0.75);
        }
        CENTRE = (int)width/2 ;
        this.init();

    }
    private void init(){
        //zone a gauche avec les instructions
        instructions = new VBox();
        int hauteur = TAILLE_CELLULE*Simulation.CARTE.length;
        instructions.setPrefSize(CENTRE, hauteur);
        instructions.setStyle("-fx-background-color: #f0f0f0;-fx-border-color: black;-fx-border-width: 1px;-fx-border-radius: 5px;");
        setLayoutX(0);
        instructions.setLayoutY(100);

        this.getChildren().add(instructions);
        var laby = this.initLabyrinthe(true);
        laby.setLayoutX(CENTRE);
        laby.setLayoutY(100);
        this.getChildren().add(laby);
        //ajout filtre de la vision
        update(simulation);
    }

    @Override
    protected void setOpacityPersonnage() {
        this.prisonnierView.setOpacity(1);
        this.gardienView.setOpacity(1);
    }

    /**
     * Met à jour les positions des personnages.
     */
    @Override
    protected void updatePositions() {
        setPositions(this.simulation.getPrisonnier().getPosition(), this.prisonnierView);
        setPositions( this.simulation.getGardien().getPosition(), this.gardienView);
    }
    private void updateInfos(){
        //on clear les infos si besoin et on les remplace
        SimulationTutoriel tuto = (SimulationTutoriel) this.simulation;
        switch (tuto.etatActuel){
            case DEBUT_Perso:
                //on affiche les infos du début
                setInfoDebutPerso();
                break;
            case DEPLACEMENT:
            case DEPLACEMENT_GARDIEN:
                //on affiche les infos du déplacement
                setInfoDeplacement();
                break;
            case GARDIEN:
                //on affiche les infos du gardien
                setInfoGardien();
                break;
            case FIN:
                //on affiche les infos de fin
                setInfoFin();
                break;
        }
    }
    private void setInfoDebutPerso(){
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
        hBox.getChildren().add(imageSortie);
        hBox.getChildren().add(label);
        instructions.getChildren().add(hBox);

    }

    private void setInfoDeplacement(){
        clearInfos();
        //on affiche les infos du déplacement
        //on peut utiliser  azeqsdwxc ou 789456123 pour se déplacer
        instructions.getChildren().add(new Label("Vous pouvez vous déplacer avec les touches suivantes :"));
        instructions.getChildren().add(new Label("Z  ou 8 : Haut"));
        instructions.getChildren().add(new Label("Q  ou 4 : Gauche"));
        instructions.getChildren().add(new Label("S  ou 5 : Aucun"));
        instructions.getChildren().add(new Label("D  ou 6 : Droite"));
        instructions.getChildren().add(new Label("X  ou 2 : Bas"));
        instructions.getChildren().add(new Label("A  ou 7 : Haut gauche"));
        instructions.getChildren().add(new Label("E  ou 9 : Haut droite"));
        instructions.getChildren().add(new Label("W  ou 1 : Bas gauche"));
        instructions.getChildren().add(new Label("C  ou 3 : Bas droite"));

    }
    private void setInfoGardien(){
        clearInfos();
        //on affiche les infos du gardien
        instructions.getChildren().add(new Label("Oh vous avez vu le gardien , si il vous attrape c'est perdu"));
        //ajout image gardien
        //ajout case gardien
        ImageView image = new ImageView("file:images/gardien.png");
        image.setFitWidth(100);
        image.setPreserveRatio(true);
        instructions.getChildren().add(image);
        instructions.getChildren().add(new Label("Ceci est le gardien"));
        //image case gardien
        ImageView imageCase = new ImageView("file:images/raccourciGardien.png");
        imageCase.setFitWidth(100);
        imageCase.setPreserveRatio(true);
        instructions.getChildren().add(imageCase);
        instructions.getChildren().add(new Label("Le gardien peut traverser ses grilles mais pas vous attention !"));

    }
    private void setInfoFin(){
        clearInfos();
        //on affiche les infos de fin
        instructions.getChildren().add(new Label("Bravo vous avez fini le tutoriel"));
    }
    private void clearInfos(){
        //on clear les infos
        instructions.getChildren().clear();
    }

    @Override
    public void update(Jeu jeu) {
        this.updatePositions();
        //this.updateVision();
        //on mets a jour selon l'état actuel
        updateInfos();
    }
}
