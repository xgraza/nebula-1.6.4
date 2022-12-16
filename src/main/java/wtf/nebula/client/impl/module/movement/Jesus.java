package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.world.EventAddBox;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;
import wtf.nebula.client.utils.player.PlayerUtils;

public class Jesus extends ToggleableModule {
    private static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.99, 1);

    private final Property<Mode> mode = new Property<>(Mode.SOLID, "Mode", "m", "type");
    private final Property<Boolean> lava = new Property<>(true, "Lava");
    private final Property<Boolean> dip = new Property<>(true, "Dip", "nodamage", "nodmg");

    private int tickTimer = 0;
    private boolean jumped = false;

    public Jesus() {
        super("Jesus", new String[]{"jesus", "waterwalk"}, ModuleCategory.MOVEMENT);
        offerProperties(mode, lava, dip);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        tickTimer = 0;

        if (jumped && mode.getValue().equals(Mode.DOLPHIN)) {
            mc.gameSettings.keyBindJump.pressed = false;
        }

        jumped = false;
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @EventListener
    public void onTick(EventTick event) {

        if (mode.getValue().equals(Mode.SOLID)) {

            if (mc.thePlayer.isSneaking()) {
                return;
            }
            if (isInLiquid()) {

                mc.thePlayer.motionY = 0.12;
                tickTimer = 0;
                return;
            }

            if (tickTimer == 0) {
                mc.thePlayer.motionY = 0.33;
            } else if (tickTimer == 1) {
                mc.thePlayer.motionY = 0.0;
            }

            ++tickTimer;
        } else if (mode.getValue().equals(Mode.TRAMPOLINE)) {

            if (mc.thePlayer.isSneaking()) {
                jumped = false;
                return;
            }

            if (!isInLiquid()) {
                if (mc.thePlayer.motionY < -0.3 || mc.thePlayer.onGround || mc.thePlayer.isOnLadder()) {
                    jumped = false;

                } else {

                    if (jumped) {
                        mc.thePlayer.motionY = mc.thePlayer.motionY / 0.9800000190734863 + (0.073 + (Math.random() / 1000.0)); // + 0.08
                        mc.thePlayer.motionY -= 0.03120000000005;
                    }
                }

            }

            if (isInLiquid()) {
                mc.thePlayer.motionY = 0.1;
                return;
            }

            if (PlayerUtils.isOverLiquid() && mc.thePlayer.motionY < 0.2) {

                ++tickTimer;
                if (tickTimer >= 2) {
                    tickTimer = 0;

                    mc.thePlayer.motionY = 0.41999998688697815;
                    jumped = true;
                }
            }

        } else if (mode.getValue().equals(Mode.DOLPHIN)) {
            if (mc.thePlayer.isInWater()) {
                jumped = true;
                mc.gameSettings.keyBindJump.pressed = true;
            } else {

                if (jumped) {
                    mc.gameSettings.keyBindJump.pressed = false;
                    jumped = false;
                }
            }
        }
    }

    @EventListener
    public void onAddBoundingBox(EventAddBox event) {
        if (isNull() || !mode.getValue().equals(Mode.SOLID)) {
            return;
        }

        if (event.getBlock() instanceof BlockLiquid) {

            if (!mc.thePlayer.equals(event.getEntity())) {
                return;
            }

            if ((event.getBlock().equals(Blocks.lava) || event.getBlock().equals(Blocks.flowing_lava)) && !lava.getValue()) {
                return;
            }

            if (dip.getValue() && (mc.thePlayer.isBurning() || mc.thePlayer.fallDistance > 3.0f)) {

                if (!event.getBlock().equals(Blocks.lava) && !event.getBlock().equals(Blocks.flowing_lava)) {
                    return;
                }

            }

            // don't trap ourselves lol
            if (isInLiquid() || mc.thePlayer.isSneaking()) {
                return;
            }

            event.setBox(FULL_AABB.copy().offset(event.getX(), event.getY(), event.getZ()));
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        if (!mode.getValue().equals(Mode.SOLID)) {
            return;
        }

        if (event.getEra().equals(Era.PRE) && PlayerUtils.isOverLiquid() && !isInLiquid() && !mc.thePlayer.isSneaking() && !mc.gameSettings.keyBindJump.pressed) {
            event.onGround = false;

            if (mc.thePlayer.ticksExisted % 2 == 0) {
                event.y += 0.02;
            }
        }
    }

    private boolean isInLiquid() {
        return mc.thePlayer.isInWater();
    }

    public enum Mode {
        SOLID, TRAMPOLINE, DOLPHIN
    }
}
