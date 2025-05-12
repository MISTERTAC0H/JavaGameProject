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
    
    int tileSize = tileMap.getTileSize();
    boolean canMoveX = true;
    boolean canMoveY = true;
    
    // Check X movement (horizontal)
    if (dx != 0) {
        double newX = x + dx;
        int leftTile = (int) (newX / tileSize);
        int rightTile = (int) ((newX + width - 1) / tileSize);
        
        // Check all vertical tiles along the movement edge
        int topEdge = (int) (y / tileSize);
        int bottomEdge = (int) ((y + height - 1) / tileSize);
        
        if (dx > 0) { // Moving right
            for (int row = topEdge; row <= bottomEdge; row++) {
                if (tileMap.isSolid(row, rightTile)) {
                    canMoveX = false;
                    break;
                }
            }
        } else { // Moving left
            for (int row = topEdge; row <= bottomEdge; row++) {
                if (tileMap.isSolid(row, leftTile)) {
                    canMoveX = false;
                    break;
                }
            }
        }
    }
    
    // Check Y movement (vertical)
    if (dy != 0) {
        double newY = y + dy;
        int topTile = (int) (newY / tileSize);
        int bottomTile = (int) ((newY + height - 1) / tileSize);
        
        // Check all horizontal tiles along the movement edge
        int leftEdge = (int) (x / tileSize);
        int rightEdge = (int) ((x + width - 1) / tileSize);
        
        if (dy > 0) { // Moving down
            for (int col = leftEdge; col <= rightEdge; col++) {
                if (tileMap.isSolid(bottomTile, col)) {
                    canMoveY = false;
                    break;
                }
            }
        } else { // Moving up
            for (int col = leftEdge; col <= rightEdge; col++) {
                if (tileMap.isSolid(topTile, col)) {
                    canMoveY = false;
                    break;
                }
            }
        }
    }
    
    // Apply movement
    if (canMoveX) x += dx;
    if (canMoveY) y += dy;
    
    // Optional: Handle sliding along walls
    if (!canMoveX && canMoveY) y += dy; // Slide vertically
    if (canMoveX && !canMoveY) x += dx; // Slide horizontally
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