package utils;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.util.Map;

public class SentimentChartBuilder {

    public static VBox buildChart(Map<String, Integer> sentiments) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Analyse de sentiments des commentaires");
        xAxis.setLabel("Sentiment");
        yAxis.setLabel("Nombre de commentaires");

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Commentaires");

        sentiments.forEach((key, value) -> dataSeries.getData().add(new XYChart.Data<>(key, value)));

        barChart.getData().add(dataSeries);
        return new VBox(barChart);
    }
}
