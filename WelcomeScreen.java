import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class WelcomeScreen extends Application {
    public void start(Stage primaryStage) {
        // create a layout for the welcome page, holding the UI vertically in the center
        VBox welcomeLayout = new VBox(20);
        welcomeLayout.getStyleClass().add("main-background");
        welcomeLayout.setAlignment(Pos.CENTER);

        // create title text
        Text title = new Text("Welcome to Pollution Solution");
        title.getStyleClass().add("welcome-title");

        // create subtitle text
        Text subtitle = new Text("By Ayesha, Khem, Irfan and Ridwan");
        subtitle.getStyleClass().add("welcome-subtitle");

        // create description text
        Text description = new Text("Our Solution – Empowering Communities with Data-Driven Insights to Track and Analyze Pollution Trends,\n Understand Environmental Impact, and Make Informed Decisions for a Smarter, Healthier Future!");
        description.getStyleClass().add("welcome-text");

        // create continue button to switch scene when pressed
        Button continueButton = new Button("Continue");
        continueButton.getStyleClass().add("welcome-button");
        
        Button instructionsButton = new Button("Instructions");
        instructionsButton.getStyleClass().add("welcome-button");
        
        
        // when clicked, switch to the main page (GeneralUI)
        continueButton.setOnAction(e -> {
            GeneralUI mainApp = new GeneralUI(); //instantiate the general UI class
            mainApp.start(primaryStage); // switch scene to main page   
        });
        
        // open instructions popup when clicked
        instructionsButton.setOnAction(e -> showInstructions());

        // switch to main UI
        // add all elements to layout vertically
        welcomeLayout.getChildren().addAll(title, subtitle, description, continueButton, instructionsButton);

        // create scene for welcome screen
        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // set up and display the stage
        primaryStage.setTitle("Welcome - Pollution Solution");
        primaryStage.setScene(welcomeScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    // method to show instructions alert popup
    private void showInstructions() {
        Alert alert = new Alert(AlertType.INFORMATION); // information type alert
        alert.setTitle("How to Use Pollution Solution"); // title of popup
        alert.setHeaderText(null); //no header to popup
        
        // instructions on how to use program
        alert.setContentText(
                "1. Select Data:\n" +
                "   • Choose a pollutant (NO2, PM10, PM2.5) and year (2018-2023).\n" +
                "2. Explore the Map:\n" +
                "   • Hover over points to view pollution data.\n" +
                "   • Click a point to set it as the active location.\n" +
                "3. Filter Data:\n" +
                "   • Use checkboxes to show/hide pollution levels, then click 'Apply Filter'.\n" +
                "4. Toggle Options:\n" +
                "   • Map Grid: Adds a reference grid.\n" +
                "   • Heat Map: Converts points into a heatmap.\n" +
                "5. View Statistics:\n" +
                "   • The Stats Tab provides insights into pollution trends over time.\n" +
                "   • Select a pollutant and year to generate a line graph.\n" +
                "   • 'Highest' – Shows top 10 pollution levels.\n" +
                "   • 'Average' – Allows 'By Area' (point’s avg) or 'By Period' (yearly avg).\n" +
                "6. Interact with the Graph:\n" +
                "   • Hover over or click data points to view exact values."
        );

        alert.showAndWait(); // wait for user to close alert 
    }
    
    // used to launch the program
    public static void main(String[] args) {
        launch(args);
    }
}
