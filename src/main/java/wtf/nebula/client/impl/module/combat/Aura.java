package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.ChatComponentText;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.event.impl.move.MotionUpdateEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.MathUtils;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;

import java.util.Comparator;

public class Aura extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.SINGLE, "Mode", "m", "type");
    private final Property<Priority> priority = new Property<>(Priority.DISTANCE, "Priority", "p", "sort", "sorting");

    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");

    private final Property<Double> range = new Property<>(4.0, 1.0, 6.0, "Range", "reach", "dist", "distance");
    private final Property<Double> wallRange = new Property<>(3.0, 1.0, 6.0, "Wall Range", "wallsrange", "wallreach");

    private final Property<Integer> min = new Property<>(8, 1, 20, "Min APS", "minaps");
    private final Property<Integer> max = new Property<>(12, 1, 20, "Max APS", "maxaps");
    private final Property<Integer> deviation = new Property<>(6, 1, 16, "Deviation", "random", "rand");

    private final Property<Boolean> autoBlock = new Property<>(true, "Auto Block", "autoblock");
    private final Property<Boolean> keepSprint = new Property<>(true, "Keep Sprint", "keepsprint");

    private final Timer timer = new Timer();
    private EntityLivingBase target = null;
    private boolean blocking = false;

    public Aura() {
        super("Aura", new String[]{"aura", "killaura", "forcefield"}, ModuleCategory.COMBAT);
        offerProperties(mode, priority, range, wallRange, min, max, deviation, autoBlock, keepSprint);
    }

    @EventListener
    public void onTick(MotionUpdateEvent event) {
        if (!isValidTarget(target) || mode.getValue().equals(Mode.SWITCH)) {
            target = (EntityLivingBase) mc.theWorld.loadedEntityList
                    .stream()
                    .filter((t) -> t instanceof EntityLivingBase && isValidTarget((EntityLivingBase) t))
                    .min(Comparator.comparingDouble((e) -> {
                        double v = 0.0;

                        switch (priority.getValue()) {
                            case HEALTH: {
                                v = ((EntityLivingBase) e).getHealth();
                                break;
                            }

                            case DISTANCE: {
                                v = mc.thePlayer.getDistanceSqToEntity(e);
                                break;
                            }
                            case ANGLE:
                                break;
                        }

                        return v;
                    }))
                    .orElse(null);
        }

        if (target == null) {
            return;
        }

        if (rotate.getValue()) {
            float[] angles = RotationUtils.calcAngles(target);
            Launcher.getInstance().getRotationManager().setRotations(angles);
        }

        if (event.getEra().equals(Era.PRE)) {

            int cps = MathUtils.random(min.getValue(), max.getValue())
                    - MathUtils.random(0, deviation.getValue())
                    + MathUtils.random(0, deviation.getValue());

            if (cps <= 0) {
                cps = MathUtils.random(min.getValue(), max.getValue());
            }

            if (autoBlock.getValue() && blocking && PlayerUtils.isHolding(ItemSword.class)) {
                blocking = false;
                mc.playerController.unblockSilent();
            }

            if (timer.hasPassed(1000L / cps, true)) {
                mc.thePlayer.swingItem();

                if (keepSprint.getValue()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                } else {
                    mc.playerController.attackEntity(mc.thePlayer, target);
                }
            }
        } else {
            if (autoBlock.getValue() && !blocking && PlayerUtils.isHolding(ItemSword.class)) {
                blocking = true;
                mc.playerController.blockSword();
            }
        }
    }

    private boolean isValidTarget(EntityLivingBase e) {
        if (e == null || !e.isEntityAlive() || e.equals(mc.thePlayer)) {
            return false;
        }

        if (e instanceof EntityPigZombie) {
            return false;
        }

        double dist = mc.thePlayer.getDistanceSqToEntity(e);
        double r = mc.thePlayer.canEntityBeSeen(e) ? range.getValue() : wallRange.getValue();

        return !(dist > r * r);
    }

    public enum Mode {
        SINGLE, SWITCH
    }

    public enum Priority {
        DISTANCE, HEALTH, ANGLE
    }
}
