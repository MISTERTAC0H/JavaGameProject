import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.*;

public class TileMap {
    private int[][] tiles;
    private int tileSize;
    private int rows, cols;
    private Image[] tileImages;

    public TileMap(String path, int tileSize) {
        this.tileSize = tileSize;
        loadTileMap(path);
        loadTileImages();
        setSolidTiles(2, 3); // Mark tiles 2 and 3 as solid
    }

    private void loadTileMap(String path) {
        try (Scanner scanner = new Scanner(getClass().getResourceAsStream(path))) {
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
        tileImages = new Image[6];
        // walk on 
        tileImages[0] = new Image(getClass().getResourceAsStream("Grass.png")); // Grass
        tileImages[1] = new Image(getClass().getResourceAsStream("GrassWithGrass.png")); // Grass With Grass
        // cant walk on
        tileImages[2] = new Image(getClass().getResourceAsStream("CobbleStone.png")); // Cobblestone
        tileImages[3] = new Image(getClass().getResourceAsStream("Tree.png")); // Tree
        tileImages[4] = new Image(getClass().getResourceAsStream("DungeonEnterance.png")); // Dungeon Enterance
        tileImages[5] = new Image(getClass().getResourceAsStream("RockFloor.png")); // Dungeon Enterance
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

    public boolean isSolid(int row, int col) {
    if (row < 0 || row >= rows || col < 0 || col >= cols) {
        return true;
    }
    return solidTiles.contains(tiles[row][col]);
}

    private final Set<Integer> solidTiles = new HashSet<>();

    public void setSolidTiles(Integer... tileIds) {
        solidTiles.addAll(Arrays.asList(tileIds));
    }

    public int getWidth() {
        return cols * tileSize;
    }

    public int getHeight() {
        return rows * tileSize;
    }

    public int getTileSize() {
        return tileSize;
    }
}