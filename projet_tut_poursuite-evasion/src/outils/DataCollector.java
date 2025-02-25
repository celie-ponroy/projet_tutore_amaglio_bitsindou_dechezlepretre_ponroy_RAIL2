package outils;

import java.io.FileWriter;
import java.io.IOException;

public class DataCollector {
    public static void saveData(double[] bayesianValues, double x, double y, int decisionMove, String fichierDeSauevgarde) {
        try (FileWriter writer = new FileWriter(fichierDeSauevgarde, true)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            for (double value : bayesianValues) {
                sb.append(value).append(",");
            }
            sb.append("\",\"");
            sb.append(x).append(",").append(y).append("\",");
            sb.append("\"");
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