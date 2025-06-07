import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.*;

public class Window extends Application {
    private TileMap tileMap;
    private Player player;
    private double cameraX = 0;
    private double cameraY = 0;
    private double playerSpeed = 2;
    private boolean[] keyPressed = new boolean[4]; // W, A, S, D
    public int currentMapNumber = 1;
    private Canvas canvas;
    private static final double DEFAULT_WIDTH = 1500;
    private static final double DEFAULT_HEIGHT = 1000;
    private double fadeOpacity = 0.0;
    private boolean isFading = false;
    private boolean isUnfading = false;
    private static final double FADE_SPEED = 0.005; // Adjust speed as needed
    private boolean fadeComplete = false;
    // Add these with your other variables in Window class
    private MainMenu mainMenu;
    private PauseMenu pauseMenu;
    private OptionsMenu optionsMenu;
    private HUD hud;
    private NPC npc;
    private ArrayList<NPC> npcs = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) {
        mainMenu = new MainMenu();
        pauseMenu = new PauseMenu();
        optionsMenu = new OptionsMenu(primaryStage);
        optionsMenu.toggleFullscreen();
        int tileSize = 60;
        System.out.println(currentMapNumber);
        tileMap = new TileMap(TileMap.mapChange(currentMapNumber), tileSize);
        tileMap.setSolidTiles(2, 3, 8);

        // Player animations
        Image idleRight = loadImage("resources/guy_right.png");
        Image walkRight1 = loadImage("resources/guy_right_walk_1.png");
        Image walkRight2 = loadImage("resources/guy_right_walk_2.png");
        Image idleLeft = loadImage("resources/guy_left.png");
        Image walkLeft1 = loadImage("resources/guy_left_walk_1.png");
        Image walkLeft2 = loadImage("resources/guy_left_walk_2.png");
        Image idleFront = loadImage("resources/guy_front.png");
        Image walkFront1 = loadImage("resources/guy_front_walk_1.png");
        Image walkFront2 = loadImage("resources/guy_front_walk_2.png");
        Image idleBack = loadImage("resources/guy_back.png");
        Image walkBack1 = loadImage("resources/guy_back_walk_1.png");
        Image walkBack2 = loadImage("resources/guy_back_walk_2.png");

        Image[] walkRightFrames = {walkRight1, walkRight2};
        Image[] walkLeftFrames = {walkLeft1, walkLeft2};
        Image[] walkFrontFrames = {walkFront1, walkFront2};
        Image[] walkBackFrames = {walkBack1, walkBack2};
        // image error
        if (idleRight == null || walkRight1 == null || walkRight2 == null || idleLeft == null || walkLeft1 == null || walkLeft2 == null ||  idleFront == null || walkFront1 == null || walkFront2 == null || idleBack == null || walkBack1 == null || walkBack2 == null) {
            System.err.println("Failed to load player image!");
            return;
        }
        // initialize player
        player = new Player(idleRight, walkRightFrames, idleLeft, walkLeftFrames, idleFront, walkFrontFrames, idleBack, walkBackFrames, tileSize * 5, tileSize * 5, this);
        hud = new HUD(player);
        initializeNPCs();
        initializeEnemies();

        canvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Pane root = new Pane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // Make the canvas resize with the window
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        // Handle window resize and update camera bounds
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            cameraX = Math.max(0, Math.min(cameraX, tileMap.getWidth() - newVal.doubleValue()));
        });

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            cameraY = Math.max(0, Math.min(cameraY, tileMap.getHeight() - newVal.doubleValue()));
        });

        // Handles button inputs
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE ) {
                if (player.getInventory().isVisible()) {
                    // Close inventory if it's open
                    player.getInventory().setVisible(false);
                    event.consume(); // Prevent other ESCAPE handlers from triggering
                } else if (!mainMenu.isActive() && !optionsMenu.isActive()) {
                    pauseMenu.togglePause();
                } else if (optionsMenu.isActive()) {
                    event.consume();    // Ensure that screen doesn't flash red when in options menu, also allows for escape as a back button
                    optionsMenu.setActive(false, null);
                    return;
                }
            }
            if (!mainMenu.isActive() && !pauseMenu.isPaused()) { // Only process movement in game when not paused
                // Walking buttons
                if (event.getCode() == KeyCode.W) keyPressed[0] = true;
                if (event.getCode() == KeyCode.A) keyPressed[1] = true;
                if (event.getCode() == KeyCode.S) keyPressed[2] = true;
                if (event.getCode() == KeyCode.D) keyPressed[3] = true;
                // Inventory/Hotbar buttons
                if (event.getCode() == KeyCode.E) player.getInventory().setVisible(!player.getInventory().isVisible());
                if (event.getCode() == KeyCode.DIGIT1) player.getInventory().selectHotbarSlot(0);
                if (event.getCode() == KeyCode.DIGIT2) player.getInventory().selectHotbarSlot(1);
                if (event.getCode() == KeyCode.DIGIT3) player.getInventory().selectHotbarSlot(2);
                if (event.getCode() == KeyCode.DIGIT4) player.getInventory().selectHotbarSlot(3);
                if (event.getCode() == KeyCode.DIGIT5) player.getInventory().selectHotbarSlot(4);
                if (event.getCode() == KeyCode.DIGIT6) player.getInventory().selectHotbarSlot(5);
                if (event.getCode() == KeyCode.DIGIT7) player.getInventory().selectHotbarSlot(6);
                if (event.getCode() == KeyCode.DIGIT8) player.getInventory().selectHotbarSlot(7);
                if (event.getCode() == KeyCode.DIGIT9) player.getInventory().selectHotbarSlot(8);
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.W) keyPressed[0] = false;
            if (event.getCode() == KeyCode.A) keyPressed[1] = false;
            if (event.getCode() == KeyCode.S) keyPressed[2] = false;
            if (event.getCode() == KeyCode.D) keyPressed[3] = false;
        });
        // scrool wheel for inventory
        scene.setOnScroll(event -> {
            if (!mainMenu.isActive() && !pauseMenu.isPaused()) {
                double delta = event.getDeltaY(); // Positive for scroll up, negative for scroll down
                if (delta > 0) {
                    // Scroll up - move hotbar selection right
                    player.getInventory().scrollHotbar(1);
                } else if (delta < 0) {
                    // Scroll down - move hotbar selection left
                    player.getInventory().scrollHotbar(-1);
                }
            }
        });

        // Mouse movement handler
        scene.setOnMouseMoved(event -> {
            if (mainMenu.isActive() && !optionsMenu.isActive()) {
                mainMenu.updateHoverStates(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight());
            } else if (pauseMenu.isPaused() && !optionsMenu.isActive()) {
                pauseMenu.updateButtonPositions(canvas.getWidth(), canvas.getHeight());
                pauseMenu.updateHoverStates(event.getX(), event.getY());
            } else if (optionsMenu.isActive()) {
                optionsMenu.updateHoverStates(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight());
            }
        });

        // Mouse click handler
        scene.setOnMouseClicked(event -> {
            if (optionsMenu.isActive()) {
                if (optionsMenu.isFullscreenClicked(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight())) {
                    optionsMenu.toggleFullscreen();
                } else if (optionsMenu.isBackClicked(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight())) {
                    optionsMenu.setActive(false, null);
                    // Return to the appropriate menu based on where options was opened from
                    if (optionsMenu.getSource() == OptionsMenu.MenuSource.PAUSE_MENU) {
                        pauseMenu.setPaused(true);
                    }
                }
            } else if (mainMenu.isActive()) {
                if (mainMenu.isStartClicked(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight())) {
                    mainMenu.setActive(false);
                    currentMapNumber = 1;
                    tileMap = new TileMap(TileMap.mapChange(currentMapNumber), tileMap.getTileSize());
                    tileMap.setSolidTiles(2, 3);
                    player.setX(tileMap.getTileSize() * 5);
                    player.setY(tileMap.getTileSize() * 5);
                } else if (mainMenu.isOptionsClicked(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight())) {
                    optionsMenu.setActive(true, OptionsMenu.MenuSource.MAIN_MENU);
                } else if (mainMenu.isExitClicked(event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight())) {
                    primaryStage.close();
                }
            } else if (pauseMenu.isPaused()) {
                if (pauseMenu.isResumeClicked(event.getX(), event.getY())) {
                    pauseMenu.setPaused(false);
                } else if (pauseMenu.isOptionsClicked(event.getX(), event.getY())) {
                    optionsMenu.setActive(true, OptionsMenu.MenuSource.PAUSE_MENU);
                } else if (pauseMenu.isExitClicked(event.getX(), event.getY())) {
                    pauseMenu.setPaused(false);
                    mainMenu.setActive(true);
                    if (tileMap != null) {
                        player.setX(tileMap.getTileSize() * 5);
                        player.setY(tileMap.getTileSize() * 5);
                    }
                }
            }
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

                // Update camera position based on player
                cameraX = player.getX() - canvas.getWidth() / 2;
                cameraY = player.getY() - canvas.getHeight() / 2;

                // Ensure camera stays within map bounds
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
                        // Create new tilemap
                        tileMap = new TileMap(newMapPath, tileSize);
                        tileMap.setSolidTiles(2, 3);

                        // Reset player position
                        player.setX(tileSize * 2);
                        player.setY(tileSize * 2);

                        // Reset camera
                        cameraX = player.getX() - canvas.getWidth() / 2;
                        cameraY = player.getY() - canvas.getHeight() / 2;
                    }
                }

                // Update player and NPCs, enemy's in future
                player.update(keyPressed[3], keyPressed[1], keyPressed[2], keyPressed[0]);
                // npc only matters when the map is 1
                if (currentMapNumber == 1) {
                    for (NPC npc : npcs) {
                        npc.update(false, false, false, false);
                    }
                }
                // spawn enemies
                for (Enemy enemy : enemies) {
                    enemy.update(false, false, false, false);  // These parameters aren't used by Enemy
                    enemy.draw(gc, cameraX, cameraY);
                    enemy.checkPlayerCollision(player);
                }

                // Clear and redraw everything
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                // main menu
                if (mainMenu.isActive()) {
                    mainMenu.draw(gc, canvas.getWidth(), canvas.getHeight());
                    if (optionsMenu.isActive()) {
                        optionsMenu.draw(gc, canvas.getWidth(), canvas.getHeight(), mainMenu.isActive(), pauseMenu.isPaused());
                    }
                } else {
                    tileMap.draw(gc, cameraX, cameraY, canvas.getWidth(), canvas.getHeight());
                    player.draw(gc, player.getX() - cameraX, player.getY() - cameraY);
                    for (Enemy enemy : enemies) {
                        enemy.draw(gc, enemy.getX() - cameraX, enemy.getY() - cameraY);
                    }
                    if (currentMapNumber == 1) {
                        for (NPC npc : npcs) {
                            npc.draw(gc, npc.getX() - cameraX, npc.getY() - cameraY);
                        }
                    }
                    if (pauseMenu.isPaused() && !optionsMenu.isActive()) {
                        pauseMenu.draw(gc);
                    }
                    if (optionsMenu.isActive()) {
                        optionsMenu.draw(gc, canvas.getWidth(), canvas.getHeight(), mainMenu.isActive(), pauseMenu.isPaused());
                    }
                    hud.draw(gc);
                    player.getInventory().draw(gc);
                }

                if (isFading || isUnfading) {
                    gc.setGlobalAlpha(fadeOpacity);
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    gc.setGlobalAlpha(1.0);

                    // Update fade opacity
                    if (isFading) {
                        fadeOpacity += FADE_SPEED;
                        if (fadeOpacity >= 1.0) {
                            fadeOpacity = 1.0;
                            isFading = false;
                            fadeComplete = true;
                        }
                    } else if (isUnfading) {
                        fadeOpacity -= FADE_SPEED;
                        if (fadeOpacity <= 0.0) {
                            fadeOpacity = 0.0;
                            isUnfading = false;
                            fadeComplete = true;
                        }
                    }
                }
            }
        }.start();

        primaryStage.setTitle("Dungeon Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeNPCs() {
        int tileSize = tileMap.getTileSize();

        // Add NPCs to the list
        npcs.add(new NPC(
                tileSize * 10, tileSize * 10, tileSize, tileSize, tileMap,
                "resources/npc_girl_1_front.png",
                "resources/npc_girl_1_right.png",
                "resources/npc_girl_1_left.png",
                "resources/npc_girl_1_back.png"
        ));
        npcs.add(new NPC(
                tileSize * 10, tileSize * 10, tileSize, tileSize, tileMap,
                "resources/npc_girl_1_front.png",
                "resources/npc_girl_1_right.png",
                "resources/npc_girl_1_left.png",
                "resources/npc_girl_1_back.png"
        ));
    }
    private void initializeEnemies() {
        int tileSize = tileMap.getTileSize();

        enemies.add(new Enemy(
                tileSize * 10, tileSize * 10, tileSize, tileSize, tileMap, this.player,
                "resources/skeleton_right.png",
                "resources/skeleton_right.png",
                "resources/skeleton_right.png",
                "resources/skeleton_right.png"
        ));
    }
    public void transitionMap(int newMapNumber) {
        // Only start transition if not already fading
        if (!isFading && !isUnfading) {
            fadeBlack();

            new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (isFadeComplete()) {
                        // When fade to black is complete, change map
                        changeMap(newMapNumber);
                        // Start unfading
                        unfadeBlack();
                        this.stop(); // Stop this timer
                    }
                }
            }.start();
        }
    }

    public void changeMap(int newMapNumber) {
        String newMapPath = TileMap.mapChange(newMapNumber);
        if (!newMapPath.equals(tileMap.getCurrentMapPath())) {
            this.currentMapNumber = newMapNumber;
            int tileSize = tileMap.getTileSize();

            // Create new tilemap
            TileMap newTileMap = new TileMap(newMapPath, tileSize);
            newTileMap.setSolidTiles(2, 3);

            // Update player position
            player.setX(tileSize * 2);
            player.setY(tileSize * 2);

            // Update all enemies with the new tilemap
            for (Enemy enemy : enemies) {
                enemy.setX(tileSize * 10);
                enemy.setY(tileSize * 10);
                enemy.setTileMap(newTileMap);
            }

            // Reset NPCs if needed
            if (currentMapNumber == 1) {
                for (NPC npc : npcs) {
                    npc.setX(tileSize * 10);
                    npc.setY(tileSize * 10);
                }
            }

            // Update the tilemap reference
            this.tileMap = newTileMap;

            // Reset camera
            cameraX = player.getX() - canvas.getWidth() / 2;
            cameraY = player.getY() - canvas.getHeight() / 2;
        }
    }
    // returns the current tile map
    public TileMap getCurrentTileMap() { return this.tileMap; }
    public void fadeBlack() {
        fadeOpacity = 0.0;
        isFading = true;
        isUnfading = false;
        fadeComplete = false;
    }

    public void unfadeBlack() {
        fadeOpacity = 1.0;
        isUnfading = true;
        isFading = false;
        fadeComplete = false;
    }

    public boolean isFadeComplete() {
        // Only return true once when the fade completes
        if (fadeComplete) {
            fadeComplete = false;  // Reset the flag
            return true;
        }
        return false;
    }

    public void setCurrentMapNumber(int newNumber) {
        this.currentMapNumber = newNumber;
        System.out.println("Map changed to: " + newNumber);
    }

    public int getCurrentMapNumber() {
        return currentMapNumber;
    }

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