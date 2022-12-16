package net.minecraft.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity
{
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
    private BaseAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final HashMap activePotionsMap = new HashMap();
    private final ItemStack[] previousEquipment = new ItemStack[5];
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;
    public float prevHealth;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw;
    public int deathTime;
    public int attackTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float field_70769_ao;
    public float field_70770_ap;
    public float renderYawOffset;
    public float prevRenderYawOffset;
    public float rotationYawHead;
    public float prevRotationYawHead;
    public float rotationPitchHead;
    public float prevRotationPitchHead;
    public float jumpMovementFactor = 0.02F;
    protected EntityPlayer attackingPlayer;
    protected int recentlyHit;
    protected boolean dead;
    public int entityAge;
    protected float field_70768_au;
    protected float field_110154_aX;
    protected float field_70764_aw;
    protected float field_70763_ax;
    protected float field_70741_aB;
    protected int scoreValue;
    protected float lastDamage;
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;
    protected int newPosRotationIncrements;
    protected double newPosX;
    protected double newPosY;
    protected double newPosZ;
    protected double newRotationYaw;
    protected double newRotationPitch;
    private boolean potionsNeedUpdate = true;
    public EntityLivingBase entityLivingToAttack;
    private int revengeTimer;
    private EntityLivingBase lastAttacker;
    private int lastAttackerTime;
    private float landMovementFactor;
    private int jumpTicks;
    private float field_110151_bq;
    private static final String __OBFID = "CL_00001549";

    public EntityLivingBase(World par1World)
    {
        super(par1World);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.field_70770_ap = (float)(Math.random() + 1.0D) * 0.01F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_70769_ao = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * Math.PI * 2.0D);
        this.rotationYawHead = this.rotationYaw;
        this.rotationPitchHead = rotationPitch;
        this.stepHeight = 0.5F;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(7, Integer.valueOf(0));
        this.dataWatcher.addObject(8, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(9, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(6, Float.valueOf(1.0F));
    }

    protected void applyEntityAttributes()
    {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);

        if (!this.isAIEnabled())
        {
            this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.10000000149011612D);
        }
    }

    protected void updateFallState(double par1, boolean par3)
    {
        if (!this.isInWater())
        {
            this.handleWaterMovement();
        }

        if (par3 && this.fallDistance > 0.0F)
        {
            int var4 = MathHelper.floor_double(this.posX);
            int var5 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int var6 = MathHelper.floor_double(this.posZ);
            Block var7 = this.worldObj.getBlock(var4, var5, var6);

            if (var7.getMaterial() == Material.air)
            {
                int var8 = this.worldObj.getBlock(var4, var5 - 1, var6).getRenderType();

                if (var8 == 11 || var8 == 32 || var8 == 21)
                {
                    var7 = this.worldObj.getBlock(var4, var5 - 1, var6);
                }
            }
            else if (!this.worldObj.isClient && this.fallDistance > 3.0F)
            {
                this.worldObj.playAuxSFX(2006, var4, var5, var6, MathHelper.ceiling_float_int(this.fallDistance - 3.0F));
            }

            var7.onFallenUpon(this.worldObj, var4, var5, var6, this, this.fallDistance);
        }

        super.updateFallState(par1, par3);
    }

    public boolean canBreatheUnderwater()
    {
        return false;
    }

    public void onEntityUpdate()
    {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");

        if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock())
        {
            this.attackEntityFrom(DamageSource.inWall, 1.0F);
        }

        if (this.isImmuneToFire() || this.worldObj.isClient)
        {
            this.extinguish();
        }

        boolean var1 = this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.disableDamage;

        if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water))
        {
            if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !var1)
            {
                this.setAir(this.decreaseAirSupply(this.getAir()));

                if (this.getAir() == -20)
                {
                    this.setAir(0);

                    for (int var2 = 0; var2 < 8; ++var2)
                    {
                        float var3 = this.rand.nextFloat() - this.rand.nextFloat();
                        float var4 = this.rand.nextFloat() - this.rand.nextFloat();
                        float var5 = this.rand.nextFloat() - this.rand.nextFloat();
                        this.worldObj.spawnParticle("bubble", this.posX + (double)var3, this.posY + (double)var4, this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
                    }

                    this.attackEntityFrom(DamageSource.drown, 2.0F);
                }
            }

            if (!this.worldObj.isClient && this.isRiding() && this.ridingEntity instanceof EntityLivingBase)
            {
                this.mountEntity((Entity)null);
            }
        }
        else
        {
            this.setAir(300);
        }

        if (this.isEntityAlive() && this.isWet())
        {
            this.extinguish();
        }

        this.prevCameraPitch = this.cameraPitch;

        if (this.attackTime > 0)
        {
            --this.attackTime;
        }

        if (this.hurtTime > 0)
        {
            --this.hurtTime;
        }

        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP))
        {
            --this.hurtResistantTime;
        }

        if (this.getHealth() <= 0.0F)
        {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0)
        {
            --this.recentlyHit;
        }
        else
        {
            this.attackingPlayer = null;
        }

        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive())
        {
            this.lastAttacker = null;
        }

        if (this.entityLivingToAttack != null)
        {
            if (!this.entityLivingToAttack.isEntityAlive())
            {
                this.setRevengeTarget((EntityLivingBase)null);
            }
            else if (this.ticksExisted - this.revengeTimer > 100)
            {
                this.setRevengeTarget((EntityLivingBase)null);
            }
        }

        this.updatePotionEffects();
        this.field_70763_ax = this.field_70764_aw;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationPitchHead = this.rotationPitchHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    public boolean isChild()
    {
        return false;
    }

    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime == 20)
        {
            int var1;

            if (!this.worldObj.isClient && (this.recentlyHit > 0 || this.isPlayer()) && this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                var1 = this.getExperiencePoints(this.attackingPlayer);

                while (var1 > 0)
                {
                    int var2 = EntityXPOrb.getXPSplit(var1);
                    var1 -= var2;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, var2));
                }
            }

            this.setDead();

            for (var1 = 0; var1 < 20; ++var1)
            {
                double var8 = this.rand.nextGaussian() * 0.02D;
                double var4 = this.rand.nextGaussian() * 0.02D;
                double var6 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("explode", this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, var8, var4, var6);
            }
        }
    }

    protected boolean func_146066_aG()
    {
        return !this.isChild();
    }

    protected int decreaseAirSupply(int par1)
    {
        int var2 = EnchantmentHelper.getRespiration(this);
        return var2 > 0 && this.rand.nextInt(var2 + 1) > 0 ? par1 : par1 - 1;
    }

    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        return 0;
    }

    protected boolean isPlayer()
    {
        return false;
    }

    public Random getRNG()
    {
        return this.rand;
    }

    public EntityLivingBase getAITarget()
    {
        return this.entityLivingToAttack;
    }

    public int func_142015_aE()
    {
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase par1EntityLivingBase)
    {
        this.entityLivingToAttack = par1EntityLivingBase;
        this.revengeTimer = this.ticksExisted;
    }

    public EntityLivingBase getLastAttacker()
    {
        return this.lastAttacker;
    }

    public int getLastAttackerTime()
    {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity par1Entity)
    {
        if (par1Entity instanceof EntityLivingBase)
        {
            this.lastAttacker = (EntityLivingBase)par1Entity;
        }
        else
        {
            this.lastAttacker = null;
        }

        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge()
    {
        return this.entityAge;
    }

    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setFloat("HealF", this.getHealth());
        par1NBTTagCompound.setShort("Health", (short)((int)Math.ceil((double)this.getHealth())));
        par1NBTTagCompound.setShort("HurtTime", (short)this.hurtTime);
        par1NBTTagCompound.setShort("DeathTime", (short)this.deathTime);
        par1NBTTagCompound.setShort("AttackTime", (short)this.attackTime);
        par1NBTTagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());
        ItemStack[] var2 = this.getLastActiveItems();
        int var3 = var2.length;
        int var4;
        ItemStack var5;

        for (var4 = 0; var4 < var3; ++var4)
        {
            var5 = var2[var4];

            if (var5 != null)
            {
                this.attributeMap.removeAttributeModifiers(var5.getAttributeModifiers());
            }
        }

        par1NBTTagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));
        var2 = this.getLastActiveItems();
        var3 = var2.length;

        for (var4 = 0; var4 < var3; ++var4)
        {
            var5 = var2[var4];

            if (var5 != null)
            {
                this.attributeMap.applyAttributeModifiers(var5.getAttributeModifiers());
            }
        }

        if (!this.activePotionsMap.isEmpty())
        {
            NBTTagList var6 = new NBTTagList();
            Iterator var7 = this.activePotionsMap.values().iterator();

            while (var7.hasNext())
            {
                PotionEffect var8 = (PotionEffect)var7.next();
                var6.appendTag(var8.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            par1NBTTagCompound.setTag("ActiveEffects", var6);
        }
    }

    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.setAbsorptionAmount(par1NBTTagCompound.getFloat("AbsorptionAmount"));

        if (par1NBTTagCompound.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isClient)
        {
            SharedMonsterAttributes.func_151475_a(this.getAttributeMap(), par1NBTTagCompound.getTagList("Attributes", 10));
        }

        if (par1NBTTagCompound.hasKey("ActiveEffects", 9))
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("ActiveEffects", 10);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                PotionEffect var5 = PotionEffect.readCustomPotionEffectFromNBT(var4);

                if (var5 != null)
                {
                    this.activePotionsMap.put(Integer.valueOf(var5.getPotionID()), var5);
                }
            }
        }

        if (par1NBTTagCompound.hasKey("HealF", 99))
        {
            this.setHealth(par1NBTTagCompound.getFloat("HealF"));
        }
        else
        {
            NBTBase var6 = par1NBTTagCompound.getTag("Health");

            if (var6 == null)
            {
                this.setHealth(this.getMaxHealth());
            }
            else if (var6.getId() == 5)
            {
                this.setHealth(((NBTTagFloat)var6).func_150288_h());
            }
            else if (var6.getId() == 2)
            {
                this.setHealth((float)((NBTTagShort)var6).func_150289_e());
            }
        }

        this.hurtTime = par1NBTTagCompound.getShort("HurtTime");
        this.deathTime = par1NBTTagCompound.getShort("DeathTime");
        this.attackTime = par1NBTTagCompound.getShort("AttackTime");
    }

    protected void updatePotionEffects()
    {
        Iterator var1 = this.activePotionsMap.keySet().iterator();

        while (var1.hasNext())
        {
            Integer var2 = (Integer)var1.next();
            PotionEffect var3 = (PotionEffect)this.activePotionsMap.get(var2);

            if (!var3.onUpdate(this))
            {
                if (!this.worldObj.isClient)
                {
                    var1.remove();
                    this.onFinishedPotionEffect(var3);
                }
            }
            else if (var3.getDuration() % 600 == 0)
            {
                this.onChangedPotionEffect(var3, false);
            }
        }

        int var11;

        if (this.potionsNeedUpdate)
        {
            if (!this.worldObj.isClient)
            {
                if (this.activePotionsMap.isEmpty())
                {
                    this.dataWatcher.updateObject(8, Byte.valueOf((byte)0));
                    this.dataWatcher.updateObject(7, Integer.valueOf(0));
                    this.setInvisible(false);
                }
                else
                {
                    var11 = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
                    this.dataWatcher.updateObject(8, Byte.valueOf((byte)(PotionHelper.func_82817_b(this.activePotionsMap.values()) ? 1 : 0)));
                    this.dataWatcher.updateObject(7, Integer.valueOf(var11));
                    this.setInvisible(this.isPotionActive(Potion.invisibility.id));
                }
            }

            this.potionsNeedUpdate = false;
        }

        var11 = this.dataWatcher.getWatchableObjectInt(7);
        boolean var12 = this.dataWatcher.getWatchableObjectByte(8) > 0;

        if (var11 > 0)
        {
            boolean var4 = false;

            if (!this.isInvisible())
            {
                var4 = this.rand.nextBoolean();
            }
            else
            {
                var4 = this.rand.nextInt(15) == 0;
            }

            if (var12)
            {
                var4 &= this.rand.nextInt(5) == 0;
            }

            if (var4 && var11 > 0)
            {
                double var5 = (double)(var11 >> 16 & 255) / 255.0D;
                double var7 = (double)(var11 >> 8 & 255) / 255.0D;
                double var9 = (double)(var11 >> 0 & 255) / 255.0D;
                this.worldObj.spawnParticle(var12 ? "mobSpellAmbient" : "mobSpell", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - (double)this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, var5, var7, var9);
            }
        }
    }

    public void clearActivePotions()
    {
        Iterator var1 = this.activePotionsMap.keySet().iterator();

        while (var1.hasNext())
        {
            Integer var2 = (Integer)var1.next();
            PotionEffect var3 = (PotionEffect)this.activePotionsMap.get(var2);

            if (!this.worldObj.isClient)
            {
                var1.remove();
                this.onFinishedPotionEffect(var3);
            }
        }
    }

    public Collection getActivePotionEffects()
    {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int par1)
    {
        return this.activePotionsMap.containsKey(Integer.valueOf(par1));
    }

    public boolean isPotionActive(Potion par1Potion)
    {
        return this.activePotionsMap.containsKey(Integer.valueOf(par1Potion.id));
    }

    public PotionEffect getActivePotionEffect(Potion par1Potion)
    {
        return (PotionEffect)this.activePotionsMap.get(Integer.valueOf(par1Potion.id));
    }

    public void addPotionEffect(PotionEffect par1PotionEffect)
    {
        if (this.isPotionApplicable(par1PotionEffect))
        {
            if (this.activePotionsMap.containsKey(Integer.valueOf(par1PotionEffect.getPotionID())))
            {
                ((PotionEffect)this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID()))).combine(par1PotionEffect);
                this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(Integer.valueOf(par1PotionEffect.getPotionID())), true);
            }
            else
            {
                this.activePotionsMap.put(Integer.valueOf(par1PotionEffect.getPotionID()), par1PotionEffect);
                this.onNewPotionEffect(par1PotionEffect);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect par1PotionEffect)
    {
        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
        {
            int var2 = par1PotionEffect.getPotionID();

            if (var2 == Potion.regeneration.id || var2 == Potion.poison.id)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isEntityUndead()
    {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    public void removePotionEffectClient(int par1)
    {
        this.activePotionsMap.remove(Integer.valueOf(par1));
    }

    public void removePotionEffect(int par1)
    {
        PotionEffect var2 = (PotionEffect)this.activePotionsMap.remove(Integer.valueOf(par1));

        if (var2 != null)
        {
            this.onFinishedPotionEffect(var2);
        }
    }

    protected void onNewPotionEffect(PotionEffect par1PotionEffect)
    {
        this.potionsNeedUpdate = true;

        if (!this.worldObj.isClient)
        {
            Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(PotionEffect par1PotionEffect, boolean par2)
    {
        this.potionsNeedUpdate = true;

        if (par2 && !this.worldObj.isClient)
        {
            Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
            Potion.potionTypes[par1PotionEffect.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(PotionEffect par1PotionEffect)
    {
        this.potionsNeedUpdate = true;

        if (!this.worldObj.isClient)
        {
            Potion.potionTypes[par1PotionEffect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), par1PotionEffect.getAmplifier());
        }
    }

    public void heal(float par1)
    {
        float var2 = this.getHealth();

        if (var2 > 0.0F)
        {
            this.setHealth(var2 + par1);
        }
    }

    public final float getHealth()
    {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float par1)
    {
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(par1, 0.0F, this.getMaxHealth())));
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else if (this.worldObj.isClient)
        {
            return false;
        }
        else
        {
            this.entityAge = 0;

            if (this.getHealth() <= 0.0F)
            {
                return false;
            }
            else if (par1DamageSource.isFireDamage() && this.isPotionActive(Potion.fireResistance))
            {
                return false;
            }
            else
            {
                if ((par1DamageSource == DamageSource.anvil || par1DamageSource == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null)
                {
                    this.getEquipmentInSlot(4).damageItem((int)(par2 * 4.0F + this.rand.nextFloat() * par2 * 2.0F), this);
                    par2 *= 0.75F;
                }

                this.limbSwingAmount = 1.5F;
                boolean var3 = true;

                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (par2 <= this.lastDamage)
                    {
                        return false;
                    }

                    this.damageEntity(par1DamageSource, par2 - this.lastDamage);
                    this.lastDamage = par2;
                    var3 = false;
                }
                else
                {
                    this.lastDamage = par2;
                    this.prevHealth = this.getHealth();
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(par1DamageSource, par2);
                    this.hurtTime = this.maxHurtTime = 10;
                }

                this.attackedAtYaw = 0.0F;
                Entity var4 = par1DamageSource.getEntity();

                if (var4 != null)
                {
                    if (var4 instanceof EntityLivingBase)
                    {
                        this.setRevengeTarget((EntityLivingBase)var4);
                    }

                    if (var4 instanceof EntityPlayer)
                    {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer)var4;
                    }
                    else if (var4 instanceof EntityWolf)
                    {
                        EntityWolf var5 = (EntityWolf)var4;

                        if (var5.isTamed())
                        {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (var3)
                {
                    this.worldObj.setEntityState(this, (byte)2);

                    if (par1DamageSource != DamageSource.drown)
                    {
                        this.setBeenAttacked();
                    }

                    if (var4 != null)
                    {
                        double var9 = var4.posX - this.posX;
                        double var7;

                        for (var7 = var4.posZ - this.posZ; var9 * var9 + var7 * var7 < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D)
                        {
                            var9 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float)(Math.atan2(var7, var9) * 180.0D / Math.PI) - this.rotationYaw;
                        this.knockBack(var4, par2, var9, var7);
                    }
                    else
                    {
                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                    }
                }

                String var10;

                if (this.getHealth() <= 0.0F)
                {
                    var10 = this.getDeathSound();

                    if (var3 && var10 != null)
                    {
                        this.playSound(var10, this.getSoundVolume(), this.getSoundPitch());
                    }

                    this.onDeath(par1DamageSource);
                }
                else
                {
                    var10 = this.getHurtSound();

                    if (var3 && var10 != null)
                    {
                        this.playSound(var10, this.getSoundVolume(), this.getSoundPitch());
                    }
                }

                return true;
            }
        }
    }

    public void renderBrokenItemStack(ItemStack par1ItemStack)
    {
        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

        for (int var2 = 0; var2 < 5; ++var2)
        {
            Vec3 var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            var3.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
            var3.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
            Vec3 var4 = this.worldObj.getWorldVec3Pool().getVecFromPool(((double)this.rand.nextFloat() - 0.5D) * 0.3D, (double)(-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
            var4.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
            var4.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
            var4 = var4.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
            this.worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(par1ItemStack.getItem()), var4.xCoord, var4.yCoord, var4.zCoord, var3.xCoord, var3.yCoord + 0.05D, var3.zCoord);
        }
    }

    public void onDeath(DamageSource par1DamageSource)
    {
        Entity var2 = par1DamageSource.getEntity();
        EntityLivingBase var3 = this.func_94060_bK();

        if (this.scoreValue >= 0 && var3 != null)
        {
            var3.addToPlayerScore(this, this.scoreValue);
        }

        if (var2 != null)
        {
            var2.onKillEntity(this);
        }

        this.dead = true;

        if (!this.worldObj.isClient)
        {
            int var4 = 0;

            if (var2 instanceof EntityPlayer)
            {
                var4 = EnchantmentHelper.getLootingModifier((EntityLivingBase)var2);
            }

            if (this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                this.dropFewItems(this.recentlyHit > 0, var4);
                this.dropEquipment(this.recentlyHit > 0, var4);

                if (this.recentlyHit > 0)
                {
                    int var5 = this.rand.nextInt(200) - var4;

                    if (var5 < 5)
                    {
                        this.dropRareDrop(var5 <= 0 ? 1 : 0);
                    }
                }
            }
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    protected void dropEquipment(boolean par1, int par2) {}

    public void knockBack(Entity par1Entity, float par2, double par3, double par5)
    {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue())
        {
            this.isAirBorne = true;
            float var7 = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
            float var8 = 0.4F;
            this.motionX /= 2.0D;
            this.motionY /= 2.0D;
            this.motionZ /= 2.0D;
            this.motionX -= par3 / (double)var7 * (double)var8;
            this.motionY += (double)var8;
            this.motionZ -= par5 / (double)var7 * (double)var8;

            if (this.motionY > 0.4000000059604645D)
            {
                this.motionY = 0.4000000059604645D;
            }
        }
    }

    protected String getHurtSound()
    {
        return "game.neutral.hurt";
    }

    protected String getDeathSound()
    {
        return "game.neutral.die";
    }

    protected void dropRareDrop(int par1) {}

    protected void dropFewItems(boolean par1, int par2) {}

    public boolean isOnLadder()
    {
        int var1 = MathHelper.floor_double(this.posX);
        int var2 = MathHelper.floor_double(this.boundingBox.minY);
        int var3 = MathHelper.floor_double(this.posZ);
        Block var4 = this.worldObj.getBlock(var1, var2, var3);
        return var4 == Blocks.ladder || var4 == Blocks.vine;
    }

    public boolean isEntityAlive()
    {
        return !this.isDead && this.getHealth() > 0.0F;
    }

    protected void fall(float par1)
    {
        super.fall(par1);
        PotionEffect var2 = this.getActivePotionEffect(Potion.jump);
        float var3 = var2 != null ? (float)(var2.getAmplifier() + 1) : 0.0F;
        int var4 = MathHelper.ceiling_float_int(par1 - 3.0F - var3);

        if (var4 > 0)
        {
            this.playSound(this.func_146067_o(var4), 1.0F, 1.0F);
            this.attackEntityFrom(DamageSource.fall, (float)var4);
            int var5 = MathHelper.floor_double(this.posX);
            int var6 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int var7 = MathHelper.floor_double(this.posZ);
            Block var8 = this.worldObj.getBlock(var5, var6, var7);

            if (var8.getMaterial() != Material.air)
            {
                Block.SoundType var9 = var8.stepSound;
                this.playSound(var9.func_150498_e(), var9.func_150497_c() * 0.5F, var9.func_150494_d() * 0.75F);
            }
        }
    }

    protected String func_146067_o(int p_146067_1_)
    {
        return p_146067_1_ > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    public void performHurtAnimation()
    {
        this.hurtTime = this.maxHurtTime = 10;
        this.attackedAtYaw = 0.0F;
    }

    public int getTotalArmorValue()
    {
        int var1 = 0;
        ItemStack[] var2 = this.getLastActiveItems();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            ItemStack var5 = var2[var4];

            if (var5 != null && var5.getItem() instanceof ItemArmor)
            {
                int var6 = ((ItemArmor)var5.getItem()).damageReduceAmount;
                var1 += var6;
            }
        }

        return var1;
    }

    protected void damageArmor(float par1) {}

    protected float applyArmorCalculations(DamageSource par1DamageSource, float par2)
    {
        if (!par1DamageSource.isUnblockable())
        {
            int var3 = 25 - this.getTotalArmorValue();
            float var4 = par2 * (float)var3;
            this.damageArmor(par2);
            par2 = var4 / 25.0F;
        }

        return par2;
    }

    protected float applyPotionDamageCalculations(DamageSource par1DamageSource, float par2)
    {
        if (par1DamageSource.isDamageAbsolute())
        {
            return par2;
        }
        else
        {
            if (this instanceof EntityZombie)
            {
                par2 = par2;
            }

            int var3;
            int var4;
            float var5;

            if (this.isPotionActive(Potion.resistance) && par1DamageSource != DamageSource.outOfWorld)
            {
                var3 = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
                var4 = 25 - var3;
                var5 = par2 * (float)var4;
                par2 = var5 / 25.0F;
            }

            if (par2 <= 0.0F)
            {
                return 0.0F;
            }
            else
            {
                var3 = EnchantmentHelper.getEnchantmentModifierDamage(this.getLastActiveItems(), par1DamageSource);

                if (var3 > 20)
                {
                    var3 = 20;
                }

                if (var3 > 0 && var3 <= 20)
                {
                    var4 = 25 - var3;
                    var5 = par2 * (float)var4;
                    par2 = var5 / 25.0F;
                }

                return par2;
            }
        }
    }

    protected void damageEntity(DamageSource par1DamageSource, float par2)
    {
        if (!this.isEntityInvulnerable())
        {
            par2 = this.applyArmorCalculations(par1DamageSource, par2);
            par2 = this.applyPotionDamageCalculations(par1DamageSource, par2);
            float var3 = par2;
            par2 = Math.max(par2 - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (var3 - par2));

            if (par2 != 0.0F)
            {
                float var4 = this.getHealth();
                this.setHealth(var4 - par2);
                this.func_110142_aN().func_94547_a(par1DamageSource, var4, par2);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - par2);
            }
        }
    }

    public CombatTracker func_110142_aN()
    {
        return this._combatTracker;
    }

    public EntityLivingBase func_94060_bK()
    {
        return (EntityLivingBase)(this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
    }

    public final float getMaxHealth()
    {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    public final int getArrowCountInEntity()
    {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    public final void setArrowCountInEntity(int par1)
    {
        this.dataWatcher.updateObject(9, Byte.valueOf((byte)par1));
    }

    private int getArmSwingAnimationEnd()
    {
        return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    public void swingItem()
    {
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0)
        {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;

            if (this.worldObj instanceof WorldServer)
            {
                ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    public void handleHealthUpdate(byte par1)
    {
        if (par1 == 2)
        {
            this.limbSwingAmount = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
            this.attackedAtYaw = 0.0F;
            this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.attackEntityFrom(DamageSource.generic, 0.0F);
        }
        else if (par1 == 3)
        {
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.setHealth(0.0F);
            this.onDeath(DamageSource.generic);
        }
        else
        {
            super.handleHealthUpdate(par1);
        }
    }

    protected void kill()
    {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
    }

    protected void updateArmSwingProgress()
    {
        int var1 = this.getArmSwingAnimationEnd();

        if (this.isSwingInProgress)
        {
            ++this.swingProgressInt;

            if (this.swingProgressInt >= var1)
            {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        }
        else
        {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float)this.swingProgressInt / (float)var1;
    }

    public IAttributeInstance getEntityAttribute(IAttribute par1Attribute)
    {
        return this.getAttributeMap().getAttributeInstance(par1Attribute);
    }

    public BaseAttributeMap getAttributeMap()
    {
        if (this.attributeMap == null)
        {
            this.attributeMap = new ServersideAttributeMap();
        }

        return this.attributeMap;
    }

    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEFINED;
    }

    public abstract ItemStack getHeldItem();

    public abstract ItemStack getEquipmentInSlot(int var1);

    public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);

    public void setSprinting(boolean par1)
    {
        super.setSprinting(par1);
        IAttributeInstance var2 = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (var2.getModifier(sprintingSpeedBoostModifierUUID) != null)
        {
            var2.removeModifier(sprintingSpeedBoostModifier);
        }

        if (par1)
        {
            var2.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    public abstract ItemStack[] getLastActiveItems();

    protected float getSoundVolume()
    {
        return 1.0F;
    }

    protected float getSoundPitch()
    {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isMovementBlocked()
    {
        return this.getHealth() <= 0.0F;
    }

    public void setPositionAndUpdate(double par1, double par3, double par5)
    {
        this.setLocationAndAngles(par1, par3, par5, this.rotationYaw, this.rotationPitch);
    }

    public void dismountEntity(Entity par1Entity)
    {
        double var3 = par1Entity.posX;
        double var5 = par1Entity.boundingBox.minY + (double)par1Entity.height;
        double var7 = par1Entity.posZ;
        byte var9 = 3;

        for (int var10 = -var9; var10 <= var9; ++var10)
        {
            for (int var11 = -var9; var11 < var9; ++var11)
            {
                if (var10 != 0 || var11 != 0)
                {
                    int var12 = (int)(this.posX + (double)var10);
                    int var13 = (int)(this.posZ + (double)var11);
                    AxisAlignedBB var2 = this.boundingBox.getOffsetBoundingBox((double)var10, 1.0D, (double)var11);

                    if (this.worldObj.func_147461_a(var2).isEmpty())
                    {
                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, var12, (int)this.posY, var13))
                        {
                            this.setPositionAndUpdate(this.posX + (double)var10, this.posY + 1.0D, this.posZ + (double)var11);
                            return;
                        }

                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, var12, (int)this.posY - 1, var13) || this.worldObj.getBlock(var12, (int)this.posY - 1, var13).getMaterial() == Material.water)
                        {
                            var3 = this.posX + (double)var10;
                            var5 = this.posY + 1.0D;
                            var7 = this.posZ + (double)var11;
                        }
                    }
                }
            }
        }

        this.setPositionAndUpdate(var3, var5, var7);
    }

    public boolean getAlwaysRenderNameTagForRender()
    {
        return false;
    }

    public IIcon getItemIcon(ItemStack par1ItemStack, int par2)
    {
        return par1ItemStack.getItem().requiresMultipleRenderPasses() ? par1ItemStack.getItem().getIconFromDamageForRenderPass(par1ItemStack.getItemDamage(), par2) : par1ItemStack.getIconIndex();
    }

    protected void jump()
    {
        this.motionY = 0.41999998688697815D;

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting())
        {
            float var1 = this.rotationYaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(var1) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(var1) * 0.2F);
        }

        this.isAirBorne = true;
    }

    public void moveEntityWithHeading(float par1, float par2)
    {
        double var8;

        if (this.isInWater() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying))
        {
            var8 = this.posY;
            this.moveFlying(par1, par2, this.isAIEnabled() ? 0.04F : 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + var8, this.motionZ))
            {
                this.motionY = 0.30000001192092896D;
            }
        }
        else if (this.handleLavaMovement() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying))
        {
            var8 = this.posY;
            this.moveFlying(par1, par2, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
            this.motionY -= 0.02D;

            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + var8, this.motionZ))
            {
                this.motionY = 0.30000001192092896D;
            }
        }
        else
        {
            float var3 = 0.91F;

            if (this.onGround)
            {
                var3 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
            }

            float var4 = 0.16277136F / (var3 * var3 * var3);
            float var5;

            if (this.onGround)
            {
                var5 = this.getAIMoveSpeed() * var4;
            }
            else
            {
                var5 = this.jumpMovementFactor;
            }

            this.moveFlying(par1, par2, var5);
            var3 = 0.91F;

            if (this.onGround)
            {
                var3 = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
            }

            if (this.isOnLadder())
            {
                float var6 = 0.15F;

                if (this.motionX < (double)(-var6))
                {
                    this.motionX = (double)(-var6);
                }

                if (this.motionX > (double)var6)
                {
                    this.motionX = (double)var6;
                }

                if (this.motionZ < (double)(-var6))
                {
                    this.motionZ = (double)(-var6);
                }

                if (this.motionZ > (double)var6)
                {
                    this.motionZ = (double)var6;
                }

                this.fallDistance = 0.0F;

                if (this.motionY < -0.15D)
                {
                    this.motionY = -0.15D;
                }

                boolean var7 = this.isSneaking() && this instanceof EntityPlayer;

                if (var7 && this.motionY < 0.0D)
                {
                    this.motionY = 0.0D;
                }
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && this.isOnLadder())
            {
                this.motionY = 0.2D;
            }

            if (this.worldObj.isClient && (!this.worldObj.blockExists((int)this.posX, 0, (int)this.posZ) || !this.worldObj.getChunkFromBlockCoords((int)this.posX, (int)this.posZ).isChunkLoaded))
            {
                if (this.posY > 0.0D)
                {
                    this.motionY = -0.1D;
                }
                else
                {
                    this.motionY = 0.0D;
                }
            }
            else
            {
                this.motionY -= 0.08D;
            }

            this.motionY *= 0.9800000190734863D;
            this.motionX *= (double)var3;
            this.motionZ *= (double)var3;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        var8 = this.posX - this.prevPosX;
        double var9 = this.posZ - this.prevPosZ;
        float var10 = MathHelper.sqrt_double(var8 * var8 + var9 * var9) * 4.0F;

        if (var10 > 1.0F)
        {
            var10 = 1.0F;
        }

        this.limbSwingAmount += (var10 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    protected boolean isAIEnabled()
    {
        return false;
    }

    public float getAIMoveSpeed()
    {
        return this.isAIEnabled() ? this.landMovementFactor : 0.1F;
    }

    public void setAIMoveSpeed(float par1)
    {
        this.landMovementFactor = par1;
    }

    public boolean attackEntityAsMob(Entity par1Entity)
    {
        this.setLastAttacker(par1Entity);
        return false;
    }

    public boolean isPlayerSleeping()
    {
        return false;
    }

    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isClient)
        {
            int var1 = this.getArrowCountInEntity();

            if (var1 > 0)
            {
                if (this.arrowHitTimer <= 0)
                {
                    this.arrowHitTimer = 20 * (30 - var1);
                }

                --this.arrowHitTimer;

                if (this.arrowHitTimer <= 0)
                {
                    this.setArrowCountInEntity(var1 - 1);
                }
            }

            for (int var2 = 0; var2 < 5; ++var2)
            {
                ItemStack var3 = this.previousEquipment[var2];
                ItemStack var4 = this.getEquipmentInSlot(var2);

                if (!ItemStack.areItemStacksEqual(var4, var3))
                {
                    ((WorldServer)this.worldObj).getEntityTracker().func_151247_a(this, new S04PacketEntityEquipment(this.getEntityId(), var2, var4));

                    if (var3 != null)
                    {
                        this.attributeMap.removeAttributeModifiers(var3.getAttributeModifiers());
                    }

                    if (var4 != null)
                    {
                        this.attributeMap.applyAttributeModifiers(var4.getAttributeModifiers());
                    }

                    this.previousEquipment[var2] = var4 == null ? null : var4.copy();
                }
            }
        }

        this.onLivingUpdate();
        double var9 = this.posX - this.prevPosX;
        double var10 = this.posZ - this.prevPosZ;
        float var5 = (float)(var9 * var9 + var10 * var10);
        float var6 = this.renderYawOffset;
        float var7 = 0.0F;
        this.field_70768_au = this.field_110154_aX;
        float var8 = 0.0F;

        if (var5 > 0.0025000002F)
        {
            var8 = 1.0F;
            var7 = (float)Math.sqrt((double)var5) * 3.0F;
            var6 = (float)Math.atan2(var10, var9) * 180.0F / (float)Math.PI - 90.0F;
        }

        if (this.swingProgress > 0.0F)
        {
            var6 = this.rotationYaw;
        }

        if (!this.onGround)
        {
            var8 = 0.0F;
        }

        this.field_110154_aX += (var8 - this.field_110154_aX) * 0.3F;
        this.worldObj.theProfiler.startSection("headTurn");
        var7 = this.func_110146_f(var6, var7);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F)
        {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F)
        {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
        {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F)
        {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F)
        {
            this.prevRotationYawHead += 360.0F;
        }

        this.worldObj.theProfiler.endSection();
        this.field_70764_aw += var7;
    }

    protected float func_110146_f(float par1, float par2)
    {
        float var3 = MathHelper.wrapAngleTo180_float(par1 - this.renderYawOffset);
        this.renderYawOffset += var3 * 0.3F;
        float var4 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        boolean var5 = var4 < -90.0F || var4 >= 90.0F;

        if (var4 < -75.0F)
        {
            var4 = -75.0F;
        }

        if (var4 >= 75.0F)
        {
            var4 = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - var4;

        if (var4 * var4 > 2500.0F)
        {
            this.renderYawOffset += var4 * 0.2F;
        }

        if (var5)
        {
            par2 *= -1.0F;
        }

        return par2;
    }

    public void onLivingUpdate()
    {
        if (this.jumpTicks > 0)
        {
            --this.jumpTicks;
        }

        if (this.newPosRotationIncrements > 0)
        {
            double var1 = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double var3 = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double var5 = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double var7 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(var1, var3, var5);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isClientWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        }
        else if (this.isClientWorld())
        {
            if (this.isAIEnabled())
            {
                this.worldObj.theProfiler.startSection("newAi");
                this.updateAITasks();
                this.worldObj.theProfiler.endSection();
            }
            else
            {
                this.worldObj.theProfiler.startSection("oldAi");
                this.updateEntityActionState();
                this.worldObj.theProfiler.endSection();
                this.rotationYawHead = this.rotationYaw;
            }
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");

        if (this.isJumping)
        {
            if (!this.isInWater() && !this.handleLavaMovement())
            {
                if (this.onGround && this.jumpTicks == 0)
                {
                    this.jump();
                    this.jumpTicks = 10;
                }
            }
            else
            {
                this.motionY += 0.03999999910593033D;
            }
        }
        else
        {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isClient)
        {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();
    }

    protected void updateAITasks() {}

    protected void collideWithNearbyEntities()
    {
        List var1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (var1 != null && !var1.isEmpty())
        {
            for (int var2 = 0; var2 < var1.size(); ++var2)
            {
                Entity var3 = (Entity)var1.get(var2);

                if (var3.canBePushed())
                {
                    this.collideWithEntity(var3);
                }
            }
        }
    }

    protected void collideWithEntity(Entity par1Entity)
    {
        par1Entity.applyEntityCollision(this);
    }

    public void updateRidden()
    {
        super.updateRidden();
        this.field_70768_au = this.field_110154_aX;
        this.field_110154_aX = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.yOffset = 0.0F;
        this.newPosX = par1;
        this.newPosY = par3;
        this.newPosZ = par5;
        this.newRotationYaw = (double)par7;
        this.newRotationPitch = (double)par8;
        this.newPosRotationIncrements = par9;
    }

    protected void updateAITick() {}

    protected void updateEntityActionState()
    {
        ++this.entityAge;
    }

    public void setJumping(boolean par1)
    {
        this.isJumping = par1;
    }

    public void onItemPickup(Entity par1Entity, int par2)
    {
        if (!par1Entity.isDead && !this.worldObj.isClient)
        {
            EntityTracker var3 = ((WorldServer)this.worldObj).getEntityTracker();

            if (par1Entity instanceof EntityItem)
            {
                var3.func_151247_a(par1Entity, new S0DPacketCollectItem(par1Entity.getEntityId(), this.getEntityId()));
            }

            if (par1Entity instanceof EntityArrow)
            {
                var3.func_151247_a(par1Entity, new S0DPacketCollectItem(par1Entity.getEntityId(), this.getEntityId()));
            }

            if (par1Entity instanceof EntityXPOrb)
            {
                var3.func_151247_a(par1Entity, new S0DPacketCollectItem(par1Entity.getEntityId(), this.getEntityId()));
            }
        }
    }

    public boolean canEntityBeSeen(Entity par1Entity)
    {
        return this.worldObj.rayTraceBlocks(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), this.worldObj.getWorldVec3Pool().getVecFromPool(par1Entity.posX, par1Entity.posY + (double)par1Entity.getEyeHeight(), par1Entity.posZ)) == null;
    }

    public Vec3 getLookVec()
    {
        return this.getLook(1.0F);
    }

    public Vec3 getLook(float par1)
    {
        float var2;
        float var3;
        float var4;
        float var5;

        if (par1 == 1.0F)
        {
            var2 = MathHelper.cos(-this.rotationYaw * 0.017453292F - (float)Math.PI);
            var3 = MathHelper.sin(-this.rotationYaw * 0.017453292F - (float)Math.PI);
            var4 = -MathHelper.cos(-this.rotationPitch * 0.017453292F);
            var5 = MathHelper.sin(-this.rotationPitch * 0.017453292F);
            return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(var3 * var4), (double)var5, (double)(var2 * var4));
        }
        else
        {
            var2 = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * par1;
            var3 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * par1;
            var4 = MathHelper.cos(-var3 * 0.017453292F - (float)Math.PI);
            var5 = MathHelper.sin(-var3 * 0.017453292F - (float)Math.PI);
            float var6 = -MathHelper.cos(-var2 * 0.017453292F);
            float var7 = MathHelper.sin(-var2 * 0.017453292F);
            return this.worldObj.getWorldVec3Pool().getVecFromPool((double)(var5 * var6), (double)var7, (double)(var4 * var6));
        }
    }

    public float getSwingProgress(float par1)
    {
        float var2 = this.swingProgress - this.prevSwingProgress;

        if (var2 < 0.0F)
        {
            ++var2;
        }

        return this.prevSwingProgress + var2 * par1;
    }

    public Vec3 getPosition(float par1)
    {
        if (par1 == 1.0F)
        {
            return this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
        }
        else
        {
            double var2 = this.prevPosX + (this.posX - this.prevPosX) * (double)par1;
            double var4 = this.prevPosY + (this.posY - this.prevPosY) * (double)par1;
            double var6 = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par1;
            return this.worldObj.getWorldVec3Pool().getVecFromPool(var2, var4, var6);
        }
    }

    public MovingObjectPosition rayTrace(double par1, float par3)
    {
        Vec3 var4 = this.getPosition(par3);
        Vec3 var5 = this.getLook(par3);
        Vec3 var6 = var4.addVector(var5.xCoord * par1, var5.yCoord * par1, var5.zCoord * par1);
        return this.worldObj.func_147447_a(var4, var6, false, false, true);
    }

    public boolean isClientWorld()
    {
        return !this.worldObj.isClient;
    }

    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    public boolean canBePushed()
    {
        return !this.isDead;
    }

    public float getEyeHeight()
    {
        return this.height * 0.85F;
    }

    protected void setBeenAttacked()
    {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    public float getRotationYawHead()
    {
        return this.rotationYawHead;
    }

    public void setRotationYawHead(float par1)
    {
        this.rotationYawHead = par1;
    }

    public float getAbsorptionAmount()
    {
        return this.field_110151_bq;
    }

    public void setAbsorptionAmount(float par1)
    {
        if (par1 < 0.0F)
        {
            par1 = 0.0F;
        }

        this.field_110151_bq = par1;
    }

    public Team getTeam()
    {
        return null;
    }

    public boolean isOnSameTeam(EntityLivingBase par1EntityLivingBase)
    {
        return this.isOnTeam(par1EntityLivingBase.getTeam());
    }

    public boolean isOnTeam(Team par1Team)
    {
        return this.getTeam() != null ? this.getTeam().isSameTeam(par1Team) : false;
    }
}
