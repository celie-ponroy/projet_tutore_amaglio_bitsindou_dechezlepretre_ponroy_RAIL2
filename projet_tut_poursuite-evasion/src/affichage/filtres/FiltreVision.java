package affichage.filtres;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import simulation.Simulation;
import simulation.personnages.Joueur;
import simulation.personnages.Position;

import java.util.Collection;


public class FiltreVision {
    public static Color nuit = Color.rgb(44, 88, 245);
    public static Color camera = Color.rgb(255, 222, 89);

    /**
     * Initialiste le filtre
     *
     * @param TAILLE_CELLULE
     * @param DecalageX
     * @param DecalageY
     * @param joueur
     * @return
     */
    public static Rectangle[][] initFiltre(int TAILLE_CELLULE, int DecalageX, int DecalageY, Joueur joueur, boolean avec_camera) {
        Rectangle[][] filtreVision = new Rectangle[Simulation.CARTE[0].length][Simulation.CARTE.length];
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setFill(nuit);
                rectangle.setLayoutX(j * TAILLE_CELLULE + DecalageX);
                rectangle.setLayoutY(i * TAILLE_CELLULE + DecalageY);
                filtreVision[j][i] = rectangle;
                boolean boolcamera = false;
                if (avec_camera) {
                    Position position = new Position(j, i);
                    if (Simulation.VISION_CAMERAS.values().stream().flatMap(Collection::stream).anyMatch(value -> value.equals(position))) {
                        rectangle.setFill(camera);
                        rectangle.setOpacity(0.3);
                        boolcamera = true;
                    }
                }
                if (!boolcamera) {
                    if (!joueur.getVision().contains(new Position(j, i))) {
                        rectangle.setOpacity(0.5);
                    } else {
                        rectangle.setOpacity(0);
                    }
                }
            }
        }
        return filtreVision;
    }

    /**
     * mets à jour le filtre
     *
     * @param filtreVision
     * @param joueur
     */
    public static void updateFiltre(Rectangle[][] filtreVision, Joueur joueur, boolean avec_camera) {
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = filtreVision[j][i];
                // Si la case n'est pas visible
                boolean boolcamera = false;
                if (avec_camera) {
                    Position position = new Position(j, i);
                    if (Simulation.VISION_CAMERAS.values().stream().flatMap(Collection::stream).anyMatch(value -> value.equals(position))) {
                        boolcamera = true;
                        rectangle.setFill(camera);
                        rectangle.setOpacity(0.3);
                    }

                }
                if (!joueur.getVision().contains(new Position(j, i))) {
                    // Création d'un filtre pour cacher la case
                    rectangle.setOpacity(0.5);
                } else {
                    if (!boolcamera)
                        rectangle.setOpacity(0);
                }
            }
        }
    }
}
