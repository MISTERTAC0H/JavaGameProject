import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class Window extends Application {

    private boolean isFullScreen = true;
    private Player player;
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private Environment environment;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        // Resizable canvas
        Canvas canvas = new Canvas();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Load sprites
        Image tile = new Image("Grass.png"); // Your tile sprite
        Image sprite = new Image("Cat.png"); // Your player sprite
        environment = new Environment(2000, 2000, tile); // Environment with 2000x2000 size
        player = new Player(sprite, 100, 100);

        // Track which keys are pressed
        scene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode());
            if (event.getCode() == KeyCode.ESCAPE) {
                isFullScreen = !isFullScreen;
                primaryStage.setFullScreen(isFullScreen);
            }
        });

        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode()));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc, canvas);
            }
        };

        timer.start();

        primaryStage.setTitle("JavaFX Player Movement");
        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);

        // Resize listeners to adjust canvas size and redrawing
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            draw(gc, canvas); // Redraw to fill new size
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            draw(gc, canvas); // Redraw to fill new size
        });

        primaryStage.show();
    }

    /*
     * Update player position based on key press
     */
    private void update() {
        // Left
        if (keysPressed.contains(KeyCode.A)) {
            player.move(-2, 0);
        }
        // Right
        if (keysPressed.contains(KeyCode.D)) {
            player.move(2, 0);
        }
        // Up
        if (keysPressed.contains(KeyCode.W)) {
            player.move(0, -2);
        }
        // Down
        if (keysPressed.contains(KeyCode.S)) {
            player.move(0, 2);
        }
    }

    /*
     * Draw method with the camera logic
     */
    private void draw(GraphicsContext gc, Canvas canvas) {
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        // Calculate camera position based on player position (center player on screen)
        double cameraX = player.getX() - canvasWidth / 2;
        double cameraY = player.getY() - canvasHeight / 2;

        // Clamp camera position to prevent scrolling out of bounds
        cameraX = Math.max(0, Math.min(cameraX, environment.getWidth() - canvasWidth));
        cameraY = Math.max(0, Math.min(cameraY, environment.getHeight() - canvasHeight));

        // Clear screen before drawing new frame
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Draw the environment (background) tiles
        environment.draw(gc, cameraX, cameraY, canvasWidth, canvasHeight);

        // Draw the player sprite
        player.draw(gc, cameraX, cameraY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
