import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class GeneralUI extends Application {
    private Scene mainScene, statsScene;
  
    public void start(Stage primaryStage) {
        // creating the borderpane
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-background");

        // make the header, containing the map and stats buttons horizontally
        HBox header = new HBox();
        header.getStyleClass().add("header");

        Button mapButton = new Button("Map");
        Button statsButton = new Button("Stats");
        
        mapButton.getStyleClass().add("header-button");
        statsButton.getStyleClass().add("header-button");
        
        // list of UI features the header should have
        header.getChildren().addAll(mapButton, statsButton);
        root.setTop(header);

        // left sidebar with dropdown boxes
        VBox sideBar = new VBox(15);
        sideBar.getStyleClass().add("sidebar");
        sideBar.setPrefWidth(200);
        sideBar.setAlignment(Pos.TOP_CENTER);

        // first dropdown box, for choosing the year
        Label dropdown1Label = new Label("Year:");
        ComboBox<String> dropdown1 = new ComboBox<>();
        dropdown1.getItems().addAll("2001", "2000", "1999");

        // second dropdownbox, for choosing the pollutant
        Label dropdown2Label = new Label("Pollutant:");
        ComboBox<String> dropdown2 = new ComboBox<>();
        dropdown2.getItems().addAll("NO2", "PM10", "PM2.5");

        // add the dropdown boxes to the sidebar
        sideBar.getChildren().addAll(dropdown1Label, dropdown1, dropdown2Label, dropdown2);
        root.setLeft(sideBar);

        // center: generic placeholder content, add London later
        Label contentLabel = new Label("Main Content Area");
        contentLabel.getStyleClass().add("content-area");
        root.setCenter(contentLabel);

        // bottom: co-ordinates
        Label footer = new Label("Co-ordinates:");
        footer.getStyleClass().add("coordinates");
        root.setBottom(footer);

        // create the primary scene (the map view)
        mainScene = new Scene(root, 800, 500);
        
        //connect external css file
        mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        //create the secondary scene (the stats view)
        statsScene = createSecondaryScene("Stats", primaryStage);

        // Button actions for scene switching
        statsButton.setOnAction(e -> primaryStage.setScene(statsScene));

        // set up primary scene (main scene)
        primaryStage.setTitle("General UI");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // creates secondary scene for stats section
    private Scene createSecondaryScene(String title, Stage primaryStage) {
        // creating the borderpane
        BorderPane root = new BorderPane();
        root.getStyleClass().add("secondary-background");

        // make the header, containing the map and stats buttons horizontally
        HBox header = new HBox();
        header.getStyleClass().add("header");

        Button mapButton = new Button("Map");
        Button statsButton = new Button("Stats");

        mapButton.getStyleClass().add("header-button");
        statsButton.getStyleClass().add("header-button");

        // list of UI features the header should have
        header.getChildren().addAll(mapButton, statsButton);
        root.setTop(header);

        // left sidebar with blahblah
        VBox sideBar = new VBox(15);
        sideBar.getStyleClass().add("sidebar");
        sideBar.setPrefWidth(200);
        sideBar.setAlignment(Pos.TOP_CENTER);
        
        // bottom: co-ordinates
        Label footer = new Label("Co-ordinates:");
        footer.getStyleClass().add("coordinates");
        root.setBottom(footer);

        // create stats title for secondary page
        Label label = new Label(title);
        label.getStyleClass().add("content-area");

        // functionality of map button to go back to primary scene
        mapButton.setOnAction(e -> primaryStage.setScene(mainScene));

        // set center content
        root.setCenter(label);
        root.setLeft(sideBar);

        // create and return the new scene
        Scene scene = new Scene(root, 800, 500);
        
        // connect external css file
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        return scene;
    }
    
    //used to launch program
    public static void main(String[] args) {
        launch(args);
    }
}