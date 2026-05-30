package us.nebula.client.cheat.impl.world;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.util.math.MathUtil;
import us.nebula.client.util.math.Timer;
import us.nebula.client.util.value.Setting;

/**
 * @author xgraza
 * @since 05/26/26
 */
@CheatManifest(name = "ChestStealer",
        description = "Automatically steals items from open chests",
        category = CheatCategory.WORLD)
public final class ChestStealerCheat extends Cheat
{
    @CheatInstance
    public static ChestStealerCheat INSTANCE;

    private static final String ENDER_CHEST_TRANSLATION_KEY = "container.enderchest";

    private final Setting<Integer> delaySetting = new Setting<>(
            "Delay", 100, 0, 1500, 1);
    public final Setting<Boolean> automaticSetting = new Setting<>(
            "Automatic", true);
    private final Setting<Boolean> randomOrderSetting = new Setting<>(
            "Random Order", false);
    private final Setting<Boolean> enderChestSetting = new Setting<>(
            "Ender Chest", false);

    public final Timer timer = new Timer();

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (!automaticSetting.getValue()
                || !hasSpaceInLocalInventory()
                || !(MC.thePlayer.openContainer instanceof ContainerChest))
        {
            return;
        }
        moveItemsFromInventory(false);
    };

    public boolean moveItemsFromInventory(boolean store)
    {
        final Container container = MC.thePlayer.openContainer;
        if (!canStealFromOpenContainer(container))
        {
            return false;
        }
        final IInventory inventory = getInventory(container);
        if (!timer.hasElapsed((long)delaySetting.getValue()))
        {
            return true;
        }

        int slot = getNextStealSlot(store ? MC.thePlayer.inventory : inventory);
        if (slot == -1)
        {
            return false;
        }
        timer.resetTime();
        if (store)
        {
            if (slot >= 9)
            {
                slot -= 9;
            } else
            {
                slot += 27;
            }
            slot += getSize(inventory);
        }

        // use this instead of PlayerControllerMP#windowClick for compat with inf items
        ((GuiContainer) MC.currentScreen).func_146984_a(null, slot, 0, 1);
        return true;
    }

    public int getSize(final IInventory inventory)
    {
        return inventory instanceof InventoryPlayer ? 9 + 27 : inventory.getSizeInventory();
    }

    public IInventory getInventory(final Container container)
    {
        if (!(container instanceof ContainerChest || container instanceof ContainerPlayer))
        {
            return null;
        }
        if (container instanceof ContainerPlayer)
        {
            return MC.thePlayer.inventory;
        }
        return ((ContainerChest) container).getLowerChestInventory();
    }

    public boolean hasSpaceInLocalInventory()
    {
        return hasSpaceInInventory(9 + 27, MC.thePlayer.inventory);
    }

    public boolean hasSpaceInInventory(final int maxSlots, final IInventory inventory)
    {
        for (int i = 0; i < maxSlots; ++i)
        {
            final ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null)
            {
                return true;
            }
        }
        return false;
    }

    public int getNextStealSlot(final IInventory inventory)
    {
        final int size = getSize(inventory) - 1;
        int slot = -1;
        while (true)
        {
            slot = randomOrderSetting.getValue()
                    ? MathUtil.random(0, size)
                    : slot + 1;
            if (slot > size)
            {
                return -1;
            }
            final ItemStack itemStack = inventory.getStackInSlot(slot);
            if (itemStack == null)
            {
                continue;
            }
            return slot;
        }
    }

    public boolean canStealFromOpenContainer(final Container container)
    {
        if (container instanceof ContainerPlayer)
        {
            return true;
        }
        if (!(container instanceof ContainerChest))
        {
            return false;
        }
        final IInventory inventory = ((ContainerChest) container).getLowerChestInventory();
        if (!enderChestSetting.getValue()
                && inventory.getInventoryName().equals(ENDER_CHEST_TRANSLATION_KEY))
        {
            return false;
        }
        return true;
    }
}
