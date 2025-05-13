import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {
    private Image sprite;
    private double x, y;
    private double width, height;
    private Window window;

    public Player(Image sprite, double x, double y, Window window) {
    if (sprite == null || window == null) {
        throw new IllegalArgumentException("Sprite image and Window cannot be null");
    }
    this.sprite = sprite;
    this.width = sprite.getWidth();
    this.height = sprite.getHeight();
    this.x = x;
    this.y = y;
    this.window = window;

    System.out.println("Player created with dimensions: " + width + "x" + height);
}


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setPosition(double x, double y, TileMap tileMap) {
        if (tileMap != null) {
            double maxX = tileMap.getWidth() - this.width;
            double maxY = tileMap.getHeight() - this.height;
            this.x = Math.max(0, Math.min(x, maxX));
            this.y = Math.max(0, Math.min(y, maxY));
        } else {
            setX(x);
            setY(y);
        }
    }

    public void tryMove(double dx, double dy, TileMap tileMap) {
        if (tileMap == null) return;

        int tileSize = tileMap.getTileSize();
        boolean canMoveX = true;
        boolean canMoveY = true;

        // Check X movement
        if (dx != 0) {
            double newX = x + dx;
            int leftTile = (int) (newX / tileSize);
            int rightTile = (int) ((newX + width - 1) / tileSize);
            int topEdge = (int) (y / tileSize);
            int bottomEdge = (int) ((y + height - 1) / tileSize);

            if (dx > 0) {
                for (int row = topEdge; row <= bottomEdge; row++) {
                    if (tileMap.isSolid(row, rightTile)) {
                        canMoveX = false;
                        break;
                    }
                }
            } else {
                for (int row = topEdge; row <= bottomEdge; row++) {
                    if (tileMap.isSolid(row, leftTile)) {
                        canMoveX = false;
                        break;
                    }
                }
            }
        }

        // Check Y movement
        if (dy != 0) {
            double newY = y + dy;
            int topTile = (int) (newY / tileSize);
            int bottomTile = (int) ((newY + height - 1) / tileSize);
            int leftEdge = (int) (x / tileSize);
            int rightEdge = (int) ((x + width - 1) / tileSize);

            if (dy > 0) {
                for (int col = leftEdge; col <= rightEdge; col++) {
                    if (tileMap.isSolid(bottomTile, col)) {
                        canMoveY = false;
                        break;
                    }
                }
            } else {
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

        // Check if standing on special tile
        int tileUnderPlayer = tileMap.getTileAtPosition(x + width / 2, y + height / 2);
        // turn map normal world
        if (tileUnderPlayer == 4) {
            System.out.println("Player touched tile 4 — triggering map change!");
            window.changeMap(window.currentMapNumber = 2);
        // turn map DungeonA1
        } else if (tileUnderPlayer == 6) {
            System.out.println("Player touched tile 6 — triggering map change!");
            window.changeMap(window.currentMapNumber = 1);
        }

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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
