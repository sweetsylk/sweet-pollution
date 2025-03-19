import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;



public class GeneralUI extends Application {
    // these are just the image width and height (1781 x 1100)
    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;
    private static final double MIN_LAT = 51.395246;
    private static final double MAX_LAT = 51.627741;
    private static final double MIN_LON = -0.40653443;
    private static final double MAX_LON = 0.20205370;
    private static String filename = "";
    private boolean gridMapOn = false;
    private boolean heatMapOn = false;
    private GridPane grid = new GridPane();
    private BorderPane stack = new BorderPane();
    private Graphs pollutionGraph = new Graphs();

    private VBox statsSideBar = new VBox(12);


    private int minStatsSideBarNodes;


    // load and display the map image
    public void start(Stage primaryStage) {
        // INITIALISATION
        // create a tab pane for switching between pages
        TabPane tabPane = new TabPane();

        // create map and stats tabs
        Tab mapTab = new Tab("Map");
        Tab statsTab = new Tab("Stats");

        // prevent tabs from being closed
        mapTab.setClosable(false);
        statsTab.setClosable(false);


        // MAP TAB CREATION
        // creating the borderpane
        BorderPane mapLayout = new BorderPane();
        mapLayout.getStyleClass().add("main-background");


        //CREATION OF MAP SIDEBAR

        // create a sidebar for dropdowns in the map tab, containing the UI vertically
        VBox mapSideBar = new VBox(12);
        mapSideBar.getStyleClass().add("sidebar");

        //setting mapside bar's display preferences
        mapSideBar.setPrefWidth(150);
        mapSideBar.setAlignment(Pos.TOP_CENTER);
        mapSideBar.prefHeightProperty().bind(stack.heightProperty());
        mapSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));


        //CREATION OF DROPDOWN BOXES FOR SIDEBAR


        // first dropdown box, for choosing the year
        Label dropdown1Label = new Label("Year:");
        ComboBox<String> dropdown1 = new ComboBox<>();
        dropdown1.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");

        // second dropdown box, for choosing the pollutant
        Label dropdown2Label = new Label("Pollutant:");
        ComboBox<String> dropdown2 = new ComboBox<>();
        dropdown2.getItems().addAll("NO2", "PM10", "PM2.5");

        //modifying the display of the comboboxes
        dropdown1.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        dropdown2.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));


        //CREATION OF TOGGLE BUTTONS FOR MAP GRID AND HEAT MAP

        // create a toggle group of toggle buttons
        ToggleGroup gridToggleGroup = new ToggleGroup();

        //MAPGRID
        // create a single toggle button for the grid
        ToggleButton mapGridOn = new ToggleButton("Map Grid");

        mapGridOn.setToggleGroup(gridToggleGroup);

        // set default state for map grid as off
        mapGridOn.setSelected(false);

        // event handler for toggling the button for the map grid
        mapGridOn.setOnAction(e -> {
            gridMapToggle();
            if (gridMapIsOn() == true) {
                gridOn(null);  // grid ON effect
            } else {
                gridOff(null); // when toggled off, call grid OFF effect
            }
        });


        //HEATMAP
        //Create sinlge toggle button for the heatmap
        ToggleButton heatMap = new ToggleButton("Heat Map");

        //modifying the display of the map grid and heatmap
        mapGridOn.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        //made heatbutton usable
        heatMap.setOnAction(this::heatMapToggle);


        //MAPSIDEBAR
        // add the dropdown boxes to the sidebar, containing the UI vertically
        mapSideBar.getChildren().addAll(dropdown2Label, dropdown2, dropdown1Label, dropdown1, mapGridOn, heatMap);
        mapLayout.setLeft(mapSideBar);


        //LONDON IMAGE
        // load and display the map image
        Image londonImage = new Image(Objects.requireNonNull(getClass().getResource("London.png")).toExternalForm()); // this is to prevent null exceptions
        ImageView londonImageView = new ImageView(londonImage);

        // ensure the image scales properly if true
        londonImageView.setPreserveRatio(false);
        londonImageView.setFitWidth(MAP_WIDTH);
        londonImageView.setFitHeight(MAP_HEIGHT);

        // map image scales with window
        londonImageView.fitWidthProperty().bind(stack.widthProperty());
        londonImageView.fitHeightProperty().bind(stack.heightProperty());


        //NEW PANE TO FIT MULTIPLE DISPLAYS ON CENTER BORDER PANE
        // use a stack pane to fit grid map onto image
        stack.getChildren().add(londonImageView);
        stack.getChildren().add(grid);

        // place the image in the center of the map layout
        mapLayout.setCenter(stack);
        // assign the completed layout to the map tab
        mapTab.setContent(mapLayout);


        // STATS TAB CREATION
        BorderPane statsLayout = new BorderPane();
        statsLayout.getStyleClass().add("main-background");


        //STATS SIDE BAR CREATION
        // create a sidebar for dropdowns in the stats tab, containing the UI vertically
        //modifying display of sidebar
        statsSideBar.getStyleClass().add("sidebar");
        statsSideBar.setAlignment(Pos.TOP_CENTER);
        statsSideBar.prefHeightProperty().bind(stack.heightProperty());
        statsSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));
        ;


        //CREATION OF STATS DROPDOWN BOXES

        // first dropdown box, for choosing the year
        Label statsdropdown1Label = new Label("Pollutant:");
        ComboBox<String> statsdropdown1 = new ComboBox<>();
        statsdropdown1.getItems().addAll("NO2", "PM10", "PM2.5");


        //second dropdown box, for highest or average
        Label statsdropdown2Label = new Label("Metric:");
        ComboBox<String> statsdropdown2 = new ComboBox<>();
        statsdropdown2.getItems().addAll("Highest", "Average");


        // dropdown for metirc depending on "Average" option if pressed (hidden by default)
        Label additionalDropdownLabel = new Label("View By:");
        ComboBox<String> additionalDropdown = new ComboBox<>();
        additionalDropdown.getItems().addAll("Area", "Period");
        additionalDropdown.setVisible(false); // initially hidden
        additionalDropdownLabel.setVisible(false);


        //Listeners for the comboboxes
        dropdown1.setOnAction(e -> handleComboBoxSelection(dropdown1, dropdown2));
        dropdown2.setOnAction(e -> handleComboBoxSelection(dropdown1, dropdown2));


        // listener for the metric dropdown of "Average"
        statsdropdown2.setOnAction(e -> {
            String selectedMetric = statsdropdown2.getSelectionModel().getSelectedItem();
            if ("Average".equals(selectedMetric)) {
                additionalDropdown.setVisible(true); // show when "Average" is selected
                additionalDropdownLabel.setVisible(true);
            } else {
                additionalDropdown.setVisible(false); // hide when "Average" is not selected
                additionalDropdownLabel.setVisible(false);
            }
            handleStatsComboBoxSelection(statsdropdown1, statsdropdown2);
        });


        Button fetchLiveDataButton = new Button("Fetch Real-Time Pollution Data");
        fetchLiveDataButton.setOnAction(e -> fetchRealTimeData());
        statsSideBar.getChildren().add(fetchLiveDataButton);



        //assigning fixed width and binding to stats side bar
        statsdropdown1.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        statsdropdown2.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));


        //adding functioning buttons and labels to stats side bar
        statsSideBar.getChildren().addAll(statsdropdown1Label, statsdropdown1, statsdropdown2Label, statsdropdown2, additionalDropdownLabel, additionalDropdown);
        statsLayout.setLeft(statsSideBar);


        //Listeners for the comboboxes
        statsdropdown1.setOnAction(e -> handleStatsComboBoxSelection(statsdropdown1, statsdropdown2));

        // place the stats chart in the center
        statsLayout.setCenter(pollutionGraph.getGraph());

        // assign the completed layout to the stats tab
        statsTab.setContent(statsLayout);

        syncPollutantSelection(dropdown2, statsdropdown1); // sync pollutant in map to stats
        syncPollutantSelection(statsdropdown1, dropdown2); // sync pollutant in stats to map


        // FINALISING SCENE
        tabPane.getTabs().addAll(mapTab, statsTab);

        // create the primary scene within the tab pane
        Scene mainScene = new Scene(tabPane, 800, 600);


        //connect external css file
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm()); // this is to prevent nullpointer exceptions when accessing css files

        // set the title of the application
        primaryStage.setTitle("Pollution Solution");


        // set up primaryStage mainScene
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);


        //taking note of the minimum number of nodes on the stats side bar
        minStatsSideBarNodes = numberOfNodes(statsSideBar);

        // Enable fullscreen AFTER switching scenes
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        //primaryStage.setFullScreen(true);
        primaryStage.show(); // Show the new window
    }

    public boolean gridMapIsOn() {
        return gridMapOn;
    }


    private void gridMapToggle() {
        gridMapOn = !gridMapOn;
    }


    public boolean heatMapIsOn() {
        return heatMapOn;
    }


    private void heatMapToggle(ActionEvent actionEvent) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("No dataset selected for heatmap!");
            return;
        }

        heatMapOn = !heatMapOn;  // Toggle the heatmap state

        // 🔹 Ensure data updates correctly
        displayData(filename);
    }




    // displays map grid
    private void gridOn(ActionEvent event) {
        grid.getChildren().clear();

        int cols = 25;
        int rows = 17;

        double cellWidth = stack.getWidth() / cols;
        double cellHeight = stack.getHeight() / rows;

        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                Rectangle cell = new Rectangle(cellWidth, cellHeight);
                cell.setStroke(Color.GREY);
                cell.setFill(Color.TRANSPARENT);


                GridPane.setColumnIndex(cell, col);
                GridPane.setRowIndex(cell, row);
                grid.add(cell, col, row);
            }
        }
    }

    // removes map grid
    private void gridOff(ActionEvent event) {
        grid.getChildren().clear(); // Completely removes all grid elements
    }

    public void handleStatsComboBoxSelection(ComboBox<String> statsdropdown1, ComboBox<String> statsdropdown2) {
        String pollutant = statsdropdown1.getSelectionModel().getSelectedItem();
        String metric = statsdropdown2.getSelectionModel().getSelectedItem();
        if (pollutant != null && metric != null) {
            if (metric.equals("Highest")) {
                displayHighestPollutantLevels(new ArrayList<>(DataHandler.getHighestPollutantTrends(pollutant)));
            } else if (metric.equals("Average"))  {
                List<Integer> years = List.of(2018, 2019, 2020, 2021, 2022, 2023);
                List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);
                pollutionGraph.loadData(years, pollutionLevels);
            }
        }
    }

    // Event handler for combo boxes
    public void handleComboBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2) {
        String year = dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = dropdown2.getSelectionModel().getSelectedItem();

        if (year != null && pollutant != null) {
            filename = DataHandler.generateFilename(pollutant, year);

            displayData(filename);
        }
    }

    public void displayData(String filename) {
        if (!filename.isEmpty()) {
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);

            // Generate heatmap or marker-based visualization
            List<Node> markers = HeatmapAndMarkerGenerator.loadData(heatMapOn, dataSet);

            Platform.runLater(() -> {
                stack.getChildren().removeIf(node -> node instanceof Shape);
                stack.getChildren().addAll(markers);
            });
        }
    }


    public void displayHighestPollutantLevels(ArrayList<Double> data) {
        for (Double value : data) {
            Label text = new Label("Pollutant Level: " + value);
            statsSideBar.getChildren().add(text);
            System.out.println(numberOfNodes(statsSideBar));
        };
    }

    private int numberOfNodes(Pane pane) {
        return pane.getChildren().size();
    }

    private void removeNodes() {
        if (statsSideBar.getChildren().size() > minStatsSideBarNodes) {
            for (int i = 0; i <= 6; i++) {
                statsSideBar.getChildren().remove(-1);
            }
        }
    }

    // create a shared pollutant selection listener to make sure both tabs have same pollutant type
    private void syncPollutantSelection(ComboBox<String> source, ComboBox<String> target) {
        source.setOnAction(event -> {
            String selectedPollutant = source.getSelectionModel().getSelectedItem();
            if (selectedPollutant != null && !selectedPollutant.equals(target.getSelectionModel().getSelectedItem())) {
                target.getSelectionModel().select(selectedPollutant);  // sync selection by choosing same option
            }
        });
    }

    private double convertLonToPixel(double lon) {
        lon += 0.0001;
        double normalized = (lon - MIN_LON) / (MAX_LON - MIN_LON);
        return normalized * MAP_WIDTH;
    }

    private double convertLatToPixel(double lat) {
        lat += 0.0001;
        double normalized = (MAX_LAT - lat) / (MAX_LAT - MIN_LAT);
        return normalized * MAP_HEIGHT;
    }

    /**
     * This method calls the fetchairpollution data method from the APIhandler to get the data of locations
     * then it checks for sensor IDS which store unique measurements
     * then it makes the point and stuff
     * this will need to be refactored to have better design qualities
     */
    private void fetchRealTimeData() {
        Platform.runLater(() -> {
            // Fetch location data (this is used to then get the sensor)
            JSONArray locationsData = APIHandler.fetchAirPollutionData();
            if (locationsData == null) {
                System.out.println("Failed to fetch location data.");
                return;
            }

            // Clear previous stats and markers
            statsSideBar.getChildren().clear();
            stack.getChildren().removeIf(node -> node instanceof Shape && !(node instanceof ImageView));

            // Loop over each location
            for (int i = 0; i < locationsData.length(); i++) {
                JSONObject locationData = locationsData.getJSONObject(i);
                String locationName = locationData.getString("name");
                JSONObject coords = locationData.getJSONObject("coordinates");
                double lat = coords.getDouble("latitude");
                double lon = coords.getDouble("longitude");

                // This just deals with no2 sensors
                JSONArray sensors = locationData.getJSONArray("sensors");
                for (int j = 0; j < sensors.length(); j++) {
                    JSONObject sensor = sensors.getJSONObject(j);
                    JSONObject parameter = sensor.getJSONObject("parameter");
                    if ("no2".equalsIgnoreCase(parameter.getString("name"))) {
                        int sensorId = sensor.getInt("id");

                        // Fetch latest measurement for this NO₂ sensor via the measurements endpoint
                        JSONObject latestMeasurement = APIHandler.fetchLatestMeasurement(sensorId);
                        if (latestMeasurement != null) {
                            double no2Value = latestMeasurement.getDouble("value");
                            // getting the date of the measurement
                            JSONObject period = latestMeasurement.getJSONObject("period");
                            JSONObject datetimeFrom = period.getJSONObject("datetimeFrom");
                            String measurementTime = datetimeFrom.getString("utc");

                            // Step 4: Update the UI
                            double xPixel = convertLonToPixel(lon);
                            double yPixel = convertLatToPixel(lat);

                            Circle marker = new Circle(5);
                            marker.setFill(getHeatmapColor(no2Value));
                            marker.setStroke(Color.BLACK);
                            marker.setStrokeWidth(1);
                            marker.setLayoutX(xPixel);
                            marker.setLayoutY(yPixel);

                            marker.setOnMouseClicked(event -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Location Details");
                                alert.setHeaderText(locationName);
                                alert.setContentText(String.format(
                                        "Latitude: %.4f\nLongitude: %.4f\nNO₂ Level: %.2f µg/m³\nMeasured at: %s",
                                        lat, lon, no2Value, measurementTime));
                                alert.showAndWait();
                            });

                            stack.getChildren().add(marker);

                            Label label = new Label(String.format(
                                    "%s\nLat: %.4f, Lon: %.4f\nNO₂ Level: %.2f µg/m³\nMeasured at: %s",
                                    locationName, lat, lon, no2Value, measurementTime));
                            statsSideBar.getChildren().add(label);
                        } else {
                            System.out.println("No measurement data for sensor ID: " + sensorId);
                        }
                    }
                }
            }
        });
    }

    /**
     * This just gets the heatmap color for the points (will be redundant)
     * @param pollution the pollution data
     * @return Color a color for the point
     */
    private static Color getHeatmapColor(double pollution) {
        double alpha = 0.5;
        if (pollution < 10) return Color.rgb(0, 191, 0, alpha);       // Green
        else if (pollution < 20) return Color.rgb(255, 215, 0, alpha); // Yellow
        else if (pollution < 30) return Color.rgb(255, 140, 0, alpha); // Orange
        else if (pollution < 40) return Color.rgb(220, 20, 60, alpha); // Red
        else if (pollution < 50) return Color.rgb(139, 0, 0, alpha);   // Crimson
        else return Color.rgb(128, 0, 128, alpha);                     // Purple
    }


    //used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}
