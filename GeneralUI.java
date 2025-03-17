import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;



public class GeneralUI extends Application {
    // these are just the image width and height (1781 x 1100)
    private static final int MAP_WIDTH = 1781;
    private static final int MAP_HEIGHT = 1100;
    private static String filename = "";
    private boolean gridMapOn = false;
    private boolean heatMapOn = false;
    private GridPane grid = new GridPane();
    private BorderPane stack = new BorderPane();
    private Graphs pollutionGraph = new Graphs();

    private VBox statsSideBar = new VBox(12);


    private boolean statsDataDisplaying;
    private boolean averageDisplaying;


    // load and display the map image
    public void start(Stage primaryStage) {
        statsDataDisplaying = false;
        
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
        dropdown1.setOnAction(e -> handleComboBoxSelection(statsdropdown1, statsdropdown2, statsdropdown2));
        dropdown2.setOnAction(e -> handleComboBoxSelection(statsdropdown1, statsdropdown2, statsdropdown2));


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
            handleStatsComboBoxSelection(statsdropdown1, dropdown1, statsdropdown2, additionalDropdown);
        });


        //Listeners for the comboboxes
        dropdown1.setOnAction(e -> handleComboBoxSelection(dropdown1, dropdown2, statsdropdown2));
        dropdown2.setOnAction(e -> handleComboBoxSelection(dropdown1, dropdown2, statsdropdown2));


        //creating buttons for stats display
        Button averagePollutionButton = new Button("Average");
        Button highestPollutionButton = new Button("Highest");
        Button trendsOverTimeButton = new Button("Trends over Time");


        //assigning fixed width and binding to stats side bar
        statsdropdown1.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        statsdropdown2.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));


        //adding functioning buttons and labels to stats side bar
        statsSideBar.getChildren().addAll(statsdropdown1Label, statsdropdown1, statsdropdown2Label, statsdropdown2, additionalDropdownLabel, additionalDropdown);
        statsLayout.setLeft(statsSideBar);


        //Listeners for the comboboxes
        statsdropdown1.setOnAction(e -> handleStatsComboBoxSelection(statsdropdown1, dropdown1, statsdropdown2, additionalDropdown));
        
        
        additionalDropdown.setOnAction(e -> handleStatsComboBoxSelection(statsdropdown1, dropdown1, statsdropdown2, additionalDropdown));
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

        // Enable fullscreen AFTER switching scenes
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
        primaryStage.show(); // Show the new window
    }

    public boolean gridMapIsOn() {
        return gridMapOn;
    }


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
        if (filename == null || filename.isEmpty()) {
            System.out.println("No dataset selected for heatmap!");
            return;
        }

        heatMapOn = !heatMapOn;  // Toggle the heatmap state

        //Ensure data updates correctly
        displayData(filename);
    }




    /**
     * displays map grid
     */
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

    /**
     * removes the map grid
     */
    private void gridOff(ActionEvent event) {
        grid.getChildren().clear(); // Completely removes all grid elements
    }
    
    /**
     * Handles the events for each combo box when something has been selected from one of them
     */
    public void handleStatsComboBoxSelection(ComboBox<String> pollutantDropDown, ComboBox<String> yearDropDown, ComboBox<String> statsdropdown2, ComboBox<String> additionalDropdown) {
        String pollutant = pollutantDropDown.getSelectionModel().getSelectedItem();
        String year = yearDropDown.getSelectionModel().getSelectedItem();
        String metric = statsdropdown2.getSelectionModel().getSelectedItem();
        String measure = additionalDropdown.getSelectionModel().getSelectedItem();
        if (pollutant != null && metric != null && year != null) {
            if (metric.equals("Highest")) {
                displayHighestPollutantLevels(new ArrayList<>(DataHandler.getHighestPollutantTrends(pollutant)));
            } else {
                List<Integer> years = List.of(2018, 2019, 2020, 2021, 2022, 2023);
                List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);
                pollutionGraph.loadData(years, pollutionLevels);
                System.out.println(measure);
                if ("period".equals(measure)){
                    displayAveragePollutantLevels(DataHandler.getAveragePollutantLevelByPeriod(pollutant, year), year, pollutant);
                }
            }
        }
    }
    
    /**
     * Creates labels for the average pollutant level and displays it
     */
    private void displayAveragePollutantLevels(double value, String year, String pollutant)
    {   
        if(statsDataDisplaying){
            removeNodes();
        }
        Label text = new Label("Average " + pollutant + " level for " + year + ": " + value);
        statsSideBar.getChildren().add(text);
        statsDataDisplaying = true;
        averageDisplaying = true;
    }
    
    // Event handler for combo boxes
    private void handleComboBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2, ComboBox<String> dropdown3) {
        String year = dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = dropdown2.getSelectionModel().getSelectedItem();
        String metric = dropdown3.getSelectionModel().getSelectedItem();

        if (year != null && pollutant != null) {
            filename = DataHandler.generateFilename(pollutant, year);
            // if (metric != null){
                // handleStatsComboBoxSelection(ComboBox<String> statsdropdown1, )
            // }
            displayData(filename);
        }
    }
    
    /**
     * Displays the markers on the map
     */
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

    /**
     * Created labels to display the data the ten highest pollutant levels
     */
    private void displayHighestPollutantLevels(ArrayList<DataPoint> data) {
        if(statsDataDisplaying){
            removeNodes();
        }
        for (DataPoint dataPoint : data) {
            Label text = new Label("Pollutant Level: " + dataPoint.value() + " Unique Grid Code: " + dataPoint.gridCode());
            statsSideBar.getChildren().add(text);
        };
        statsDataDisplaying = true;
        averageDisplaying = false;
    }
    
    /**
     * Removes nodes from the stats sidebar when changing between displaying different metrics of data
     * The amount of nodes needed to be removed are based on the nodes that previously displayed 
     */
    private void removeNodes() {
        int paneSize = statsSideBar.getChildren().size();
        int numToRemove = 0;
        if (averageDisplaying){
            numToRemove = 1;
        }
        else{
            numToRemove = 10;
        }
        if (paneSize > numToRemove) {
            for (int i = 0; i <= numToRemove; i++) {
                statsSideBar.getChildren().remove(paneSize - 1 - i);
            }
        }
    }

    /**
     * create a shared pollutant selection listener to make sure both pollutant combo boxes have same pollutant type
     */
    private void syncPollutantSelection(ComboBox<String> source, ComboBox<String> target) {
        source.setOnAction(event -> {
            String selectedPollutant = source.getSelectionModel().getSelectedItem();
            if (selectedPollutant != null && !selectedPollutant.equals(target.getSelectionModel().getSelectedItem())) {
                target.getSelectionModel().select(selectedPollutant);  // sync selection by choosing same option
            }
        });
    }

    //used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}
