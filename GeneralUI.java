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

    private ComboBox<String> yearDropdown;
    private ComboBox<String> pollutantDropdown;
    private ComboBox<String> statsDropdown;


    public void start(Stage primaryStage) {
     statsDataDisplaying = false;

        // INITIALISATION
        // create a tab pane for switching between pages
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

        yearDropdown = new ComboBox<>();
        yearDropdown.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");

        pollutantDropdown = new ComboBox<>();
        pollutantDropdown.getItems().addAll("NO2", "PM10", "PM2.5");

        setupMapSideBar(mapSideBar, yearDropdown, pollutantDropdown);

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
     * @param yearDropdown the year dropdown
     * @param pollutantDropdown the pollutant dropdown
     */
    private void setupMapSideBar(VBox mapSideBar, ComboBox<String> yearDropdown, ComboBox<String> pollutantDropdown) {
        Label yearLabel = new Label("Year:");
        Label pollutantLabel = new Label("Pollutant:");

        yearDropdown.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        pollutantDropdown.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));

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
        fetchLiveDataButton.setOnAction(e -> fetchRealTimeData(pollutantDropdown));

        Button toggleDarkMode = new Button("Toggle Dark Mode");
        toggleDarkMode.setOnAction(e -> toggleDarkMode(mapSideBar.getScene()));

        mapSideBar.getChildren().addAll(
                pollutantLabel, pollutantDropdown, yearLabel, yearDropdown,
                mapGridOn, heatMap, fetchLiveDataButton,
                filterLabel, greenCheck, yellowCheck, orangeCheck, redCheck, crimsonCheck, purpleCheck,
                applyFilter, toggleDarkMode
        );

        yearDropdown.setOnAction(e -> handleComboBoxSelection(pollutantDropdown, yearDropdown, new ComboBox<>()));
        pollutantDropdown.setOnAction(e -> handleComboBoxSelection(pollutantDropdown, yearDropdown, new ComboBox<>()));
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
        statsDropdown.getItems().addAll("Highest", "Average by Period");
        statsDropdown.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        statsDropdown.setOnAction(e -> handleComboBoxSelection(pollutantDropdown, yearDropdown, statsDropdown));

        Label pollutantLabel = new Label("Pollutant");
        ComboBox<String> pollutantDropdown = new ComboBox<>();
        pollutantDropdown.getItems().addAll("NO2", "PM10", "PM2.5");
        pollutantDropdown.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));

        Label yearLabel = new Label("Year:");
        ComboBox<String> yearDropdown = new ComboBox<>();
        yearDropdown.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");
        yearDropdown.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));

        yearDropdown.setOnAction(e -> {
            this.yearDropdown.getSelectionModel().select(yearDropdown.getSelectionModel().getSelectedItem());
            handleComboBoxSelection(pollutantDropdown, this.yearDropdown, this.statsDropdown);
        });

        pollutantDropdown.setOnAction(e -> {
            pollutantDropdown.getSelectionModel().select(pollutantDropdown.getSelectionModel().getSelectedItem());
            handleComboBoxSelection(pollutantDropdown, this.yearDropdown, this.statsDropdown);
        });

        statsSideBar.getChildren().addAll(statsDropdownLabel, statsDropdown, yearLabel, yearDropdown, pollutantLabel, pollutantDropdown);
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
                    if (metric.equals("Average by Period")){
                        displayAveragePollutantLevelsByYear(DataHandler.getAveragePollutantLevelByPeriod(pollutant, year), year, pollutant);
                        }
                    }
                }
            }
        if (!Objects.equals(pollutant, previousPollutant)){ //load up the graph
            List<Integer> years = List.of(2018, 2019, 2020, 2021, 2022, 2023);
            List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);
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