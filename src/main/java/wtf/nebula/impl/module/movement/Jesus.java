package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.BlockUtil;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", ModuleCategory.MOVEMENT);
        setBind(Keyboard.KEY_J);
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
        if (event.getPacket() instanceof Packet11PlayerPosition) {

            Packet11PlayerPosition packet = event.getPacket();

            // prevent phase checks
            if (isAboveLiquid() && overLiquidTicks >= 4) {

                // spoof ground state
                packet.onGround = false;

                // spoof downwards
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    packet.yPosition -= 0.05;
                }
            }
        }
    }

    private boolean isAboveLiquid() {
        Vec3 pos = mc.thePlayer.getPosition(1.0f);
        pos.xCoord -= 2.0;

        Block block = BlockUtil.getBlockFromVec(pos);
        if (!lava.getValue() && (block == Block.lavaMoving || block == Block.lavaStill)) {
            return false;
        }

        return block instanceof BlockFluid;
    }
}
