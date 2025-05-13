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
    public int currentMapNumber = 1;

    @Override
    public void start(Stage primaryStage) {
        int tileSize = 60;
        System.out.println(currentMapNumber);
        tileMap = new TileMap(TileMap.mapChange(currentMapNumber), tileSize);
        tileMap.setSolidTiles(2, 3);
        // Load the player's sprite - FIXED LINE 29
        // In Window.java
        Image playerImage = loadImage("Cat.png");
        if (playerImage == null) {
            System.err.println("Failed to load player image!");
            return;
        }

        // Use the image's actual dimensions
        player = new Player(playerImage, tileSize * 5, tileSize * 5, this);

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

                int tileSize = tileMap.getTileSize();
                int centerCol = (int)((player.getX() + player.getWidth()/2) / tileSize);
                int centerRow = (int)((player.getY() + player.getHeight()/2) / tileSize);
                
                if (tileMap.getTileAtPosition(centerRow, centerCol) == 5) {
                    // Transition to next map
                    currentMapNumber++;
                    String newMapPath = TileMap.mapChange(currentMapNumber);
                    if (!newMapPath.equals(tileMap.getCurrentMapPath())) {
                        // Save player position if needed
                        double oldX = player.getX();
                        double oldY = player.getY();
                        
                        // Create new tilemap
                        tileMap = new TileMap(newMapPath, tileSize);
                        tileMap.setSolidTiles(2, 3);
                        
                        // Reset player position or set to entrance position
                        player.setX(tileSize * 2); // Example entrance position
                        player.setY(tileSize * 2);
                        
                        // Reset camera
                        cameraX = player.getX() - canvas.getWidth() / 2;
                        cameraY = player.getY() - canvas.getHeight() / 2;
                    }
                }

                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                tileMap.draw(gc, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
                player.draw(gc, player.getX() - cameraX, player.getY() - cameraY);
            }
        }.start();

        primaryStage.setTitle("Dungeon Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void changeMap(int newMapNumber) {
    String newMapPath = TileMap.mapChange(newMapNumber);
    if (!newMapPath.equals(tileMap.getCurrentMapPath())) {
        this.currentMapNumber = newMapNumber;

        int tileSize = tileMap.getTileSize();

        tileMap = new TileMap(newMapPath, tileSize);
        tileMap.setSolidTiles(2, 3);

        player.setX(tileSize * 2);
        player.setY(tileSize * 2);

        // Reset camera
        cameraX = player.getX() - 400; // canvas.getWidth() / 2
        cameraY = player.getY() - 300; // canvas.getHeight() / 2

        System.out.println("Changed to map #" + newMapNumber);
    }
}


    public void setCurrentMapNumber(int newNumber) {
        this.currentMapNumber = newNumber;
        System.out.println("Map changed to: " + newNumber);
    }

    public int getCurrentMapNumber() {
        return currentMapNumber;
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