import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Window extends Application {
    private TileMap tileMap;
    private Player player;
    private double cameraX = 0;
    private double cameraY = 0;
    private double playerSpeed = 1;
    private boolean[] keyPressed = new boolean[4]; // W, A, S, D

    @Override
    public void start(Stage primaryStage) {
        int tileSize = 60;
        String tileMapPath = "/world.txt"; // Note the leading slash for resources

        // Create the tile map
        tileMap = new TileMap(tileMapPath, tileSize);
        tileMap.setSolidTiles(2, 3);
        // Load the player's sprite - FIXED LINE 29
        // In Window.java
        Image playerImage = loadImage("/Cat.png");
        if (playerImage == null) {
            System.err.println("Failed to load player image!");
            return;
        }

        // Use the image's actual dimensions
        player = new Player(playerImage, tileSize * 5, tileSize * 5);

        // Rest of your code remains the same...
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 800, 600);

        // Key event handlers
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W) keyPressed[0] = true;
            if (event.getCode() == KeyCode.A) keyPressed[1] = true;
            if (event.getCode() == KeyCode.S) keyPressed[2] = true;
            if (event.getCode() == KeyCode.D) keyPressed[3] = true;
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.W) keyPressed[0] = false;
            if (event.getCode() == KeyCode.A) keyPressed[1] = false;
            if (event.getCode() == KeyCode.S) keyPressed[2] = false;
            if (event.getCode() == KeyCode.D) keyPressed[3] = false;
        });

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dx = 0, dy = 0;
                if (keyPressed[0]) dy = -playerSpeed;
                if (keyPressed[1]) dx = -playerSpeed;
                if (keyPressed[2]) dy = playerSpeed;
                if (keyPressed[3]) dx = playerSpeed;
                
                if (dx != 0 || dy != 0) {
                    player.tryMove(dx, dy, tileMap);
                }

                cameraX = player.getX() - canvas.getWidth() / 2;
                cameraY = player.getY() - canvas.getHeight() / 2;
                
                cameraX = Math.max(0, Math.min(cameraX, tileMap.getWidth() - canvas.getWidth()));
                cameraY = Math.max(0, Math.min(cameraY, tileMap.getHeight() - canvas.getHeight()));

                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                tileMap.draw(gc, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
                player.draw(gc, player.getX() - cameraX, player.getY() - cameraY);
            }
        }.start();

        primaryStage.setTitle("Cat Adventure");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Helper method to load images with error handling
    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}