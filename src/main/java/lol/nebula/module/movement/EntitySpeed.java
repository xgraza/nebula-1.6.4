package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventEntityRidingUpdate;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class EntitySpeed extends Module {

    private final Setting<Float> multiplier = new Setting<>(1.0f, 0.01f, 1.0f, 20.0f, "Multiplier");
    private final Setting<Boolean> boats = new Setting<>(true, "Boats");
    private final Setting<Boolean> minecarts = new Setting<>(true, "Minecarts");
    private final Setting<Boolean> horses = new Setting<>(true, "Horses");

    private boolean usingTimer;

    public EntitySpeed() {
        super("Entity Speed", "Makes riding entities go faster", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (usingTimer) mc.timer.timerSpeed = 1.0f;
        usingTimer = false;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (!mc.thePlayer.isRiding() && usingTimer) {
            usingTimer = false;
            mc.timer.timerSpeed = 1.0f;
        }
    }

    @Listener
    public void onEntityRidingUpdate(EventEntityRidingUpdate event) {
        Entity riding = event.getRidingEntity();

        if ((!boats.getValue() && riding instanceof EntityBoat)
                || (!minecarts.getValue() && riding instanceof EntityMinecart)
                || (!horses.getValue() && riding instanceof EntityHorse)) return;

        usingTimer = true;
        mc.timer.timerSpeed = multiplier.getValue();
    }

}
