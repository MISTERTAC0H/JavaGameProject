package game;
import javafx.scene.image.Image;

import java.io.InputStream;

// Loads files from the classpath. Paths are relative to the src/ folder, e.g. "resources/Tree.png" or "maps/world.txt"
public final class Assets {
    private Assets() {}

    public static InputStream open(String path) {
        // leading "/" makes the lookup start at the classpath root instead of this class's package folder
        InputStream stream = Assets.class.getResourceAsStream("/" + path);
        if (stream == null) {
            throw new IllegalStateException("Missing asset: " + path + " (expected at src/" + path + ")");
        }
        return stream;
    }

    public static Image image(String path) {
        return new Image(open(path));
    }
}
