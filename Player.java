import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {
    private Image sprite;
    private double x, y;
    private double width, height;

    public Player(Image sprite, double x, double y) {
        if (sprite == null) {
            throw new IllegalArgumentException("Sprite image cannot be null");
        }
        this.sprite = sprite;
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.x = x;
        this.y = y;
        
        System.out.println("Player created with dimensions: " + width + "x" + height);
    }

    public void tryMove(double dx, double dy, TileMap tileMap) {
        if (tileMap == null) return;
        
        double newX = x + dx;
        double newY = y + dy;
        
        int tileSize = tileMap.getTileSize();
        int leftTile = (int) (newX / tileSize);
        int rightTile = (int) ((newX + width - 1) / tileSize);
        int topTile = (int) (newY / tileSize);
        int bottomTile = (int) ((newY + height - 1) / tileSize);
        
        // Collision checks
        if (dx > 0) { // Moving right
            if (tileMap.isSolid(topTile, rightTile) || tileMap.isSolid(bottomTile, rightTile)) {
                newX = (rightTile) * tileSize - width;
            }
        } else if (dx < 0) { // Moving left
            if (tileMap.isSolid(topTile, leftTile) || tileMap.isSolid(bottomTile, leftTile)) {
                newX = (leftTile + 1) * tileSize;
            }
        }
        
        if (dy > 0) { // Moving down
            if (tileMap.isSolid(bottomTile, leftTile) || tileMap.isSolid(bottomTile, rightTile)) {
                newY = (bottomTile) * tileSize - height;
            }
        } else if (dy < 0) { // Moving up
            if (tileMap.isSolid(topTile, leftTile) || tileMap.isSolid(topTile, rightTile)) {
                newY = (topTile + 1) * tileSize;
            }
        }
        
        x = newX;
        y = newY;
    }

    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (gc == null || sprite == null) return;
        gc.drawImage(sprite, screenX, screenY, width, height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}