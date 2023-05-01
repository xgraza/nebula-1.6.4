package lol.nebula.module.player;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.input.EventMouseInput;
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
    public void onMiddleClick(EventMouseInput event) {

        // if the button is not the middle button, return
        if (event.getButton() != 2) return;

        MovingObjectPosition result = mc.objectMouseOver;
        if (result == null) return;

        if (result.typeOfHit == MovingObjectType.ENTITY) {

            // if we do not friend or unfriend, return
            if (!friend.getValue() || !unfriend.getValue()) return;

            // if the player we have our crosshair over is not a player, return
            if (!(result.entityHit instanceof EntityPlayer)) return;
            EntityPlayer player = (EntityPlayer) result.entityHit;

            if (Nebula.getInstance().getFriends().isFriend(player)) {
                // if the player is a friend but we do not want to unfriend, return
                if (!unfriend.getValue()) return;

                // remove friend
                Nebula.getInstance().getFriends().removeFriend(player);
            } else {
                // if the player is not a friend but we do not to add friends, return
                if (!friend.getValue()) return;

                // add friend
                Nebula.getInstance().getFriends().addFriend(player);
            }
        } else if (result.typeOfHit == MovingObjectType.MISS) {

            // if we do not want to pearl on middle click, return
            if (!pearl.getValue()) return;

            // find pearl slot
            int slot = -1;
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || stack.getItem() == Items.ender_pearl) slot = i;
            }

            // if the slot is -1, return
            if (slot == -1) return;

            // swap to slot, send C08 and swap back
            Nebula.getInstance().getInventory().setSlot(slot);
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    -1, -1, -1, 255,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    0.0F, 0.0F, 0.0F));
            Nebula.getInstance().getInventory().sync();

        }
    }
}
