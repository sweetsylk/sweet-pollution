import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;

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
        Text description = new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                                  + "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        description.getStyleClass().add("welcome-text");

        // create continue button to switch scene when pressed
        Button continueButton = new Button("Continue");
        continueButton.getStyleClass().add("continue-button");

        // when clicked, switch to the main page (GeneralUI)
        continueButton.setOnAction(e -> {
            GeneralUI mainApp = new GeneralUI(); //instantiate the class
            mainApp.start(primaryStage); // switch scene to main page   
        });

        // add all elements to layout vertically
        welcomeLayout.getChildren().addAll(title, subtitle, description, continueButton);

        // create scene for welcome screen
        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // set up and display the stage
        primaryStage.setTitle("Pollution Solution - Welcome");
        primaryStage.setScene(welcomeScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    //launch the program
    public static void main(String[] args) {
        launch(args);
    }
}
