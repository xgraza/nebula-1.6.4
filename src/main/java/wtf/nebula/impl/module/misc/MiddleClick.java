package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import wtf.nebula.event.MiddleClickMouseEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.player.inventory.InventoryRegion;
import wtf.nebula.util.world.player.inventory.InventoryUtil;

public class MiddleClick extends Module {
    public MiddleClick() {
        super("MiddleClick", ModuleCategory.MISC);
    }

    public final Value<Boolean> friend = new Value<>("Friend", true);
    public final Value<Boolean> pearl = new Value<>("Pearl", true);

    @EventListener
    public void onMiddleClickMouse(MiddleClickMouseEvent event) {

        MovingObjectPosition result = event.getObjectMouseOver();

        if (result == null) {

            if (pearl.getValue()) {
                int slot = InventoryUtil.findSlot(InventoryRegion.HOTBAR,
                        (stack) -> stack.getItem() != null && stack.getItem().equals(Item.enderPearl));

                if (slot != -1) {
                    int oldSlot = mc.thePlayer.inventory.currentItem;

                    mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(slot));
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet15Place(-1, -1, -1, 255, mc.thePlayer.inventory.getStackInSlot(slot), 0.0f, 0.0f, 0.0f));
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(oldSlot));
                }
            }
        }

        else {

            if (result.typeOfHit.equals(EnumMovingObjectType.ENTITY)) {
                Entity entity = result.entityHit;
                if (!(entity instanceof EntityPlayer)) {
                    return;
                }

                if (friend.getValue()) {
                    EntityPlayer player = (EntityPlayer) entity;

                    // TODO: friend manager
                }
            }
        }
    }
}
