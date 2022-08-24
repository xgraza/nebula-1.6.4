package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import wtf.nebula.event.AddBoundingBoxEvent;
import wtf.nebula.event.CollisionBoxEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.player.PlayerUtil;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", ModuleCategory.MOVEMENT);
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.SOLID);
    public final Value<Boolean> lava = new Value<>("Lava", true);

    private int overLiquidTicks = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        overLiquidTicks = 0;

        if (mode.getValue().equals(Mode.DOLPHIN)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, false);
        }
    }

    @EventListener
    public void onTick(TickEvent event) {

        switch (mode.getValue()) {
            case SOLID:

                if (!PlayerUtil.isOnLiquid(lava.getValue())) {
                    overLiquidTicks = 0;
                }

                else {
                    overLiquidTicks++;
                }

                break;

            case DOLPHIN:

                if (mc.thePlayer.isInWater()) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.keyCode, true);
                }

                break;

            case TRAMPOLINE:

                // TODO
                mode.setValue(Mode.SOLID);

                break;
        }
    }

    @EventListener
    public void onCollisionBox(AddBoundingBoxEvent event) {

        if (mode.getValue().equals(Mode.SOLID) && event.getEntity() != null && event.getEntity().equals(mc.thePlayer)) {
            Block block = event.getBlock();

            if (!(block instanceof BlockLiquid)) {
                return;
            }

            if (!lava.getValue() && (block.equals(Blocks.lava) || block.equals(Blocks.flowing_lava))) {
                return;
            }

            event.setBox(new AxisAlignedBB(0, 0, 0, 1, 0.99, 1)
                    .offset(event.getX(), event.getY(), event.getZ()));
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        // if this is a move packet
        if (event.getPacket() instanceof C03PacketPlayer && event.getEra().equals(Era.PRE)) {

            // the movement packet
            C03PacketPlayer packet = event.getPacket();

            if (mode.getValue().equals(Mode.SOLID)) {

                // prevent phase checks
                if (overLiquidTicks >= 5) {

                    // spoof ground state
                    packet.onGround = true;

                    // spoof downwards
                    if (overLiquidTicks % 2 == 0) {

                        // spoof falling down
                        packet.stance -= 0.01;
                        packet.y -= 0.01;

                    } else {

                        // spoof floating up (pressing space bar)
                        packet.stance += 0.01;
                        packet.y += 0.01;
                    }
                }
            }
        }
    }

    public enum Mode {
        SOLID, DOLPHIN, TRAMPOLINE
    }
}
