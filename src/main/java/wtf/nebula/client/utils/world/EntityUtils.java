package wtf.nebula.client.utils.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import wtf.nebula.client.utils.client.Wrapper;

public class EntityUtils implements Wrapper {
    public static boolean isMob(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return false;
        }

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;

            if (livingBase.getCreatureAttribute().equals(EnumCreatureAttribute.UNDEAD)) {
                return true;
            }

            if (livingBase instanceof EntitySlime && ((EntitySlime) livingBase).getSlimeSize() > 1) {
                return true;
            }

            if (livingBase instanceof EntityMob) {
                return true;
            }

            return mc.thePlayer.equals(livingBase.entityLivingToAttack);
        }

        return false;
    }

    public static boolean isPassive(Entity entity) {
        if (isMob(entity)) {
            return false;
        }

        if (entity instanceof EntityPlayer) {
            return false;
        }

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase base = (EntityLivingBase) entity;

            if (base instanceof EntityAmbientCreature || base instanceof EntityWaterMob) {
                return true;
            }

            return base.getCreatureAttribute().equals(EnumCreatureAttribute.UNDEFINED);
        }

        // assume passive
        return true;
    }
}
