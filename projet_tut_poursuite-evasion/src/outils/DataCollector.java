package outils;

import java.io.FileWriter;
import java.io.IOException;

public class DataCollector {
    public static void saveDataMLP(double[] bayesianValues, int decisionMove, String fichierDeSauevgarde) {
        try (FileWriter writer = new FileWriter(fichierDeSauevgarde, true)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            for (double value : bayesianValues) {
                sb.append(value).append(",");
            }

            //On supprime la virgule en trop
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\",\"");
            sb.append(decisionMove);
            sb.append("\"\n");
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde des données sous format cnn
     * @param bayesianValues carte vue bayesienne en vect taille 1
     * @param x position x de l'agent
     * @param y position y de l'agent
     * @param carteReel carte avecl les murs
     * @param decisionMove
     * @param fichierDeSauevgarde
     */
    public static void saveDataCNN(double[] bayesianValues, double x, double y, double[] carteReel, double decisionMove, String fichierDeSauevgarde) {
        try (FileWriter writer = new FileWriter(fichierDeSauevgarde, true)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            for (double value : bayesianValues) {
                sb.append(value).append(",");
            }
            sb.append("\",\"");
            sb.append(x).append(",").append(y).append("\",");
            sb.append("\"");
            for (double value : carteReel) {
                sb.append(value).append(",");
            }
            sb.append("\",\"");
            sb.append(decisionMove).append("\"\n");
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void etiquettage(String etiq, String fichierDeSauevgarde) {
        try (FileWriter writer = new FileWriter(fichierDeSauevgarde, true)) {
            StringBuilder sb = new StringBuilder();
            sb.append(etiq).append("\n");
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}