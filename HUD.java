import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class HUD {
    private Player player;
    private double x, y;
    private double width, height;
    private double padding;
    private double staminaBarOffset; // Vertical offset for stamina bar

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
        this.staminaBarOffset = height + padding; // Position stamina bar below health
    }

    public void draw(GraphicsContext gc) {
        if (player == null) return;

        // Draw health bar (existing code)
        double healthPercentage = player.getHealth() / (double)player.getMaxHealth();
        healthPercentage = Math.max(0, Math.min(1, healthPercentage));

        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(x, y, width, height);

        gc.setFill(Color.RED);
        gc.fillRect(x, y, width * healthPercentage, height);

        gc.setStroke(Color.rgb(30, 30, 30));
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);

        // Draw stamina bar (new code)
        double staminaPercentage = player.getStamina() / (double)player.getMaxStamina();
        staminaPercentage = Math.max(0, Math.min(1, staminaPercentage));

        // Stamina bar background (dark gray)
        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(x, y + staminaBarOffset, width, height);

        // Current stamina (green)
        gc.setFill(getStaminaColor(staminaPercentage));
        gc.fillRect(x, y + staminaBarOffset, width * staminaPercentage, height);

        // Stamina bar border
        gc.setStroke(Color.rgb(30, 30, 30));
        gc.setLineWidth(3);
        gc.strokeRect(x, y + staminaBarOffset, width, height);
    }

    private Color getStaminaColor(double percentage) {
        // Different shades of green based on stamina level
        if (percentage > 0.6) return Color.rgb(0, 200, 0);  // Bright green
        if (percentage > 0.3) return Color.rgb(0, 150, 0);  // Medium green
        return Color.rgb(0, 100, 0);  // Dark green
    }

    // Getters and setters
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        this.staminaBarOffset = height + padding;
    }

    public void setPadding(double padding) {
        this.padding = padding;
        this.staminaBarOffset = height + padding;
    }
}