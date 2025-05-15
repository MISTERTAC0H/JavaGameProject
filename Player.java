import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.*;

public class Player {
    private Image idleRight;  // guy_right.png
    private Image idleLeft;  // guy_left.png
    private Image idleFront; // guy_front.png
    private Image idleBack; // guy_back.png
    private Image[] walkRightFrames;  // [guy_right_walk_1.png, guy_right_walk_2.png]
    private Image[] walkLeftFrames;  // [guy_left_walk_1.png, guy_left_walk_2.png]
    private Image[] walkFrontFrames;  // [guy_front_walk_1.png, guy_front_walk_2.png]
    private Image[] walkBackFrames;  // [guy_back_walk_1.png, guy_back_walk_2.png]
    private Image currentFrame;
    private double x, y;
    private double width, height;
    private Window window;
    private boolean movingRight = false;
    private boolean movingLeft = false;
    private boolean movingFront = false;
    private boolean movingBack = false;
    private int currentFrameIndex = 0;
    private long lastFrameTime = 0;
    private static final long FRAME_DELAY = 200; // milliseconds between frames

    public Player(Image idleRight, Image[] walkRightFrames,Image idleLeft, Image[] walkLeftFrames, Image idleFront, Image[] walkFrontFrames, Image idleBack, Image[] walkBackFrames, double x, double y, Window window) {
        this.idleRight = idleRight;
        this.walkRightFrames = walkRightFrames;
        this.idleLeft = idleLeft;
        this.walkLeftFrames = walkLeftFrames;
        this.idleFront = idleFront;
        this.walkFrontFrames = walkFrontFrames;
        this.idleBack = idleFront;
        this.walkBackFrames = walkFrontFrames;
        this.currentFrame = idleRight;
        this.width = idleRight.getWidth() * 1.5;
        this.height = idleRight.getHeight() * 1.5;
        this.x = x;
        this.y = y;
        this.window = window;
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
            System.out.println("tile 4 — map change!");
            //window.fadeBlack();
            //window.changeMap(window.currentMapNumber = 2);
            window.transitionMap(2);
        // turn map DungeonA1
        } else if (tileUnderPlayer == 6) {
            System.out.println("tile 6 — map change!");
            //window.fadeBlack();
            //window.changeMap(window.currentMapNumber = 1);
            window.transitionMap(1);
        }

    }

    public void update(boolean isMovingRight, boolean isMovingLeft, boolean isMovingFront, boolean isMovingBack) {
        long currentTime = System.currentTimeMillis();
        // move right
        if (isMovingRight) {
            // Only animate if enough time has passed
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrameIndex = (currentFrameIndex + 1) % walkRightFrames.length;
                currentFrame = walkRightFrames[currentFrameIndex];
                lastFrameTime = currentTime;
            }
            movingRight = true;
        } else if (movingRight) {
            // Just stopped moving right - return to idle
            currentFrame = idleRight;
            movingRight = false;
            currentFrameIndex = 0;
            // move left
        } else if (isMovingLeft) {
            // Only animate if enough time has passed
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrameIndex = (currentFrameIndex + 1) % walkLeftFrames.length;
                currentFrame = walkLeftFrames[currentFrameIndex];
                lastFrameTime = currentTime;
            }
            movingLeft = true;
        } else if (movingLeft) {
            // Just stopped moving right - return to idle
            currentFrame = idleLeft;
            movingLeft = false;
            currentFrameIndex = 0;
            // move front
        } else if (isMovingFront) {
            // Only animate if enough time has passed
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrameIndex = (currentFrameIndex + 1) % walkFrontFrames.length;
                currentFrame = walkFrontFrames[currentFrameIndex];
                lastFrameTime = currentTime;
            }
            movingFront = true;
        } else if (movingFront) {
            // Just stopped moving right - return to idle
            currentFrame = idleFront;
            movingFront = false;
            currentFrameIndex = 0;
        } else if (isMovingBack) {
            // Only animate if enough time has passed
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrameIndex = (currentFrameIndex + 1) % walkBackFrames.length;
                currentFrame = walkFrontFrames[currentFrameIndex];
                lastFrameTime = currentTime;
            }
            movingBack = true;
        } else if (movingBack) {
            // Just stopped moving right - return to idle
            currentFrame = idleBack;
            movingBack= false;
            currentFrameIndex = 0;
        }
    }

    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (gc != null && currentFrame != null) {
            gc.drawImage(currentFrame, screenX, screenY, width, height);
        }
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
