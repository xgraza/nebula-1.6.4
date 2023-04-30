package lol.nebula.listener.events.entity;

import net.minecraft.entity.EntityLivingBase;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class EventArmSwingSpeed {
    private final EntityLivingBase entity;
    private int swingSpeed;

    public EventArmSwingSpeed(EntityLivingBase entity, int swingSpeed) {
        this.entity = entity;
        this.swingSpeed = swingSpeed;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public int getSwingSpeed() {
        return swingSpeed;
    }

    public void setSwingSpeed(int swingSpeed) {
        this.swingSpeed = swingSpeed;
    }
}
