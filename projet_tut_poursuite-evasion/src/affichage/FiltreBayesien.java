package affichage;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import simulation.Simulation;

public class FiltreBayesien {
    static Rectangle[][] initFiltre(double[][] carteBayes,int TAILLE_CELLULE) {
        Rectangle[][] caseBayesienne = new Rectangle[carteBayes.length][carteBayes[0].length];

        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = new Rectangle(TAILLE_CELLULE, TAILLE_CELLULE);
                rectangle.setX(j * TAILLE_CELLULE);
                rectangle.setY(i * TAILLE_CELLULE);
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
    static void updateBayes(Rectangle[][] caseBayesienne, double[][] carteBayes) {
        for (int i = 0; i < Simulation.CARTE.length; i++) {
            for (int j = 0; j < Simulation.CARTE[i].length; j++) {
                Rectangle rectangle = caseBayesienne[i][j];
                if (carteBayes[i][j] == -1) {
                    rectangle.setFill(new Color(0.0, 0, 0, 0.5));
                } else {
                    rectangle.setFill(Color.rgb(190, 35, 0, 1 *Math.pow( carteBayes[i][j], 0.5)));
                }
            }
        }
    }

}
