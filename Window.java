import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Window extends Application {

    private double mousePressedX;
    private double mousePressedY;
    private double offsetX = 0;
    private double offsetY = 0;
    private double sensitivity = 0.1;  // Lower value = less sensitivity

    @Override
    public void start(Stage primaryStage) {
        // Create root pane
        Pane root = new Pane();

        // Create a blue square
        int squareSize = 100;
        Rectangle blueSquare = new Rectangle(squareSize, squareSize, Color.BLUE);

        // Center the square initially
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            blueSquare.setX((newVal.doubleValue() - squareSize) / 2);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            blueSquare.setY((newVal.doubleValue() - squareSize) / 2);
        });

        root.getChildren().add(blueSquare);

        // Mouse pressed event to record initial position
        root.setOnMousePressed(event -> {
            mousePressedX = event.getSceneX();
            mousePressedY = event.getSceneY();
        });

        // Mouse dragged event to move the scene view
        root.setOnMouseDragged(event -> {
            // Calculate the offset between the initial and current mouse position
            offsetX += (event.getSceneX() - mousePressedX) * sensitivity;
            offsetY += (event.getSceneY() - mousePressedY) * sensitivity;

            // Update the root pane's position to simulate the "view" movement
            root.setLayoutX(root.getLayoutX() + offsetX);
            root.setLayoutY(root.getLayoutY() + offsetY);

            // Update the initial mouse position for the next drag
            mousePressedX = event.getSceneX();
            mousePressedY = event.getSceneY();
        });

        // Create the scene and stage
        Scene scene = new Scene(root, 1000, 1000); // Initial size
        primaryStage.setTitle("First Person View");
        primaryStage.setScene(scene);

        // Center the window on the screen
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
