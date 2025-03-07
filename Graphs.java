import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.Node;
import java.util.List;

public class Graphs {
    private NumberAxis xAxis = new NumberAxis(2018, 2023, 1);
    private NumberAxis yAxis = new NumberAxis();
    private LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    private XYChart.Series<Number, Number> series;

    public Graphs() {
        xAxis.setLabel("Year");
        yAxis.setLabel("Pollution Level");
        chart.setTitle("Pollution Trends");

        series = new XYChart.Series<>();
        series.setName("Pollution Over Time");

        chart.getData().add(series);
    }

    /**
     * This takes in the data and plots it onto the graph
     * @param years
     * @param pollutionLevels
     */
    public void loadData(List<Integer> years, List<Double> pollutionLevels) {
        series.getData().clear();

        for (int i = 0; i < years.size(); i++) {
            series.getData().add(new XYChart.Data<>(years.get(i), pollutionLevels.get(i)));
        }
    }

    public Node getGraph() {
        return chart;
    }
}
