package us.nebula.client.cheat.impl.world;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.network.EventPacket;
import us.nebula.client.util.player.ItemUtil;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "AutoFish",
        description = "Automatically uses a rod to catch items from a body of water",
        category = CheatCategory.WORLD)
public final class AutoFishCheat extends Cheat
{
    private static final String RANDOM_SPLASH = "random.splash";

    private final Setting<Boolean> autoCastSetting = new Setting<>(
            "Auto Cast", true);
    private final Setting<Boolean> autoSwapSetting = new Setting<>(
            "Auto Swap", true);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (autoSwapSetting.getValue())
        {
            final int slot = getFishingHookItem();
            if (slot != -1)
            {
                MC.thePlayer.inventory.currentItem = slot;
            }
        }

        if (isNotHoldingRod())
        {
            return;
        }

        if (autoCastSetting.getValue()
                && MC.thePlayer.fishEntity == null
                && MC.thePlayer.ticksExisted % 10 == 0)
        {
            cast();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (MC.thePlayer == null || MC.thePlayer.fishEntity == null || isNotHoldingRod())
        {
            return;
        }
        if (event.getPacket() instanceof S29PacketSoundEffect)
        {
            final S29PacketSoundEffect packet = event.getPacket();
            if (!packet.getName().equals(RANDOM_SPLASH))
            {
                return;
            }
            final EntityFishHook entity = MC.thePlayer.fishEntity;
            if (entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 1.0)
            {
                cast();
            }
        }
    };

    private void cast()
    {
        MC.playerController.sendUseItem(MC.thePlayer, MC.theWorld, MC.thePlayer.getHeldItem());
        MC.thePlayer.swingItem();
    }

    private boolean isNotHoldingRod()
    {
        return MC.thePlayer.getHeldItem() == null
                || !(MC.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod);
    }

    private int getFishingHookItem()
    {
        int slot = -1;
        float score = 1.0f;

        for (int i = 0; i < 9; ++i)
        {
            final ItemStack stack = MC.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemFishingRod))
            {
                continue;
            }
            float itemScore = 0.0f;

            itemScore += ItemUtil.getEnchantLevelNoLimit(Enchantment.unbreaking, stack) * 1.5f;
            itemScore += ItemUtil.getEnchantLevelNoLimit(Enchantment.field_151370_z, stack) * 5.5f;
            itemScore += ItemUtil.getEnchantLevelNoLimit(Enchantment.field_151369_A, stack) * 2.2f;

            if (itemScore > score || slot == -1)
            {
                slot = i;
                score = itemScore;
            }
        }

        return slot;
    }
}
