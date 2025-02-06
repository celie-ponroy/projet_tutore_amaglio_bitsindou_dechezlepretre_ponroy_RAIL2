package affichage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;

public class VueAnalyse {

    /**
     * Méthode créant le graphique camembert
     */
    public PieChart graphiqueCamembert() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Groupe 1", 30),
                new PieChart.Data("Groupe 2", 20),
                new PieChart.Data("Groupe 3", 10),
                new PieChart.Data("Groupe 4", 40)
        );
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Répartition des groupes");
        pieChart.setLegendVisible(true); // Activation de la légende
        return pieChart;
    }

    /**
     * Méthode créant le graphique en courbes
     */
    public LineChart<String, Number> graphiqueCourbes() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Mois");
        yAxis.setLabel("Nombre de personnes");

        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Évolution du nombre de personnes");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de personnes");
        series.getData().add(new XYChart.Data<>("Janvier", 100));
        series.getData().add(new XYChart.Data<>("Février", 200));
        series.getData().add(new XYChart.Data<>("Mars", 50));
        series.getData().add(new XYChart.Data<>("Avril", 75));
        series.getData().add(new XYChart.Data<>("Mai", 110));
        series.getData().add(new XYChart.Data<>("Juin", 300));

        lineChart.getData().add(series);
        lineChart.setCreateSymbols(true); // Points visibles
        return lineChart;
    }
}