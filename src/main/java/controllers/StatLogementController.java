package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import service.LogementService;

import java.util.Map;

public class StatLogementController {

    @FXML
    private BarChart<String, Number> barChart;

    private final LogementService logementService = new LogementService();

    @FXML
    public void initialize() {
        loadChartData();
    }

    private void loadChartData() {
        Map<String, Integer> stats = logementService.getCountByLocalisation();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Logements");

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }
}
