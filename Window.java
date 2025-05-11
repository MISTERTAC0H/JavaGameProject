import com.google.gson.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

public class Window extends JPanel implements ActionListener {

    Timer timer = new Timer(16, this);
    BufferedImage spriteSheet;
    Rectangle frameRect;
    int x = 50, y = 50; // Position of sprite
    int speed = 5;

    public Window() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.BLACK);
        loadSpriteData();
        timer.start();

        // Optional: move with arrow keys
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> x -= speed;
                    case KeyEvent.VK_RIGHT -> x += speed;
                    case KeyEvent.VK_UP -> y -= speed;
                    case KeyEvent.VK_DOWN -> y += speed;
                }
                repaint();
            }
        });
        setFocusable(true);
    }

    private void loadSpriteData() {
        try {
            // Load JSON
            Reader reader = new FileReader("Cat.json");
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            // Load image
            spriteSheet = ImageIO.read(new File("New Piskel.png"));

            // Read frame info
            JsonObject frames = json.getAsJsonObject("frames");
            Map.Entry<String, JsonElement> firstFrame = frames.entrySet().iterator().next(); // We assume one frame for now
            JsonObject frameObj = firstFrame.getValue().getAsJsonObject().getAsJsonObject("frame");

            int fx = frameObj.get("x").getAsInt();
            int fy = frameObj.get("y").getAsInt();
            int fw = frameObj.get("w").getAsInt();
            int fh = frameObj.get("h").getAsInt();

            frameRect = new Rectangle(fx, fy, fw, fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (spriteSheet != null && frameRect != null) {
            g.drawImage(spriteSheet,
                x, y, x + frameRect.width, y + frameRect.height,
                frameRect.x, frameRect.y, frameRect.x + frameRect.width, frameRect.y + frameRect.height,
                this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cat Sprite Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Window());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
