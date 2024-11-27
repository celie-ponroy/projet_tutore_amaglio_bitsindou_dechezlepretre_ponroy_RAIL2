package affichage;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
     * @param canvas canvas dans lequel dessiner l'etat du jeu
     */
    @Override
    public void dessinerJeu(Jeu jeu, Canvas canvas) {
        this.simulation = (Simulation) jeu;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //on parcourt toutes la carte du jeu
        for (int i = 0; i < this.simulation.CARTE.length; i++) {
            for (int j = 0; j < this.simulation.CARTE[i].length; j++) {
                switch (this.simulation.CARTE[i][j]) {
                    case Simulation.MUR:
                        //on trace un carre rouge pour dessiner le mur
                        gc.setFill(Color.RED);
                        gc.fillRect(j * 50, i * 50, 50, 50);
                        break;

                    case Simulation.SOL:
                        //on trace un carre gris pour dessiner le sol
                        gc.setFill(Color.LIGHTGRAY);
                        gc.fillRect(j * 50, i * 50, 50, 50);
                        break;

                    case Simulation.SORTIE:
                        //on trace un carre vert pour dessiner la sortie
                        gc.setFill(Color.GREEN);
                        gc.fillRect(j * 50, i * 50, 50, 50);
                        break;
                }
            }
        }
        //Gestion des personnages
        //dessin du prisonnier (rond orange)
        gc.setFill(Color.ORANGE);
        //Position du prisonnier
        Position posP = this.simulation.getPrisonnier().getPosition();
        gc.fillOval(posP.getX() * 50, posP.getY() * 50, 50, 50);

        //dessin du gardien (rond bleu)
        gc.setFill(Color.BLUE);
        //Position du prisonnier
        Position posG = this.simulation.getGardien().getPosition();
        gc.fillOval(posG.getX() * 50, posG.getY() * 50, 50, 50);

        //this.simulation = (Simulation) jeu;

    }
}
