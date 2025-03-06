import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class GeneralUI extends Application {
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
        
        // add the dropdown boxes to the sidebar 
        mapSideBar.getChildren().addAll(dropdown1Label, dropdown1, dropdown2Label, dropdown2);
        mapLayout.setLeft(mapSideBar);

        // load and display the map image
        Image londonImage = new Image(getClass().getResource("London.png").toExternalForm());
        ImageView londonImageView = new ImageView(londonImage);

        // ensure the image scales properly
        londonImageView.setPreserveRatio(true);
        londonImageView.setFitWidth(1310); // only one dimension has to change

        // place the image in the center of the map layout
        mapLayout.setCenter(londonImageView);

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
    
    //used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}