package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet13PlayerLookMove;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.player.MotionUtil;

public class FastSwim extends Module {
    private static final double CONCEAL = 1.0 / StrictMath.sqrt(2);

    public FastSwim() {
        super("FastSwim", ModuleCategory.MOVEMENT);
    }

    public final Value<Double> modifier = new Value<>("Modifier", 0.42, 0.1, 2.0);
    public final Value<Double> vSpeed = new Value<>("VSpeed", 0.11, 0.1, 2.0);

    public final Value<Boolean> whileUpDown = new Value<>("WhileUpDown", false);
    public final Value<Boolean> strict = new Value<>("Strict", true);

    private int lagTime = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        lagTime = 0;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.thePlayer.isInWater()) {
            --lagTime;
            if (lagTime > 0) {
                return;
            }

            if (!whileUpDown.getValue() && (mc.gameSettings.keyBindJump.pressed || mc.gameSettings.keyBindSneak.pressed)) {
                return;
            }

            double moveSpeed = 0.272 * modifier.getValue();

            if (strict.getValue() && mc.thePlayer.ticksExisted % 4 == 0) {
                moveSpeed -= 0.0624; //0.0544;
            }

            if ((mc.gameSettings.keyBindJump.pressed || mc.gameSettings.keyBindSneak.pressed) && strict.getValue()) {
                moveSpeed *= CONCEAL;
            }

            double[] strafe = MotionUtil.strafe(moveSpeed);

            mc.thePlayer.motionX = strafe[0];
            mc.thePlayer.motionZ = strafe[1];

            if (!strict.getValue()) {
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.motionY = vSpeed.getValue();
                } else if (mc.gameSettings.keyBindSneak.pressed) {
                    mc.thePlayer.motionY = -vSpeed.getValue();
                }
            }
        }
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof Packet13PlayerLookMove) {
            lagTime = 20;
        }
    }
}
