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

    public void moveItemsFromInventory(boolean store)
    {
        final Container container = MC.thePlayer.openContainer;
        if (!canStealFromOpenContainer(container))
        {
            return;
        }
        final IInventory inventory = getInventory(container);
        if (!timer.hasElapsed((long)delaySetting.getValue()))
        {
            return;
        }
        int nextSlot = getNextStealSlot(store ? 36 : getSize(inventory), store ? MC.thePlayer.inventory : inventory);
        if (nextSlot == -1)
        {
            return;
        }
        timer.resetTime();
        if (store)
        {
            final int totalSize = getSize(getInventory(container)) + 36;
            nextSlot = totalSize - nextSlot - 1;
        }

        // use this instead of PlayerControllerMP#windowClick for compat with inf items
        ((GuiContainer) MC.currentScreen).func_146984_a(container.getSlot(nextSlot), 0, 0, 1);
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

    public int getNextStealSlot(final int maxSlots, final IInventory inventory)
    {
        int slot = -1;
        while (true)
        {
            final int nextSlot = randomOrderSetting.getValue()
                    ? MathUtil.random(0, maxSlots - 1)
                    : (slot += 1);
            if (nextSlot >= maxSlots)
            {
                return -1;
            }
            final ItemStack itemStack = inventory.getStackInSlot(nextSlot);
            if (itemStack != null)
            {
                // ChatUtil.sendNebula("%s, -> %s", nextSlot, itemStack);
                return nextSlot;
            }
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
