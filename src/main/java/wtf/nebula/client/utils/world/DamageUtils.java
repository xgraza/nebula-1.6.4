package wtf.nebula.client.utils.world;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldSettings;
import wtf.nebula.client.utils.client.Wrapper;
import wtf.nebula.client.utils.player.PlayerUtils;

public class DamageUtils implements Wrapper {

    public static float calcDamage(EntityLivingBase target, Vec3 vec, float size) {
        if (target instanceof EntityPlayer && PlayerUtils.isUnkillable((EntityPlayer) target)) {
            return 0.0f;
        }

        double explosionSize = size * 2.0;

        double dist = target.getDistance(vec.xCoord, vec.yCoord, vec.zCoord) / explosionSize;
        if (dist > 1.0) {
            return 0.0f;
        }

        double v = (1.0 - dist) * target.worldObj.getBlockDensity(vec, target.boundingBox.copy());

        float roughDamage = (float) ((v * v + v) / 2.0 * 7.0 * explosionSize + 1.0);
        float damage = getDamageAfterAbsorb(getDamageMultiplierOnDiff(roughDamage), target.getTotalArmorValue(), (float) target.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttribute().getDefaultValue());

        DamageSource src = DamageSource.setExplosionSource(new Explosion(target.worldObj, target, vec.xCoord, vec.yCoord, vec.zCoord, size));

        if (target instanceof EntityPlayer) {
            int n = EnchantmentHelper.getEnchantmentModifierDamage(((EntityPlayer) target).inventory.armorInventory, src);
            damage = getDamageAfterMagicAbsorb(damage, (float) n);
        }

        if (target.isPotionActive(Potion.resistance.id)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.resistance).getAmplifier();
            damage = damage * (25.0f - (amp + 1) * 5) / 25.0f;
        }

        return (float) Math.max(0.0, damage);
    }

    public static float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute) {
        float f = 2.0F + toughnessAttribute / 4.0F;
        float f1 = MathHelper.clamp_float(totalArmor - damage / f, totalArmor * 0.2F, 20.0F);
        return damage * (1.0F - f1 / 25.0F);
    }

    public static float getDamageAfterMagicAbsorb(float damage, float enchantModifiers) {
        float f = MathHelper.clamp_float(enchantModifiers, 0.0F, 20.0F);
        return damage * (1.0F - f / 25.0F);
    }

    public static float getDamageMultiplierOnDiff(float damage) {
        switch (mc.theWorld.difficultySetting) {
            case PEACEFUL:
                return 0.0f;

            case EASY:
                return Math.min(damage / 2.0f + 1.0f, damage);

            case HARD:
                return damage * 3.0f / 2.0f;
        }

        return damage;
    }
}
