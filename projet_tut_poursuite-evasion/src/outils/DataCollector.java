package outils;
import java.io.FileWriter;
import java.io.IOException;

public class DataCollector {
    public static void saveData(double[] bayesianValues, double x, double y, int decisionMove) {
        try (FileWriter writer = new FileWriter("donnees/game_data.csv", true)) {
            StringBuilder sb = new StringBuilder();
            for (double value : bayesianValues) {
                sb.append(value).append(",");
            }
            sb.append(x).append(",").append(y).append(",");
            sb.append(decisionMove).append("\n");
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
