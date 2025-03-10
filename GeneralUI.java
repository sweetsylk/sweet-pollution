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
    private boolean heatMapOn = false;
    private GridPane grid = new GridPane();
    private BorderPane stack = new BorderPane();
    private Graphs pollutionGraph = new Graphs();
    private VBox statsSideBar = new VBox(12);
    
    // load and display the map image
    public void start(Stage primaryStage) {
        // INITIALISATION
        
        //Welcome Page not added to the start scene
        Label projectTitleLabel = new Label("Pollution Solution \nby Ayesha, Irfan, Ridwan and Khem");
        projectTitleLabel.setId("projectTitleLabel");
        Label instructionsLabel = new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. \nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. \nDuis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. \nExcepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        instructionsLabel.setId("instructionsLabel");
        Button continueBtn = new Button("Continue");
        continueBtn.setId("continueBtn");
        StackPane topWelcomePane = new StackPane(projectTitleLabel);
        StackPane centerWelcomePane = new StackPane(instructionsLabel);
        StackPane bottomWelcomePane = new StackPane(continueBtn);
        BorderPane WelcomePane = new BorderPane(centerWelcomePane, topWelcomePane, null, bottomWelcomePane, null);
        WelcomePane.getStyleClass().add("main-background");
        
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

        // create a sidebar for dropdowns in the map tab, containing the UI vertically
        
        VBox mapSideBar = new VBox(12);
        mapSideBar.getStyleClass().add("sidebar");
        mapSideBar.setPrefWidth(150);
        mapSideBar.setAlignment(Pos.TOP_CENTER);
        mapSideBar.prefHeightProperty().bind(stack.heightProperty());
        mapSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));

    

        // first dropdown box, for choosing the year
        Label dropdown1Label = new Label("Year:");
        ComboBox<String> dropdown1 = new ComboBox<>();
        dropdown1.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");
        
        // second dropdown box, for choosing the pollutant
        Label dropdown2Label = new Label("Pollutant:");
        ComboBox<String> dropdown2 = new ComboBox<>();
        dropdown2.getItems().addAll("NO2", "PM10", "PM2.5");

        //buttons to turn the grid on and off for the map
        ToggleButton mapGridOn = new ToggleButton("map grid on ");
        ToggleButton mapGridOff = new ToggleButton("map grid off ");
        
        // create a toggle group of toggle buttons
        ToggleGroup gridToggleGroup = new ToggleGroup();
        
        mapGridOn.setToggleGroup(gridToggleGroup);
        mapGridOff.setToggleGroup(gridToggleGroup);

        ToggleButton heatMap = new ToggleButton("Heat Map");

        dropdown1.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        dropdown2.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        mapGridOn.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        mapGridOff.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));
        heatMap.prefWidthProperty().bind(mapSideBar.widthProperty().multiply(0.9));


        
        // add the dropdown boxes to the sidebar, containing the UI vertically 
        mapSideBar.getChildren().addAll(dropdown1Label, dropdown1, dropdown2Label, dropdown2, mapGridOn, mapGridOff, heatMap);
        mapLayout.setLeft(mapSideBar);

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

        // use a stack pane to fit grid map onto image
        stack.getChildren().add(londonImageView);
        stack.getChildren().add(grid);
        mapGridOn.setOnAction(this::gridOn);
        mapGridOff.setOnAction(this::gridOff);
        heatMap.setOnAction(this::heatMapToggle);

        // place the image in the center of the map layout
        mapLayout.setCenter(stack);

        // assign the completed layout to the map tab
        mapTab.setContent(mapLayout);

        // STATS TAB CREATION
        BorderPane statsLayout = new BorderPane();
        statsLayout.getStyleClass().add("main-background");

        // create an temporarily empty sidebar for the stats tab
        statsSideBar.getStyleClass().add("sidebar");
        statsSideBar.setPrefWidth(200);
        
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
        dropdown1.setOnAction(_ -> handleComboBoxSelection(dropdown1, dropdown2, statsdropdown2));
        dropdown2.setOnAction(_ -> handleComboBoxSelection(dropdown1, dropdown2, statsdropdown2));
        
        // listener for the metric dropdown of "Average" 
        statsdropdown2.setOnAction(_ -> {
            String selectedMetric = statsdropdown2.getSelectionModel().getSelectedItem();
            if ("Average".equals(selectedMetric)) {
                additionalDropdown.setVisible(true); // show when "Average" is selected
                additionalDropdownLabel.setVisible(true);
            } else {
                handleMetricBoxSelection(dropdown1, dropdown2, statsdropdown2);
                additionalDropdown.setVisible(false); // hide when "Average" is not selected
                additionalDropdownLabel.setVisible(false);
            }
        });

        Button averagePollutionButton = new Button("Average");
        Button highestPollutionButton = new Button("Highest");
        Button trendsOverTimeButton = new Button("Trends over Time");

        statsdropdown1.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        statsdropdown2.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        averagePollutionButton.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        highestPollutionButton.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));
        trendsOverTimeButton.prefWidthProperty().bind(statsSideBar.widthProperty().multiply(0.9));


        statsSideBar.setPrefWidth(150);
        statsSideBar.setAlignment(Pos.TOP_CENTER);
        statsSideBar.prefHeightProperty().bind(stack.heightProperty());
        statsSideBar.prefWidthProperty().bind(tabPane.widthProperty().multiply(0.15));
        
        statsSideBar.getChildren().addAll(statsdropdown1Label, statsdropdown1, statsdropdown2Label, statsdropdown2, additionalDropdownLabel, additionalDropdown);
        statsLayout.setLeft(statsSideBar);

        //Listeners for the comboboxes
        statsdropdown1.setOnAction(_ -> handleStatsComboBoxSelection(statsdropdown1));

        // bottom area: display coordinates
        Label statsFooter = new Label("Co-ordinates:");
        statsFooter.getStyleClass().add("coordinates");
        statsLayout.setBottom(statsFooter);

        // create and style the placeholder
        Label statsLabel = new Label("Stats View");
        statsLabel.getStyleClass().add("content-area");

        // place the stats chart in the center
        statsLayout.setCenter(pollutionGraph.getGraph());


        // assign the completed layout to the stats tab
        statsTab.setContent(statsLayout);

        // FINALISING SCENE
        tabPane.getTabs().addAll(mapTab, statsTab);

        // create the primary scene within the tab pane
        Scene mainScene = new Scene(tabPane, 1600, 1400);
        //tabPane
        //connect external css file
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm()); // this is to prevent nullpointer exceptions when accessing css files

        // set the title of the application
        primaryStage.setTitle("Pollution Solution");
        
        // display primaryStage
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(true);
        primaryStage.show();
        
        // Enable fullscreen AFTER switching scenes
        primaryStage.setFullScreen(true);
        primaryStage.show(); // Show the new window
    }

    private void heatMapToggle(ActionEvent actionEvent) {
        heatMapOn = !heatMapOn;
        displayData();
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

    public boolean heatMapIsOn()
    {
        return heatMapOn;
    }
    
    // removes map grid
    private void gridOff(ActionEvent event) {
    grid.getChildren().clear(); // Completely removes all grid elements
    }

    public void handleMetricBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2, ComboBox<String> dropdown3){
        String year = dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = dropdown2.getSelectionModel().getSelectedItem();
        String metric = dropdown3.getSelectionModel().getSelectedItem();
        if (year != null && pollutant != null && metric != null){
            if (metric.equals("Highest")){
                displayHighestPollutantLevels(DataHandler.getHighestPollutantLevel(year, pollutant));
            }
        }
    }
    // Event handler for combo boxes
    public void handleComboBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2, ComboBox<String> dropdown3) {

        String year = dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = dropdown2.getSelectionModel().getSelectedItem();

        if (year != null && pollutant != null) {
            switch (pollutant) {
                case "NO2":
                    filename = String.format("UKAirPollutionData/%s/mapno2%s.csv", pollutant, year);
                    break;
                case "PM2.5":
                    filename = String.format("UKAirPollutionData/%s/mappm25%sg.csv", pollutant, year);
                    break;
                case "PM10":
                    filename = String.format("UKAirPollutionData/%s/mappm10%sg.csv", pollutant, year);
                    break;
                default:
                    System.out.println("Unknown file loaded");
                    return;
            }
         // refresh heatmap and markers
            displayData();
            handleMetricBoxSelection(dropdown1, dropdown2, dropdown3);
        }
    }

    public void handleStatsComboBoxSelection(ComboBox<String> statsdropdown1) {
        String pollutant = statsdropdown1.getSelectionModel().getSelectedItem();

        if (pollutant != null) {
            // Get trend data
            List<Integer> years = List.of(2018, 2019, 2020, 2021, 2022, 2023);
            List<Double> pollutionLevels = DataHandler.getPollutantTrends(pollutant);

            pollutionGraph.loadData(years, pollutionLevels);
        }
    }


    public void displayData() {
        if (!filename.isEmpty()) {
            DataLoader loader = new DataLoader();
            DataSet dataSet = loader.loadDataFile(filename);

            //System.out.println("Dataset Loaded with " + dataSet.getData().size() + " data points.");

            List<Node> markers = HeatmapAndMarkerGenerator.loadData(heatMapIsOn(), dataSet);

            Platform.runLater(() -> {
                stack.getChildren().removeIf(node -> node instanceof Shape);
                stack.getChildren().addAll(markers);
            });

        }
    }

    public void displayHighestPollutantLevels(ArrayList<DataPoint> data){
        for (DataPoint dataPoints : data){
            Label text = new Label("Pollutant Level: " + dataPoints.value() + "x = " + dataPoints.x() + "y = " + dataPoints.y() + "UGC: " + dataPoints.gridCode());
            statsSideBar.getChildren().add(text);
        }
    }

    //used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}