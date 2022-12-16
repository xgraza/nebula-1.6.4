package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.MovingObjectPosition;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.input.EventMiddleClick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class MiddleClick extends ToggleableModule {
    private final Property<Boolean> pearl = new Property<>(true, "Pearl", "endpearl", "enderpearl");
    private final Property<Boolean> friend = new Property<>(true, "Friend", "friendadd");

    public MiddleClick() {
        super("Middle Click", new String[]{"middleclick", "midclick", "mcf", "mcp"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(pearl, friend);

        setDrawn(false);
        setRunning(true); // default running
    }

    @EventListener
    public void onMiddleClick(EventMiddleClick event) {

        MovingObjectPosition result = event.getResult();
        if (result == null) {
            return;
        }

        switch (result.typeOfHit) {
            case ENTITY: {
                if (friend.getValue()) {
                    Entity e = result.entityHit;
                    if (e == null || !(e instanceof EntityPlayer)) {
                        return;
                    }

                    EntityPlayer player = (EntityPlayer) e;
                    if (Nebula.getInstance().getFriendManager().isFriend(player)) {
                        Nebula.getInstance().getFriendManager().remove(player);
                    } else {
                        Nebula.getInstance().getFriendManager().add(player);
                    }
                }
                break;
            }

            case MISS: {
                if (pearl.getValue()) {
                    int pearlSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (stack != null && stack.getItem() != null && stack.getItem().equals(Items.ender_pearl)) {
                            pearlSlot = i;
                        }
                    }

                    if (pearlSlot == -1) {
                        return;
                    }

                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(pearlSlot));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, mc.thePlayer.inventory.getStackInSlot(pearlSlot), 0.0F, 0.0F, 0.0F));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                break;
            }

            case BLOCK: {
                break;
            }
        }
    }
}
