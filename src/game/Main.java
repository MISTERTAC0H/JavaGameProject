package game;
import javafx.application.Application;

// Entry point. Launching through a class that doesn't extend Application lets the game
// start even when JavaFX is on the classpath instead of the module path (e.g. IDE run buttons)
public class Main {
    public static void main(String[] args) {
        Application.launch(Window.class, args);
    }
}
