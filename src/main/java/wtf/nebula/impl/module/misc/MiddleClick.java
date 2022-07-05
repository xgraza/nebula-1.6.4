package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import wtf.nebula.event.MiddleClickMouseEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.FriendRepository;
import wtf.nebula.util.world.player.inventory.InventoryRegion;
import wtf.nebula.util.world.player.inventory.InventoryUtil;

public class MiddleClick extends Module {
    public MiddleClick() {
        super("MiddleClick", ModuleCategory.MISC);
    }

    public final Value<Boolean> friend = new Value<>("Friend", true);
    public final Value<Boolean> pearl = new Value<>("Pearl", true);

    private int pearlTicks = -1;
    private int oldSlot = -1, slot = -1;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        pearlTicks = -1;
        oldSlot = -1;
        slot = -1;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (pearlTicks != -1) {
            switch (pearlTicks) {
                case 1:
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(slot));
                    break;

                case 2:
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet15Place(-1, -1, -1, 255, mc.thePlayer.inventory.getStackInSlot(slot), 0.0f, 0.0f, 0.0f));
                    break;

                case 3:
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(oldSlot));
                    break;

                case 4:
                    pearlTicks = -1;
                    oldSlot = -1;
                    slot = -1;
                    return;
            }

            ++pearlTicks;
        }
    }

    @EventListener
    public void onMiddleClickMouse(MiddleClickMouseEvent event) {

        MovingObjectPosition result = event.getObjectMouseOver();

        if (result == null) {

            if (pearl.getValue()) {
                slot = InventoryUtil.findSlot(InventoryRegion.HOTBAR, (stack) -> stack.getItem().equals(Item.enderPearl));

                if (slot != -1) {
                    pearlTicks = 1;
                    oldSlot = mc.thePlayer.inventory.currentItem;
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
                    String username = result.entityHit.getEntityName();

                    if (FriendRepository.get().isFriend(username)) {
                        FriendRepository.get().removeChild(username);
                        sendChatMessage("Removed " + EnumChatFormatting.GREEN + username + EnumChatFormatting.RESET + " from your friends list");
                    }

                    else {
                        FriendRepository.get().addChild(username);
                        sendChatMessage("Added " + EnumChatFormatting.GREEN + username + EnumChatFormatting.RESET + " to your friends list");

                        // we should notify this player they were added
                        mc.thePlayer.sendQueue.addToSendQueue(new Packet3Chat("/msg " + username + " I just added you as a friend on Nebula!"));
                    }
                }
            }
        }
    }
}
