package affichage;


import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Position;



public class FiltreVision {

    /**
     * Initialiste le filtre
     * @param TAILLE_CELLULE
     * @param DecalageX
     * @param DecalageY
     * @param joueur
     * @return
     */
    static Rectangle[][] initFiltre( int TAILLE_CELLULE, int DecalageX, int DecalageY,Joueur joueur) {
        Rectangle[][] filtreVision = new Rectangle[Simulation.CARTE[0].length][Simulation.CARTE.length];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(Color.rgb(44, 88, 245));
                rectangle.setLayoutX(j * TAILLE_CELLULE+DecalageX);
                rectangle.setLayoutY(i * TAILLE_CELLULE+DecalageY);
                filtreVision[j][i] = rectangle;

                if (!joueur.getVision().contains(new Position(j, i))) {
                    rectangle.setOpacity(0.5);
                }else{
                    rectangle.setOpacity(0);
                }
            }
        }
        return filtreVision;
    }

    /**
     * mets à jour le filtre
     * @param filtreVision
     * @param joueur
     */
    static void updateFiltre(Rectangle[][] filtreVision, Joueur joueur) {
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = filtreVision[j][i];
                // Si la case n'est pas visible
                if (!joueur.getVision().contains(new Position(j, i))) {
                    // Création d'un filtre pour cacher la case
                    rectangle.setOpacity(0.5);
                }else{
                    rectangle.setOpacity(0);
                }
            }
        }
    }
}
