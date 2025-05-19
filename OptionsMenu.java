import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;

public class OptionsMenu {
    private boolean isActive = false;
    private Image fullscreenButton, fullscreenButtonHover;
    private Image backButton, backButtonHover;
    private boolean isFullscreenHovered = false;
    private boolean isBackHovered = false;
    private double buttonWidth = 150;
    private double buttonHeight = 150;
    private double buttonSpacing = 20;
    private Stage primaryStage;
    private MenuSource source;

    public OptionsMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadImages();
    }

    public enum MenuSource {
        MAIN_MENU,
        PAUSE_MENU
    }

    private void loadImages() {
        fullscreenButton = loadImage("resources/fullscreen.png");
        fullscreenButtonHover = loadImage("resources/fullscreen_hover.png");
        backButton = loadImage("resources/back.png");
        backButtonHover = loadImage("resources/back_hover.png");
    }

    private Image loadImage(String path) {
        try {
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.err.println("Error loading image: " + path);
            e.printStackTrace();
            return null;
        }
    }

    public void draw(GraphicsContext gc, double canvasWidth, double canvasHeight, boolean isMainMenuActive, boolean isGamePaused) {
        // Draw appropriate background based on where options was opened from
        if (isMainMenuActive) {
            gc.setFill(javafx.scene.paint.Color.WHITE);
        } else if (isGamePaused) {
            gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3));
        }
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Calculate positions for side-by-side buttons
        double totalWidth = (buttonWidth * 2) + buttonSpacing;
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        // Draw buttons side by side
        gc.drawImage(isFullscreenHovered ? fullscreenButtonHover : fullscreenButton,
                startX, centerY, buttonWidth, buttonHeight);

        gc.drawImage(isBackHovered ? backButtonHover : backButton,
                startX + buttonWidth + buttonSpacing, centerY,
                buttonWidth, buttonHeight);
    }

    public void updateHoverStates(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        double totalWidth = (buttonWidth * 2) + buttonSpacing;
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        // Fullscreen button hover
        isFullscreenHovered = mouseX >= startX &&
                mouseX <= startX + buttonWidth &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;

        // Back button hover
        isBackHovered = mouseX >= startX + buttonWidth + buttonSpacing &&
                mouseX <= startX + buttonWidth * 2 + buttonSpacing &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isFullscreenClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        double totalWidth = (buttonWidth * 2) + buttonSpacing;
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        return mouseX >= startX &&
                mouseX <= startX + buttonWidth &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isBackClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        double totalWidth = (buttonWidth * 2) + buttonSpacing;
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        return mouseX >= startX + buttonWidth + buttonSpacing &&
                mouseX <= startX + buttonWidth * 2 + buttonSpacing &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active, MenuSource source) {
        this.isActive = active;
        this.source = source;
    }

    public MenuSource getSource() {
        return source;
    }
    public void toggleFullscreen() {
        primaryStage.setFullScreenExitHint(""); // Remove the default message
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Disable ESC exit
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }
}