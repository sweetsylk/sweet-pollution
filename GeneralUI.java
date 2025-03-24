import java.util.ArrayList;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.json.JSONObject;

/**
 * This is the class that handles UI related stuff
 * @author Ridwan Adam, Irfan Hussein, Khem-Talah, Ayesha Stevens
 * @version 1.0
 */
public class GeneralUI extends Application {
    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;
    private static String filename = "";
    private boolean gridMapOn = false;
    private boolean heatMapOn = false;

    private GridPane grid = new GridPane();
    private BorderPane stack = new BorderPane();
    private Graphs pollutionGraph = new Graphs();
    private VBox statsSideBar = new VBox(12.0);

    private boolean statsDataDisplaying;
    private boolean averageDisplaying;
    private String previousPollutant;

    private ComboBox<String> mapYearDropdown;
    private ComboBox<String> mapPollutantDropdown;
    private ComboBox<String> yearDropdown;
    private ComboBox<String> pollutantDropdown;
    private ComboBox<String> statsDropdown;

    private static int selectedGridCode = -1;

    /**
     * This is the intitial set up of the JavaFx program for the main program
     * @param primaryStage
     */
    public void start(Stage primaryStage) {
        statsDataDisplaying = false;

        // Initialize dropdowns for the map sidebar
        mapYearDropdown = new ComboBox<>();
        mapYearDropdown.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");

        mapPollutantDropdown = new ComboBox<>();
        mapPollutantDropdown.getItems().addAll("NO2", "PM10", "PM2.5");

        // Initialize dropdowns for the stats sidebar
        yearDropdown = new ComboBox<>();
        yearDropdown.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");

        pollutantDropdown = new ComboBox<>();
        pollutantDropdown.getItems().addAll("NO2", "PM10", "PM2.5");

        // Synchronize selections between both sets of dropdowns
        mapYearDropdown.setOnAction(e -> yearDropdown.setValue(mapYearDropdown.getValue()));
        yearDropdown.setOnAction(e -> mapYearDropdown.setValue(yearDropdown.getValue()));

        mapPollutantDropdown.setOnAction(e -> pollutantDropdown.setValue(mapPollutantDropdown.getValue()));
        pollutantDropdown.setOnAction(e -> mapPollutantDropdown.setValue(pollutantDropdown.getValue()));

        TabPane tabPane = new TabPane();
        createMapTab(tabPane);
        createStatsTab(tabPane);
        createMainScene(primaryStage, tabPane);
    }


