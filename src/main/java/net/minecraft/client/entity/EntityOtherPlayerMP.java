package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOtherPlayerMP extends AbstractClientPlayer
{
    private boolean isItemInUse;
    private int otherPlayerMPPosRotationIncrements;
    private double otherPlayerMPX;
    private double otherPlayerMPY;
    private double otherPlayerMPZ;
    private double otherPlayerMPYaw;
    private double otherPlayerMPPitch;
    private static final String __OBFID = "CL_00000939";

    public EntityOtherPlayerMP(World p_i45075_1_, GameProfile p_i45075_2_)
    {
        super(p_i45075_1_, p_i45075_2_);
        this.yOffset = 0.0F;
        this.stepHeight = 0.0F;
        this.noClip = true;
        this.field_71082_cx = 0.25F;
        this.renderDistanceWeight = 10.0D;
    }

    protected void resetHeight()
    {
        this.yOffset = 0.0F;
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        return true;
    }

    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.otherPlayerMPX = par1;
        this.otherPlayerMPY = par3;
        this.otherPlayerMPZ = par5;
        this.otherPlayerMPYaw = (double)par7;
        this.otherPlayerMPPitch = (double)par8;
        this.otherPlayerMPPosRotationIncrements = par9;
    }

    public void onUpdate()
    {
        this.field_71082_cx = 0.0F;
        super.onUpdate();
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double var1 = this.posX - this.prevPosX;
        double var3 = this.posZ - this.prevPosZ;
        float var5 = MathHelper.sqrt_double(var1 * var1 + var3 * var3) * 4.0F;

        if (var5 > 1.0F)
        {
            var5 = 1.0F;
        }

        this.limbSwingAmount += (var5 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;

        if (!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null)
        {
            ItemStack var6 = this.inventory.mainInventory[this.inventory.currentItem];
            this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], var6.getItem().getMaxItemUseDuration(var6));
            this.isItemInUse = true;
        }
        else if (this.isItemInUse && !this.isEating())
        {
            this.clearItemInUse();
            this.isItemInUse = false;
        }
    }

    public float getShadowSize()
    {
        return 0.0F;
    }

    public void onLivingUpdate()
    {
        super.updateEntityActionState();

        if (this.otherPlayerMPPosRotationIncrements > 0)
        {
            double var1 = this.posX + (this.otherPlayerMPX - this.posX) / (double)this.otherPlayerMPPosRotationIncrements;
            double var3 = this.posY + (this.otherPlayerMPY - this.posY) / (double)this.otherPlayerMPPosRotationIncrements;
            double var5 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double)this.otherPlayerMPPosRotationIncrements;
            double var7;

            for (var7 = this.otherPlayerMPYaw - (double)this.rotationYaw; var7 < -180.0D; var7 += 360.0D)
            {
                ;
            }

            while (var7 >= 180.0D)
            {
                var7 -= 360.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.otherPlayerMPPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.otherPlayerMPPitch - (double)this.rotationPitch) / (double)this.otherPlayerMPPosRotationIncrements);
            --this.otherPlayerMPPosRotationIncrements;
            this.setPosition(var1, var3, var5);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }

        this.prevCameraYaw = this.cameraYaw;
        float var9 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float var2 = (float)Math.atan(-this.motionY * 0.20000000298023224D) * 15.0F;

        if (var9 > 0.1F)
        {
            var9 = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0.0F)
        {
            var9 = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0.0F)
        {
            var2 = 0.0F;
        }

        this.cameraYaw += (var9 - this.cameraYaw) * 0.4F;
        this.cameraPitch += (var2 - this.cameraPitch) * 0.8F;
    }

    public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
    {
        if (par1 == 0)
        {
            this.inventory.mainInventory[this.inventory.currentItem] = par2ItemStack;
        }
        else
        {
            this.inventory.armorInventory[par1 - 1] = par2ItemStack;
        }
    }

    public float getEyeHeight()
    {
        return 1.82F;
    }

    public void addChatMessage(IChatComponent p_145747_1_)
    {
        Minecraft.getMinecraft().ingameGUI.getChatGui().printChatMessage(p_145747_1_);
    }

    public boolean canCommandSenderUseCommand(int par1, String par2Str)
    {
        return false;
    }

    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(MathHelper.floor_double(this.posX + 0.5D), MathHelper.floor_double(this.posY + 0.5D), MathHelper.floor_double(this.posZ + 0.5D));
    }
}
