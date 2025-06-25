package us.nebula.impl.cheat.world;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.util.player.ItemUtil;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "AutoFish",
        description = "Automatically casts and catches with a fishing pole",
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

        if (!isHoldingFishingRod())
        {
            return;
        }

        if (MC.thePlayer.fishEntity == null)
        {
            if (!autoCastSetting.getValue())
            {
                return;
            }
            cast();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (MC.thePlayer == null || MC.thePlayer.fishEntity == null || !isHoldingFishingRod())
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

    private boolean isHoldingFishingRod()
    {
        return MC.thePlayer.getHeldItem() != null
                && MC.thePlayer.getHeldItem().getItem() instanceof ItemFishingRod;
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
