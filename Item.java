import javafx.scene.image.Image;

public abstract class Item {
    protected String name;
    protected String description;
    protected Image sprite;
    protected int stackSize;
    protected int maxStackSize;
    protected int value;

    public Item(String name, String description, String spritePath, int maxStackSize, int value) {
        this.name = name;
        this.description = description;
        this.sprite = new Image(spritePath);
        this.stackSize = 1;
        this.maxStackSize = maxStackSize;
        this.value = value;
    }

    // Common methods for all items
    public abstract void use();

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Image getSprite() { return sprite; }
    public int getStackSize() { return stackSize; }
    public int getMaxStackSize() { return maxStackSize; }
    public int getValue() { return value; }

    public void setStackSize(int stackSize) {
        this.stackSize = Math.min(stackSize, maxStackSize);
    }

    public boolean combine(Item other) {
        if (!this.getClass().equals(other.getClass())) return false;
        if (!this.name.equals(other.name)) return false;

        int total = this.stackSize + other.stackSize;
        if (total <= maxStackSize) {
            this.stackSize = total;
            return true;
        } else {
            this.stackSize = maxStackSize;
            other.stackSize = total - maxStackSize;
            return false;
        }
    }

    @Override
    public String toString() {
        return name + " (" + stackSize + "/" + maxStackSize + ")";
    }
}