package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

import lol.nebula.Nebula;
import lol.nebula.listener.events.entity.EventEntityTurn;
import lol.nebula.listener.events.entity.move.EventPushOutOfBlocks;
import lol.nebula.listener.events.entity.move.EventStep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity
{
    private static int nextEntityID;
    private int field_145783_c;
    public double renderDistanceWeight;

    /**
     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
     * prevent spawning.
     */
    public boolean preventEntitySpawning;

    /** The entity that is riding this entity */
    public Entity riddenByEntity;

    /** The entity we are currently riding */
    public Entity ridingEntity;
    public boolean forceSpawn;

    /** Reference to the World object. */
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;

    /** Entity position X */
    public double posX;

    /** Entity position Y */
    public double posY;

    /** Entity position Z */
    public double posZ;

    /** Entity motion X */
    public double motionX;

    /** Entity motion Y */
    public double motionY;

    /** Entity motion Z */
    public double motionZ;

    /** Entity rotation Yaw */
    public float rotationYaw;

    /** Entity rotation Pitch */
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;

    /** Axis aligned bounding box. */
    public final AxisAlignedBB boundingBox;
    public boolean onGround;

    /**
     * True if after a move this entity has collided with something on X- or Z-axis
     */
    public boolean isCollidedHorizontally;

    /**
     * True if after a move this entity has collided with something on Y-axis
     */
    public boolean isCollidedVertically;

    /**
     * True if after a move this entity has collided with something either vertically or horizontally
     */
    public boolean isCollided;
    public boolean velocityChanged;
    public boolean isInWeb;
    public boolean field_70135_K;

    /**
     * Gets set by setDead, so this must be the flag whether an Entity is dead (inactive may be better term)
     */
    public boolean isDead;
    public float yOffset;

    /** How wide this entity is considered to be */
    public float width;

    /** How high this entity is considered to be */
    public float height;

    /** The previous ticks distance walked multiplied by 0.6 */
    public float prevDistanceWalkedModified;

    /** The distance walked multiplied by 0.6 */
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;

    /**
     * The distance that has to be exceeded in order to triger a new step sound and an onEntityWalking event on a block
     */
    private int nextStepDistance;

    /**
     * The entity's X coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosX;

    /**
     * The entity's Y coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosY;

    /**
     * The entity's Z coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosZ;
    public float ySize;

    /**
     * How high this entity can step up when running into a block to try to get over it (currently make note the entity
     * will always step up this amount and not just the amount needed)
     */
    public float stepHeight;

    /**
     * Whether this entity won't clip with collision or not (make note it won't disable gravity)
     */
    public boolean noClip;

    /**
     * Reduces the velocity applied by entity collisions by the specified percent.
     */
    public float entityCollisionReduction;
    protected Random rand;

    /** How many ticks has this entity had ran since being alive */
    public int ticksExisted;

    /**
     * The amount of ticks you have to stand inside of fire before be set on fire
     */
    public int fireResistance;
    private int fire;

    /**
     * Whether this entity is currently inside of water (if it handles water movement that is)
     */
    protected boolean inWater;

    /**
     * Remaining time an entity will be "immune" to further damage after being hurt.
     */
    public int hurtResistantTime;
    private boolean firstUpdate;
    protected boolean isImmuneToFire;
    protected DataWatcher dataWatcher;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;

    /** Has this entity been added to the chunk its within */
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    /**
     * Render entity even if it is outside the camera frustum. Only true in EntityFish for now. Used in RenderGlobal:
     * render if ignoreFrustumCheck or in frustum.
     */
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    public int timeUntilPortal;

    /** Whether the entity is inside a Portal */
    public boolean inPortal;
    protected int portalCounter;

    /** Which dimension the player is in (-1 = the Nether, 0 = normal world) */
    public int dimension;
    protected int teleportDirection;
    private boolean invulnerable;
    protected UUID entityUniqueID;
    public Entity.EnumEntitySize myEntitySize;
    private static final String __OBFID = "CL_00001533";

    public int getEntityId()
    {
        return this.field_145783_c;
    }

    public void setEntityId(int p_145769_1_)
    {
        this.field_145783_c = p_145769_1_;
    }

    public Entity(World par1World)
    {
        this.field_145783_c = nextEntityID++;
        this.renderDistanceWeight = 1.0D;
        this.boundingBox = AxisAlignedBB.getBoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        this.field_70135_K = true;
        this.width = 0.6F;
        this.height = 1.8F;
        this.nextStepDistance = 1;
        this.rand = new Random();
        this.fireResistance = 1;
        this.firstUpdate = true;
        this.entityUniqueID = UUID.randomUUID();
        this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        this.worldObj = par1World;
        this.setPosition(0.0D, 0.0D, 0.0D);

        if (par1World != null)
        {
            this.dimension = par1World.provider.dimensionId;
        }

        this.dataWatcher = new DataWatcher(this);
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        this.entityInit();
    }

    protected abstract void entityInit();

    public DataWatcher getDataWatcher()
    {
        return this.dataWatcher;
    }

    public boolean equals(Object par1Obj)
    {
        return par1Obj instanceof Entity ? ((Entity)par1Obj).field_145783_c == this.field_145783_c : false;
    }

    public int hashCode()
    {
        return this.field_145783_c;
    }

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    protected void preparePlayerToSpawn()
    {
        if (this.worldObj != null)
        {
            while (this.posY > 0.0D)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
                {
                    break;
                }

                ++this.posY;
            }

            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.rotationPitch = 0.0F;
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        this.isDead = true;
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected void setSize(float par1, float par2)
    {
        float var3;

        if (par1 != this.width || par2 != this.height)
        {
            var3 = this.width;
            this.width = par1;
            this.height = par2;
            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;

            if (this.width > var3 && !this.firstUpdate && !this.worldObj.isClient)
            {
                this.moveEntity((double)(var3 - this.width), 0.0D, (double)(var3 - this.width));
            }
        }

        var3 = par1 % 2.0F;

        if ((double)var3 < 0.375D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_1;
        }
        else if ((double)var3 < 0.75D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        }
        else if ((double)var3 < 1.0D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_3;
        }
        else if ((double)var3 < 1.375D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_4;
        }
        else if ((double)var3 < 1.75D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_5;
        }
        else
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        }
    }

    /**
     * Sets the rotation of the entity
     */
    protected void setRotation(float par1, float par2)
    {
        this.rotationYaw = par1 % 360.0F;
        this.rotationPitch = par2 % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double par1, double par3, double par5)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        float var7 = this.width / 2.0F;
        float var8 = this.height;
        this.boundingBox.setBounds(par1 - (double)var7, par3 - (double)this.yOffset + (double)this.ySize, par5 - (double)var7, par1 + (double)var7, par3 - (double)this.yOffset + (double)this.ySize + (double)var8, par5 + (double)var7);
    }

    /**
     * Adds par1*0.15 to the entity's yaw, and *subtracts* par2*0.15 from the pitch. Clamps pitch from -90 to 90. Both
     * arguments in degrees.
     */
    public void setAngles(float par1, float par2)
    {
        if (Nebula.getBus().dispatch(new EventEntityTurn(this, par1, par2))) return;

        float var3 = this.rotationPitch;
        float var4 = this.rotationYaw;
        this.rotationYaw = (float)((double)this.rotationYaw + (double)par1 * 0.15D);
        this.rotationPitch = (float)((double)this.rotationPitch - (double)par2 * 0.15D);

        if (this.rotationPitch < -90.0F)
        {
            this.rotationPitch = -90.0F;
        }

        if (this.rotationPitch > 90.0F)
        {
            this.rotationPitch = 90.0F;
        }

        this.prevRotationPitch += this.rotationPitch - var3;
        this.prevRotationYaw += this.rotationYaw - var4;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.onEntityUpdate();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        this.worldObj.theProfiler.startSection("entityBaseTick");

        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        int var2;

        if (!this.worldObj.isClient && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer var1 = ((WorldServer)this.worldObj).func_73046_m();
            var2 = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (var1.getAllowNether())
                {
                    if (this.ridingEntity == null && this.portalCounter++ >= var2)
                    {
                        this.portalCounter = var2;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte var3;

                        if (this.worldObj.provider.dimensionId == -1)
                        {
                            var3 = 0;
                        }
                        else
                        {
                            var3 = -1;
                        }

                        this.travelToDimension(var3);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }

        if (this.isSprinting() && !this.isInWater())
        {
            int var5 = MathHelper.floor_double(this.posX);
            var2 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
            int var6 = MathHelper.floor_double(this.posZ);
            Block var4 = this.worldObj.getBlock(var5, var2, var6);

            if (var4.getMaterial() != Material.air)
            {
                this.worldObj.spawnParticle("blockcrack_" + Block.getIdFromBlock(var4) + "_" + this.worldObj.getBlockMetadata(var5, var2, var6), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.boundingBox.minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D);
            }
        }

        this.handleWaterMovement();

        if (this.worldObj.isClient)
        {
            this.fire = 0;
        }
        else if (this.fire > 0)
        {
            if (this.isImmuneToFire)
            {
                this.fire -= 4;

                if (this.fire < 0)
                {
                    this.fire = 0;
                }
            }
            else
            {
                if (this.fire % 20 == 0)
                {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                }

                --this.fire;
            }
        }

        if (this.handleLavaMovement())
        {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (!this.worldObj.isClient)
        {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime()
    {
        return 0;
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void setOnFireFromLava()
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.lava, 4.0F);
            this.setFire(15);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setFire(int par1)
    {
        int var2 = par1 * 20;
        var2 = EnchantmentProtection.getFireTimeForEntity(this, var2);

        if (this.fire < var2)
        {
            this.fire = var2;
        }
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.fire = 0;
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.setDead();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     */
    public boolean isOffsetPositionInLiquid(double par1, double par3, double par5)
    {
        AxisAlignedBB var7 = this.boundingBox.getOffsetBoundingBox(par1, par3, par5);
        List var8 = this.worldObj.getCollidingBoundingBoxes(this, var7);
        return !var8.isEmpty() ? false : !this.worldObj.isAnyLiquid(var7);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double par1, double par3, double par5)
    {
        if (this.noClip)
        {
            this.boundingBox.offset(par1, par3, par5);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            this.ySize *= 0.4F;
            double var7 = this.posX;
            double var9 = this.posY;
            double var11 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                par1 *= 0.25D;
                par3 *= 0.05000000074505806D;
                par5 *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double var13 = par1;
            double var15 = par3;
            double var17 = par5;
            AxisAlignedBB var19 = this.boundingBox.copy();
            boolean var20 = this.onGround && this.isSneaking() && this instanceof EntityPlayer;

            if (var20)
            {
                double var21;

                for (var21 = 0.05D; par1 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0D, 0.0D)).isEmpty(); var13 = par1)
                {
                    if (par1 < var21 && par1 >= -var21)
                    {
                        par1 = 0.0D;
                    }
                    else if (par1 > 0.0D)
                    {
                        par1 -= var21;
                    }
                    else
                    {
                        par1 += var21;
                    }
                }

                for (; par5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0D, -1.0D, par5)).isEmpty(); var17 = par5)
                {
                    if (par5 < var21 && par5 >= -var21)
                    {
                        par5 = 0.0D;
                    }
                    else if (par5 > 0.0D)
                    {
                        par5 -= var21;
                    }
                    else
                    {
                        par5 += var21;
                    }
                }

                while (par1 != 0.0D && par5 != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(par1, -1.0D, par5)).isEmpty())
                {
                    if (par1 < var21 && par1 >= -var21)
                    {
                        par1 = 0.0D;
                    }
                    else if (par1 > 0.0D)
                    {
                        par1 -= var21;
                    }
                    else
                    {
                        par1 += var21;
                    }

                    if (par5 < var21 && par5 >= -var21)
                    {
                        par5 = 0.0D;
                    }
                    else if (par5 > 0.0D)
                    {
                        par5 -= var21;
                    }
                    else
                    {
                        par5 += var21;
                    }

                    var13 = par1;
                    var17 = par5;
                }
            }

            List var36 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(par1, par3, par5));

            for (int var22 = 0; var22 < var36.size(); ++var22)
            {
                par3 = ((AxisAlignedBB)var36.get(var22)).calculateYOffset(this.boundingBox, par3);
            }

            this.boundingBox.offset(0.0D, par3, 0.0D);

            if (!this.field_70135_K && var15 != par3)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            boolean var37 = this.onGround || var15 != par3 && var15 < 0.0D;
            int var23;

            for (var23 = 0; var23 < var36.size(); ++var23)
            {
                par1 = ((AxisAlignedBB)var36.get(var23)).calculateXOffset(this.boundingBox, par1);
            }

            this.boundingBox.offset(par1, 0.0D, 0.0D);

            if (!this.field_70135_K && var13 != par1)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            for (var23 = 0; var23 < var36.size(); ++var23)
            {
                par5 = ((AxisAlignedBB)var36.get(var23)).calculateZOffset(this.boundingBox, par5);
            }

            this.boundingBox.offset(0.0D, 0.0D, par5);

            if (!this.field_70135_K && var17 != par5)
            {
                par5 = 0.0D;
                par3 = 0.0D;
                par1 = 0.0D;
            }

            double var25;
            double var27;
            int var30;
            double var38;

            if (this.stepHeight > 0.0F && var37 && (var20 || this.ySize < 0.05F) && (var13 != par1 || var17 != par5))
            {
                var38 = par1;
                var25 = par3;
                var27 = par5;
                par1 = var13;
                par3 = (double)this.stepHeight;
                par5 = var17;
                AxisAlignedBB var29 = this.boundingBox.copy();
                this.boundingBox.setBB(var19);
                var36 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(var13, par3, var17));

                for (var30 = 0; var30 < var36.size(); ++var30)
                {
                    par3 = ((AxisAlignedBB)var36.get(var30)).calculateYOffset(this.boundingBox, par3);
                }

                this.boundingBox.offset(0.0D, par3, 0.0D);

                if (!this.field_70135_K && var15 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (var30 = 0; var30 < var36.size(); ++var30)
                {
                    par1 = ((AxisAlignedBB)var36.get(var30)).calculateXOffset(this.boundingBox, par1);
                }

                this.boundingBox.offset(par1, 0.0D, 0.0D);

                if (!this.field_70135_K && var13 != par1)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                for (var30 = 0; var30 < var36.size(); ++var30)
                {
                    par5 = ((AxisAlignedBB)var36.get(var30)).calculateZOffset(this.boundingBox, par5);
                }

                this.boundingBox.offset(0.0D, 0.0D, par5);

                if (!this.field_70135_K && var17 != par5)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }

                if (!this.field_70135_K && var15 != par3)
                {
                    par5 = 0.0D;
                    par3 = 0.0D;
                    par1 = 0.0D;
                }
                else
                {
                    par3 = (double)(-this.stepHeight);

                    for (var30 = 0; var30 < var36.size(); ++var30)
                    {
                        par3 = ((AxisAlignedBB)var36.get(var30)).calculateYOffset(this.boundingBox, par3);
                    }

                    this.boundingBox.offset(0.0D, par3, 0.0D);
                }

                if (var38 * var38 + var27 * var27 >= par1 * par1 + par5 * par5)
                {
                    par1 = var38;
                    par3 = var25;
                    par5 = var27;
                    this.boundingBox.setBB(var29);
                } else {
                    Nebula.getBus().dispatch(new EventStep(this, boundingBox));
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
            this.isCollidedHorizontally = var13 != par1 || var17 != par5;
            this.isCollidedVertically = var15 != par3;
            this.onGround = var15 != par3 && var15 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(par3, this.onGround);

            if (var13 != par1)
            {
                this.motionX = 0.0D;
            }

            if (var15 != par3)
            {
                this.motionY = 0.0D;
            }

            if (var17 != par5)
            {
                this.motionZ = 0.0D;
            }

            var38 = this.posX - var7;
            var25 = this.posY - var9;
            var27 = this.posZ - var11;

            if (this.canTriggerWalking() && !var20 && this.ridingEntity == null)
            {
                int var39 = MathHelper.floor_double(this.posX);
                var30 = MathHelper.floor_double(this.posY - 0.20000000298023224D - (double)this.yOffset);
                int var31 = MathHelper.floor_double(this.posZ);
                Block var32 = this.worldObj.getBlock(var39, var30, var31);
                int var33 = this.worldObj.getBlock(var39, var30 - 1, var31).getRenderType();

                if (var33 == 11 || var33 == 32 || var33 == 21)
                {
                    var32 = this.worldObj.getBlock(var39, var30 - 1, var31);
                }

                if (var32 != Blocks.ladder)
                {
                    var25 = 0.0D;
                }

                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(var38 * var38 + var27 * var27) * 0.6D);
                this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(var38 * var38 + var25 * var25 + var27 * var27) * 0.6D);

                if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && var32.getMaterial() != Material.air)
                {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;

                    if (this.isInWater())
                    {
                        float var34 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.35F;

                        if (var34 > 1.0F)
                        {
                            var34 = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), var34, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.func_145780_a(var39, var30, var31, var32);
                    var32.onEntityWalking(this.worldObj, var39, var30, var31, this);
                }
            }

            try
            {
                this.func_145775_I();
            }
            catch (Throwable var35)
            {
                CrashReport var42 = CrashReport.makeCrashReport(var35, "Checking entity block collision");
                CrashReportCategory var41 = var42.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(var41);
                throw new ReportedException(var42);
            }

            boolean var40 = this.isWet();

            if (this.worldObj.func_147470_e(this.boundingBox.contract(0.001D, 0.001D, 0.001D)))
            {
                this.dealFireDamage(1);

                if (!var40)
                {
                    ++this.fire;

                    if (this.fire == 0)
                    {
                        this.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0)
            {
                this.fire = -this.fireResistance;
            }

            if (var40 && this.fire > 0)
            {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }
    }

    protected String getSwimSound()
    {
        return "game.neutral.swim";
    }

    protected void func_145775_I()
    {
        int var1 = MathHelper.floor_double(this.boundingBox.minX + 0.001D);
        int var2 = MathHelper.floor_double(this.boundingBox.minY + 0.001D);
        int var3 = MathHelper.floor_double(this.boundingBox.minZ + 0.001D);
        int var4 = MathHelper.floor_double(this.boundingBox.maxX - 0.001D);
        int var5 = MathHelper.floor_double(this.boundingBox.maxY - 0.001D);
        int var6 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001D);

        if (this.worldObj.checkChunksExist(var1, var2, var3, var4, var5, var6))
        {
            for (int var7 = var1; var7 <= var4; ++var7)
            {
                for (int var8 = var2; var8 <= var5; ++var8)
                {
                    for (int var9 = var3; var9 <= var6; ++var9)
                    {
                        Block var10 = this.worldObj.getBlock(var7, var8, var9);

                        try
                        {
                            var10.onEntityCollidedWithBlock(this.worldObj, var7, var8, var9, this);
                        }
                        catch (Throwable var14)
                        {
                            CrashReport var12 = CrashReport.makeCrashReport(var14, "Colliding entity with block");
                            CrashReportCategory var13 = var12.makeCategory("Block being collided with");
                            CrashReportCategory.func_147153_a(var13, var7, var8, var9, var10, this.worldObj.getBlockMetadata(var7, var8, var9));
                            throw new ReportedException(var12);
                        }
                    }
                }
            }
        }
    }

    protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_)
    {
        Block.SoundType var5 = p_145780_4_.stepSound;

        if (this.worldObj.getBlock(p_145780_1_, p_145780_2_ + 1, p_145780_3_) == Blocks.snow_layer)
        {
            var5 = Blocks.snow_layer.stepSound;
            this.playSound(var5.func_150498_e(), var5.func_150497_c() * 0.15F, var5.func_150494_d());
        }
        else if (!p_145780_4_.getMaterial().isLiquid())
        {
            this.playSound(var5.func_150498_e(), var5.func_150497_c() * 0.15F, var5.func_150494_d());
        }
    }

    public void playSound(String par1Str, float par2, float par3)
    {
        this.worldObj.playSoundAtEntity(this, par1Str, par2, par3);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return true;
    }

    /**
     * Takes in the distance the entity has fallen this tick and whether its on the ground to update the fall distance
     * and deal fall damage if landing on the ground.  Args: distanceFallenThisTick, onGround
     */
    protected void updateFallState(double par1, boolean par3)
    {
        if (par3)
        {
            if (this.fallDistance > 0.0F)
            {
                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        }
        else if (par1 < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - par1);
        }
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int par1)
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.inFire, (float)par1);
        }
    }

    public final boolean isImmuneToFire()
    {
        return this.isImmuneToFire;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float par1)
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.fall(par1);
        }
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean isWet()
    {
        return this.inWater || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double)this.height), MathHelper.floor_double(this.posZ));
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean isInWater()
    {
        return this.inWater;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        if (this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                float var1 = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

                if (var1 > 1.0F)
                {
                    var1 = 1.0F;
                }

                this.playSound(this.getSplashSound(), var1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                float var2 = (float)MathHelper.floor_double(this.boundingBox.minY);
                int var3;
                float var4;
                float var5;

                for (var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3)
                {
                    var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("bubble", this.posX + (double)var4, (double)(var2 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ);
                }

                for (var3 = 0; (float)var3 < 1.0F + this.width * 20.0F; ++var3)
                {
                    var4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    var5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + (double)var4, (double)(var2 + 1.0F), this.posZ + (double)var5, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        }
        else
        {
            this.inWater = false;
        }

        return this.inWater;
    }

    protected String getSplashSound()
    {
        return "game.neutral.swim.splash";
    }

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    public boolean isInsideOfMaterial(Material par1Material)
    {
        double var2 = this.posY + (double)this.getEyeHeight();
        int var4 = MathHelper.floor_double(this.posX);
        int var5 = MathHelper.floor_float((float)MathHelper.floor_double(var2));
        int var6 = MathHelper.floor_double(this.posZ);
        Block var7 = this.worldObj.getBlock(var4, var5, var6);

        if (var7.getMaterial() == par1Material)
        {
            float var8 = BlockLiquid.func_149801_b(this.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111F;
            float var9 = (float)(var5 + 1) - var8;
            return var2 < (double)var9;
        }
        else
        {
            return false;
        }
    }

    public float getEyeHeight()
    {
        return 0.0F;
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean handleLavaMovement()
    {
        return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }

    /**
     * Used in both water and by flying objects
     */
    public void moveFlying(float par1, float par2, float par3)
    {
        float var4 = par1 * par1 + par2 * par2;

        if (var4 >= 1.0E-4F)
        {
            var4 = MathHelper.sqrt_float(var4);

            if (var4 < 1.0F)
            {
                var4 = 1.0F;
            }

            var4 = par3 / var4;
            par1 *= var4;
            par2 *= var4;
            float var5 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
            float var6 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
            this.motionX += (double)(par1 * var6 - par2 * var5);
            this.motionZ += (double)(par2 * var6 + par1 * var5);
        }
    }

    public int getBrightnessForRender(float par1)
    {
        int var2 = MathHelper.floor_double(this.posX);
        int var3 = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(var2, 0, var3))
        {
            double var4 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int var6 = MathHelper.floor_double(this.posY - (double)this.yOffset + var4);
            return this.worldObj.getLightBrightnessForSkyBlocks(var2, var6, var3, 0);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float par1)
    {
        int var2 = MathHelper.floor_double(this.posX);
        int var3 = MathHelper.floor_double(this.posZ);

        if (this.worldObj.blockExists(var2, 0, var3))
        {
            double var4 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
            int var6 = MathHelper.floor_double(this.posY - (double)this.yOffset + var4);
            return this.worldObj.getLightBrightness(var2, var6, var3);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(World par1World)
    {
        this.worldObj = par1World;
    }

    /**
     * Sets the entity's position and rotation. Args: posX, posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation(double par1, double par3, double par5, float par7, float par8)
    {
        this.prevPosX = this.posX = par1;
        this.prevPosY = this.posY = par3;
        this.prevPosZ = this.posZ = par5;
        this.prevRotationYaw = this.rotationYaw = par7;
        this.prevRotationPitch = this.rotationPitch = par8;
        this.ySize = 0.0F;
        double var9 = (double)(this.prevRotationYaw - par7);

        if (var9 < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }

        if (var9 >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(par7, par8);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double par1, double par3, double par5, float par7, float par8)
    {
        this.lastTickPosX = this.prevPosX = this.posX = par1;
        this.lastTickPosY = this.prevPosY = this.posY = par3 + (double)this.yOffset;
        this.lastTickPosZ = this.prevPosZ = this.posZ = par5;
        this.rotationYaw = par7;
        this.rotationPitch = par8;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float getDistanceToEntity(Entity par1Entity)
    {
        float var2 = (float)(this.posX - par1Entity.posX);
        float var3 = (float)(this.posY - par1Entity.posY);
        float var4 = (float)(this.posZ - par1Entity.posZ);
        return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
    }

    /**
     * Gets the squared distance to the position. Args: x, y, z
     */
    public double getDistanceSq(double par1, double par3, double par5)
    {
        double var7 = this.posX - par1;
        double var9 = this.posY - par3;
        double var11 = this.posZ - par5;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * Gets the distance to the position. Args: x, y, z
     */
    public double getDistance(double par1, double par3, double par5)
    {
        double var7 = this.posX - par1;
        double var9 = this.posY - par3;
        double var11 = this.posZ - par5;
        return (double)MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
    }

    /**
     * Returns the squared distance to the entity. Args: entity
     */
    public double getDistanceSqToEntity(Entity par1Entity)
    {
        double var2 = this.posX - par1Entity.posX;
        double var4 = this.posY - par1Entity.posY;
        double var6 = this.posZ - par1Entity.posZ;
        return var2 * var2 + var4 * var4 + var6 * var6;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {}

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity par1Entity)
    {
        if (par1Entity.riddenByEntity != this && par1Entity.ridingEntity != this)
        {
            double var2 = par1Entity.posX - this.posX;
            double var4 = par1Entity.posZ - this.posZ;
            double var6 = MathHelper.abs_max(var2, var4);

            if (var6 >= 0.009999999776482582D)
            {
                var6 = (double)MathHelper.sqrt_double(var6);
                var2 /= var6;
                var4 /= var6;
                double var8 = 1.0D / var6;

                if (var8 > 1.0D)
                {
                    var8 = 1.0D;
                }

                var2 *= var8;
                var4 *= var8;
                var2 *= 0.05000000074505806D;
                var4 *= 0.05000000074505806D;
                var2 *= (double)(1.0F - this.entityCollisionReduction);
                var4 *= (double)(1.0F - this.entityCollisionReduction);
                this.addVelocity(-var2, 0.0D, -var4);
                par1Entity.addVelocity(var2, 0.0D, var4);
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double par1, double par3, double par5)
    {
        this.motionX += par1;
        this.motionY += par3;
        this.motionZ += par5;
        this.isAirBorne = true;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        this.velocityChanged = true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            return false;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(Entity par1Entity, int par2) {}

    public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_)
    {
        double var7 = this.posX - p_145770_1_;
        double var9 = this.posY - p_145770_3_;
        double var11 = this.posZ - p_145770_5_;
        double var13 = var7 * var7 + var9 * var9 + var11 * var11;
        return this.isInRangeToRenderDist(var13);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double par1)
    {
        double var3 = this.boundingBox.getAverageEdgeLength();
        var3 *= 64.0D * this.renderDistanceWeight;
        return par1 < var3 * var3;
    }

    /**
     * Like writeToNBTOptional but does not check if the entity is ridden. Used for saving ridden entities with their
     * riders.
     */
    public boolean writeMountToNBT(NBTTagCompound par1NBTTagCompound)
    {
        String var2 = this.getEntityString();

        if (!this.isDead && var2 != null)
        {
            par1NBTTagCompound.setString("id", var2);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound)
    {
        String var2 = this.getEntityString();

        if (!this.isDead && var2 != null && this.riddenByEntity == null)
        {
            par1NBTTagCompound.setString("id", var2);
            this.writeToNBT(par1NBTTagCompound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        try
        {
            par1NBTTagCompound.setTag("Pos", this.newDoubleNBTList(new double[] {this.posX, this.posY + (double)this.ySize, this.posZ}));
            par1NBTTagCompound.setTag("Motion", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));
            par1NBTTagCompound.setTag("Rotation", this.newFloatNBTList(new float[] {this.rotationYaw, this.rotationPitch}));
            par1NBTTagCompound.setFloat("FallDistance", this.fallDistance);
            par1NBTTagCompound.setShort("Fire", (short)this.fire);
            par1NBTTagCompound.setShort("Air", (short)this.getAir());
            par1NBTTagCompound.setBoolean("OnGround", this.onGround);
            par1NBTTagCompound.setInteger("Dimension", this.dimension);
            par1NBTTagCompound.setBoolean("Invulnerable", this.invulnerable);
            par1NBTTagCompound.setInteger("PortalCooldown", this.timeUntilPortal);
            par1NBTTagCompound.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            par1NBTTagCompound.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
            this.writeEntityToNBT(par1NBTTagCompound);

            if (this.ridingEntity != null)
            {
                NBTTagCompound var2 = new NBTTagCompound();

                if (this.ridingEntity.writeMountToNBT(var2))
                {
                    par1NBTTagCompound.setTag("Riding", var2);
                }
            }
        }
        catch (Throwable var5)
        {
            CrashReport var3 = CrashReport.makeCrashReport(var5, "Saving entity NBT");
            CrashReportCategory var4 = var3.makeCategory("Entity being saved");
            this.addEntityCrashInfo(var4);
            throw new ReportedException(var3);
        }
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        try
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("Pos", 6);
            NBTTagList var6 = par1NBTTagCompound.getTagList("Motion", 6);
            NBTTagList var7 = par1NBTTagCompound.getTagList("Rotation", 5);
            this.motionX = var6.func_150309_d(0);
            this.motionY = var6.func_150309_d(1);
            this.motionZ = var6.func_150309_d(2);

            if (Math.abs(this.motionX) > 10.0D)
            {
                this.motionX = 0.0D;
            }

            if (Math.abs(this.motionY) > 10.0D)
            {
                this.motionY = 0.0D;
            }

            if (Math.abs(this.motionZ) > 10.0D)
            {
                this.motionZ = 0.0D;
            }

            this.prevPosX = this.lastTickPosX = this.posX = var2.func_150309_d(0);
            this.prevPosY = this.lastTickPosY = this.posY = var2.func_150309_d(1);
            this.prevPosZ = this.lastTickPosZ = this.posZ = var2.func_150309_d(2);
            this.prevRotationYaw = this.rotationYaw = var7.func_150308_e(0);
            this.prevRotationPitch = this.rotationPitch = var7.func_150308_e(1);
            this.fallDistance = par1NBTTagCompound.getFloat("FallDistance");
            this.fire = par1NBTTagCompound.getShort("Fire");
            this.setAir(par1NBTTagCompound.getShort("Air"));
            this.onGround = par1NBTTagCompound.getBoolean("OnGround");
            this.dimension = par1NBTTagCompound.getInteger("Dimension");
            this.invulnerable = par1NBTTagCompound.getBoolean("Invulnerable");
            this.timeUntilPortal = par1NBTTagCompound.getInteger("PortalCooldown");

            if (par1NBTTagCompound.func_150297_b("UUIDMost", 4) && par1NBTTagCompound.func_150297_b("UUIDLeast", 4))
            {
                this.entityUniqueID = new UUID(par1NBTTagCompound.getLong("UUIDMost"), par1NBTTagCompound.getLong("UUIDLeast"));
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.readEntityFromNBT(par1NBTTagCompound);

            if (this.shouldSetPosAfterLoading())
            {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
        catch (Throwable var5)
        {
            CrashReport var3 = CrashReport.makeCrashReport(var5, "Loading entity NBT");
            CrashReportCategory var4 = var3.makeCategory("Entity being loaded");
            this.addEntityCrashInfo(var4);
            throw new ReportedException(var3);
        }
    }

    protected boolean shouldSetPosAfterLoading()
    {
        return true;
    }

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String getEntityString()
    {
        return EntityList.getEntityString(this);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void readEntityFromNBT(NBTTagCompound var1);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected abstract void writeEntityToNBT(NBTTagCompound var1);

    public void onChunkLoad() {}

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected NBTTagList newDoubleNBTList(double ... par1ArrayOfDouble)
    {
        NBTTagList var2 = new NBTTagList();
        double[] var3 = par1ArrayOfDouble;
        int var4 = par1ArrayOfDouble.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            double var6 = var3[var5];
            var2.appendTag(new NBTTagDouble(var6));
        }

        return var2;
    }

    /**
     * Returns a new NBTTagList filled with the specified floats
     */
    protected NBTTagList newFloatNBTList(float ... par1ArrayOfFloat)
    {
        NBTTagList var2 = new NBTTagList();
        float[] var3 = par1ArrayOfFloat;
        int var4 = par1ArrayOfFloat.length;

        for (int var5 = 0; var5 < var4; ++var5)
        {
            float var6 = var3[var5];
            var2.appendTag(new NBTTagFloat(var6));
        }

        return var2;
    }

    public float getShadowSize()
    {
        return this.height / 2.0F;
    }

    public EntityItem func_145779_a(Item p_145779_1_, int p_145779_2_)
    {
        return this.func_145778_a(p_145779_1_, p_145779_2_, 0.0F);
    }

    public EntityItem func_145778_a(Item p_145778_1_, int p_145778_2_, float p_145778_3_)
    {
        return this.entityDropItem(new ItemStack(p_145778_1_, p_145778_2_, 0), p_145778_3_);
    }

    /**
     * Drops an item at the position of the entity.
     */
    public EntityItem entityDropItem(ItemStack par1ItemStack, float par2)
    {
        if (par1ItemStack.stackSize != 0 && par1ItemStack.getItem() != null)
        {
            EntityItem var3 = new EntityItem(this.worldObj, this.posX, this.posY + (double)par2, this.posZ, par1ItemStack);
            var3.delayBeforeCanPickup = 10;
            this.worldObj.spawnEntityInWorld(var3);
            return var3;
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        for (int var1 = 0; var1 < 8; ++var1)
        {
            float var2 = ((float)((var1 >> 0) % 2) - 0.5F) * this.width * 0.8F;
            float var3 = ((float)((var1 >> 1) % 2) - 0.5F) * 0.1F;
            float var4 = ((float)((var1 >> 2) % 2) - 0.5F) * this.width * 0.8F;
            int var5 = MathHelper.floor_double(this.posX + (double)var2);
            int var6 = MathHelper.floor_double(this.posY + (double)this.getEyeHeight() + (double)var3);
            int var7 = MathHelper.floor_double(this.posZ + (double)var4);

            if (this.worldObj.getBlock(var5, var6, var7).isNormalCube())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer par1EntityPlayer)
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return null;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        if (this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();

            if (this.ridingEntity != null)
            {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

                for (this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D)
                {
                    ;
                }

                while (this.entityRiderYawDelta < -180.0D)
                {
                    this.entityRiderYawDelta += 360.0D;
                }

                while (this.entityRiderPitchDelta >= 180.0D)
                {
                    this.entityRiderPitchDelta -= 360.0D;
                }

                while (this.entityRiderPitchDelta < -180.0D)
                {
                    this.entityRiderPitchDelta += 360.0D;
                }

                double var1 = this.entityRiderYawDelta * 0.5D;
                double var3 = this.entityRiderPitchDelta * 0.5D;
                float var5 = 10.0F;

                if (var1 > (double)var5)
                {
                    var1 = (double)var5;
                }

                if (var1 < (double)(-var5))
                {
                    var1 = (double)(-var5);
                }

                if (var3 > (double)var5)
                {
                    var3 = (double)var5;
                }

                if (var3 < (double)(-var5))
                {
                    var3 = (double)(-var5);
                }

                this.entityRiderYawDelta -= var1;
                this.entityRiderPitchDelta -= var3;
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return (double)this.yOffset;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.75D;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        this.entityRiderPitchDelta = 0.0D;
        this.entityRiderYawDelta = 0.0D;

        if (par1Entity == null)
        {
            if (this.ridingEntity != null)
            {
                this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else
        {
            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = par1Entity;
            par1Entity.riddenByEntity = this;
        }
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
        List var10 = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.contract(0.03125D, 0.0D, 0.03125D));

        if (!var10.isEmpty())
        {
            double var11 = 0.0D;

            for (int var13 = 0; var13 < var10.size(); ++var13)
            {
                AxisAlignedBB var14 = (AxisAlignedBB)var10.get(var13);

                if (var14.maxY > var11)
                {
                    var11 = var14.maxY;
                }
            }

            par3 += var11 - this.boundingBox.minY;
            this.setPosition(par1, par3, par5);
        }
    }

    public float getCollisionBorderSize()
    {
        return 0.1F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return null;
    }

    /**
     * Called by portal blocks when an entity is within it.
     */
    public void setInPortal()
    {
        if (this.timeUntilPortal > 0)
        {
            this.timeUntilPortal = this.getPortalCooldown();
        }
        else
        {
            double var1 = this.prevPosX - this.posX;
            double var3 = this.prevPosZ - this.posZ;

            if (!this.worldObj.isClient && !this.inPortal)
            {
                this.teleportDirection = Direction.getMovementDirection(var1, var3);
            }

            this.inPortal = true;
        }
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 300;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double par1, double par3, double par5)
    {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
    }

    public void handleHealthUpdate(byte par1) {}

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation() {}

    public ItemStack[] getLastActiveItems()
    {
        return null;
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack) {}

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        boolean var1 = this.worldObj != null && this.worldObj.isClient;
        return !this.isImmuneToFire && (this.fire > 0 || var1 && this.getFlag(0));
    }

    /**
     * Returns true if the entity is riding another entity, used by render to rotate the legs to be in 'sit' position
     * for players.
     */
    public boolean isRiding()
    {
        return this.ridingEntity != null;
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.getFlag(1);
    }

    /**
     * Sets the sneaking flag.
     */
    public void setSneaking(boolean par1)
    {
        this.setFlag(1, par1);
    }

    /**
     * Get if the Entity is sprinting.
     */
    public boolean isSprinting()
    {
        return this.getFlag(3);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean par1)
    {
        this.setFlag(3, par1);
    }

    public boolean isInvisible()
    {
        return this.getFlag(5);
    }

    /**
     * Only used by renderer in EntityLivingBase subclasses.\nDetermines if an entity is visible or not to a specfic
     * player, if the entity is normally invisible.\nFor EntityLivingBase subclasses, returning false when invisible
     * will render the entity semitransparent.
     */
    public boolean isInvisibleToPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.isInvisible();
    }

    public void setInvisible(boolean par1)
    {
        this.setFlag(5, par1);
    }

    public boolean isEating()
    {
        return this.getFlag(4);
    }

    public void setEating(boolean par1)
    {
        this.setFlag(4, par1);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean getFlag(int par1)
    {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << par1) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(int par1, boolean par2)
    {
        byte var3 = this.dataWatcher.getWatchableObjectByte(0);

        if (par2)
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(var3 | 1 << par1)));
        }
        else
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(var3 & ~(1 << par1))));
        }
    }

    public int getAir()
    {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(int par1)
    {
        this.dataWatcher.updateObject(1, Short.valueOf((short)par1));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt)
    {
        this.dealFireDamage(5);
        ++this.fire;

        if (this.fire == 0)
        {
            this.setFire(8);
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLivingBase par1EntityLivingBase) {}

    protected boolean func_145771_j(double p_145771_1_, double p_145771_3_, double p_145771_5_)
    {
        int var7 = MathHelper.floor_double(p_145771_1_);
        int var8 = MathHelper.floor_double(p_145771_3_);
        int var9 = MathHelper.floor_double(p_145771_5_);
        double var10 = p_145771_1_ - (double)var7;
        double var12 = p_145771_3_ - (double)var8;
        double var14 = p_145771_5_ - (double)var9;
        List var16 = this.worldObj.func_147461_a(this.boundingBox);

        if (var16.isEmpty() && !this.worldObj.func_147469_q(var7, var8, var9))
        {
            return false;
        }
        else
        {
            boolean var17 = !this.worldObj.func_147469_q(var7 - 1, var8, var9);
            boolean var18 = !this.worldObj.func_147469_q(var7 + 1, var8, var9);
            boolean var19 = !this.worldObj.func_147469_q(var7, var8 - 1, var9);
            boolean var20 = !this.worldObj.func_147469_q(var7, var8 + 1, var9);
            boolean var21 = !this.worldObj.func_147469_q(var7, var8, var9 - 1);
            boolean var22 = !this.worldObj.func_147469_q(var7, var8, var9 + 1);
            byte var23 = 3;
            double var24 = 9999.0D;

            if (var17 && var10 < var24)
            {
                var24 = var10;
                var23 = 0;
            }

            if (var18 && 1.0D - var10 < var24)
            {
                var24 = 1.0D - var10;
                var23 = 1;
            }

            if (var20 && 1.0D - var12 < var24)
            {
                var24 = 1.0D - var12;
                var23 = 3;
            }

            if (var21 && var14 < var24)
            {
                var24 = var14;
                var23 = 4;
            }

            if (var22 && 1.0D - var14 < var24)
            {
                var24 = 1.0D - var14;
                var23 = 5;
            }

            float var26 = this.rand.nextFloat() * 0.2F + 0.1F;

            if (var23 == 0)
            {
                this.motionX = (double)(-var26);
            }

            if (var23 == 1)
            {
                this.motionX = (double)var26;
            }

            if (var23 == 2)
            {
                this.motionY = (double)(-var26);
            }

            if (var23 == 3)
            {
                this.motionY = (double)var26;
            }

            if (var23 == 4)
            {
                this.motionZ = (double)(-var26);
            }

            if (var23 == 5)
            {
                this.motionZ = (double)var26;
            }

            return true;
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName()
    {
        String var1 = EntityList.getEntityString(this);

        if (var1 == null)
        {
            var1 = "generic";
        }

        return StatCollector.translateToLocal("entity." + var1 + ".name");
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return null;
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity par1Entity)
    {
        return this == par1Entity;
    }

    public float getRotationYawHead()
    {
        return 0.0F;
    }

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(float par1) {}

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(Entity par1Entity)
    {
        return false;
    }

    public String toString()
    {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] {this.getClass().getSimpleName(), this.getCommandSenderName(), Integer.valueOf(this.field_145783_c), this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(), Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)});
    }

    /**
     * Return whether this entity is invulnerable to damage.
     */
    public boolean isEntityInvulnerable()
    {
        return this.invulnerable;
    }

    /**
     * Sets this entity's location and angles to the location and angles of the passed in entity.
     */
    public void copyLocationAndAnglesFrom(Entity par1Entity)
    {
        this.setLocationAndAngles(par1Entity.posX, par1Entity.posY, par1Entity.posZ, par1Entity.rotationYaw, par1Entity.rotationPitch);
    }

    /**
     * Copies important data from another entity to this entity. Used when teleporting entities between worlds, as this
     * actually deletes the teleporting entity and re-creates it on the other side. Params: Entity to copy from, unused
     * (always true)
     */
    public void copyDataFrom(Entity par1Entity, boolean par2)
    {
        NBTTagCompound var3 = new NBTTagCompound();
        par1Entity.writeToNBT(var3);
        this.readFromNBT(var3);
        this.timeUntilPortal = par1Entity.timeUntilPortal;
        this.teleportDirection = par1Entity.teleportDirection;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(int par1)
    {
        if (!this.worldObj.isClient && !this.isDead)
        {
            this.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer var2 = MinecraftServer.getServer();
            int var3 = this.dimension;
            WorldServer var4 = var2.worldServerForDimension(var3);
            WorldServer var5 = var2.worldServerForDimension(par1);
            this.dimension = par1;

            if (var3 == 1 && par1 == 1)
            {
                var5 = var2.worldServerForDimension(0);
                this.dimension = 0;
            }

            this.worldObj.removeEntity(this);
            this.isDead = false;
            this.worldObj.theProfiler.startSection("reposition");
            var2.getConfigurationManager().transferEntityToWorld(this, var3, var4, var5);
            this.worldObj.theProfiler.endStartSection("reloading");
            Entity var6 = EntityList.createEntityByName(EntityList.getEntityString(this), var5);

            if (var6 != null)
            {
                var6.copyDataFrom(this, true);

                if (var3 == 1 && par1 == 1)
                {
                    ChunkCoordinates var7 = var5.getSpawnPoint();
                    var7.posY = this.worldObj.getTopSolidOrLiquidBlock(var7.posX, var7.posZ);
                    var6.setLocationAndAngles((double)var7.posX, (double)var7.posY, (double)var7.posZ, var6.rotationYaw, var6.rotationPitch);
                }

                var5.spawnEntityInWorld(var6);
            }

            this.isDead = true;
            this.worldObj.theProfiler.endSection();
            var4.resetUpdateEntityTick();
            var5.resetUpdateEntityTick();
            this.worldObj.theProfiler.endSection();
        }
    }

    public float func_145772_a(Explosion p_145772_1_, World p_145772_2_, int p_145772_3_, int p_145772_4_, int p_145772_5_, Block p_145772_6_)
    {
        return p_145772_6_.getExplosionResistance(this);
    }

    public boolean func_145774_a(Explosion p_145774_1_, World p_145774_2_, int p_145774_3_, int p_145774_4_, int p_145774_5_, Block p_145774_6_, float p_145774_7_)
    {
        return true;
    }

    /**
     * The number of iterations PathFinder.getSafePoint will execute before giving up.
     */
    public int getMaxSafePointTries()
    {
        return 3;
    }

    public int getTeleportDirection()
    {
        return this.teleportDirection;
    }

    public boolean doesEntityNotTriggerPressurePlate()
    {
        return false;
    }

    public void addEntityCrashInfo(CrashReportCategory par1CrashReportCategory)
    {
        par1CrashReportCategory.addCrashSectionCallable("Entity Type", new Callable()
        {
            private static final String __OBFID = "CL_00001534";
            public String call()
            {
                return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }
        });
        par1CrashReportCategory.addCrashSection("Entity ID", Integer.valueOf(this.field_145783_c));
        par1CrashReportCategory.addCrashSectionCallable("Entity Name", new Callable()
        {
            private static final String __OBFID = "CL_00001535";
            public String call()
            {
                return Entity.this.getCommandSenderName();
            }
        });
        par1CrashReportCategory.addCrashSection("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)}));
        par1CrashReportCategory.addCrashSection("Entity\'s Block location", CrashReportCategory.getLocationInfo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)));
        par1CrashReportCategory.addCrashSection("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.motionX), Double.valueOf(this.motionY), Double.valueOf(this.motionZ)}));
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return this.isBurning();
    }

    public UUID getUniqueID()
    {
        return this.entityUniqueID;
    }

    public boolean isPushedByWater()
    {
        return true;
    }

    public IChatComponent func_145748_c_()
    {
        return new ChatComponentText(this.getCommandSenderName());
    }

    public void func_145781_i(int p_145781_1_) {}

    public static enum EnumEntitySize
    {
        SIZE_1("SIZE_1", 0),
        SIZE_2("SIZE_2", 1),
        SIZE_3("SIZE_3", 2),
        SIZE_4("SIZE_4", 3),
        SIZE_5("SIZE_5", 4),
        SIZE_6("SIZE_6", 5);

        private static final Entity.EnumEntitySize[] $VALUES = new Entity.EnumEntitySize[]{SIZE_1, SIZE_2, SIZE_3, SIZE_4, SIZE_5, SIZE_6};
        private static final String __OBFID = "CL_00001537";

        private EnumEntitySize(String par1Str, int par2) {}

        public int multiplyBy32AndRound(double par1)
        {
            double var3 = par1 - ((double)MathHelper.floor_double(par1) + 0.5D);

            switch (Entity.SwitchEnumEntitySize.field_96565_a[this.ordinal()])
            {
                case 1:
                    if (var3 < 0.0D)
                    {
                        if (var3 < -0.3125D)
                        {
                            return MathHelper.ceiling_double_int(par1 * 32.0D);
                        }
                    }
                    else if (var3 < 0.3125D)
                    {
                        return MathHelper.ceiling_double_int(par1 * 32.0D);
                    }

                    return MathHelper.floor_double(par1 * 32.0D);

                case 2:
                    if (var3 < 0.0D)
                    {
                        if (var3 < -0.3125D)
                        {
                            return MathHelper.floor_double(par1 * 32.0D);
                        }
                    }
                    else if (var3 < 0.3125D)
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }

                    return MathHelper.ceiling_double_int(par1 * 32.0D);

                case 3:
                    if (var3 > 0.0D)
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }

                    return MathHelper.ceiling_double_int(par1 * 32.0D);

                case 4:
                    if (var3 < 0.0D)
                    {
                        if (var3 < -0.1875D)
                        {
                            return MathHelper.ceiling_double_int(par1 * 32.0D);
                        }
                    }
                    else if (var3 < 0.1875D)
                    {
                        return MathHelper.ceiling_double_int(par1 * 32.0D);
                    }

                    return MathHelper.floor_double(par1 * 32.0D);

                case 5:
                    if (var3 < 0.0D)
                    {
                        if (var3 < -0.1875D)
                        {
                            return MathHelper.floor_double(par1 * 32.0D);
                        }
                    }
                    else if (var3 < 0.1875D)
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }

                    return MathHelper.ceiling_double_int(par1 * 32.0D);

                case 6:
                default:
                    if (var3 > 0.0D)
                    {
                        return MathHelper.ceiling_double_int(par1 * 32.0D);
                    }
                    else
                    {
                        return MathHelper.floor_double(par1 * 32.0D);
                    }
            }
        }
    }

    static final class SwitchEnumEntitySize
    {
        static final int[] field_96565_a = new int[Entity.EnumEntitySize.values().length];
        private static final String __OBFID = "CL_00001536";

        static
        {
            try
            {
                field_96565_a[Entity.EnumEntitySize.SIZE_1.ordinal()] = 1;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                field_96565_a[Entity.EnumEntitySize.SIZE_2.ordinal()] = 2;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                field_96565_a[Entity.EnumEntitySize.SIZE_3.ordinal()] = 3;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                field_96565_a[Entity.EnumEntitySize.SIZE_4.ordinal()] = 4;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                field_96565_a[Entity.EnumEntitySize.SIZE_5.ordinal()] = 5;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                field_96565_a[Entity.EnumEntitySize.SIZE_6.ordinal()] = 6;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
