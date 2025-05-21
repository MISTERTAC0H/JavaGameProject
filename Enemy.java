import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Enemy extends Entity {
    private Image frontImage;
    private Image rightImage;
    private Image leftImage;
    private Image backImage;
    private TileMap tileMap;
    private double speed = 1.5;
    private int detectionRange = 10;
    private double directionX = 0;
    private double directionY = 0;
    private Player player;

    public Enemy(double x, double y, double width, double height, TileMap tileMap,
                 Player player,  // This is now properly used
                 String frontImagePath, String rightImagePath,
                 String leftImagePath, String backImagePath) {
        super(x, y, width, height, new Image(frontImagePath));
        this.tileMap = tileMap;
        this.player = player;

        this.frontImage = new Image(frontImagePath);
        this.rightImage = new Image(rightImagePath);
        this.leftImage = new Image(leftImagePath);
        this.backImage = new Image(backImagePath);
    }

    public void tryMove(double dx, double dy) {
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
    }

    @Override
    public void update(boolean isMovingRight, boolean isMovingLeft,
                       boolean isMovingFront, boolean isMovingBack) {
        if (player == null) return; // Safety check

        // Calculate distance to player
        double distanceX = player.getX() - x;
        double distanceY = player.getY() - y;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        // Only chase if player is within range
        if (distance <= detectionRange * tileMap.getTileSize()) {
            // Normalize direction
            directionX = distanceX / distance;
            directionY = distanceY / distance;

            // Set appropriate sprite
            if (Math.abs(directionX) > Math.abs(directionY)) {
                currentFrame = directionX > 0 ? rightImage : leftImage;
            } else {
                currentFrame = directionY > 0 ? frontImage : backImage;
            }

            // Move toward player
            tryMove(directionX * speed, directionY * speed);
        }
    }

    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (gc != null && currentFrame != null) {
            gc.drawImage(currentFrame, screenX, screenY, width / 1.5, height);
        }
    }

    // Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public int getDetectionRange() { return detectionRange; }
    public void setDetectionRange(int range) { this.detectionRange = range; }
}