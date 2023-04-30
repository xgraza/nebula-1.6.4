package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventAttack;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

import static lol.nebula.util.player.InventoryUtils.is32k;
import static lol.nebula.util.player.InventoryUtils.isInfinite;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class AntiRevert extends Module {
    private final Setting<Boolean> max = new Setting<>(true, "32ks");

    public AntiRevert() {
        super("Anti Revert", "Attempts to stop infinite's from getting reverted", ModuleCategory.PLAYER);
    }

    @Listener
    public void onAttack(EventAttack event) {

        // if we are holding an illegal, cancel
        if (isHoldingIllegal()) event.cancel();
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();
            if (packet.getAction() != Action.ATTACK) return;

            // if we are holding an illegal, do not send packet to server
            if (isHoldingIllegal()) event.cancel();
        }
    }

    /**
     * Checks if we are holding an "illegal" item
     * @return if the item is an infinite or a 32k (if option is checked)
     */
    private boolean isHoldingIllegal() {
        ItemStack stack = mc.thePlayer.getHeldItem();
        return stack != null && (isInfinite(stack) || (max.getValue() && is32k(stack)));
    }
}
