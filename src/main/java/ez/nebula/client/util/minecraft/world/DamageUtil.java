package ez.nebula.client.util.minecraft.world;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;

public final class DamageUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static float getExplosionDamage(final EntityPlayer entity, final double x, double y, double z, float size, float strength)
    {
        if (y < 0)
        {
            return 0.0f;
        }

        final double distanceScaled = entity.getDistance(x, y, z) / size;
        if (distanceScaled > 1.0)
        {
            return 0.0f;
        }

        final double v = (1.0 - distanceScaled) * entity.worldObj.getBlockDensity(
                Vec3.createVectorHelper(x + 0.5, y + 0.5, z + 0.5),
                entity.boundingBox.copy());

        float damage = getDamageAfterAbsorb(
                getDmgMultiplier((float) ((v * v + v) / 2.0 * 8.0 * size + 1.0)),
                entity.getTotalArmorValue(),
                (float) entity.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
                        .getAttribute().getDefaultValue());

        final Explosion explosion = new Explosion(entity.worldObj,
                entity,
                x + 0.5, y + 0.5, z + 0.5,
                strength);
        explosion.isFlaming = true;
        explosion.isSmoking = true;

        final DamageSource damageSource = DamageSource.setExplosionSource(explosion);
        final int modifier = EnchantmentHelper.getEnchantmentModifierDamage(
                entity.inventory.armorInventory, damageSource);
        damage = getDamageAfterMagicAbsorb(damage, modifier);
        damage = (float) EnchantmentProtection.func_92092_a(MC.thePlayer, damage);

        if (entity.isPotionActive(Potion.resistance.id))
        {
            final int amp = MC.thePlayer.getActivePotionEffect(Potion.resistance).getAmplifier();
            damage = damage * (25.0f - (amp + 1.0f) * 5.0f) / 25.0f;
        }
        return (float) Math.max(0.0, damage);
    }

    private static float getDamageAfterAbsorb(final float damage, final float armor, final float toughness)
    {
        float f = 2.0F + toughness / 4.0f;
        float f1 = MathHelper.clamp_float(armor - damage / f, armor * 0.2f, 20.0f);
        return damage * (1.0f - f1 / 25.0f);
    }

    private static float getDamageAfterMagicAbsorb(final float damage, final int enchantModifiers)
    {
        return damage * (1.0f - (float) MathHelper.clamp_int(enchantModifiers, 0, 20) / 25.0f);
    }

    private static float getDmgMultiplier(final float damage)
    {
        switch (MC.theWorld.difficultySetting)
        {
            case EASY:
            {
                return Math.min(damage / 2.0f + 1.0f, damage);
            }
            case HARD:
            {
                return damage * 3.0f / 2.0f;
            }
            default:
            {
                return damage;
            }
        }
    }

}
