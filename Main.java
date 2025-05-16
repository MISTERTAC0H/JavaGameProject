import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/*


FILE NOT CURRENTLY IN USE FOR THE ACTUAL GAME


 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create buttons with images
        Button startButton = createImageButton("start.png", "start_hover.png");
        Button exitButton = createImageButton("exit.png", "exit_hover.png");

        // Set button actions
        startButton.setOnAction(e -> System.out.println("Start button clicked!"));
        exitButton.setOnAction(e -> primaryStage.close());

        // Create layout and add buttons
        VBox root = new VBox(20, startButton, exitButton);
        root.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-background-color: #f0f0f0;");

        // Set up the scene
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("JavaFX Image Buttons");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createImageButton(String normalImage, String hoverImage) {
        // Load images from resources folder
        Image normalImg = new Image(getClass().getResourceAsStream(normalImage));
        Image hoverImg = new Image(getClass().getResourceAsStream(hoverImage));

        // Create button with normal image
        Button button = new Button();
        button.setGraphic(new ImageView(normalImg));
        button.setStyle("-fx-background-color: transparent;");

        // Set hover effects
        button.setOnMouseEntered(e -> button.setGraphic(new ImageView(hoverImg)));
        button.setOnMouseExited(e -> button.setGraphic(new ImageView(normalImg)));

        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}