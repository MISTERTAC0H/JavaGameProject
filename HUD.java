import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class HUD {
    private Player player;
    private double x, y;
    private double width, height;
    private double padding;

    public HUD(Player player) {
        this(player, 10, 10, 200, 25, 5);
    }

    public HUD(Player player, double x, double y, double width, double height, double padding) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.padding = padding;
    }

    public void draw(GraphicsContext gc) {
        if (player == null) return;  // Only draw when in game

        double healthPercentage = player.getHealth() / (double)player.getMaxHealth();
        healthPercentage = Math.max(0, Math.min(1, healthPercentage));

        // Draw health bar background (dark gray)
        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(x, y, width, height);

        // Draw current health (red)
        gc.setFill(Color.RED);  // Use a darker red to be less flashy
        gc.fillRect(x, y, width * healthPercentage, height);

        // Draw subtle border
        gc.setStroke(Color.rgb(30, 30, 30));
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);
    }

    private Color getHealthColor(double percentage) {
        if (percentage > 0.6) return Color.LIMEGREEN;
        if (percentage > 0.3) return Color.GOLD;
        return Color.CRIMSON;
    }

    // Getters and setters
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setPadding(double padding) {
        this.padding = padding;
    }
}