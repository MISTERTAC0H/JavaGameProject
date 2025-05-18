import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PauseMenu {
    private boolean gamePaused = false;
    private Image resumeButton;
    private Image resumeButtonHover;
    private Image exitButton;
    private Image exitButtonHover;
    private Image optionsButton;
    private Image optionsButtonHover;
    private boolean isResumeHovered = false;
    private boolean isExitHovered = false;
    private boolean isOptionsHovered = false;
    private double buttonWidth = 150;
    private double buttonHeight = 150;
    private double buttonSpacing = 20;
    private double resumeButtonX, resumeButtonY;
    private double exitButtonX, exitButtonY;
    private double optionsButtonX, optionsButtonY;
    private double canvasWidth, canvasHeight;

    public PauseMenu() {
        loadImages();
    }

    private void loadImages() {
        resumeButton = loadImage("resources/resume.png");
        resumeButtonHover = loadImage("resources/resume_hover.png");
        exitButton = loadImage("resources/exit.png");
        exitButtonHover = loadImage("resources/exit_hover.png");
        optionsButton = loadImage("resources/options.png");
        optionsButtonHover = loadImage("resources/options_hover.png");
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

    public void updateButtonPositions(double canvasWidth, double canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        double centerX = canvasWidth / 2;
        double centerY = canvasHeight / 2;

        // Calculate total width of all buttons with spacing
        double totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        double startX = centerX - totalWidth / 2;

        resumeButtonX = startX;
        resumeButtonY = centerY - buttonHeight/2;

        optionsButtonX = startX + buttonWidth + buttonSpacing;
        optionsButtonY = centerY - buttonHeight/2;

        exitButtonX = startX + (buttonWidth + buttonSpacing) * 2;
        exitButtonY = centerY - buttonHeight/2;
    }

    public void updateHoverStates(double mouseX, double mouseY) {
        isResumeHovered = mouseX >= resumeButtonX && mouseX <= resumeButtonX + buttonWidth &&
                mouseY >= resumeButtonY && mouseY <= resumeButtonY + buttonHeight;

        isOptionsHovered = mouseX >= optionsButtonX && mouseX <= optionsButtonX + buttonWidth &&
                mouseY >= optionsButtonY && mouseY <= optionsButtonY + buttonHeight;

        isExitHovered = mouseX >= exitButtonX && mouseX <= exitButtonX + buttonWidth &&
                mouseY >= exitButtonY && mouseY <= exitButtonY + buttonHeight;
    }

    public boolean isResumeClicked(double mouseX, double mouseY) {
        return mouseX >= resumeButtonX && mouseX <= resumeButtonX + buttonWidth &&
                mouseY >= resumeButtonY && mouseY <= resumeButtonY + buttonHeight;
    }

    public boolean isOptionsClicked(double mouseX, double mouseY) {
        return mouseX >= optionsButtonX && mouseX <= optionsButtonX + buttonWidth &&
                mouseY >= optionsButtonY && mouseY <= optionsButtonY + buttonHeight;
    }

    public boolean isExitClicked(double mouseX, double mouseY) {
        return mouseX >= exitButtonX && mouseX <= exitButtonX + buttonWidth &&
                mouseY >= exitButtonY && mouseY <= exitButtonY + buttonHeight;
    }

    public void draw(GraphicsContext gc) {
        // Draw semi-transparent overlay
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.3));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Draw buttons with hover effects
        gc.drawImage(isResumeHovered ? resumeButtonHover : resumeButton,
                resumeButtonX, resumeButtonY, buttonWidth, buttonHeight);

        gc.drawImage(isOptionsHovered ? optionsButtonHover : optionsButton,
                optionsButtonX, optionsButtonY, buttonWidth, buttonHeight);

        gc.drawImage(isExitHovered ? exitButtonHover : exitButton,
                exitButtonX, exitButtonY, buttonWidth, buttonHeight);
    }

    public boolean isPaused() {
        return gamePaused;
    }

    public void setPaused(boolean paused) {
        this.gamePaused = paused;
    }

    public void togglePause() {
        this.gamePaused = !this.gamePaused;
    }
}