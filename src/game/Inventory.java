package game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Inventory {
    // Inventory configuration
    private static final int TOTAL_SLOTS = 36; // 27 main + 9 hotbar
    private static final int HOTBAR_SLOTS = 9;
    private static final int MAIN_INVENTORY_SLOTS = TOTAL_SLOTS - HOTBAR_SLOTS;

    // Layout constants
    private static final int MAIN_COLS = 9;
    private static final int HOTBAR_COLS = 9;
    private static final int MAIN_ROWS = MAIN_INVENTORY_SLOTS / MAIN_COLS;
    private static final int HOTBAR_ROWS = 1;
    private static final double TITLE_HEIGHT = 50;

    // slots[0..26] = main inventory, slots[27..35] = hotbar, null = empty
    private final Item[] slots = new Item[TOTAL_SLOTS];
    private int selectedHotbarIndex = 0;
    private int selectedInventoryIndex = -1;

    private boolean isVisible = false;
    private double slotSize = 50;
    private double slotSpacing = 5;
    private double padding = 20;
    private Font itemFont = new Font("Arial", 14);
    private int hoveredSlotIndex = -1;

    // Drag and drop state
    private Item heldItem = null;
    private int heldFromSlot = -1;
    private double mouseX, mouseY;

    public Inventory() {}

    // Add item to first available slot, filling the hotbar first
    public boolean addItem(Item item) {
        // If stackable and exists, increase quantity
        if (item.isStackable()) {
            for (Item existing : slots) {
                if (existing != null && existing.getName().equals(item.getName())) {
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                    return true;
                }
            }
        }

        for (int i = MAIN_INVENTORY_SLOTS; i < TOTAL_SLOTS; i++) {
            if (slots[i] == null) { slots[i] = item; return true; }
        }
        for (int i = 0; i < MAIN_INVENTORY_SLOTS; i++) {
            if (slots[i] == null) { slots[i] = item; return true; }
        }
        return false; // Inventory full
    }

    // Add item to specific slot
    public boolean addItemToSlot(Item item, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= TOTAL_SLOTS) return false;

        Item existing = slots[slotIndex];
        if (existing == null) {
            slots[slotIndex] = item;
            return true;
        }
        if (existing.isStackable() && existing.getName().equals(item.getName())) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            return true;
        }
        return false; // Slot occupied by different item
    }

    public Item getItem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= TOTAL_SLOTS) return null;
        return slots[slotIndex];
    }

    public Item getSelectedHotbarItem() {
        return slots[MAIN_INVENTORY_SLOTS + selectedHotbarIndex];
    }

    public void selectHotbarSlot(int index) {
        if (index >= 0 && index < HOTBAR_SLOTS) {
            selectedHotbarIndex = index;
        }
    }

    public void scrollHotbar(int direction) {
        selectedHotbarIndex = (selectedHotbarIndex + direction + HOTBAR_SLOTS) % HOTBAR_SLOTS;
    }

    // ---------- Mouse input (screen coordinates) ----------

    // Returns true if the click was used by the inventory (inventory open or a hotbar slot was clicked)
    public boolean mousePressed(double x, double y, double screenWidth, double screenHeight) {
        mouseX = x;
        mouseY = y;
        int slot = getSlotAtPosition(x, y, screenWidth, screenHeight);
        if (slot < 0) return isVisible;

        // Clicking a hotbar slot selects it, whether the inventory is open or not
        if (slot >= MAIN_INVENTORY_SLOTS) {
            selectHotbarSlot(slot - MAIN_INVENTORY_SLOTS);
        }

        // Pick up the item so it can be dragged (only while the inventory is open)
        if (isVisible && slots[slot] != null) {
            heldItem = slots[slot];
            heldFromSlot = slot;
            slots[slot] = null;
        }
        return true;
    }

    public void mouseDragged(double x, double y, double screenWidth, double screenHeight) {
        mouseX = x;
        mouseY = y;
        updateHoverState(x, y, screenWidth, screenHeight);
    }

    public void mouseReleased(double x, double y, double screenWidth, double screenHeight) {
        if (heldItem == null) return;

        int target = getSlotAtPosition(x, y, screenWidth, screenHeight);
        if (target < 0) {
            returnHeldItem();
            return;
        }

        Item targetItem = slots[target];
        if (targetItem != null && targetItem.isStackable() && targetItem.getName().equals(heldItem.getName())) {
            // Merge stacks, anything that doesn't fit goes back where it came from
            targetItem.combine(heldItem);
            if (heldItem.getQuantity() > 0) {
                slots[heldFromSlot] = heldItem;
            }
        } else {
            // Swap: whatever was in the target slot moves to where the held item came from
            slots[heldFromSlot] = targetItem;
            slots[target] = heldItem;
        }
        heldItem = null;
        heldFromSlot = -1;
    }

    // Puts a dragged item back in its original slot (e.g. dropped outside the inventory or inventory closed)
    private void returnHeldItem() {
        if (heldItem != null) {
            slots[heldFromSlot] = heldItem;
            heldItem = null;
            heldFromSlot = -1;
        }
    }

    public boolean isDragging() { return heldItem != null; }

    // ---------- Layout ----------

    private double inventoryWidth() { return MAIN_COLS * (slotSize + slotSpacing) + padding * 2; }
    private double inventoryHeight() { return (MAIN_ROWS + HOTBAR_ROWS + 1) * (slotSize + slotSpacing) + padding * 2; }
    private double inventoryX(double screenWidth) { return (screenWidth - inventoryWidth()) / 2; }
    private double inventoryY(double screenHeight) { return (screenHeight - inventoryHeight()) / 2; }

    // Top-left corner of the hotbar, which moves depending on whether the inventory is open
    private double hotbarX(double screenWidth) {
        if (isVisible) return inventoryX(screenWidth) + padding;
        return (screenWidth - HOTBAR_COLS * (slotSize + slotSpacing)) / 2;
    }
    private double hotbarY(double screenHeight) {
        if (isVisible) return inventoryY(screenHeight) + TITLE_HEIGHT + MAIN_ROWS * (slotSize + slotSpacing) + padding;
        return screenHeight - slotSize - 20;
    }

    private double slotX(int slotIndex, double screenWidth) {
        if (slotIndex >= MAIN_INVENTORY_SLOTS) {
            return hotbarX(screenWidth) + (slotIndex - MAIN_INVENTORY_SLOTS) * (slotSize + slotSpacing);
        }
        return inventoryX(screenWidth) + padding + (slotIndex % MAIN_COLS) * (slotSize + slotSpacing);
    }
    private double slotY(int slotIndex, double screenHeight) {
        if (slotIndex >= MAIN_INVENTORY_SLOTS) return hotbarY(screenHeight);
        return inventoryY(screenHeight) + TITLE_HEIGHT + (slotIndex / MAIN_COLS) * (slotSize + slotSpacing);
    }

    // Returns the slot under the given screen position, or -1. Only hotbar slots count while the inventory is closed
    public int getSlotAtPosition(double x, double y, double screenWidth, double screenHeight) {
        int first = isVisible ? 0 : MAIN_INVENTORY_SLOTS;
        for (int i = first; i < TOTAL_SLOTS; i++) {
            double sx = slotX(i, screenWidth);
            double sy = slotY(i, screenHeight);
            if (x >= sx && x <= sx + slotSize && y >= sy && y <= sy + slotSize) {
                return i;
            }
        }
        return -1;
    }

    // ---------- Drawing ----------

    public void draw(GraphicsContext gc) {
        double screenWidth = gc.getCanvas().getWidth();
        double screenHeight = gc.getCanvas().getHeight();

        if (isVisible) {
            double invX = inventoryX(screenWidth);
            double invY = inventoryY(screenHeight);

            // Draw background
            gc.setFill(Color.rgb(30, 30, 30, 0.9));
            gc.fillRoundRect(invX, invY, inventoryWidth(), inventoryHeight(), 10, 10);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRoundRect(invX, invY, inventoryWidth(), inventoryHeight(), 10, 10);

            // Draw title
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 20));
            gc.fillText("INVENTORY", invX + inventoryWidth() / 2 - 50, invY + 30);

            // Draw main inventory slots
            for (int i = 0; i < MAIN_INVENTORY_SLOTS; i++) {
                drawSlot(gc, slotX(i, screenWidth), slotY(i, screenHeight), i, i == selectedInventoryIndex);
            }
        }

        drawHotbar(gc, screenWidth, screenHeight);

        // Draw the dragged item on top of everything, centered on the mouse
        if (heldItem != null) {
            drawItem(gc, heldItem, mouseX - slotSize / 2, mouseY - slotSize / 2);
        }
    }

    private void drawHotbar(GraphicsContext gc, double screenWidth, double screenHeight) {
        double x = hotbarX(screenWidth);
        double y = hotbarY(screenHeight);

        // Hotbar background
        gc.setFill(Color.rgb(50, 50, 50, isVisible ? 0.7 : 0.5));
        gc.fillRoundRect(x - 5, y - 5, HOTBAR_COLS * (slotSize + slotSpacing) + 10, slotSize + 10, 10, 10);

        // Draw hotbar slots
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            int slotIndex = MAIN_INVENTORY_SLOTS + i;
            drawSlot(gc, slotX(slotIndex, screenWidth), y, slotIndex, i == selectedHotbarIndex);
        }
    }

    private void drawSlot(GraphicsContext gc, double x, double y, int slotIndex, boolean isSelected) {
        // Draw slot background - hover takes precedence over selection
        if (slotIndex == hoveredSlotIndex) {
            gc.setFill(Color.rgb(255, 255, 0, 0.3)); // Semi-transparent yellow
        } else if (isSelected) {
            gc.setFill(Color.GOLD);
        } else {
            gc.setFill(Color.DARKGRAY);
        }

        gc.fillRect(x, y, slotSize, slotSize);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, slotSize, slotSize);

        if (slots[slotIndex] != null) {
            drawItem(gc, slots[slotIndex], x, y);
        }
    }

    // Draws an item scaled to fit inside a slot-sized box at (x, y)
    private void drawItem(GraphicsContext gc, Item item, double x, double y) {
        Image image = item.getImage();
        if (image != null && image.getWidth() > 0 && image.getHeight() > 0) {
            double maxSize = slotSize - 10;
            double scale = Math.min(maxSize / image.getWidth(), maxSize / image.getHeight());
            double w = image.getWidth() * scale;
            double h = image.getHeight() * scale;
            gc.setImageSmoothing(false); // keep pixel art crisp
            gc.drawImage(image, x + (slotSize - w) / 2, y + (slotSize - h) / 2, w, h);
        }

        if (item.isStackable() && item.getQuantity() > 1) {
            gc.setFill(Color.WHITE);
            gc.setFont(itemFont);
            gc.fillText(String.valueOf(item.getQuantity()), x + slotSize - 15, y + slotSize - 5);
        }
    }

    public void updateHoverState(double x, double y, double screenWidth, double screenHeight) {
        mouseX = x;
        mouseY = y;
        hoveredSlotIndex = getSlotAtPosition(x, y, screenWidth, screenHeight);
    }
    public void clearHoverState() { hoveredSlotIndex = -1; }

    public int getMainCols() { return MAIN_COLS; }
    public int getHotbarCols() { return HOTBAR_COLS; }
    public int getMainRows() { return MAIN_ROWS; }
    public int getHotbarRows() { return HOTBAR_ROWS; }
    public double getSlotSize() { return slotSize; }
    public double getSlotSpacing() { return slotSpacing; }
    public double getPadding() { return padding; }
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) {
        if (!visible) {
            returnHeldItem();
        }
        isVisible = visible;
        hoveredSlotIndex = -1;
    }
    public int getSelectedHotbarIndex() { return selectedHotbarIndex; }
    public int getSelectedInventoryIndex() { return selectedInventoryIndex; }
    public void setSelectedInventoryIndex(int index) {
        if (index >= -1 && index < MAIN_INVENTORY_SLOTS) {
            selectedInventoryIndex = index;
        }
    }
    public void setSlotSize(double slotSize) { this.slotSize = slotSize; }
    public void setSlotSpacing(double slotSpacing) { this.slotSpacing = slotSpacing; }
    public void setPadding(double padding) { this.padding = padding; }
    public void setItemFont(Font font) { this.itemFont = font; }
    public int getTotalSlots() { return TOTAL_SLOTS; }
    public int getHotbarSlots() { return HOTBAR_SLOTS; }
    public int getMainInventorySlots() { return MAIN_INVENTORY_SLOTS; }
}