    /**
     * Makes the tab that has the map and its related features
     * @param tabPane the tabpane
     */
    private void createMapTab(TabPane tabPane) {
        Tab mapTab = new Tab("Map");
        mapTab.setClosable(false);

        BorderPane mapLayout = new BorderPane();
        mapLayout.getStyleClass().add("main-background");

        VBox mapSideBar = new VBox(12);
        mapSideBar.getStyleClass().add("sidebar");
        mapSideBar.setPrefWidth(150);
        mapSideBar.setAlignment(Pos.TOP_CENTER);
        mapSideBar.prefHeightProperty().bind(stack.heightProperty());
        mapSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));

        setupMapSideBar(mapSideBar);

        ImageView londonImageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResource("London.png")).toExternalForm()));
        londonImageView.setPreserveRatio(false);
        londonImageView.setFitWidth(MAP_WIDTH);
        londonImageView.setFitHeight(MAP_HEIGHT);
        londonImageView.fitWidthProperty().bind(stack.widthProperty());
        londonImageView.fitHeightProperty().bind(stack.heightProperty());

        stack.getChildren().addAll(londonImageView, grid);
        mapLayout.setCenter(stack);
        mapLayout.setLeft(mapSideBar);

        mapTab.setContent(mapLayout);
        tabPane.getTabs().add(mapTab);
    }

    /**
     * adds components to the mapside bar
     * @param mapSideBar the map sidebar
     */
    private void setupMapSideBar(VBox mapSideBar) {
        mapPollutantDropdown.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        mapYearDropdown.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));

        Label filterLabel = new Label("Filter by Pollution Level:");
        CheckBox greenCheck = new CheckBox("Low (Green)");
        CheckBox yellowCheck = new CheckBox("Moderate (Yellow)");
        CheckBox orangeCheck = new CheckBox("High (Orange)");
        CheckBox redCheck = new CheckBox("Very High (Red)");
        CheckBox crimsonCheck = new CheckBox("Severe (Crimson)");
        CheckBox purpleCheck = new CheckBox("Hazardous (Purple)");

        greenCheck.setSelected(true);
        yellowCheck.setSelected(true);
        orangeCheck.setSelected(true);
        redCheck.setSelected(true);
        crimsonCheck.setSelected(true);
        purpleCheck.setSelected(true);

        Button applyFilter = new Button("Apply Filter");
        applyFilter.setOnAction(e -> applyPollutionFilter(greenCheck, yellowCheck, orangeCheck, redCheck, crimsonCheck, purpleCheck));

        ToggleButton mapGridOn = new ToggleButton("Map Grid");
        mapGridOn.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        mapGridOn.setOnAction(e -> {
            gridMapToggle();
            if (gridMapIsOn()) {
                gridOn();
            } else {
                gridOff();
            }
        });

        ToggleButton heatMap = new ToggleButton("Heat Map");
        heatMap.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.setOnAction(this::heatMapToggle);

        Button fetchLiveDataButton = new Button("Fetch Real-Time Pollution Data");
        fetchLiveDataButton.setOnAction(e -> fetchRealTimeData(mapPollutantDropdown));

        Button toggleDarkMode = new Button("Toggle Dark Mode");
        toggleDarkMode.setOnAction(e -> toggleDarkMode(mapSideBar.getScene()));

        mapSideBar.getChildren().addAll(
                new Label("Pollutant:"), mapPollutantDropdown,
                new Label("Year:"), mapYearDropdown,
                mapGridOn, heatMap, fetchLiveDataButton,
                filterLabel, greenCheck, yellowCheck, orangeCheck, redCheck, crimsonCheck, purpleCheck,
                applyFilter, toggleDarkMode
        );
    }

    /**
     * Handles the CSS changes between light/dark mode
     * @param scene the scene
     */
    private void toggleDarkMode(Scene scene) {
        if (scene.getStylesheets().contains(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm())) {
            scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-style.css")).toExternalForm());
        } else {
            scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("dark-style.css")).toExternalForm());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        }
    }

    /**
     * makes the tabs for the stats related components
     * @param tabPane the tab pane
     */
    private void createStatsTab(TabPane tabPane) {
        Tab statsTab = new Tab("Stats");
        statsTab.setClosable(false);

        BorderPane statsLayout = new BorderPane();
        statsLayout.getStyleClass().add("main-background");

        statsSideBar.getStyleClass().add("sidebar");
        statsSideBar.setAlignment(Pos.TOP_CENTER);
        statsSideBar.prefHeightProperty().bind(stack.heightProperty());
        statsSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));

        Label statsDropdownLabel = new Label("Metric:");
        statsDropdown = new ComboBox<>();
        statsDropdown.getItems().addAll("Highest", "Average by Period", "Average for Area");
        statsDropdown.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        statsDropdown.setOnAction(e -> handleComboBoxSelection(pollutantDropdown, yearDropdown, statsDropdown));



        this.pollutantDropdown.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));

        this.yearDropdown.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));

        this.yearDropdown.setOnAction(e -> {
            this.yearDropdown.getSelectionModel().select(yearDropdown.getSelectionModel().getSelectedItem());
            handleComboBoxSelection(this.pollutantDropdown, this.yearDropdown, statsDropdown);
        });

        this.pollutantDropdown.setOnAction(e -> {
            pollutantDropdown.getSelectionModel().select(pollutantDropdown.getSelectionModel().getSelectedItem());
            handleComboBoxSelection(this.pollutantDropdown, this.yearDropdown, statsDropdown);
        });

        statsSideBar.getChildren().addAll(
                statsDropdownLabel, statsDropdown,
                new Label("Year:"), yearDropdown,
                new Label("Pollutant:"), pollutantDropdown
        );
        statsLayout.setLeft(statsSideBar);
        statsLayout.setCenter(pollutionGraph.getGraph());

        // Disable the dropdowns when no pollutant or year is selected
        statsDropdown.disableProperty().bind(
                pollutantDropdown.getSelectionModel().selectedItemProperty().isNull()
                        .or(this.yearDropdown.getSelectionModel().selectedItemProperty().isNull())
        );


        statsTab.setContent(statsLayout);
        tabPane.getTabs().add(statsTab);
    }

    /**
     * makes and sets up the main scene
     * @param primaryStage the stage used
     * @param tabPane the tab pane used
     */
    private void createMainScene(Stage primaryStage, TabPane tabPane) {
        Scene mainScene = new Scene(tabPane, 800, 600);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-style.css")).toExternalForm());

        primaryStage.setTitle("Pollution Solution");
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }


    /**
     * this just tells you if the grid map is on
     * @return gridMapon
     */
    public boolean gridMapIsOn() {
        return gridMapOn;
    }


    /**
     * turns the grid map on and off
     */
    private void gridMapToggle() {
        gridMapOn = !gridMapOn;
    }

    /**
     * returns weather the heat map is on or off
     */
    public boolean heatMapIsOn() {
        return heatMapOn;
    }

    /**
     * Turns the heatmap on and off
     */
    private void heatMapToggle(ActionEvent actionEvent) {
        heatMapOn = !heatMapOn;  // Toggle the heatmap state
        displayDataOntoMap(filename);
    }
    public static void setSelectedGridCode(int gridCode) {
        selectedGridCode = gridCode;
    }

    /**
     * displays map grid
     */
    private void gridOn() {
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

    /**
     * removes the map grid
     */
    private void gridOff() {
        grid.getChildren().clear(); // Completely removes all grid elements
    }

    /**
     * handles the actions done when the map related comboboxes are selected
     * @param pollutantDropDown which pollutant is selected
     * @param yearDropDown which year is selected
     * @param statsdropdown which metric from the stats page is selected
     */
    public void handleComboBoxSelection(ComboBox<String> pollutantDropDown, ComboBox<String> yearDropDown, ComboBox<String> statsdropdown) {
        String pollutant = pollutantDropDown.getSelectionModel().getSelectedItem();
        String year = yearDropDown.getSelectionModel().getSelectedItem();
        String metric = statsdropdown.getSelectionModel().getSelectedItem();
        if (pollutant != null && year != null) {

            filename = DataHandler.generateFilename(pollutant, year);
            displayDataOntoMap(filename);
            if (metric != null){
                if (metric.equals("Highest")) {
                displayHighestPollutantLevels(new ArrayList<>(DataHandler.getHighestPollutantTrends(pollutant)));
                }
                else {
                    if (metric.equals("Average by Period")) {
                        displayAveragePollutantLevelsByYear(DataHandler.getAveragePollutantLevelByPeriod(pollutant, year), year, pollutant);
                    }

                    else if (metric.equals("Average for Area")) {
                        System.out.println("lol");
                        displayAveragePollutantLevelsByArea(pollutant, selectedGridCode);

                    }
                }
                }
            }
        if (!Objects.equals(pollutant, previousPollutant)) {
            List<Integer> years = new ArrayList<>();
            List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);

            for (int currentYear = 2018; currentYear <= 2023; currentYear++) {
                if (pollutionLevels.size() >= (currentYear - 2018 + 1)) {
                    years.add(currentYear);
                }
            }

            // Now load data without mismatched indices
            pollutionGraph.loadData(years, pollutionLevels);
            previousPollutant = pollutant;


    }
    }


    /**
     * Displays the average levels of pollution by years
     * @param value the pollution level
     * @param year the year
     * @param pollutant the pollutant
     */
    private void displayAveragePollutantLevelsByYear(double value, String year, String pollutant)
    {   
        if(statsDataDisplaying){
            removeNodes();
        }
        Label text = new Label("Average " + pollutant + " level for " + year + ": " + value);
        text.setWrapText(true);
        statsSideBar.getChildren().add(text);
        statsDataDisplaying = true;
        averageDisplaying = true;
    }

    /**
     * This method just shows the average pollution for a given gridcode on the stats tab
     * @param pollutant the pollutant
     * @param gridCode the gridcode
     */
    private void displayAveragePollutantLevelsByArea(String pollutant, int gridCode) {
        if (statsDataDisplaying) {
            removeNodes();
        }
        double average = DataHandler.getAveragePollutantLevelForArea(pollutant, gridCode);
        Label text;

        if (average == 0.0) {
            text = new Label("No data found for grid code: " + gridCode + ". Please double-click a point on the map.");
            text.setTextFill(Color.GRAY);
        } else {
            text = new Label(String.format("Average %s level for grid code %d (2018–2023): %.2f", pollutant, gridCode, average));
            text.setWrapText(true);
            if (average < 10) text.setTextFill(Color.GREEN);
            else if (average < 20) text.setTextFill(Color.GOLDENROD);
            else if (average < 30) text.setTextFill(Color.ORANGE);
            else text.setTextFill(Color.DARKRED);
        }

        statsSideBar.getChildren().add(text);
        statsDataDisplaying = true;
        averageDisplaying = true;
    }



    /**
     * Displays the data of onto the map
     * @param filename the filename of the dataset used
     */
    public void displayDataOntoMap(String filename) {
        if (!filename.isEmpty()) {

            DataSet dataSet = DataHandler.loadDataSet(filename);
            List<Node> markers = HeatmapAndMarkerGenerator.generateMarkers(heatMapIsOn(), dataSet);
            Platform.runLater(() -> {
                this.stack.getChildren().removeIf((node) -> node instanceof Shape);
                this.stack.getChildren().addAll(markers);
            });
        }

    }

    /**
     * shows the highest pollution levels recorded for each grid element
     * @param data the list of datapoints
     */
    private void displayHighestPollutantLevels(ArrayList<DataPoint> data) {
        if(statsDataDisplaying){
            removeNodes();
        }
        int i = 0;
        for (DataPoint dataPoint : data) {
            i++;
            Label text = new Label(i +". " + "Pollutant Level: " + dataPoint.value() + " Unique Grid Code: " + dataPoint.gridCode());
            text.setWrapText(true);
            statsSideBar.getChildren().add(text);
        }
        statsDataDisplaying = true;
        averageDisplaying = false;
    }

    /**
     * Removes nodes from the stats sidebar when changing between displaying different metrics of data
     * The amount of nodes needed to be removed are based on the nodes that previously displayed 
     */
    private void removeNodes() {
        int paneSize = statsSideBar.getChildren().size();
        int numToRemove;
        if (averageDisplaying){
            numToRemove = 1;
        }
        else{
            numToRemove = 10;
        }
        if (paneSize > numToRemove) {
            for (int i = 0; i < numToRemove; i++) {
                statsSideBar.getChildren().remove(paneSize - 1 - i);
            }
        }
    }

    /**
     * calls filterPollutionPoints() method to hide certain markers
     * @param greenCheck checkbox
     * @param yellowCheck checkbox
     * @param orangeCheck checkbox
     * @param redCheck checkbox
     * @param crimsonCheck checkbox
     * @param purpleCheck checkbox
     */
    private void applyPollutionFilter(CheckBox greenCheck, CheckBox yellowCheck, CheckBox orangeCheck, CheckBox redCheck, CheckBox crimsonCheck, CheckBox purpleCheck) {
        HeatmapAndMarkerGenerator.filterPollutionPoints(this.stack.getChildren(), greenCheck.isSelected(), yellowCheck.isSelected(), orangeCheck.isSelected(), redCheck.isSelected(), crimsonCheck.isSelected(), purpleCheck.isSelected());
    }


    /**
     * This calls methods from APIHandler to retrieve real time API responses for pollution
     * @param dropdown the pollutant selected
     */
    private void fetchRealTimeData(ComboBox<String> dropdown) {
        Platform.runLater(() -> {
            Alert alertWarning = new Alert(AlertType.INFORMATION);
            alertWarning.setTitle("Just a warning...");
            alertWarning.setHeaderText("This may take a while");
            alertWarning.setContentText("You may need to wait up to a minute due to API request limits");
            alertWarning.showAndWait();

            String pollutant = dropdown.getSelectionModel().getSelectedItem();
            if (dropdown.getValue() != null) {
                APIHandler handler = new APIHandler(); // instance used to make sure the correct apikey is passed through
                List<LocationData> locations = handler.loadAllLocationData();
                this.stack.getChildren().removeIf((node) -> node instanceof Shape);

                for (LocationData locationData : locations) {
                    JSONObject latestMeasurement;

                    try {
                        latestMeasurement = handler.fetchLatestMeasurementByLocation(locationData, pollutant);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (latestMeasurement != null) {
                        double value = latestMeasurement.getDouble("value");
                        String measurementTime = latestMeasurement.getJSONObject("datetime").getString("utc");
                        if (measurementTime.startsWith("2025")) {
                            double longitude = locationData.getLongitude();
                            double latitude = locationData.getLatitude();
                            String locationName = locationData.getLocationName();
                            List<Circle> markers = HeatmapAndMarkerGenerator.generateApiPoints(longitude, latitude, pollutant, value, measurementTime, locationName);
                            this.stack.getChildren().addAll(markers);
                        }
                    }
                }
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Issue");
                alert.setHeaderText("Pollutant missing");
                alert.setContentText("Select a Pollutant please");
                alert.showAndWait();
            }

        });
    }

    //used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}