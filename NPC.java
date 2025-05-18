import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class NPC extends Entity {
    private Image frontImage;
    private Image rightImage;
    private Image leftImage;
    private Image backImage;

    // Simple AI states
    private boolean isMoving = false;
    private double moveTimer = 0;
    private double directionX = 0;
    private double directionY = 0;
    private double speed = 1.0;

    public NPC(double x, double y, double width, double height) {
        super(x, y, width, height, new Image("resources/npc_girl_1_front.png"));

        // Load all direction sprites
        this.frontImage = new Image("resources/npc_girl_1_front.png");
        this.rightImage = new Image("resources/npc_girl_1_right.png");
        this.leftImage = new Image("resources/npc_girl_1_left.png");
        this.backImage = new Image("resources/npc_girl_1_back.png");

        // Start with front image
        this.currentFrame = frontImage;
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
            move(directionX * speed, directionY * speed);
        }
    }

    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (gc != null && currentFrame != null) {
            gc.drawImage(currentFrame, screenX, screenY, width / 1.5, height);
        }
    }

    // Additional NPC-specific methods can be added here
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void interact() {
        // Method to handle player interaction with the NPC
        System.out.println("NPC says: Hello there!");
        isMoving = false; // Stop moving when interacted with
        moveTimer = 120; // Don't move for a while after interaction
    }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}