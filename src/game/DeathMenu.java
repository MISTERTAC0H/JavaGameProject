package game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

// Shown when the player's health reaches 0: "You Died" with Respawn and Exit buttons
public class DeathMenu {
    private boolean isActive = false;
    private Image respawnButton, respawnButtonHover;
    private Image exitButton, exitButtonHover;
    private boolean isRespawnHovered = false;
    private boolean isExitHovered = false;
    private double buttonWidth = 150;
    private double buttonHeight = 150;
    private double buttonSpacing = 20;
    private Font titleFont = new Font("Arial", 64);

    public DeathMenu() {
        respawnButton = Assets.image("resources/respawn.png");
        respawnButtonHover = Assets.image("resources/respawn_hover.png");
        exitButton = Assets.image("resources/exit.png");
        exitButtonHover = Assets.image("resources/exit_hover.png");
    }

    // Button layout: two buttons side by side, centered on screen
    private double respawnX(double canvasWidth) { return canvasWidth / 2 - buttonWidth - buttonSpacing / 2; }
    private double exitX(double canvasWidth) { return canvasWidth / 2 + buttonSpacing / 2; }
    private double buttonY(double canvasHeight) { return canvasHeight / 2 - buttonHeight / 2; }

    private boolean isOver(double mouseX, double mouseY, double buttonX, double buttonY) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    public void updateHoverStates(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        isRespawnHovered = isOver(mouseX, mouseY, respawnX(canvasWidth), buttonY(canvasHeight));
        isExitHovered = isOver(mouseX, mouseY, exitX(canvasWidth), buttonY(canvasHeight));
    }

    public boolean isRespawnClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        return isOver(mouseX, mouseY, respawnX(canvasWidth), buttonY(canvasHeight));
    }

    public boolean isExitClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        return isOver(mouseX, mouseY, exitX(canvasWidth), buttonY(canvasHeight));
    }

    public void draw(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        // Dark red overlay over the frozen game
        gc.setFill(Color.rgb(60, 0, 0, 0.6));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Title
        gc.setFill(Color.rgb(200, 30, 30));
        gc.setFont(titleFont);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("YOU DIED", canvasWidth / 2, buttonY(canvasHeight) - 40);
        gc.setTextAlign(TextAlignment.LEFT); // reset so other text isn't affected

        // Buttons with hover effects (pixel art, so no smoothing)
        gc.setImageSmoothing(false);
        gc.drawImage(isRespawnHovered ? respawnButtonHover : respawnButton,
                respawnX(canvasWidth), buttonY(canvasHeight), buttonWidth, buttonHeight);
        gc.drawImage(isExitHovered ? exitButtonHover : exitButton,
                exitX(canvasWidth), buttonY(canvasHeight), buttonWidth, buttonHeight);
        gc.setImageSmoothing(true);
    }

    public boolean isActive() { return isActive; }

    public void setActive(boolean active) {
        isActive = active;
        if (!active) {
            isRespawnHovered = false;
            isExitHovered = false;
        }
    }
}
