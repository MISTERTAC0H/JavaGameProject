import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MainMenu {
    private boolean isActive = true;
    private Image startButton, startButtonHover;
    private Image optionsButton, optionsButtonHover;
    private Image exitButton, exitButtonHover;
    private boolean isStartHovered = false;
    private boolean isOptionsHovered = false;
    private boolean isExitHovered = false;
    private double buttonWidth = 150;
    private double buttonHeight = 150;
    private double buttonSpacing = 20;

    public MainMenu() {
        loadImages();
    }

    private void loadImages() {
        startButton = loadImage("resources/start.png");
        startButtonHover = loadImage("resources/start_hover.png");
        optionsButton = loadImage("resources/options.png");
        optionsButtonHover = loadImage("resources/options_hover.png");
        exitButton = loadImage("resources/exit.png");
        exitButtonHover = loadImage("resources/exit_hover.png");
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

    public void draw(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        // Draw white background
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Calculate total width of all buttons with spacing
        double totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        // Draw buttons side by side
        gc.drawImage(isStartHovered ? startButtonHover : startButton,
                startX, centerY, buttonWidth, buttonHeight);

        gc.drawImage(isOptionsHovered ? optionsButtonHover : optionsButton,
                startX + buttonWidth + buttonSpacing,
                centerY, buttonWidth, buttonHeight);

        gc.drawImage(isExitHovered ? exitButtonHover : exitButton,
                startX + (buttonWidth + buttonSpacing) * 2,
                centerY, buttonWidth, buttonHeight);
    }

    public void updateHoverStates(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        // Calculate total width of all buttons with spacing
        double totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        // Start button hover
        isStartHovered = mouseX >= startX &&
                mouseX <= startX + buttonWidth &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;

        // Options button hover
        isOptionsHovered = mouseX >= startX + buttonWidth + buttonSpacing &&
                mouseX <= startX + buttonWidth * 2 + buttonSpacing &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;

        // Exit button hover
        isExitHovered = mouseX >= startX + (buttonWidth + buttonSpacing) * 2 &&
                mouseX <= startX + (buttonWidth + buttonSpacing) * 2 + buttonWidth &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isStartClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        double totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        return mouseX >= startX &&
                mouseX <= startX + buttonWidth &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isOptionsClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        double totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        return mouseX >= startX + buttonWidth + buttonSpacing &&
                mouseX <= startX + buttonWidth * 2 + buttonSpacing &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isExitClicked(double mouseX, double mouseY, double canvasWidth, double canvasHeight) {
        double totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        double startX = (canvasWidth - totalWidth) / 2;
        double centerY = canvasHeight / 2 - buttonHeight/2;

        return mouseX >= startX + (buttonWidth + buttonSpacing) * 2 &&
                mouseX <= startX + (buttonWidth + buttonSpacing) * 2 + buttonWidth &&
                mouseY >= centerY &&
                mouseY <= centerY + buttonHeight;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}