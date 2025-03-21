import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
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

        chart.setCreateSymbols(true);
        chart.setLegendVisible(false);
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
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(years.get(i), pollutionLevels.get(i));

            series.getData().add(dataPoint);
            generateTooltips();
        }
    }

    private void generateTooltips() {

            for (XYChart.Data<Number, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    Tooltip tooltip = new Tooltip(
                            String.format("Year: %d\nPollution: %.2f µg/m³",
                                    data.getXValue().intValue(),
                                    data.getYValue().doubleValue())
                    );
                    Tooltip.install(node, tooltip);

                    node.setOnMouseClicked(event -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Pollution Level");
                        alert.setHeaderText("Year " + data.getXValue().intValue());
                        alert.setContentText(String.format("Pollution Level: %.2f", data.getYValue().doubleValue()));
                        alert.showAndWait();

                    });
                }
            }
        };


    public Node getGraph() {
        return chart;
    }
}
