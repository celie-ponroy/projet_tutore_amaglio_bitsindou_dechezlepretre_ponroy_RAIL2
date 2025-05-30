package affichage.filtres;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import simulation.Simulation;

public class FiltreBayesien {
    /**
     * Initialiste le filtre
     *
     * @param carteBayes
     * @param TAILLE_CELLULE
     * @return
     */
    public static Rectangle[][] initFiltre(double[][] carteBayes, int TAILLE_CELLULE, int DecalageX, int DecalageY) {
        Rectangle[][] caseBayesienne = new Rectangle[carteBayes.length][carteBayes[0].length];

        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setX(j * TAILLE_CELLULE + DecalageX);
                rectangle.setY(i * TAILLE_CELLULE + DecalageY);
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    rectangle.setFill(Color.rgb(190, 35, 0, 1 * carteBayes[i][j]));
                }
                caseBayesienne[i][j] = rectangle;

            }
        }
        return caseBayesienne;
    }

    /**
     * mets à jour le filtre
     *
     * @param caseBayesienne
     * @param carteBayes
     */
    public static void updateBayes(Rectangle[][] caseBayesienne, double[][] carteBayes) {
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = caseBayesienne[i][j];
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    rectangle.setFill(Color.rgb(190, 35, 0, 1 * Math.pow(carteBayes[i][j], 0.5)));
                }
            }
        }
    }

}
