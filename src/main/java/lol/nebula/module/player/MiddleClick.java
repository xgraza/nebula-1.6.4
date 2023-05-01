package lol.nebula.module.player;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.input.EventMiddleClick;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class MiddleClick extends Module {
    private final Setting<Boolean> friend = new Setting<>(true, "Friend");
    private final Setting<Boolean> unfriend = new Setting<>(true, "Unfriend");
    private final Setting<Boolean> pearl = new Setting<>(true, "Pearl");

    public MiddleClick() {
        super("Middle Click", "Does things on a middle click", ModuleCategory.PLAYER);
    }

    @Listener
    public void onMiddleClick(EventMiddleClick event) {
        MovingObjectPosition result = mc.objectMouseOver;
        if (result == null) return;

        if (result.typeOfHit == MovingObjectType.ENTITY) {
            if (!friend.getValue() || !unfriend.getValue()) return;

            if (!(result.entityHit instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) result.entityHit;

            if (Nebula.getInstance().getFriends().isFriend(player)) {
                if (!unfriend.getValue()) return;
                Nebula.getInstance().getFriends().removeFriend(player);
            } else {
                if (!friend.getValue()) return;
                Nebula.getInstance().getFriends().addFriend(player);
            }
        } else if (result.typeOfHit == MovingObjectType.MISS) {
            if (!pearl.getValue()) return;

            int slot = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || stack.getItem() == Items.ender_pearl) slot = i;
            }

            if (slot == -1) return;

            Nebula.getInstance().getInventory().setSlot(slot);
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    -1, -1, -1, 255,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    0.0F, 0.0F, 0.0F));
            Nebula.getInstance().getInventory().sync();

        }
    }
}
