import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class NPC extends Entity {
    private Image frontImage;
    private Image rightImage;
    private Image leftImage;
    private Image backImage;
    private TileMap tileMap;
    private double speed = 0.5;

    // Simple AI states
    private boolean isMoving = false;
    private double moveTimer = 0;
    private double directionX = 0;
    private double directionY = 0;

    public NPC(double x, double y, double width, double height, TileMap tileMap,
               String frontImagePath, String rightImagePath,
               String leftImagePath, String backImagePath) {
        super(x, y, width, height, new Image(frontImagePath));
        this.tileMap = tileMap;

        // Load all direction sprites
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
    public void update(boolean isMovingRight, boolean isMovingLeft, boolean isMovingFront, boolean isMovingBack) {
        // Simple AI: Move randomly
        moveTimer--;

        if (moveTimer <= 0) {
            // Change movement direction randomly
            isMoving = Math.random() > 0.3; // 70% chance to move
            if (isMoving) {
                directionX = Math.random() * 2 - 1; // -1 to 1
                directionY = Math.random() * 2 - 1; // -1 to 1

                // Normalize diagonal movement
                double length = Math.sqrt(directionX * directionX + directionY * directionY);
                if (length > 0) {
                    directionX /= length;
                    directionY /= length;
                }

                // Set appropriate sprite based on direction
                if (Math.abs(directionX) > Math.abs(directionY)) {
                    currentFrame = directionX > 0 ? rightImage : leftImage;
                } else {
                    currentFrame = directionY > 0 ? frontImage : backImage;
                }
            }

            moveTimer = 60 + Math.random() * 60; // 1-2 seconds (assuming 60 FPS)
        }

        if (isMoving) {
            tryMove(directionX * speed, directionY * speed);
        }
    }

    public void interact() {
        System.out.println("NPC says: Hello there!");
        isMoving = false; // Stop moving when interacted with
        moveTimer = 120; // Don't move for a while after interaction
    }

    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (gc != null && currentFrame != null) {
            gc.drawImage(currentFrame, screenX, screenY, width / 1.5, height);
        }
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getX() { return x; }
    public double getY() { return y; }

    // Set both positions at once
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}