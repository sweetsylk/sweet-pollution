import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
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
    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;
    private static final double MIN_LAT = 51.395246;
    private static final double MAX_LAT = 51.627741;
    private static final double MIN_LON = -0.40653443;
    private static final double MAX_LON = 0.2020537;
    private static String filename = "";
    private boolean gridMapOn = false;
    private boolean heatMapOn = false;
    private GridPane grid = new GridPane();
    private BorderPane stack = new BorderPane();
    private Graphs pollutionGraph = new Graphs();
    private VBox statsSideBar = new VBox(12.0);
    private int minStatsSideBarNodes;

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
        ComboBox<String> dropdown1 = new ComboBox();
        dropdown1.getItems().addAll(new String[]{"2023", "2022", "2021", "2020", "2019", "2018"});
        Label dropdown2Label = new Label("Pollutant:");
        ComboBox<String> dropdown2 = new ComboBox();
        dropdown2.getItems().addAll(new String[]{"NO2", "PM10", "PM2.5"});
        dropdown1.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        dropdown2.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        ToggleGroup gridToggleGroup = new ToggleGroup();
        ToggleButton mapGridOn = new ToggleButton("Map Grid");
        mapGridOn.setToggleGroup(gridToggleGroup);
        mapGridOn.setSelected(false);
        mapGridOn.setOnAction((e) -> {
            this.gridMapToggle();
            if (this.gridMapIsOn()) {
                this.gridOn((ActionEvent)null);
            } else {
                this.gridOff((ActionEvent)null);
            }

        });
        ToggleButton heatMap = new ToggleButton("Heat Map");
        mapGridOn.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.setOnAction(this::heatMapToggle);
        mapSideBar.getChildren().addAll(new Node[]{dropdown2Label, dropdown2, dropdown1Label, dropdown1, mapGridOn, heatMap});
        mapLayout.setLeft(mapSideBar);
        Image londonImage = new Image(((URL)Objects.requireNonNull(this.getClass().getResource("London.png"))).toExternalForm());
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
        ComboBox<String> statsdropdown1 = new ComboBox();
        statsdropdown1.getItems().addAll(new String[]{"NO2", "PM10", "PM2.5"});
        Label statsdropdown2Label = new Label("Metric:");
        ComboBox<String> statsdropdown2 = new ComboBox();
        statsdropdown2.getItems().addAll(new String[]{"Highest", "Average"});
        Label additionalDropdownLabel = new Label("View By:");
        ComboBox<String> additionalDropdown = new ComboBox();
        additionalDropdown.getItems().addAll(new String[]{"Area", "Period"});
        additionalDropdown.setVisible(false);
        additionalDropdownLabel.setVisible(false);
        dropdown1.setOnAction((e) -> {
            this.handleComboBoxSelection(dropdown1, dropdown2);
        });
        dropdown2.setOnAction((e) -> {
            this.handleComboBoxSelection(dropdown1, dropdown2);
        });
        statsdropdown2.setOnAction((e) -> {
            String selectedMetric = (String)statsdropdown2.getSelectionModel().getSelectedItem();
            if ("Average".equals(selectedMetric)) {
                additionalDropdown.setVisible(true);
                additionalDropdownLabel.setVisible(true);
            } else {
                additionalDropdown.setVisible(false);
                additionalDropdownLabel.setVisible(false);
            }

            this.handleStatsComboBoxSelection(statsdropdown1, statsdropdown2);
        });
        Button fetchLiveDataButton = new Button("Fetch Real-Time Pollution Data");
        fetchLiveDataButton.setOnAction((e) -> {
            this.fetchRealTimeData();
        });
        this.statsSideBar.getChildren().add(fetchLiveDataButton);
        statsdropdown1.prefWidthProperty().bind(this.statsSideBar.widthProperty().multiply(0.9));
        statsdropdown2.prefWidthProperty().bind(this.statsSideBar.widthProperty().multiply(0.9));
        this.statsSideBar.getChildren().addAll(new Node[]{statsdropdown1Label, statsdropdown1, statsdropdown2Label, statsdropdown2, additionalDropdownLabel, additionalDropdown});
        statsLayout.setLeft(this.statsSideBar);
        statsdropdown1.setOnAction((e) -> {
            this.handleStatsComboBoxSelection(statsdropdown1, statsdropdown2);
        });
        statsLayout.setCenter(this.pollutionGraph.getGraph());
        statsTab.setContent(statsLayout);
        this.syncPollutantSelection(dropdown2, statsdropdown1);
        this.syncPollutantSelection(statsdropdown1, dropdown2);
        tabPane.getTabs().addAll(new Tab[]{mapTab, statsTab});
        Scene mainScene = new Scene(tabPane, 800.0, 600.0);
        mainScene.getStylesheets().add(((URL)Objects.requireNonNull(this.getClass().getResource("style.css"))).toExternalForm());
        primaryStage.setTitle("Pollution Solution");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        this.minStatsSideBarNodes = this.numberOfNodes(this.statsSideBar);
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

    public boolean heatMapIsOn() {
        return this.heatMapOn;
    }

    private void heatMapToggle(ActionEvent actionEvent) {
        if (filename != null && !filename.isEmpty()) {
            this.heatMapOn = !this.heatMapOn;
            this.displayData(filename);
        } else {
            System.out.println("No dataset selected for heatmap!");
        }

    }

    private void gridOn(ActionEvent event) {
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

    private void gridOff(ActionEvent event) {
        this.grid.getChildren().clear();
    }

    public void handleStatsComboBoxSelection(ComboBox<String> statsdropdown1, ComboBox<String> statsdropdown2) {
        String pollutant = (String)statsdropdown1.getSelectionModel().getSelectedItem();
        String metric = (String)statsdropdown2.getSelectionModel().getSelectedItem();
        if (pollutant != null && metric != null) {
            if (metric.equals("Highest")) {
                this.displayHighestPollutantLevels(new ArrayList(DataHandler.getHighestPollutantTrends(pollutant)));
            } else if (metric.equals("Average")) {
                List<Integer> years = List.of(2018, 2019, 2020, 2021, 2022, 2023);
                List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);
                this.pollutionGraph.loadData(years, pollutionLevels);
            }
        }

    }

    public void handleComboBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2) {
        String year = (String)dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = (String)dropdown2.getSelectionModel().getSelectedItem();
        if (year != null && pollutant != null) {
            filename = DataHandler.generateFilename(pollutant, year);
            this.displayData(filename);
        }

    }

    public void displayData(String filename) {
        if (!filename.isEmpty()) {
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);
            List<Node> markers = HeatmapAndMarkerGenerator.loadData(this.heatMapOn, dataSet);
            Platform.runLater(() -> {
                this.stack.getChildren().removeIf((node) -> {
                    return node instanceof Shape;
                });
                this.stack.getChildren().addAll(markers);
            });
        }

    }

    public void displayHighestPollutantLevels(ArrayList<Double> data) {
        Iterator var2 = data.iterator();

        while(var2.hasNext()) {
            Double value = (Double)var2.next();
            Label text = new Label("Pollutant Level: " + value);
            this.statsSideBar.getChildren().add(text);
            System.out.println(this.numberOfNodes(this.statsSideBar));
        }

    }

    private int numberOfNodes(Pane pane) {
        return pane.getChildren().size();
    }

    private void removeNodes() {
        if (this.statsSideBar.getChildren().size() > this.minStatsSideBarNodes) {
            for(int i = 0; i <= 6; ++i) {
                this.statsSideBar.getChildren().remove(-1);
            }
        }

    }

    private void syncPollutantSelection(ComboBox<String> source, ComboBox<String> target) {
        source.setOnAction((event) -> {
            String selectedPollutant = (String)source.getSelectionModel().getSelectedItem();
            if (selectedPollutant != null && !selectedPollutant.equals(target.getSelectionModel().getSelectedItem())) {
                target.getSelectionModel().select(selectedPollutant);
            }

        });
    }

    private double convertLonToPixel(double lon) {
        lon += 1.0E-4;
        double normalized = (lon - -0.40653443) / 0.60858813;
        return normalized * 1781.0;
    }

    private double convertLatToPixel(double lat) {
        lat += 1.0E-4;
        double normalized = (51.627741 - lat) / 0.23249500000000012;
        return normalized * 1100.0;
    }

    private void fetchRealTimeData() {
        Platform.runLater(() -> {
            List<LocationData> locations = APIHandler.loadAllLocationData();
            this.statsSideBar.getChildren().clear();
            this.stack.getChildren().removeIf((node) -> {
                return node instanceof Shape && !(node instanceof ImageView);
            });
            Iterator var2 = locations.iterator();

            while(var2.hasNext()) {
                LocationData locationData = (LocationData)var2.next();
                JSONObject latestNO2Measurement = APIHandler.fetchLatestMeasurementByLocation(locationData);

                try {
                    Thread.sleep(1050L);
                } catch (InterruptedException var14) {
                    InterruptedException var14x = var14;
                    InterruptedException e = var14x;
                    throw new RuntimeException(e);
                }

                if (latestNO2Measurement != null) {
                    double value = latestNO2Measurement.getDouble("value");
                    String measurementTime = latestNO2Measurement.getJSONObject("datetime").getString("utc");
                    double xPixel = this.convertLonToPixel(locationData.getLongitude());
                    double yPixel = this.convertLatToPixel(locationData.getLatitude());
                    Circle marker = new Circle(5.0);
                    marker.setFill(getHeatmapColor(value));
                    marker.setStroke(Color.BLACK);
                    marker.setStrokeWidth(1.0);
                    marker.setLayoutX(xPixel);
                    marker.setLayoutY(yPixel);
                    marker.setOnMouseClicked((event) -> {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Location Details");
                        alert.setHeaderText(locationData.getLocationName());
                        alert.setContentText(String.format("Latitude: %.4f\nLongitude: %.4f\nNO₂ Level: %.2f µg/m³\nMeasured at: %s", locationData.getLatitude(), locationData.getLongitude(), value, measurementTime));
                        alert.showAndWait();
                    });
                    this.stack.getChildren().add(marker);
                    Label label = new Label(String.format("%s\nLat: %.4f, Lon: %.4f\nNO₂ Level: %.2f µg/m³\nMeasured at: %s", locationData.getLocationName(), locationData.getLatitude(), locationData.getLongitude(), value, measurementTime));
                    this.statsSideBar.getChildren().add(label);
                }
            }

        });
    }

    private static Color getHeatmapColor(double pollution) {
        double alpha = 0.5;
        if (pollution < 10.0) {
            return Color.rgb(0, 191, 0, alpha);
        } else if (pollution < 20.0) {
            return Color.rgb(255, 215, 0, alpha);
        } else if (pollution < 30.0) {
            return Color.rgb(255, 140, 0, alpha);
        } else if (pollution < 40.0) {
            return Color.rgb(220, 20, 60, alpha);
        } else {
            return pollution < 50.0 ? Color.rgb(139, 0, 0, alpha) : Color.rgb(128, 0, 128, alpha);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
