package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.BlockUtil;
import wtf.nebula.util.world.player.MotionUtil;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", ModuleCategory.MOVEMENT);
    }

    public final Value<Boolean> lava = new Value<>("Lava", true);

    private int overLiquidTicks = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        overLiquidTicks = 0;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (isAboveLiquid()) {
            ++overLiquidTicks;
        }

        else {
            overLiquidTicks = 0;
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        // if this is a move packet
        if (event.getPacket() instanceof Packet10Flying && event.getEra().equals(Era.PRE)) {

            // the movement packet
            Packet10Flying packet = event.getPacket();

            // prevent phase checks
            if (isAboveLiquid() && overLiquidTicks >= 5) {

                // spoof ground state
                packet.onGround = true;

                // spoof downwards
                if (overLiquidTicks % 2 == 0) {

                    // spoof falling down
                    packet.stance -= 0.01;
                    packet.yPosition -= 0.01;

                }

                else {

                    if (MotionUtil.isMoving() && packet.moving) {

                        // spoof floating up (pressing space bar)
                        packet.stance += 0.01;
                        packet.yPosition += 0.01;
                    }
                }
            }
        }
    }

    private boolean isAboveLiquid() {
        if (mc.thePlayer.isInWater()) {
            return false;
        }

        Vec3 pos = Vec3.createVectorHelper(
                Math.floor(mc.thePlayer.posX),
                mc.thePlayer.boundingBox.minY - 1.0,
                Math.floor(mc.thePlayer.posZ));

        Block block = BlockUtil.getBlockFromVec(pos);
        if (!lava.getValue() && (block == Block.lavaMoving || block == Block.lavaStill)) {
            return false;
        }

        return block instanceof BlockFluid;
    }
}
