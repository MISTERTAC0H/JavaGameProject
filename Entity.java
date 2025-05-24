import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {
    protected double x, y;
    protected double width, height;
    protected Image currentFrame;

    // RPG Attributes
    protected int xp;
    protected int health;
    protected int maxHealth;
    protected int attack;
    protected int stamina;
    protected int defense;
    protected int criticalHit; // Percentage chance (0-100)

    public Entity(double x, double y, double width, double height, Image startingImage) {
        this(x, y, width, height, startingImage, 0, 100, 10, 50, 5, 5);
    }

    public Entity(double x, double y, double width, double height, Image startingImage,
                  int xp, int health, int attack, int stamina, int defense, int criticalHit) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentFrame = startingImage;
        this.xp = xp;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.stamina = stamina;
        this.defense = defense;
        this.criticalHit = Math.min(100, Math.max(0, criticalHit)); // Clamp 0-100
    }

    public abstract void update(boolean isMovingRight, boolean isMovingLeft, boolean isMovingFront, boolean isMovingBack);

    // Combat methods
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int calculateDamage() {
        // Critical hit chance
        if (Math.random() * 100 < criticalHit) {
            return attack * 2; // Critical hit does double damage
        }
        return attack;
    }

    // Attribute getters and setters
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
    public void addXp(int amount) { this.xp += amount; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public int getCriticalHit() { return criticalHit; }
    public void setCriticalHit(int criticalHit) {
        this.criticalHit = Math.min(100, Math.max(0, criticalHit));
    }

    // Position getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}