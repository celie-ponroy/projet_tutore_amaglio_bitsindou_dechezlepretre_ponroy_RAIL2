package affichage;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import moteur.Jeu;


/**
 * interface pour afficher le jeu
 */
public interface DessinJeu {

    /**
     * affiche l'etat du jeu dans le canvas passe en parametre
     *
     * @param jeu    jeu a afficher
     * @param pane pane dans lequel dessiner l'etat du jeu
     */
    void dessinerJeu(Jeu jeu, Pane pane);

}
