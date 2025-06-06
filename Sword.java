import javafx.scene.image.Image;

public class Sword extends Item {
    private int damage;
    private boolean isEquipped;

    public Sword() {
        super("Iron Sword", "A sharp blade for combat", "resources/IronSword.png", 1, 50);
        this.damage = 10;
        this.isEquipped = false;
    }

    @Override
    public void use() {
        isEquipped = !isEquipped; // Toggle equipped state
        System.out.println(isEquipped ? "Sword equipped" : "Sword unequipped");
    }

    public int getDamage() { return damage; }
    public boolean isEquipped() { return isEquipped; }
}