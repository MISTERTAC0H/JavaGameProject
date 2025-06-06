import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player extends Entity {
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
    private int maxStamina = 100;
    private int stamina = maxStamina;
    private long lastDamageTime = 0; // used for ddamage
    private static final long DAMAGE_COOLDOWN = 1000; // 1 second
    // Add these with your other fields
    private double knockbackX = 0;
    private double knockbackY = 0;
    private boolean isKnockbackActive = false;
    private long knockbackEndTime = 0;
    private static final long KNOCKBACK_DURATION = 300; // milliseconds
    private static final double KNOCKBACK_FORCE = 8.0;
    private boolean isHit = false;
    private long hitStartTime = 0;
    private static final long HIT_FLASH_DURATION = 200; // milliseconds
    private Inventory inventory;

    public Player(Image idleRight, Image[] walkRightFrames,Image idleLeft, Image[] walkLeftFrames, Image idleFront,
                  Image[] walkFrontFrames, Image idleBack, Image[] walkBackFrames, double x, double y, Window window) {
        super(x, y, idleRight.getWidth() * 1.5, idleRight.getHeight() * 1.5, idleRight);
        this.idleRight = idleRight;
        this.walkRightFrames = walkRightFrames;
        this.idleLeft = idleLeft;
        this.walkLeftFrames = walkLeftFrames;
        this.idleFront = idleFront;
        this.walkFrontFrames = walkFrontFrames;
        this.idleBack = idleBack;
        this.walkBackFrames = walkBackFrames;
        this.currentFrame = idleRight;
        this.width = idleRight.getWidth() * 1.5;
        this.height = idleRight.getHeight() * 1.5;
        this.x = x;
        this.y = y;
        this.window = window;
        this.inventory = new Inventory();
    }
    public Inventory getInventory() {
        return inventory;
    }
    /*
        checks player x and y and compare it to walkable and nonwalkable tiles,
        if trying to move onto a nonwalkable tile, doesnt do anything, acts as a barrier
     */
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
    /*
        updates the playes sprite with its different sprite "positions" like front, back, left, and right
    */
    @Override
    public void update(boolean isMovingRight, boolean isMovingLeft, boolean isMovingFront, boolean isMovingBack) {
        applyKnockback(window.getCurrentTileMap()); // You'll need to add this method to Window
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
            // move back
        } else if (isMovingBack) {
            // Only animate if enough time has passed
            if (currentTime - lastFrameTime > FRAME_DELAY) {
                currentFrameIndex = (currentFrameIndex + 1) % walkBackFrames.length;
                currentFrame = walkBackFrames[currentFrameIndex];
                lastFrameTime = currentTime;
            }
            movingBack = true;
        } else if (movingBack) {
            // Just stopped moving back - return to idle
            currentFrame = idleBack;
            movingBack= false;
            currentFrameIndex = 0;
        }
    }
    // draws the current frame of the player
    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (gc != null && currentFrame != null) {
            // Flash effect when hit - add this at the start of the method
            if (isHit) {
                long timeSinceHit = System.currentTimeMillis() - hitStartTime;
                if (timeSinceHit < HIT_FLASH_DURATION) {
                    // Calculate fading flash intensity
                    double flashIntensity = 0.7 * (1 - (timeSinceHit / (double)HIT_FLASH_DURATION));
                    gc.setFill(Color.rgb(255, 0, 0, flashIntensity));
                    gc.fillRect(screenX, screenY, width, height);
                } else {
                    isHit = false; // Reset hit state when duration ends
                }
            }

            // Original drawing code - keep this
            gc.drawImage(currentFrame, screenX, screenY, width, height);
        }
    }
    // checks if the player is capable of taking damage, designated time must pass for damage to reoccur
    public boolean canTakeDamage() {
        return System.currentTimeMillis() - lastDamageTime > DAMAGE_COOLDOWN;
    }
    // does the damage

    public void takeDamage(int amount, double sourceX, double sourceY) {
        if (canTakeDamage()) {
            health = Math.max(0, health - amount);
            lastDamageTime = System.currentTimeMillis();

            // Calculate direction away from damage source
            double dx = this.x - sourceX;
            double dy = this.y - sourceY;
            double distance = Math.sqrt(dx*dx + dy*dy);

            // Normalize and apply knockback force
            if (distance > 0) {
                knockbackX = (dx/distance) * KNOCKBACK_FORCE;
                knockbackY = (dy/distance) * KNOCKBACK_FORCE;
            }

            isKnockbackActive = true;
            knockbackEndTime = System.currentTimeMillis() + KNOCKBACK_DURATION;

            // Visual feedback
            isHit = true;
            hitStartTime = System.currentTimeMillis();
        }
    }
    // applys the knockback to the player.
    private void applyKnockback(TileMap tileMap) {
        if (isKnockbackActive) {
            if (System.currentTimeMillis() > knockbackEndTime) {
                isKnockbackActive = false;
            } else {
                // Apply knockback regardless of player input
                tryMove(knockbackX, knockbackY, tileMap);

                // Reduce knockback over time for smoother effect
                knockbackX *= 0.9;
                knockbackY *= 0.9;
            }
        }
    }

    public int getStamina() { return stamina; }
    public int getMaxStamina() { return maxStamina; }
    public void setStamina(int stamina) { this.stamina = Math.max(0, Math.min(maxStamina, stamina)); }
    public void reduceStamina(int amount) { this.stamina = Math.max(0, stamina - amount); }
    public void restoreStamina(int amount) { this.stamina = Math.min(maxStamina, stamina + amount); }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }
}