package game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;

public class TileMap {
    private int[][] tiles;
    private int tileSize;
    private int rows, cols;
    private Image[] tileImages;
    private String currentMapPath;
    private Window window;

    public TileMap(Window window) {
        this.window = window;
    }

    public TileMap(String path, int tileSize) {
        this.tileSize = tileSize;
        this.currentMapPath = path;
        loadTileMap(path);
        loadTileImages();
        setSolidTiles(2, 3, 8);
        //mapChange(4);
    }

    public static String mapChange(int mapNumber) {
        // Define a base path if needed (adjust according to your project structure)
        //String basePath = "maps/"; // Example: "maps/" or "" if files are in root
        if (mapNumber < 0) {
            mapNumber = 1;
        }
        switch (mapNumber) {
            case 1:
                return "maps/world.txt";
            case 2:
                return "maps/DungeonA1.txt";
            case 3:
                return "maps/StartHouse.txt";
            default:
                // Return a default map or handle invalid input
                // return basePath + "world.txt";
                return "maps/world.txt";
        }
    }


    public String getCurrentMapPath() { return currentMapPath; }

    private void loadTileMap(String path) {
        try (Scanner scanner = new Scanner(Assets.open(path))) {
            List<int[]> tempRows = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] tokens = line.split(" ");
                    int[] row = new int[tokens.length];
                    for (int i = 0; i < tokens.length; i++) {
                        row[i] = Integer.parseInt(tokens[i]);
                    }
                    tempRows.add(row);
                }
            }
            rows = tempRows.size();
            cols = tempRows.get(0).length;
            tiles = new int[rows][cols];
            for (int r = 0; r < rows; r++) {
                tiles[r] = tempRows.get(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to a small empty map if loading fails
            rows = 20;
            cols = 20;
            tiles = new int[rows][cols];
        }
    }

    private void loadTileImages() {
        tileImages = new Image[9];
        // walk on
        tileImages[0] = Assets.image("resources/Grass2.png"); // Grass
        tileImages[1] = Assets.image("resources/GrassWithGrass1.png"); // Grass With Grass
        tileImages[7] = Assets.image("resources/GrassWithGrass2.png"); // Grass With Grass
        tileImages[5] = Assets.image("resources/RockFloor.png"); // Rock floor
        tileImages[4] = Assets.image("resources/DungeonEnterance.png"); // Dungeon Enterance
        tileImages[5] = Assets.image("resources/RockFloor.png"); // Rock floor
        tileImages[6] = Assets.image("resources/DungeonExit.png"); // Dungeon Enterance
        // cant walk on
        tileImages[2] = Assets.image("resources/CobbleStone.png"); // Cobblestone
        tileImages[3] = Assets.image("resources/Tree.png"); // Tree
        tileImages[8] = Assets.image("resources/Black.png"); // Tree
    }

    public void draw(GraphicsContext gc, double cameraX, double cameraY, double canvasWidth, double canvasHeight) {
        int startCol = (int)(cameraX / tileSize);
        int startRow = (int)(cameraY / tileSize);
        int endCol = Math.min(cols, (int)((cameraX + canvasWidth) / tileSize) + 1);
        int endRow = Math.min(rows, (int)((cameraY + canvasHeight) / tileSize) + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                int tile = tiles[row][col];
                if (tile >= 0 && tile < tileImages.length && tileImages[tile] != null) {
                    double drawX = col * tileSize - cameraX;
                    double drawY = row * tileSize - cameraY;
                    gc.drawImage(tileImages[tile], drawX, drawY, tileSize, tileSize);
                }
            }
        }
    }

    public int getTileAtPosition(double worldX, double worldY) {
        int tileSize = getTileSize();
        int col = (int)(worldX / tileSize);
        int row = (int)(worldY / tileSize);

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return -1; // Out of bounds
        }
        return tiles[row][col];
    }


    public boolean isSolid(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true;
        }
        return solidTiles.contains(tiles[row][col]);
    }

    // Returns the world position {x, y} of the top-left corner of a random walkable tile that is at least
    // minDistance away from (avoidX, avoidY), or null if the map has no walkable tiles.
    // Map-transition tiles (dungeon entrance/exit) are never picked.
    public double[] getRandomOpenTile(double avoidX, double avoidY, double minDistance) {
        List<int[]> open = new ArrayList<>();
        List<int[]> farEnough = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int tile = tiles[row][col];
                if (solidTiles.contains(tile) || tile == 4 || tile == 6) continue;
                open.add(new int[]{row, col});
                double dx = col * tileSize - avoidX;
                double dy = row * tileSize - avoidY;
                if (Math.sqrt(dx * dx + dy * dy) >= minDistance) {
                    farEnough.add(new int[]{row, col});
                }
            }
        }
        // Prefer tiles away from the point, but fall back to any open tile on small maps
        List<int[]> choices = farEnough.isEmpty() ? open : farEnough;
        if (choices.isEmpty()) return null;
        int[] pick = choices.get(RANDOM.nextInt(choices.size()));
        return new double[]{pick[1] * tileSize, pick[0] * tileSize};
    }
    private static final Random RANDOM = new Random();

    private final Set<Integer> solidTiles = new HashSet<>();
    public void setSolidTiles(Integer... tileIds) { solidTiles.addAll(Arrays.asList(tileIds)); }
    public int getWidth() { return cols * tileSize; }
    public int getHeight() { return rows * tileSize; }
    public int getTileSize() { return tileSize; }
}