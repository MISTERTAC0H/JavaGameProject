import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class Window extends Application {

    private boolean isFullScreen = true;
    private final Set<KeyCode> activeKeys = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        // size of square
        int squareSize = 50;
        Rectangle blueSquare = new Rectangle(squareSize, squareSize, Color.BLUE);
        blueSquare.setX((1000 - squareSize) / 2.0);
        blueSquare.setY((1000 - squareSize) / 2.0);

        root.getChildren().add(blueSquare);

        //size of windowed screen
        Scene scene = new Scene(root, 1000, 1000);

        // Handle key press and release
        scene.setOnKeyPressed(event -> {
            activeKeys.add(event.getCode());
            if (event.getCode() == KeyCode.ESCAPE) {
                isFullScreen = !isFullScreen;
            }
        });

        scene.setOnKeyReleased(event -> activeKeys.remove(event.getCode()));

        // Use AnimationTimer for smooth movement
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // sets the squares speed
                double speed = 2;
                if (activeKeys.contains(KeyCode.W)) {
                    blueSquare.setY(blueSquare.getY() - speed);
                }
                if (activeKeys.contains(KeyCode.S)) {
                    blueSquare.setY(blueSquare.getY() + speed);
                }
                if (activeKeys.contains(KeyCode.A)) {
                    blueSquare.setX(blueSquare.getX() - speed);
                }
                if (activeKeys.contains(KeyCode.D)) {
                    blueSquare.setX(blueSquare.getX() + speed);
                }

                if (primaryStage.isFullScreen() != isFullScreen) {
                    primaryStage.setFullScreen(isFullScreen);
                }
            }
        };
        timer.start();

        primaryStage.setScene(scene);
        primaryStage.setTitle("WASD Square Movement");
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}