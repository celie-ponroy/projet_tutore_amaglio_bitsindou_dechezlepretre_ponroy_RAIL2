package affichage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import moteur.Jeu;
import simulation.Simulation;
import javafx.scene.paint.Color;
import simulation.personnages.Position;


/**
 * Classe permettant de dessiner le labyrinthe
 */
public class VuePrincipale implements DessinJeu {
    private Simulation simulation;

    /**
     * Affiche le labyrinthe en fonction de la carte du jeu
     * @param jeu jeu a afficher
     * @param pane pane dans lequel dessiner l'etat du jeu
     */
    @Override
    public void dessinerJeu(Jeu jeu, Pane pane) {
        //on netttoie le pane
        pane.getChildren().clear();

        this.simulation = (Simulation) jeu;
        //on parcourt toutes la carte du jeu
        for (int i = 0; i < this.simulation.CARTE.length; i++) {
            for (int j = 0; j < this.simulation.CARTE[i].length; j++) {
                Rectangle r = new Rectangle(j * 50, i * 50, 50, 50);
                switch (this.simulation.CARTE[i][j]) {
                    case Simulation.MUR:
                        //on crée un carré rouge pour dessiner un mur sur la carte
                        r.setFill(Color.RED);
                        break;

                    case Simulation.SOL:
                        //on trace un carre gris pour dessiner le sol
                        r.setFill(Color.GREY);
                        break;

                    case Simulation.SORTIE:
                        //on trace un carre vert pour dessiner la sortie
                        r.setFill(Color.GREEN);;
                        break;
                }
                //on ajoute le carré au pane
                pane.getChildren().add(r);
            }
        }
        //Gestion des personnages
        //dessin du prisonnier (rond orange)
        Circle priso = new Circle();
        priso.setRadius(25);
        priso.setFill(Color.ORANGE);
        //Position du prisonnier
        Position posP = this.simulation.getPrisonnier().getPosition();
        // Positionner le cercle du prisonnier
        priso.setCenterX(posP.getX() * 50 + 25); // Position X (milieu du carré)
        priso.setCenterY(posP.getY() * 50 + 25); // Position Y (milieu du carré)
        pane.getChildren().add(priso);

        //dessin du gardien (rond bleu)
        Circle gard = new Circle();
        gard.setRadius(25);
        gard.setFill(Color.BLUE);
        //Position du gardien
        Position posG = this.simulation.getGardien().getPosition();
        // Positionner le cercle du gardien
        gard.setCenterX(posG.getX() * 50 + 25); // Position X (milieu du carré)
        gard.setCenterY(posG.getY() * 50 + 25); // Position Y (milieu du carré)
        pane.getChildren().add(gard);

        //this.simulation = (Simulation) jeu;

    }
}
