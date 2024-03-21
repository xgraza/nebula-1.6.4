package net.minecraft.client.entity;

import nebula.client.Nebula;
import nebula.client.listener.event.EventStage;
import nebula.client.listener.event.player.EventMoveUpdate;
import nebula.client.listener.event.player.EventMultiUpdate;
import nebula.client.listener.event.player.EventUpdate;
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

public class EntityClientPlayerMP extends EntityPlayerSP
{
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter field_146108_bO;
    private double oldPosX;

    /** Old Minimum Y of the bounding box */
    private double oldMinY;
    private double oldPosZ;
    private float oldRotationYaw;
    private float oldRotationPitch;

    /** Check if was on ground last update */
    private boolean wasOnGround;

    /** should the player stop sneaking? */
    private boolean serverSneaking;
    private boolean serverSprinting;

    /**
     * Counter used to ensure that the server sends a move packet (Packet11, 12 or 13) to the client at least once a
     * second.
     */
    private int ticksSinceMovePacket;

    /** has the client player's health been set? */
    private boolean hasSetHealth;
    private String field_142022_ce;
    private static final String __OBFID = "CL_00000887";

    public EntityClientPlayerMP(Minecraft p_i45064_1_, World p_i45064_2_, Session p_i45064_3_, NetHandlerPlayClient p_i45064_4_, StatFileWriter p_i45064_5_)
    {
        super(p_i45064_1_, p_i45064_2_, p_i45064_3_, 0);
        this.sendQueue = p_i45064_4_;
        this.field_146108_bO = p_i45064_5_;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        return false;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(float par1) {}

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity par1Entity)
    {
        super.mountEntity(par1Entity);

        if (par1Entity instanceof EntityMinecart)
        {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)par1Entity));
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ)))
        {
            Nebula.BUS.dispatch(new EventUpdate());
            super.onUpdate();

            if (this.isRiding())
            {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            }
            else
            {
                this.sendMotionUpdates();

                EventMultiUpdate event = new EventMultiUpdate();
                Nebula.BUS.dispatch(event);

                if (event.updates() > 1) {

                    for (int i = 0; i < event.updates(); ++i) {
                        int oldItemInUse = itemInUseCount;
                        int oldHurtTime = hurtTime;
                        float oldPSwingPro = prevSwingProgress;
                        float oldSwingPro = swingProgress;
                        int oldSwingProI = swingProgressInt;
                        boolean oldISSwing = isSwingInProgress;
                        float oldYaw = rotationYaw;
                        float oldPYaw = prevRotationYaw;
                        float oldYawOff = renderYawOffset;
                        float oldPYawOff = prevRenderYawOffset;
                        float oldYawHead = rotationYawHead;
                        float oldPYawHead = prevRotationYawHead;
                        float oldCamYaw = cameraYaw;
                        float oldPCamYaw = prevCameraYaw;
                        float oldRArmYaw = renderArmYaw;
                        float oldPRArmYaw = prevRenderArmYaw;
                        float oldRenderArmP = renderArmPitch;
                        float oldPRenderArmP = prevRenderArmPitch;
                        float oldDistWalkedM = distanceWalkedModified;
                        float oldPDistWalkedM = prevDistanceWalkedModified;
                        float oldLimbSwingAmount = limbSwingAmount;
                        float oldPLimbSwingAmount = prevLimbSwingAmount;
                        float oldLimbSwing = limbSwing;
                        super.onUpdate();
                        itemInUseCount = oldItemInUse;
                        hurtTime = oldHurtTime;
                        prevSwingProgress = oldPSwingPro;
                        swingProgress = oldSwingPro;
                        swingProgressInt = oldSwingProI;
                        isSwingInProgress = oldISSwing;
                        rotationYaw = oldYaw;
                        prevRotationYaw = oldPYaw;
                        renderYawOffset = oldYawOff;
                        prevRenderYawOffset = oldPYawOff;
                        rotationYawHead = oldYawHead;
                        prevRotationYawHead = oldPYawHead;
                        cameraYaw = oldCamYaw;
                        prevCameraYaw = oldPCamYaw;
                        renderArmYaw = oldRArmYaw;
                        prevRenderArmYaw = oldPRArmYaw;
                        renderArmPitch = oldRenderArmP;
                        prevRenderArmPitch = oldPRenderArmP;
                        distanceWalkedModified = oldDistWalkedM;
                        prevDistanceWalkedModified = oldPDistWalkedM;
                        limbSwingAmount = oldLimbSwingAmount;
                        prevLimbSwingAmount = oldPLimbSwingAmount;
                        limbSwing = oldLimbSwing;
                        sendMotionUpdates();
                    }
                }
            }
        }
    }

    /**
     * Send updated motion and position information to the server
     */
    public void sendMotionUpdates()
    {
        EventMoveUpdate event = new EventMoveUpdate(EventStage.PRE,
          posX, boundingBox.minY, posY, posZ,
          rotationYaw, rotationPitch, onGround);
        if (Nebula.BUS.dispatch(event)) return;

        if (isSprinting() != serverSprinting) {
            sendQueue.addToSendQueue(new C0BPacketEntityAction(
              this, isSprinting() ? 4 : 5));
            serverSprinting = isSprinting();
        }

        if (isSneaking() != serverSneaking) {
            sendQueue.addToSendQueue(new C0BPacketEntityAction(
              this, isSneaking() ? 1 : 2));
            serverSneaking = isSneaking();
        }

        double diffX = event.x() - oldPosX;
        double diffY = event.y() - oldMinY;
        double diffZ = event.z() - oldPosZ;
        boolean moved = diffX * diffX + diffY * diffY + diffZ * diffZ > 9.0E-4D || ticksSinceMovePacket >= 20;

        float diffYaw = event.yaw() - oldRotationYaw;
        float diffPitch = event.pitch() - oldRotationPitch;
        boolean rotated = diffYaw != 0.0f || diffPitch != 0.0f;

        if (ridingEntity != null) {
            sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(
              motionX, -999.0, -999.0, motionZ,
              event.yaw(), event.pitch(), event.ground()));
            moved = false;
        } else if (moved && rotated) {
            sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(
              event.x(), event.y(), event.stance(), event.z(),
              event.yaw(), event.pitch(), event.ground()));
        } else if (moved) {
            sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
              event.x(), event.y(), event.stance(), event.z(), event.ground()));
        } else if (rotated) {
            sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(
              event.yaw(), event.pitch(), event.ground()));
        } else {
            sendQueue.addToSendQueue(new C03PacketPlayer(event.ground()));
        }

        ++ticksSinceMovePacket;
        wasOnGround = event.ground();

        if (moved) {
            oldPosX = event.x();
            oldMinY = event.y();
            oldPosZ = event.z();
            ticksSinceMovePacket = 0;
        }

        if (rotated) {
            oldRotationYaw = event.yaw();
            oldRotationPitch = event.pitch();
        }

        Nebula.BUS.dispatch(new EventMoveUpdate(EventStage.POST,
          posX, boundingBox.minY, posY, posZ,
          rotationYaw, rotationPitch, onGround));
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem dropOneItem(boolean par1)
    {
        int var2 = par1 ? 3 : 4;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(var2, 0, 0, 0, 0));
        return null;
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    protected void joinEntityItemWithWorld(EntityItem par1EntityItem) {}

    /**
     * Sends a chat message from the player. Args: chatMessage
     */
    public void sendChatMessage(String par1Str)
    {
        this.sendQueue.addToSendQueue(new C01PacketChatMessage(par1Str));
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        super.swingItem();
        this.sendQueue.addToSendQueue(new C0APacketAnimation(this, 1));
    }

    public void swingSilent() {
        this.sendQueue.addToSendQueue(new C0APacketAnimation(this, 1));
    }

    public void respawnPlayer()
    {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void damageEntity(DamageSource par1DamageSource, float par2)
    {
        if (!this.isEntityInvulnerable())
        {
            this.setHealth(this.getHealth() - par2);
        }
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeScreen()
    {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenNoPacket();
    }

    /**
     * Closes the GUI screen without sending a packet to the server
     */
    public void closeScreenNoPacket()
    {
        this.inventory.setItemStack((ItemStack)null);
        super.closeScreen();
    }

    /**
     * Updates health locally.
     */
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

    /**
     * Adds a value to a statistic field.
     */
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

    /**
     * Sends the player's abilities to the server (if there is one).
     */
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
