package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import net.minecraft.world.World;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.player.EventSwingArm;

public class EntityClientPlayerMP extends EntityPlayerSP
{
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter field_146108_bO;
    public double oldPosX;
    public double oldMinY;
    public double oldPosY;
    public double oldPosZ;
    private float oldRotationYaw;
    private float oldRotationPitch;
    private boolean wasOnGround;
    private boolean shouldStopSneaking;
    private boolean wasSneaking;
    private int ticksSinceMovePacket;
    private boolean hasSetHealth;
    private String field_142022_ce;
    private static final String __OBFID = "CL_00000887";

    public EntityClientPlayerMP(Minecraft p_i45064_1_, World p_i45064_2_, Session p_i45064_3_, NetHandlerPlayClient p_i45064_4_, StatFileWriter p_i45064_5_)
    {
        super(p_i45064_1_, p_i45064_2_, p_i45064_3_, 0);
        this.sendQueue = p_i45064_4_;
        this.field_146108_bO = p_i45064_5_;
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        return false;
    }

    public void heal(float par1) {}

    public void mountEntity(Entity par1Entity)
    {
        super.mountEntity(par1Entity);

        if (par1Entity instanceof EntityMinecart)
        {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)par1Entity));
        }
    }

    public void onUpdate()
    {
        if (this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)))
        {
            super.onUpdate();

            if (this.isRiding())
            {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            }
            else
            {
                this.sendMotionUpdates();
            }
        }
    }

    public void sendMotionUpdates()
    {
        EventMotionUpdate event = new EventMotionUpdate(posX, boundingBox.minY, posY, posZ, rotationYaw, rotationPitch, onGround);
        Nebula.BUS.post(event);

        if (event.isCancelled()) {
            return;
        }

        double x = event.x;
        double y = event.y;
        double stance = event.stance;
        double z = event.z;

        float yaw = event.yaw;
        float pitch = event.pitch;

        boolean ground = event.onGround;

        boolean var1 = this.isSprinting();

        if (var1 != this.wasSneaking)
        {
            if (var1)
            {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 4));
            }
            else
            {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 5));
            }

            this.wasSneaking = var1;
        }

        boolean var2 = this.isSneaking();

        if (var2 != this.shouldStopSneaking)
        {
            if (var2)
            {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 1));
            }
            else
            {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 2));
            }

            this.shouldStopSneaking = var2;
        }

        double var3 = x - this.oldPosX;
        double var5 = y - this.oldMinY;
        double var7 = z - this.oldPosZ;
        double var9 = (double)(yaw - this.oldRotationYaw);
        double var11 = (double)(pitch - this.oldRotationPitch);
        boolean var13 = var3 * var3 + var5 * var5 + var7 * var7 > 9.0E-4D || this.ticksSinceMovePacket >= 20;
        boolean var14 = var9 != 0.0D || var11 != 0.0D;

        if (this.ridingEntity != null)
        {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, -999.0D, this.motionZ, yaw, pitch, ground));
            var13 = false;
        }
        else if (var13 && var14)
        {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, stance, z, yaw, pitch, ground));
        }
        else if (var13)
        {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, stance, z, ground));
        }
        else if (var14)
        {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, ground));
        }
        else
        {
            this.sendQueue.addToSendQueue(new C03PacketPlayer(ground));
        }

        ++this.ticksSinceMovePacket;
        this.wasOnGround = ground;

        if (var13)
        {
            this.oldPosX = x;
            this.oldMinY = y;
            this.oldPosY = stance;
            this.oldPosZ = z;
            this.ticksSinceMovePacket = 0;
        }

        if (var14)
        {
            this.oldRotationYaw = yaw;
            this.oldRotationPitch = pitch;
        }

        Nebula.BUS.post(new EventMotionUpdate());
    }

    public EntityItem dropOneItem(boolean par1)
    {
        int var2 = par1 ? 3 : 4;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(var2, 0, 0, 0, 0));
        return null;
    }

    protected void joinEntityItemWithWorld(EntityItem par1EntityItem) {}

    public void sendChatMessage(String par1Str)
    {
        this.sendQueue.addToSendQueue(new C01PacketChatMessage(par1Str));
    }

    public void swingItem()
    {
        if (!Nebula.BUS.post(new EventSwingArm(this))) {
            super.swingItem();
            this.sendQueue.addToSendQueue(new C0APacketAnimation(this, 1));
        }
    }

    public void swingItemSilent() {
        this.sendQueue.addToSendQueue(new C0APacketAnimation(this, 1));
    }

    public void respawnPlayer()
    {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    protected void damageEntity(DamageSource par1DamageSource, float par2)
    {
        if (!this.isEntityInvulnerable())
        {
            this.setHealth(this.getHealth() - par2);
        }
    }

    public void closeScreen()
    {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenNoPacket();
    }

    public void closeScreenNoPacket()
    {
        this.inventory.setItemStack((ItemStack)null);
        super.closeScreen();
    }

    public void setPlayerSPHealth(float par1)
    {
        if (this.hasSetHealth)
        {
            super.setPlayerSPHealth(par1);
        }
        else
        {
            this.setHealth(par1);
            this.hasSetHealth = true;
        }
    }

    public void addStat(StatBase par1StatBase, int par2)
    {
        if (par1StatBase != null)
        {
            if (par1StatBase.isIndependent)
            {
                super.addStat(par1StatBase, par2);
            }
        }
    }

    public void sendPlayerAbilities()
    {
        this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
    }

    protected void func_110318_g()
    {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 6, (int)(this.getHorseJumpPower() * 100.0F)));
    }

    public void func_110322_i()
    {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, 7));
    }

    public void func_142020_c(String par1Str)
    {
        this.field_142022_ce = par1Str;
    }

    public String func_142021_k()
    {
        return this.field_142022_ce;
    }

    public StatFileWriter func_146107_m()
    {
        return this.field_146108_bO;
    }
}
