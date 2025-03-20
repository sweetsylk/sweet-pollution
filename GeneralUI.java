import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.json.JSONObject;

public class GeneralUI extends Application {
    private static String filename = "";
    private boolean gridMapOn = false;
    private boolean heatMapOn = false;
    private GridPane grid = new GridPane();
    private BorderPane stack = new BorderPane();
    private Graphs pollutionGraph = new Graphs();
    private VBox statsSideBar = new VBox(12.0);

    public GeneralUI() {
    }

    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        Tab mapTab = new Tab("Map");
        Tab statsTab = new Tab("Stats");
        mapTab.setClosable(false);
        statsTab.setClosable(false);
        BorderPane mapLayout = new BorderPane();
        mapLayout.getStyleClass().add("main-background");
        VBox mapSideBar = new VBox(12.0);
        mapSideBar.getStyleClass().add("sidebar");
        mapSideBar.setPrefWidth(150.0);
        mapSideBar.setAlignment(Pos.TOP_CENTER);
        mapSideBar.prefHeightProperty().bind(this.stack.heightProperty());
        mapSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));
        Label dropdown1Label = new Label("Year:");
        ComboBox<String> dropdown1 = new ComboBox<>();
        dropdown1.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");
        Label dropdown2Label = new Label("Pollutant:");
        ComboBox<String> dropdown2 = new ComboBox<>();
        dropdown2.getItems().addAll("NO2", "PM10", "PM2.5");
        dropdown1.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        dropdown2.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        ToggleGroup gridToggleGroup = new ToggleGroup();
        ToggleButton mapGridOn = new ToggleButton("Map Grid");
        mapGridOn.setToggleGroup(gridToggleGroup);
        mapGridOn.setSelected(false);
        mapGridOn.setOnAction((e) -> {
            this.gridMapToggle();
            if (this.gridMapIsOn()) {
                this.gridOn();
            } else {
                this.gridOff();
            }

        });
        ToggleButton heatMap = new ToggleButton("Heat Map");
        mapGridOn.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.setOnAction(this::heatMapToggle);

        Button fetchLiveDataButton = new Button("Fetch Real-Time Pollution Data");
        fetchLiveDataButton.setOnAction((e) -> this.fetchRealTimeData(dropdown2));
        mapSideBar.getChildren().addAll(dropdown2Label, dropdown2, dropdown1Label, dropdown1, mapGridOn, heatMap, fetchLiveDataButton);
        mapLayout.setLeft(mapSideBar);
        Image londonImage = new Image(Objects.requireNonNull(this.getClass().getResource("London.png")).toExternalForm());
        ImageView londonImageView = new ImageView(londonImage);
        londonImageView.setPreserveRatio(false);
        londonImageView.setFitWidth(1781.0);
        londonImageView.setFitHeight(1100.0);
        londonImageView.fitWidthProperty().bind(this.stack.widthProperty());
        londonImageView.fitHeightProperty().bind(this.stack.heightProperty());
        this.stack.getChildren().add(londonImageView);
        this.stack.getChildren().add(this.grid);
        mapLayout.setCenter(this.stack);
        mapTab.setContent(mapLayout);
        BorderPane statsLayout = new BorderPane();
        statsLayout.getStyleClass().add("main-background");
        this.statsSideBar.getStyleClass().add("sidebar");
        this.statsSideBar.setAlignment(Pos.TOP_CENTER);
        this.statsSideBar.prefHeightProperty().bind(this.stack.heightProperty());
        this.statsSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));
        Label statsdropdown1Label = new Label("Pollutant:");
        ComboBox<String> statsdropdown1 = new ComboBox<>();
        statsdropdown1.getItems().addAll("NO2", "PM10", "PM2.5");
        Label statsdropdown2Label = new Label("Metric:");
        ComboBox<String> statsdropdown2 = new ComboBox<>();
        statsdropdown2.getItems().addAll("Highest", "Average");
        Label additionalDropdownLabel = new Label("View By:");
        ComboBox<String> additionalDropdown = new ComboBox<>();
        additionalDropdown.getItems().addAll("Area", "Period");
        additionalDropdown.setVisible(false);
        additionalDropdownLabel.setVisible(false);
        dropdown1.setOnAction((e) -> this.handleComboBoxSelection(dropdown1, dropdown2));
        dropdown2.setOnAction((e) -> this.handleComboBoxSelection(dropdown1, dropdown2));
        statsdropdown2.setOnAction((e) -> {
            String selectedMetric = statsdropdown2.getSelectionModel().getSelectedItem();
            if ("Average".equals(selectedMetric)) {
                additionalDropdown.setVisible(true);
                additionalDropdownLabel.setVisible(true);
            } else {
                additionalDropdown.setVisible(false);
                additionalDropdownLabel.setVisible(false);
            }

            this.handleStatsComboBoxSelection(statsdropdown1, statsdropdown2);
        });
        statsdropdown1.prefWidthProperty().bind(this.statsSideBar.widthProperty().multiply(0.9));
        statsdropdown2.prefWidthProperty().bind(this.statsSideBar.widthProperty().multiply(0.9));
        this.statsSideBar.getChildren().addAll(statsdropdown1Label, statsdropdown1, statsdropdown2Label, statsdropdown2, additionalDropdownLabel, additionalDropdown);
        statsLayout.setLeft(this.statsSideBar);
        statsdropdown1.setOnAction((e) -> this.handleStatsComboBoxSelection(statsdropdown1, statsdropdown2));
        statsLayout.setCenter(this.pollutionGraph.getGraph());
        statsTab.setContent(statsLayout);
        this.syncPollutantSelection(dropdown2, statsdropdown1);
        this.syncPollutantSelection(statsdropdown1, dropdown2);
        tabPane.getTabs().addAll(mapTab, statsTab);
        Scene mainScene = new Scene(tabPane, 800.0, 600.0);
        mainScene.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("style.css")).toExternalForm());
        primaryStage.setTitle("Pollution Solution");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public boolean gridMapIsOn() {
        return this.gridMapOn;
    }

    private void gridMapToggle() {
        this.gridMapOn = !this.gridMapOn;
    }

    private void heatMapToggle(ActionEvent actionEvent) {
        if (filename != null && !filename.isEmpty()) {
            this.heatMapOn = !this.heatMapOn;
            this.displayData(filename);
        } else {
            System.out.println("No dataset selected for heatmap!");
        }
    }

    private void gridOn() {
        this.grid.getChildren().clear();
        int cols = 25;
        int rows = 17;
        double cellWidth = this.stack.getWidth() / (double)cols;
        double cellHeight = this.stack.getHeight() / (double)rows;

        for(int col = 0; col < cols; ++col) {
            for(int row = 0; row < rows; ++row) {
                Rectangle cell = new Rectangle(cellWidth, cellHeight);
                cell.setStroke(Color.GREY);
                cell.setFill(Color.TRANSPARENT);
                GridPane.setColumnIndex(cell, col);
                GridPane.setRowIndex(cell, row);
                this.grid.add(cell, col, row);
            }
        }

    }

    private void gridOff() {
        this.grid.getChildren().clear();
    }

    public void handleStatsComboBoxSelection(ComboBox<String> statsdropdown1, ComboBox<String> statsdropdown2) {
        String pollutant = statsdropdown1.getSelectionModel().getSelectedItem();
        String metric = statsdropdown2.getSelectionModel().getSelectedItem();
        if (pollutant != null && metric != null) {
            if (metric.equals("Highest")) {
                this.displayHighestPollutantLevels(new ArrayList<>(DataHandler.getHighestPollutantTrends(pollutant)));
            } else if (metric.equals("Average")) {
                List<Integer> years = List.of(2018, 2019, 2020, 2021, 2022, 2023);
                List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);
                this.pollutionGraph.loadData(years, pollutionLevels);
            }
        }

    }

    public void handleComboBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2) {
        String year = dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = dropdown2.getSelectionModel().getSelectedItem();
        if (year != null && pollutant != null) {
            filename = DataHandler.generateFilename(pollutant, year);
            this.displayData(filename);
        }

    }

    public void displayData(String filename) {
        if (!filename.isEmpty()) {
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            List<Node> markers = HeatmapAndMarkerGenerator.generateMarkers(this.heatMapOn, dataSet);
            Platform.runLater(() -> {
                this.stack.getChildren().removeIf((node) -> node instanceof Shape);
                this.stack.getChildren().addAll(markers);
            });
        }

    }

    public void displayHighestPollutantLevels(ArrayList<Double> data) {

        for (Double value : data) {
            Label text = new Label("Pollutant Level: " + value);
            this.statsSideBar.getChildren().add(text);
            System.out.println(this.numberOfNodes(this.statsSideBar));
        }

    }

    private int numberOfNodes(Pane pane) {
        return pane.getChildren().size();
    }


    private void syncPollutantSelection(ComboBox<String> source, ComboBox<String> target) {
        source.setOnAction((event) -> {
            String selectedPollutant = source.getSelectionModel().getSelectedItem();
            if (selectedPollutant != null && !selectedPollutant.equals(target.getSelectionModel().getSelectedItem())) {
                target.getSelectionModel().select(selectedPollutant);
            }

        });
    }


    private void fetchRealTimeData(ComboBox<String> dropdown) {
        Platform.runLater(() -> {
            String pollutant = dropdown.getSelectionModel().getSelectedItem();
            if (dropdown.getValue() != null) {
                List<LocationData> locations = APIHandler.loadAllLocationData();
                this.stack.getChildren().removeIf((node) -> node instanceof Shape);


                for (LocationData locationData : locations) {

                    JSONObject latestMeasurement = APIHandler.fetchLatestMeasurement(locationData, pollutant);

                    if (latestMeasurement != null) {
                        double value = latestMeasurement.getDouble("value");
                        String measurementTime = latestMeasurement.getJSONObject("datetime").getString("utc");
                        if (!measurementTime.startsWith("2025")) {
                            continue;
                        }
                        double longitude = locationData.getLongitude();
                        double latitude = locationData.getLatitude();
                        String locationName = locationData.getLocationName();
                        List<Circle> markers = HeatmapAndMarkerGenerator.generateApiPoints(longitude, latitude, pollutant, value, measurementTime, locationName);
                        this.stack.getChildren().addAll(markers);
                    }
                }

            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Issue");
                alert.setHeaderText("Pollutant missing");
                alert.setContentText("Select a Pollutant please");
                alert.showAndWait();

            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
