package affichage;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Position;

import java.util.Collection;

public class FiltreCamera {
    public static Color camera = Color.rgb(255, 222, 89);
    /**
     * Initialiste le filtre
     * @param TAILLE_CELLULE
     * @param DecalageX
     * @param DecalageY
     * @return
     */
    static Rectangle[][] initFiltre(int TAILLE_CELLULE, int DecalageX, int DecalageY) {
        Rectangle[][] filtreVision = new Rectangle[Simulation.CARTE[0].length][Simulation.CARTE.length];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(camera);
                rectangle.setLayoutX(j * TAILLE_CELLULE+DecalageX);
                rectangle.setLayoutY(i * TAILLE_CELLULE+DecalageY);
                filtreVision[j][i] = rectangle;

                Position position = new Position(j,i);
                if(Simulation.VISION_CAMERAS.values().stream().flatMap(Collection::stream).anyMatch(value -> value.equals(position))){
                    rectangle.setOpacity(0.3);
                }else{
                    rectangle.setOpacity(0);
                }
            }
        }
        return filtreVision;
    }
}
