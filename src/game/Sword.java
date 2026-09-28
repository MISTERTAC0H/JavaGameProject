package game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sword extends Item {
    private int damage;

    // Swing settings
    private static final long SWING_DURATION = 250; // milliseconds for the blade to sweep the arc
    private static final long END_HOLD = 80; // milliseconds the blade stays at the end of the arc before the next swing can start
    private static final double SWING_ARC = Math.toRadians(120); // total angle the blade sweeps through
    private static final double SPRITE_SCALE = 2.5;
    private static final double HANDLE_OFFSET = 10; // gap between the player's center and the sword handle
    private static final double KNOCKBACK = 25;

    // Swing state. Whether a swing is active is worked out from the start time alone,
    // so use(), update() and draw() always agree no matter what order they're called in
    private long swingStartTime = -1; // -1 = never swung
    private double swingAngle = 0; // direction of the mouse at the start of the swing, in radians
    private final Set<Enemy> hitThisSwing = new HashSet<>(); // so each swing only hits an enemy once

    public Sword() {
        super("Iron Sword", "A sharp blade for combat", "resources/IronSword.png", 1, 50);
        this.damage = 10;
    }

    @Override
    public void use(Player user, double targetX, double targetY) {
        if (isSwinging()) return; // finish the whole swing (including the end hold) first

        double centerX = user.getX() + user.getWidth() / 2;
        double centerY = user.getY() + user.getHeight() / 2;
        swingAngle = Math.atan2(targetY - centerY, targetX - centerX);
        swingStartTime = System.currentTimeMillis();
        hitThisSwing.clear();
    }

    @Override
    public void update(Player user, List<Enemy> enemies) {
        // Only deal damage while the blade is moving, not during the end hold
        if (!isSwinging() || timeSinceSwingStart() >= SWING_DURATION) return;

        double centerX = user.getX() + user.getWidth() / 2;
        double centerY = user.getY() + user.getHeight() / 2;
        double reach = HANDLE_OFFSET + getLength();

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive() || hitThisSwing.contains(enemy)) continue;

            double dx = enemy.getX() + enemy.getWidth() / 2 - centerX;
            double dy = enemy.getY() + enemy.getHeight() / 2 - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double enemyRadius = Math.max(enemy.getWidth(), enemy.getHeight()) / 2;

            // Hit if the enemy is within reach and inside the swing's arc
            if (distance <= reach + enemyRadius && angleBetween(Math.atan2(dy, dx), swingAngle) <= SWING_ARC / 2) {
                enemy.takeDamage(damage);
                if (distance > 0) {
                    enemy.knockback(dx / distance * KNOCKBACK, dy / distance * KNOCKBACK);
                }
                hitThisSwing.add(enemy);
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc, Player user, double cameraX, double cameraY) {
        if (!isSwinging() || sprite == null) return;

        // Clamped to 1 so the blade rests at the end of the arc during the end hold
        double progress = Math.min(1.0, timeSinceSwingStart() / (double) SWING_DURATION);
        double startAngle = swingAngle - SWING_ARC / 2;
        double bladeAngle = startAngle + SWING_ARC * progress;

        double screenX = user.getX() + user.getWidth() / 2 - cameraX;
        double screenY = user.getY() + user.getHeight() / 2 - cameraY;
        double length = getLength();
        double width = sprite.getWidth() * SPRITE_SCALE;

        // Faint trail showing the part of the arc swept so far
        double reach = HANDLE_OFFSET + length;
        gc.setFill(Color.rgb(255, 255, 255, 0.25));
        // JavaFX arcs use degrees measured counter-clockwise, while screen angles go clockwise, so negate
        gc.fillArc(screenX - reach, screenY - reach, reach * 2, reach * 2,
                -Math.toDegrees(startAngle), -Math.toDegrees(bladeAngle - startAngle), ArcType.ROUND);

        // Sprite points straight up, so rotate an extra 90 degrees to point it along bladeAngle
        gc.save();
        gc.translate(screenX, screenY);
        gc.rotate(Math.toDegrees(bladeAngle) + 90);
        gc.setImageSmoothing(false);
        gc.drawImage(sprite, -width / 2, -HANDLE_OFFSET - length, width, length);
        gc.restore();
    }

    private double getLength() {
        return sprite.getHeight() * SPRITE_SCALE;
    }

    // Smallest angle between two directions, in radians (0 to PI)
    private static double angleBetween(double a, double b) {
        double diff = Math.abs(a - b) % (2 * Math.PI);
        return diff > Math.PI ? 2 * Math.PI - diff : diff;
    }

    public int getDamage() { return damage; }
    public boolean isSwinging() {
        return swingStartTime >= 0 && timeSinceSwingStart() < SWING_DURATION + END_HOLD;
    }

    private long timeSinceSwingStart() {
        return System.currentTimeMillis() - swingStartTime;
    }
}
