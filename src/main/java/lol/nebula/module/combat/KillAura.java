package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.MathUtils;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.Comparator;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class KillAura extends Module {
    private final Setting<Mode> mode = new Setting<>(Mode.SINGLE, "Mode");
    private final Setting<Priority> priority = new Setting<>(Priority.DISTANCE, "Priority");

    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    private final Setting<Double> range = new Setting<>(4.5, 0.01, 1.0, 6.0, "Range");
    private final Setting<Double> wallRange = new Setting<>(2.0, 0.01, 1.0, 6.0, "Wall Range");

    private final Setting<Integer> minCPS = new Setting<>(8, 0, 20, "Min CPS");
    private final Setting<Integer> maxCPS = new Setting<>(14, 0, 20, "Max CPS");

    private final Setting<Boolean> autoBlock = new Setting<>(true, "Auto Block");

    private final Timer timer = new Timer();
    private EntityLivingBase target;
    private boolean blocking;

    public KillAura() {
        super("Kill Aura", "Attacks entities around you", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;

        if (blocking) {
            blocking = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    5, 0, 0, 0, 255));
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        if (!isValidEntity(target) || mode.getValue() == Mode.SWITCH) {
            target = (EntityLivingBase) mc.theWorld.loadedEntityList
                    .stream()
                    .filter((e) -> e != null && e instanceof EntityLivingBase && isValidEntity((EntityLivingBase) e))
                    .min(Comparator.comparingDouble((x) -> {
                        double compared = 0.0;
                        switch (priority.getValue()) {
                            case DISTANCE:
                                compared = mc.thePlayer.getDistanceSqToEntity((Entity) x);
                                break;

                            case HEALTH:
                                compared = ((EntityLivingBase) x).getHealth();
                                break;

                            case ARMOR:
                                compared = ((EntityLivingBase) x).getTotalArmorValue();
                                break;
                        }
                        return compared;
                    }))
                    .orElse(null);
        }

        if (target == null) {

            if (autoBlock.getValue() && blocking) {
                blocking = false;
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        5, 0, 0, 0, 255));
            }

            return;
        }

        if (rotate.getValue()) {
            RotationUtils.setRotations(event, RotationUtils.rotateTo(target));
        }

        if (autoBlock.getValue()) {
            if (blocking) {
                mc.playerController.sendUseItemClient(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            }
        } else {
            if (blocking) {
                blocking = false;
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        5, 0, 0, 0, 255));
            }
        }

        if (event.getStage() == EventStage.PRE) {

            if (timer.ms((long) getAttackDelay(), false)) {

                if (autoBlock.getValue() && blocking) {
                    blocking = false;
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                            5, 0, 0, 0, 255));
                }

                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        } else {
            ItemStack stack = mc.thePlayer.getHeldItem();
            if (!blocking && (stack != null && stack.getItem() instanceof ItemSword)) {
                blocking = true;
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                        -1, -1, -1, 255, stack, 0.0F, 0.0F, 0.0F));
            }
        }
    }

    private boolean isValidEntity(EntityLivingBase target) {

        if (target == null || target.isDead || target.getHealth() <= 0.0f || target.equals(mc.thePlayer)) {
            return false;
        }

        double distance = mc.thePlayer.getDistanceSqToEntity(target);
        if (!mc.thePlayer.canEntityBeSeen(target) && distance > wallRange.getValue() * wallRange.getValue()) {
            return false;
        }

        return distance <= range.getValue() * range.getValue();
    }

    private double getAttackDelay() {
        double min = minCPS.getValue() * MathUtils.random(0.0, 1.0);
        double max = maxCPS.getValue() * MathUtils.random(0.0, 1.0);
        return 1000.0 / (max / min * MathUtils.random(min, max));
    }

    public boolean isBlocking() {
        return blocking;
    }

    public enum Mode {
        SINGLE, SWITCH
    }

    public enum Priority {
        DISTANCE, HEALTH, ARMOR
    }
}
