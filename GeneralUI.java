import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;

public class GeneralUI extends Application {
    private GridPane grid = new GridPane();
    private StackPane stack = new StackPane();
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

        // create a sidebar for dropdowns in the map tab, containing the UI vertically
        VBox mapSideBar = new VBox(15);
        mapSideBar.getStyleClass().add("sidebar");
        mapSideBar.setPrefWidth(200);
        mapSideBar.setAlignment(Pos.TOP_CENTER);
        
        // first dropdown box, for choosing the year
        Label dropdown1Label = new Label("Year:");
        ComboBox<String> dropdown1 = new ComboBox<>();
        dropdown1.getItems().addAll("2023", "2022", "2021", "2020", "2019", "2018");
        
        // second dropdown box, for choosing the pollutant
        Label dropdown2Label = new Label("Pollutant:");
        ComboBox<String> dropdown2 = new ComboBox<>();
        dropdown2.getItems().addAll("NO2", "PM10", "PM2.5");


        //first button to turn on grid for map
        Button mapGridOn = new Button("map grid on ");
        Button mapGridOff = new Button("map grid off ");
        
        
        // add the dropdown boxes to the sidebar, containing the UI vertically 
        mapSideBar.getChildren().addAll(dropdown1Label, dropdown1, dropdown2Label, dropdown2, mapGridOn, mapGridOff);
        
        //Listeners for the comboboxes
        dropdown1.setOnAction(event -> handleComboBoxSelection(dropdown1, dropdown2));
        dropdown2.setOnAction(event -> handleComboBoxSelection(dropdown1, dropdown2));

        
        // add the dropdown boxes to the sidebar 
        //Listeners for the comboboxes
        dropdown1.setOnAction(event -> handleComboBoxSelection(dropdown1, dropdown2));
        dropdown2.setOnAction(event -> handleComboBoxSelection(dropdown1, dropdown2));

        
        // add the dropdown boxes to the sidebar 
        mapLayout.setLeft(mapSideBar);

        // load and display the map image
        Image londonImage = new Image(getClass().getResource("London.png").toExternalForm());
        ImageView londonImageView = new ImageView(londonImage);

        // ensure the image scales properly
        londonImageView.setPreserveRatio(true);
        londonImageView.setFitWidth(1310); // only one dimension has to change
        
        // create a stack pane to fit grid map onto image 
        StackPane stack = new StackPane();
        stack.getChildren().add(londonImageView);
        stack.getChildren().add(grid);
        mapGridOn.setOnAction(this::gridOn);
        mapGridOff.setOnAction(this::gridOff);

        // place the image in the center of the map layout
        mapLayout.setCenter(stack);

        // bottom area: display coordinates
        Label mapFooter = new Label("Co-ordinates:");
        mapFooter.getStyleClass().add("coordinates");
        mapLayout.setBottom(mapFooter);

        // assign the completed layout to the map tab
        mapTab.setContent(mapLayout);
        
        
        // STATS TAB CREATION
        BorderPane statsLayout = new BorderPane();
        statsLayout.getStyleClass().add("secondary-background");

        // create an temporarily empty sidebar for the stats tab
        VBox statsSideBar = new VBox();
        statsSideBar.getStyleClass().add("sidebar");
        statsSideBar.setPrefWidth(200);
        statsLayout.setLeft(statsSideBar);

        // bottom area: display coordinates
        Label statsFooter = new Label("Co-ordinates:");
        statsFooter.getStyleClass().add("coordinates");
        statsLayout.setBottom(statsFooter);

        // create and style the placeholder 
        Label statsLabel = new Label("Stats View");
        statsLabel.getStyleClass().add("content-area");

        // place the stats label in the center
        statsLayout.setCenter(statsLabel);

        // assign the completed layout to the stats tab
        statsTab.setContent(statsLayout);
        
        

        // FINALISING SCENE
        tabPane.getTabs().addAll(mapTab, statsTab);

        // create the primary scene within the tab pane
        Scene mainScene = new Scene(tabPane, 1600, 1400);
        
        //connect external css file
        mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // enable automatic fullscreen and set the title of the application
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Pollution Application");
        
        // display primaryStage
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    

    //displays map grid
    private void gridOn(ActionEvent Event){
        grid.setHgap(0);
        grid.setVgap(0);
        
        for(int col = 0; col<25; col++){
            for(int row = 0; row<17;row++){
                Rectangle cell = new Rectangle(50,50);
                cell.setStroke(Color.GREY);
                cell.setFill(Color.TRANSPARENT);
                grid.add(cell, col, row);
            }
        }
    }
    // removes map grid
    private void gridOff(ActionEvent Event){

        for(int col = 0; col<25; col++){
            for(int row = 0; row<17;row++){
                grid.getChildren().remove(col, row);
                System.out.println(col + "," + row);
            }

        }
    }
 

    // Event handler for combo boxes
    public void handleComboBoxSelection(ComboBox<String> dropdown1, ComboBox<String> dropdown2) {
        String year = dropdown1.getSelectionModel().getSelectedItem();
        String pollutant = dropdown2.getSelectionModel().getSelectedItem();
        if (year != null && pollutant != null) {
            String filename = "";
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
            }
            if (!filename.equals("")){
                new LocationHandler(filename);
            }
        }
    }
    
    //used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}