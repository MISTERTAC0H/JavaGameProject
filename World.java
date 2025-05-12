import javafx.scene.canvas.GraphicsContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class World {
    private int[][] tiles;
    private int tileSize;
    private int rows, cols;

    public World(String path, int tileSize) {
        this.tileSize = tileSize;
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
        }
    }

    public int getWidth() {
        return cols * tileSize;
    }

    public int getHeight() {
        return rows * tileSize;
    }

    public void draw(GraphicsContext gc, double cameraX, double cameraY, double canvasWidth, double canvasHeight) {
        // You'd loop through visible tiles and draw them here
        // e.g., gc.drawImage(tileImages[tiles[row][col]], ...)
    }
}
