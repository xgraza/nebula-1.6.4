package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.ItemBow;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet19EntityAction;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.MathUtil;

// wtf lmao
public class FastProjectile extends Module {
    public FastProjectile() {
        super("FastProjectile", ModuleCategory.COMBAT);
    }

    public final Value<Double> charge = new Value<>("Charge", 10.0, 5.0, 100.0);

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet14BlockDig && event.getEra().equals(Era.PRE)) {

            // we have to be holding a bow
            if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBow)) {
                return;
            }

            Packet14BlockDig packet = event.getPacket();

            if (packet.status != 5) {
                return;
            }

            // funni bipass
            mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 5));

            double cos = Math.cos(mc.thePlayer.rotationYaw) * charge.getValue();
            double sin = -Math.sin(mc.thePlayer.rotationYaw) * charge.getValue();

            if (MathUtil.RNG.nextBoolean()) {
                mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                        mc.thePlayer.posX + cos,
                        mc.thePlayer.boundingBox.minY,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + sin,
                        false
                ));

                mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                        mc.thePlayer.posX - cos,
                        mc.thePlayer.boundingBox.minY,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - sin,
                        false
                ));
            }

            else {
                mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                        mc.thePlayer.posX - cos,
                        mc.thePlayer.boundingBox.minY,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - sin,
                        false
                ));

                mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                        mc.thePlayer.posX + cos,
                        mc.thePlayer.boundingBox.minY,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + sin,
                        false
                ));
            }
        }
    }
}
