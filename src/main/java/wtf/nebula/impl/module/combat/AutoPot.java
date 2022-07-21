package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.Timer;

import java.util.List;

public class AutoPot extends Module {
    public AutoPot() {
        super("AutoPot", ModuleCategory.COMBAT);
    }

    public final Value<Integer> delay = new Value<>("Delay", 10, 2, 20);
    public final Value<Boolean> speed = new Value<>("Speed", true);
    public final Value<Boolean> strength = new Value<>("Strength", true);
    public final Value<Rotate> rotate = new Value<>("Rotate", Rotate.DOWN);

    private final Timer timer = new Timer();
    private int potSlot = -1;
    private int time = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        potSlot = -1;
        time = 0;

        mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(mc.thePlayer.inventory.currentItem));
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (potSlot == -1) {
            if (!timer.passedTime(delay.getValue().longValue() * 50L, false)) {
                return;
            }

            time = 0;
            timer.resetTime();

            int neededPotion = -1;

            if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                neededPotion = Potion.moveSpeed.id;
            } else if (!mc.thePlayer.isPotionActive(Potion.damageBoost)) {
                neededPotion = Potion.damageBoost.id;
            }

            if (neededPotion == -1) {
                return;
            }

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || !(stack.getItem() instanceof ItemPotion)) {
                    continue;
                }

                if (!ItemPotion.isSplash(stack.getItemDamage())) {
                    continue;
                }

                ItemPotion potion = (ItemPotion) stack.getItem();

                List<PotionEffect> effects = potion.getEffects(stack);
                if (effects.isEmpty()) {
                    return;
                }

                int finalNeededPotion = neededPotion;
                if (effects.stream().anyMatch((effect) -> effect.getPotionID() == finalNeededPotion)) {
                    potSlot = i;
                }
            }
        }

        else {
            switch (time) {
                case 0:
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(potSlot));
                    break;

                case 1:
                    mc.thePlayer.swingItem();
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet15Place(-1, -1, -1, 255, mc.thePlayer.inventory.getStackInSlot(potSlot), 0.0f, 0.0f, 0.0f));
                    break;

                case 2:
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet16BlockItemSwitch(mc.thePlayer.inventory.currentItem));
                    break;

                case 3:
                    potSlot = -1;
                    time = 0;

                    return;

            }

            ++time;
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying) {
            Packet10Flying packet = event.getPacket();

            if (!rotate.getValue().equals(Rotate.NONE) && potSlot != -1) {

                event.setCancelled(true);

                float pitch = rotate.getValue().pitch;

                if (packet.moving) {
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet13PlayerLookMove(packet.xPosition, packet.yPosition, packet.stance, packet.zPosition, packet.yaw, pitch, packet.onGround));
                } else {
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet12PlayerLook(mc.thePlayer.rotationYaw, pitch, packet.onGround));
                }
            }
        }
    }

    public enum Rotate {
        NONE(-1.0f), UP(-90.0f), DOWN(90.0f);

        private final float pitch;

        Rotate(float pitch) {
            this.pitch = pitch;
        }
    }
}
