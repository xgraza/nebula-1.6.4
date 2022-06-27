package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.MotionUpdateEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT);
        setBind(Keyboard.KEY_K);
    }

    public final Value<Double> range = new Value<>("Range", 4.0, 1.0, 6.0);
    public final Value<Boolean> walls = new Value<>("Walls", true);

    public final Value<Integer> minCps = new Value<>("MinCPS", 12, 1, 20);
    public final Value<Integer> maxCps = new Value<>("MaxCPS", 16, 1, 20);

    public final Value<Boolean> autoBlock = new Value<>("AutoBlock", true);

    private long lastAttack = 0L;
    private boolean blocking = false;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (!nullCheck() && blocking) {
            blocking = false;
            mc.thePlayer.stopUsingItem();
        }
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        EntityLivingBase target = null;
        double r = 0.0;

        for (Object obj : new ArrayList<>(mc.theWorld.loadedEntityList)) {
            if (!(obj instanceof EntityLivingBase)) {
                continue;
            }

            EntityLivingBase base = (EntityLivingBase) obj;

            if (base.equals(mc.thePlayer) || base.isDead || base.getHealth() <= 0.0f) {
                continue;
            }

            if (!mc.thePlayer.canEntityBeSeen(base) && !walls.getValue()) {
                continue;
            }

            double entityRange = mc.thePlayer.getDistanceSqToEntity(base);
            if (entityRange > range.getValue() * range.getValue()) {
                continue;
            }

            if (r == 0.0 || r > entityRange) {
                r = entityRange;
                target = base;
            }
        }

        if (target == null) {
            return;
        }

        boolean holdingSword = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        if (holdingSword && autoBlock.getValue()) {
            if (event.getEra().equals(Era.PRE)) {
                blocking = false;
                mc.thePlayer.stopUsingItem();
            }

            else {
                blocking = true;
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            }
        }

        if (System.currentTimeMillis() - lastAttack >= 1000L / randomInBounds(minCps.getValue(), maxCps.getValue())) {
            lastAttack = System.currentTimeMillis();

            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, target);
        }
    }

    private int randomInBounds(int min, int max) {
        return ThreadLocalRandom.current().nextInt((max + 1) - min) + min;
    }
}
