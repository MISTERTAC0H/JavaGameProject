import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected Image currentFrame;

    public Entity(double x, double y, double width, double height, Image startingImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentFrame = startingImage;
    }

    public abstract void update(boolean isMovingRight, boolean isMovingLeft, boolean isMovingFront, boolean isMovingBack);

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }


    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }
}
