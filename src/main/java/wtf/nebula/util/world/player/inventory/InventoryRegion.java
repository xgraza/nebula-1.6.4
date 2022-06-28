package wtf.nebula.util.world.player.inventory;

public enum InventoryRegion {
    HOTBAR(0, 9),
    INVENTORY(9, 36),
    ALL(0, 36),
    CRAFTING(-1, -1);

    public final int start, end;

    InventoryRegion(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
