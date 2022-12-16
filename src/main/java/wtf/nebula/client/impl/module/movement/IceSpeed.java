package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.move.EventMotion;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.world.WorldUtils;

public class IceSpeed extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.VANILLA, "Mode", "m", "type");

    private final Property<Double> speed = new Property<>(0.4, 0.1, 2.5, "Speed", "s", "mult");
    private final Property<Boolean> potionFactor = new Property<>(true, "Potion Factor", "pfactor")
            .setVisibility(() -> mode.getValue().equals(Mode.MOTION));

    public IceSpeed() {
        super("Ice Speed", new String[]{"icespeed", "fastice"}, ModuleCategory.MOVEMENT);
        offerProperties(mode, speed, potionFactor /*, jumpBoost */);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        Blocks.ice.slipperiness = 0.98f;
        Blocks.packed_ice.slipperiness = 0.98f;
    }

    @EventListener
    public void onMotion(EventMotion event) {
        if (mode.getValue().equals(Mode.VANILLA)) {
            Blocks.ice.slipperiness = speed.getValue().floatValue();
            Blocks.packed_ice.slipperiness = speed.getValue().floatValue();
        } else {
            Blocks.ice.slipperiness = 0.98f;
            Blocks.packed_ice.slipperiness = 0.98f;

            Block under = WorldUtils.getBlock(PlayerUtils.getPosUnder());
            if (under != null && (under.equals(Blocks.ice) || under.equals(Blocks.packed_ice))) {
                double s = speed.getValue();

                if (potionFactor.getValue()) {
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
                        int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                        s *= 1.0 + (0.2 * (amp + 1));
                    }

                    if (mc.thePlayer.isPotionActive(Potion.moveSlowdown.id)) {
                        int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier();
                        s /= 1.0 + (0.2 * (amp + 1));
                    }
                }

                double[] strafe = MoveUtils.calcStrafe(s);

                if (MoveUtils.isMoving()) {
                    event.x = strafe[0];
                    event.z = strafe[1];
                } else {
                    event.x = 0.0;
                    event.z = 0.0;
                }
            }
        }
    }

    public enum Mode {
        VANILLA, MOTION
    }
}
