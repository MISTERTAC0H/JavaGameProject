import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, Item> items;
    private List<String> itemOrder;
    private int selectedHotbarIndex = 0;
    private int selectedInventoryIndex = -1;

    private boolean isVisible = false;
    private double slotSize = 50;
    private double slotSpacing = 5;
    private double padding = 20;
    private Font itemFont = new Font("Arial", 14);

    public Inventory() {
        this.items = new HashMap<>();
        this.itemOrder = new ArrayList<>();
    }

    // Add item to first available slot
    public boolean addItem(Item item) {
        if (items.size() >= TOTAL_SLOTS) return false;

        // If stackable and exists, increase quantity
        if (item.isStackable()) {
            for (String itemName : itemOrder) {
                Item existing = items.get(itemName);
                if (existing.getName().equals(item.getName())) {
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                    return true;
                }
            }
        }

        // Find first empty slot
        int emptySlot = itemOrder.size();
        if (emptySlot >= TOTAL_SLOTS) return false;

        items.put(item.getName(), item);
        itemOrder.add(item.getName());
        return true;
    }

    // Add item to specific slot
    public boolean addItemToSlot(Item item, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= TOTAL_SLOTS) return false;

        // If slot is occupied
        if (slotIndex < itemOrder.size()) {
            Item existing = items.get(itemOrder.get(slotIndex));
            if (existing.isStackable() && existing.getName().equals(item.getName())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                return true;
            }
            return false; // Slot occupied by different item
        }

        // Add to specified slot
        items.put(item.getName(), item);
        itemOrder.add(slotIndex, item.getName());
        return true;
    }

    public Item getSelectedHotbarItem() {
        int hotbarStart = MAIN_INVENTORY_SLOTS;
        int selectedSlot = hotbarStart + selectedHotbarIndex;

        if (selectedSlot < itemOrder.size()) {
            return items.get(itemOrder.get(selectedSlot));
        }
        return null;
    }

    public void selectHotbarSlot(int index) {
        if (index >= 0 && index < HOTBAR_SLOTS) {
            selectedHotbarIndex = index;
        }
    }

    public void scrollHotbar(int direction) {
        selectedHotbarIndex = (selectedHotbarIndex + direction + HOTBAR_SLOTS) % HOTBAR_SLOTS;
    }

    public void draw(GraphicsContext gc) {
        if (!isVisible) {
            // Draw only hotbar when inventory is closed
            drawHotbar(gc, false, 0, 0);
            return;
        }

        // Calculate inventory dimensions
        double inventoryWidth = MAIN_COLS * (slotSize + slotSpacing) + padding * 2;
        double inventoryHeight = (MAIN_ROWS + HOTBAR_ROWS + 1) * (slotSize + slotSpacing) + padding * 2;

        // Center inventory on screen
        double screenWidth = gc.getCanvas().getWidth();
        double screenHeight = gc.getCanvas().getHeight();
        double inventoryX = (screenWidth - inventoryWidth) / 2;
        double inventoryY = (screenHeight - inventoryHeight) / 2;

        // Draw background
        gc.setFill(Color.rgb(30, 30, 30, 0.9));
        gc.fillRoundRect(inventoryX, inventoryY, inventoryWidth, inventoryHeight, 10, 10);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(inventoryX, inventoryY, inventoryWidth, inventoryHeight, 10, 10);

        // Draw title
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 20));
        gc.fillText("INVENTORY", inventoryX + inventoryWidth/2 - 50, inventoryY + 30);

        // Draw main inventory slots
        double slotsStartY = inventoryY + 50;
        for (int row = 0; row < MAIN_ROWS; row++) {
            for (int col = 0; col < MAIN_COLS; col++) {
                int slotIndex = row * MAIN_COLS + col;
                double slotX = inventoryX + padding + col * (slotSize + slotSpacing);
                double slotY = slotsStartY + row * (slotSize + slotSpacing);

                drawSlot(gc, slotX, slotY, slotIndex, slotIndex == selectedInventoryIndex);
            }
        }

        // Draw hotbar as part of inventory
        double hotbarY = slotsStartY + MAIN_ROWS * (slotSize + slotSpacing) + padding;
        drawHotbar(gc, true, inventoryX + padding, hotbarY);
    }

    private void drawHotbar(GraphicsContext gc, boolean isInventoryOpen, double hotbarX, double hotbarY) {
        if (!isInventoryOpen) {
            // Position hotbar at bottom center when inventory is closed
            double hotbarWidth = HOTBAR_COLS * (slotSize + slotSpacing);
            hotbarX = (gc.getCanvas().getWidth() - hotbarWidth) / 2;
            hotbarY = gc.getCanvas().getHeight() - slotSize - 20;
        }

        // Hotbar background
        gc.setFill(Color.rgb(50, 50, 50, isInventoryOpen ? 0.7 : 0.5));
        gc.fillRoundRect(hotbarX - 5, hotbarY - 5,
                HOTBAR_COLS * (slotSize + slotSpacing) + 10,
                slotSize + 10, 10, 10);

        // Draw hotbar slots
        int hotbarStart = MAIN_INVENTORY_SLOTS;
        for (int i = 0; i < HOTBAR_SLOTS; i++) {
            double slotX = hotbarX + i * (slotSize + slotSpacing);
            double slotY = hotbarY;
            int slotIndex = hotbarStart + i;

            drawSlot(gc, slotX, slotY, slotIndex, i == selectedHotbarIndex);
        }
    }

    private void drawSlot(GraphicsContext gc, double x, double y, int slotIndex, boolean isSelected) {
        // Draw slot background
        gc.setFill(isSelected ? Color.GOLD : Color.DARKGRAY);
        gc.fillRect(x, y, slotSize, slotSize);
        gc.setStroke(Color.WHITE);
        gc.strokeRect(x, y, slotSize, slotSize);

        // Draw item if exists
        if (slotIndex < itemOrder.size()) {
            Item item = items.get(itemOrder.get(slotIndex));
            if (item.getImage() != null) {
                gc.drawImage(item.getImage(),
                        x + (slotSize - item.getImage().getWidth()) / 2,
                        y + (slotSize - item.getImage().getHeight()) / 2);
            }

            // Draw quantity if stackable
            if (item.isStackable() && item.getQuantity() > 1) {
                gc.setFill(Color.WHITE);
                gc.setFont(itemFont);
                gc.fillText(String.valueOf(item.getQuantity()),
                        x + slotSize - 15,
                        y + slotSize - 5);
            }
        }
    }

    // Getters and setters for potential future use...
    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }
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