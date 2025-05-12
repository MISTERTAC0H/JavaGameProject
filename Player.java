import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player {
    private Image sprite;
    private double x, y;

    public Player(Image sprite, double x, double y) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    // Adjust the player's position based on camera position
    public void draw(GraphicsContext gc, double cameraX, double cameraY) {
        // Adjust drawing coordinates to account for the camera offset
        double adjustedX = x - cameraX;
        double adjustedY = y - cameraY;

        gc.drawImage(sprite, adjustedX, adjustedY);
    }

    // Getter methods to retrieve the player's position
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
