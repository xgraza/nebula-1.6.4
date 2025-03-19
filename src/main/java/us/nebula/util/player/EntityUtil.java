package us.nebula.util.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;

/**
 * @author xgraza
 * @since 03/17/25
 */
public final class EntityUtil
{
    public static boolean isEntityPassive(final Entity entity)
    {
        return !isEntityHostile(entity) &&
                (entity instanceof EntityAmbientCreature
                        || entity instanceof EntityAnimal
                        || entity instanceof EntitySquid
                        || entity instanceof EntityVillager);
    }

    public static boolean isEntityHostile(final Entity entity)
    {
        if (entity instanceof EntityWolf)
        {
            return ((EntityWolf) entity).isAngry();
        }
        return entity instanceof EntityMob
                || entity instanceof EntityDragon
                || entity instanceof EntityDragonPart
                || entity instanceof EntitySlime
                || entity instanceof EntityGhast;
    }
}
