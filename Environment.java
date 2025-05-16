import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/*


FILE NOT CURRENTLY IN USE FOR THE ACTUAL GAME


 */

public class Environment {
    private double width;
    private double height;
    private Image tile;

    public Environment(double width, double height, Image tile) {
        this.width = width;
        this.height = height;
        this.tile = tile;
    }

    public void draw(GraphicsContext gc, double offsetX, double offsetY, double viewWidth, double viewHeight) {
        int tileWidth = (int) tile.getWidth();
        int tileHeight = (int) tile.getHeight();

        int startX = (int) (offsetX / tileWidth);
        int startY = (int) (offsetY / tileHeight);
        int endX = (int) ((offsetX + viewWidth) / tileWidth) + 1;
        int endY = (int) ((offsetY + viewHeight) / tileHeight) + 1;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                double drawX = x * tileWidth - offsetX;
                double drawY = y * tileHeight - offsetY;
                gc.drawImage(tile, drawX, drawY);
            }
        }
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
